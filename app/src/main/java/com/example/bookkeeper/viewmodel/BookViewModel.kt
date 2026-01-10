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
    private val _currentUser = MutableStateFlow<User?>(null); val currentUser = _currentUser.asStateFlow()
    private val _isLoading = MutableStateFlow(true); val isLoading = _isLoading.asStateFlow()
    private val _isDarkTheme = MutableStateFlow(false); val isDarkTheme = _isDarkTheme.asStateFlow()
    private val prefs = application.getSharedPreferences("bookkeeper_prefs", Context.MODE_PRIVATE)

    private val _elapsedTimeSeconds = MutableStateFlow(0L); val elapsedTimeSeconds = _elapsedTimeSeconds.asStateFlow()
    private val _isTimerRunning = MutableStateFlow(false); val isTimerRunning = _isTimerRunning.asStateFlow()
    private val _activeBookForSession = MutableStateFlow<Book?>(null); val activeBookForSession = _activeBookForSession.asStateFlow()
    private var timerJob: Job? = null

    private val _showSaveSessionDialog = MutableStateFlow(false); val showSaveSessionDialog = _showSaveSessionDialog.asStateFlow()
    var pagesReadInput by mutableStateOf("")

    @OptIn(ExperimentalCoroutinesApi::class) val totalPagesRead = _currentUser.flatMapLatest { if (it != null) repository.getTotalPagesRead(it.id) else flowOf(0) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            _isLoading.value = true
            val savedUserId = prefs.getInt("logged_user_id", -1)
            _isDarkTheme.value = prefs.getBoolean("dark_mode", false)
            if (savedUserId != -1) { delay(1000); val user = repository.getUserById(savedUserId); if (user != null) _currentUser.value = user }
            _isLoading.value = false
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class) val books = _currentUser.flatMapLatest { if (it != null) repository.getBooksForUser(it.id) else flowOf(emptyList()) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setActiveBookForSession(book: Book?) { _activeBookForSession.value = book; if (book == null && !_isTimerRunning.value) _elapsedTimeSeconds.value = 0 }
    fun getBookSessions(bookId: Int) = repository.getSessionsForBook(bookId)
    fun toggleTimer() { if (_isTimerRunning.value) stopTimer() else startTimer() }
    private fun startTimer() { if (_isTimerRunning.value) return; _isTimerRunning.value = true; timerJob = viewModelScope.launch { while (true) { delay(1000L); _elapsedTimeSeconds.value += 1 } } }
    private fun stopTimer() { _isTimerRunning.value = false; timerJob?.cancel() }
    fun finishSession() { stopTimer(); _showSaveSessionDialog.value = true }
    fun dismissSessionDialog() { _showSaveSessionDialog.value = false }
    fun confirmSaveSession(bookId: Int) {
        val pagesRead = pagesReadInput.toIntOrNull() ?: 0
        if (pagesRead > 0) saveReadingSession(bookId, pagesRead, _elapsedTimeSeconds.value) else showToast("Sessão descartada ou sem páginas.")
        pagesReadInput = ""; _elapsedTimeSeconds.value = 0; _showSaveSessionDialog.value = false; _activeBookForSession.value = null
    }

    private fun saveReadingSession(bookId: Int, pages: Int, duration: Long) {
        viewModelScope.launch {
            repository.insertReadingSession(ReadingSession(bookId = bookId, startTime = System.currentTimeMillis() - (duration * 1000), endTime = System.currentTimeMillis(), pagesRead = pages, durationSeconds = duration))
            val currentBook = books.value.find { it.id == bookId }
            if (currentBook != null) {
                val newPage = currentBook.currentPage + pages; val finalPage = if (newPage > currentBook.totalPages) currentBook.totalPages else newPage
                val newStatus = if (finalPage == currentBook.totalPages) "Lido" else "Lendo"
                repository.updateBook(currentBook.copy(currentPage = finalPage, status = newStatus))
                if (newStatus == "Lido") showToast("Parabéns! Livro concluído!")
            }
            showToast("Sessão salva: +$pages páginas")
        }
    }

    fun getBookNotes(bookId: Int) = repository.getNotesForBook(bookId)
    fun saveNote(bookId: Int, content: String, noteId: Int = 0) { val note = Note(id = noteId, bookId = bookId, content = content, timestamp = System.currentTimeMillis()); viewModelScope.launch { if (noteId == 0) repository.insertNote(note) else repository.updateNote(note); showToast(if (noteId == 0) "Nota criada!" else "Nota atualizada!") } }
    fun deleteNote(note: Note) { viewModelScope.launch { repository.deleteNote(note); showToast("Nota excluída.") } }

    fun searchAndSaveBook(isbn: String, onSuccess: () -> Unit) {
        val user = _currentUser.value ?: return showToast("Faça login primeiro.")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.api.searchBookByIsbn("isbn:$isbn")
                val item = response.items?.firstOrNull()
                if (item != null && item.volumeInfo != null) {
                    val info = item.volumeInfo
                    val title = info.title ?: "Sem Título"
                    if (books.value.any { it.title.equals(title, true) }) showToast("Livro já está na estante!") else {
                        repository.saveBook(Book(title = title, author = info.authors?.joinToString(", ") ?: "Desconhecido", totalPages = info.pageCount ?: 0, currentPage = 0, status = "Quero Ler", review = info.description ?: "", userId = user.id, coverUrl = info.imageLinks?.thumbnail?.replace("http:", "https:"))); showToast("Livro salvo!"); onSuccess()
                    }
                } else showToast("Livro não encontrado.")
            } catch (e: Exception) { showToast("Erro: ${e.message}") } finally { _isLoading.value = false }
        }
    }

    suspend fun saveImageToInternalStorage(uri: android.net.Uri) = withContext(Dispatchers.IO) { try { val context = getApplication<Application>().applicationContext; val fileName = "cover_${System.currentTimeMillis()}.jpg"; val file = java.io.File(context.filesDir, fileName); context.contentResolver.openInputStream(uri)?.use { i -> java.io.FileOutputStream(file).use { o -> i.copyTo(o) } }; file.absolutePath } catch (e: Exception) { null } }
    suspend fun saveBitmapToInternalStorage(bitmap: android.graphics.Bitmap) = withContext(Dispatchers.IO) { try { val context = getApplication<Application>().applicationContext; val fileName = "cam_${System.currentTimeMillis()}.jpg"; val file = java.io.File(context.filesDir, fileName); java.io.FileOutputStream(file).use { o -> bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, o) }; file.absolutePath } catch (e: Exception) { null } }

    fun saveBook(book: Book) { viewModelScope.launch { _currentUser.value?.let { repository.saveBook(book.copy(userId = it.id)) } } }
    fun updateBook(book: Book) { viewModelScope.launch { repository.updateBook(book) } }
    fun deleteBook(book: Book) { viewModelScope.launch { repository.deleteBook(book) } }
    fun login(e: String, p: String, r: (Boolean) -> Unit) { viewModelScope.launch { val u = repository.login(e, p); if (u != null) { _currentUser.value = u; prefs.edit().putInt("logged_user_id", u.id).apply(); r(true) } else r(false) } }
    fun register(n: String, e: String, p: String, r: (Boolean) -> Unit) { viewModelScope.launch { if (repository.getUserByEmail(e) == null) { val u = repository.registerUser(User(name=n, email=e, password=p)); if (u != null) { _currentUser.value = u; prefs.edit().putInt("logged_user_id", u.id).apply(); r(true) } else r(false) } else r(false) } }
    fun logout() { _currentUser.value = null; prefs.edit().clear().apply() }
    fun deleteAccount(onSuccess: () -> Unit) { viewModelScope.launch { _currentUser.value?.let { repository.deleteUser(it); logout(); onSuccess() } } }
    fun updateUserProfile(n: String, b: String, e: String, p: String, i: String?) { viewModelScope.launch { _currentUser.value?.let { val newU = it.copy(name=n, bio=b, email=e, password=p, profilePictureUri=i); repository.updateUser(newU); _currentUser.value = newU } } }
    fun toggleTheme() { _isDarkTheme.value = !_isDarkTheme.value; prefs.edit().putBoolean("dark_mode", _isDarkTheme.value).apply() }
    private fun showToast(msg: String) { viewModelScope.launch(Dispatchers.Main) { Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show() } }

    companion object { val Factory: ViewModelProvider.Factory = viewModelFactory { initializer { val app = (this[APPLICATION_KEY] as BookKeeperApplication); BookViewModel(app, app.repository) } } }
}