package com.example.bookkeeper.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookkeeper.BookKeeperApplication
import com.example.bookkeeper.data.BookRepository
import com.example.bookkeeper.data.api.RetrofitClient
import com.example.bookkeeper.model.Book
import com.example.bookkeeper.model.Note
import com.example.bookkeeper.model.ReadingSession
import com.example.bookkeeper.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookViewModel(application: Application, private val repository: BookRepository) : AndroidViewModel(application) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    private val prefs = application.getSharedPreferences("bookkeeper_prefs", Context.MODE_PRIVATE)

    private val _elapsedTimeSeconds = MutableStateFlow(0L)
    val elapsedTimeSeconds = _elapsedTimeSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning = _isTimerRunning.asStateFlow()

    private val _activeBookForSession = MutableStateFlow<Book?>(null)
    val activeBookForSession = _activeBookForSession.asStateFlow()

    private var timerJob: Job? = null

    private val _showSaveSessionDialog = MutableStateFlow(false)
    val showSaveSessionDialog = _showSaveSessionDialog.asStateFlow()

    var pagesReadInput by mutableStateOf("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val books = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getBooksForUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBooksRead = books.map { list ->
        list.count { it.status == "Lido" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val currentReadingBook = books.map { list ->
        list.find { it.status == "Lendo" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalPagesRead = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getTotalPagesRead(user.id) else flowOf(0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            _isLoading.value = true
            val savedUserId = prefs.getInt("logged_user_id", -1)
            _isDarkTheme.value = prefs.getBoolean("dark_mode", false)
            delay(1000)
            if (savedUserId != -1) {
                val user = repository.getUserById(savedUserId)
                if (user != null) _currentUser.value = user
            }
            _isLoading.value = false
        }
    }

    private fun executeWithLoading(action: suspend () -> Unit, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            action()
            _isLoading.value = false
            onComplete?.invoke()
        }
    }

    fun setActiveBookForSession(book: Book?) {
        _activeBookForSession.value = book
    }

    fun getBookSessions(bookId: Int) = repository.getSessionsForBook(bookId)

    fun toggleTimer() { if (_isTimerRunning.value) stopTimer() else startTimer() }

    private fun startTimer() {
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (true) { delay(1000L); _elapsedTimeSeconds.value += 1 }
        }
    }

    private fun stopTimer() { _isTimerRunning.value = false; timerJob?.cancel() }

    fun finishSession() { stopTimer(); _showSaveSessionDialog.value = true }

    fun dismissSessionDialog() { _showSaveSessionDialog.value = false }

    fun confirmSaveSession(bookId: Int) {
        val pages = pagesReadInput.toIntOrNull() ?: 0
        executeWithLoading(
            action = { saveReadingSession(bookId, pages, _elapsedTimeSeconds.value) },
            onComplete = {
                pagesReadInput = ""; _elapsedTimeSeconds.value = 0
                _showSaveSessionDialog.value = false; _activeBookForSession.value = null
            }
        )
    }

    private suspend fun saveReadingSession(bookId: Int, pages: Int, duration: Long) {
        repository.insertReadingSession(ReadingSession(bookId = bookId, startTime = System.currentTimeMillis() - (duration*1000), endTime = System.currentTimeMillis(), pagesRead = pages, durationSeconds = duration))
        val book = books.value.find { it.id == bookId } ?: return

        if (book.totalPages > 0) {
            val newPage = (book.currentPage + pages).coerceAtMost(book.totalPages)
            val newStatus = if (newPage == book.totalPages) "Lido" else "Lendo"
            repository.updateBook(book.copy(currentPage = newPage, status = newStatus))
        } else {
            repository.updateBook(book.copy(status = "Lendo"))
            withContext(Dispatchers.Main) {
                Toast.makeText(getApplication(), "Defina o total de páginas para acompanhar o progresso!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getBookNotes(bookId: Int) = repository.getNotesForBook(bookId)
    fun saveNote(bookId: Int, content: String, noteId: Int = 0) {
        val note = Note(id = noteId, bookId = bookId, content = content, timestamp = System.currentTimeMillis())
        executeWithLoading({ if (noteId == 0) repository.insertNote(note) else repository.updateNote(note) })
    }
    fun deleteNote(note: Note) = executeWithLoading({ repository.deleteNote(note) })

    fun searchAndSaveBook(isbn: String, onSuccess: () -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.api.searchBookByIsbn("isbn:$isbn")
                val info = response.items?.firstOrNull()?.volumeInfo
                if (info != null) {
                    val newBook = Book(
                        title = info.title ?: "Sem Título",
                        author = info.authors?.joinToString(", ") ?: "Desconhecido",
                        totalPages = info.pageCount ?: 0, // Pode vir 0 da API
                        currentPage = 0,
                        status = "Quero Ler",
                        review = info.description ?: "",
                        userId = user.id,
                        coverUrl = info.imageLinks?.thumbnail?.replace("http:", "https:")
                    )
                    repository.saveBook(newBook)
                    withContext(Dispatchers.Main) { onSuccess() }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { Toast.makeText(getApplication(), "Erro na busca", Toast.LENGTH_SHORT).show() }
            }
            _isLoading.value = false
        }
    }

    fun saveBook(book: Book, onComplete: (() -> Unit)? = null) {
        val user = _currentUser.value ?: return
        executeWithLoading({ repository.saveBook(book.copy(userId = user.id)) }, onComplete)
    }

    fun updateBook(book: Book) {
        val updated = if (book.status == "Lido" && book.totalPages > 0) {
            book.copy(currentPage = book.totalPages)
        } else book
        executeWithLoading({ repository.updateBook(updated) })
    }

    fun deleteBook(book: Book) = executeWithLoading({ repository.deleteBook(book) })

    suspend fun saveImageToInternalStorage(uri: android.net.Uri) = withContext(Dispatchers.IO) {
        try {
            val context = getApplication<Application>().applicationContext
            val fileName = "cover_${System.currentTimeMillis()}.jpg"
            val file = java.io.File(context.filesDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { input ->
                java.io.FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            file.absolutePath
        } catch (e: Exception) { null }
    }

    suspend fun saveBitmapToInternalStorage(bitmap: android.graphics.Bitmap) = withContext(Dispatchers.IO) {
        try {
            val context = getApplication<Application>().applicationContext
            val fileName = "cam_${System.currentTimeMillis()}.jpg"
            val file = java.io.File(context.filesDir, fileName)
            java.io.FileOutputStream(file).use { o -> bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, o) }
            file.absolutePath
        } catch (e: Exception) { null }
    }

    fun login(e: String, p: String, r: (Boolean) -> Unit) = viewModelScope.launch {
        _isLoading.value = true
        val u = repository.login(e, p)
        if (u != null) { _currentUser.value = u; prefs.edit().putInt("logged_user_id", u.id).apply(); r(true) } else r(false)
        _isLoading.value = false
    }

    fun register(n: String, e: String, p: String, r: (Boolean) -> Unit) = viewModelScope.launch {
        _isLoading.value = true
        val u = repository.registerUser(User(name=n, email=e, password=p))
        if (u != null) { _currentUser.value = u; prefs.edit().putInt("logged_user_id", u.id).apply(); r(true) } else r(false)
        _isLoading.value = false
    }

    fun logout() { _currentUser.value = null; prefs.edit().clear().apply() }
    fun deleteAccount(onSuccess: () -> Unit) = executeWithLoading({ _currentUser.value?.let { repository.deleteUser(it) }; logout() }, onSuccess)
    fun updateUserProfile(n: String, b: String, e: String, p: String, i: String?) = executeWithLoading({
        _currentUser.value?.let {
            val newU = it.copy(name=n, bio=b, email=e, password=p, profilePictureUri=i)
            repository.updateUser(newU); _currentUser.value = newU
        }
    })

    fun toggleTheme() { _isDarkTheme.value = !_isDarkTheme.value; prefs.edit().putBoolean("dark_mode", _isDarkTheme.value).apply() }
    private fun showToast(msg: String) { viewModelScope.launch(Dispatchers.Main) { Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show() } }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as BookKeeperApplication)
                val db = com.example.bookkeeper.data.BookDatabase.getDatabase(app.applicationContext)
                BookViewModel(app, BookRepository(db.bookDao(), db.userDao(), db.readingSessionDao(), db.noteDao()))
            }
        }
    }
}