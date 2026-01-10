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
import com.example.bookkeeper.model.ReadingSession
import com.example.bookkeeper.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookViewModel(
    application: Application,
    private val repository: BookRepository
) : AndroidViewModel(application) {

    // --- ESTADOS ---
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val prefs = application.getSharedPreferences("bookkeeper_prefs", Context.MODE_PRIVATE)

    // --- CRONÔMETRO ---
    private val _elapsedTimeSeconds = MutableStateFlow(0L)
    val elapsedTimeSeconds: StateFlow<Long> = _elapsedTimeSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

    // --- DIALOG ---
    private val _showSaveSessionDialog = MutableStateFlow(false)
    val showSaveSessionDialog: StateFlow<Boolean> = _showSaveSessionDialog.asStateFlow()
    var pagesReadInput by mutableStateOf("")

    init {
        viewModelScope.launch {
            _isLoading.value = true
            val savedUserId = prefs.getInt("logged_user_id", -1)
            _isDarkTheme.value = prefs.getBoolean("dark_mode", false)
            if (savedUserId != -1) {
                delay(1000)
                val user = repository.getUserById(savedUserId)
                if (user != null) _currentUser.value = user
            }
            _isLoading.value = false
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val books: StateFlow<List<Book>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getBooksForUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- FUNÇÕES DE TIMER E SESSÃO ---
    fun getBookSessions(bookId: Int) = repository.getSessionsForBook(bookId)

    fun toggleTimer() {
        if (_isTimerRunning.value) stopTimer() else startTimer()
    }

    private fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                _elapsedTimeSeconds.value += 1
            }
        }
    }

    private fun stopTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        _showSaveSessionDialog.value = true
    }

    fun dismissSessionDialog() { _showSaveSessionDialog.value = false }

    fun confirmSaveSession(bookId: Int) {
        val pagesRead = pagesReadInput.toIntOrNull() ?: 0
        if (pagesRead > 0) {
            saveReadingSession(bookId, pagesRead, _elapsedTimeSeconds.value)
        } else {
            showToast("Sessão descartada.")
        }
        pagesReadInput = ""
        _elapsedTimeSeconds.value = 0
        _showSaveSessionDialog.value = false
    }

    private fun saveReadingSession(bookId: Int, pages: Int, duration: Long) {
        viewModelScope.launch {
            repository.insertReadingSession(
                ReadingSession(
                    bookId = bookId,
                    startTime = System.currentTimeMillis() - (duration * 1000),
                    endTime = System.currentTimeMillis(),
                    pagesRead = pages,
                    durationSeconds = duration
                )
            )
            val currentBook = books.value.find { it.id == bookId }
            if (currentBook != null) {
                val newPage = currentBook.currentPage + pages
                val finalPage = if (newPage > currentBook.totalPages) currentBook.totalPages else newPage
                val newStatus = if (finalPage == currentBook.totalPages) "Lido" else "Lendo"

                repository.updateBook(currentBook.copy(currentPage = finalPage, status = newStatus))

                if (newStatus == "Lido") showToast("Parabéns! Livro concluído!")
            }
            showToast("Sessão salva: +$pages páginas")
        }
    }

    // --- API GOOGLE BOOKS (CORREÇÃO DE ERROS DE TIPO) ---
    fun searchAndSaveBook(isbn: String, onSuccess: () -> Unit) {
        val user = _currentUser.value ?: return showToast("Faça login primeiro.")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.api.searchBookByIsbn("isbn:$isbn")
                val item = response.items?.firstOrNull()

                // Tratamento seguro de nulos
                if (item != null) {
                    val info = item.volumeInfo
                    if (info != null) {
                        val title = info.title ?: "Sem Título"

                        // Verifica duplicatas
                        if (books.value.any { it.title.equals(title, true) }) {
                            showToast("Livro já está na estante!")
                        } else {
                            val newBook = Book(
                                title = title,
                                author = info.authors?.joinToString(", ") ?: "Desconhecido",
                                totalPages = info.pageCount ?: 0,
                                currentPage = 0,
                                status = "Quero Ler",
                                review = info.description ?: "", // O erro de String? estava aqui
                                userId = user.id,
                                coverUrl = info.imageLinks?.thumbnail?.replace("http:", "https:")
                            )
                            repository.saveBook(newBook)
                            showToast("Livro salvo!")
                            onSuccess()
                        }
                    } else {
                        showToast("Informações do livro incompletas.")
                    }
                } else {
                    showToast("Livro não encontrado.")
                }
            } catch (e: Exception) {
                showToast("Erro: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- SALVAR IMAGEM (SUSPEND) ---
    suspend fun saveImageToInternalStorage(uri: android.net.Uri): String? {
        return withContext(Dispatchers.IO) {
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
    }

    suspend fun saveBitmapToInternalStorage(bitmap: android.graphics.Bitmap): String? {
        return withContext(Dispatchers.IO) {
            try {
                val context = getApplication<Application>().applicationContext
                val fileName = "cam_${System.currentTimeMillis()}.jpg"
                val file = java.io.File(context.filesDir, fileName)
                java.io.FileOutputStream(file).use { output ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, output)
                }
                file.absolutePath
            } catch (e: Exception) { null }
        }
    }

    // --- CRUD ---
    fun saveBook(book: Book) {
        val user = _currentUser.value ?: return
        viewModelScope.launch { repository.saveBook(book.copy(userId = user.id)) }
    }
    fun updateBook(book: Book) { viewModelScope.launch { repository.updateBook(book) } }
    fun deleteBook(book: Book) { viewModelScope.launch { repository.deleteBook(book) } }
    fun updateBookNotes(book: Book, notes: String) {
        viewModelScope.launch { repository.updateBook(book.copy(userNotes = notes)) }
    }

    // --- AUTH ---
    fun login(e: String, p: String, r: (Boolean) -> Unit) {
        viewModelScope.launch {
            val u = repository.login(e, p)
            if (u != null) { _currentUser.value = u; prefs.edit().putInt("logged_user_id", u.id).apply(); r(true) }
            else r(false)
        }
    }
    fun register(n: String, e: String, p: String, r: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (repository.getUserByEmail(e) == null) {
                val u = repository.registerUser(User(name=n, email=e, password=p))
                if (u != null) { _currentUser.value = u; prefs.edit().putInt("logged_user_id", u.id).apply(); r(true) }
                else r(false)
            } else r(false)
        }
    }
    fun logout() { _currentUser.value = null; prefs.edit().clear().apply() }
    fun deleteAccount(r: () -> Unit) { viewModelScope.launch { _currentUser.value?.let { repository.deleteUser(it) }; logout(); r() } }
    fun toggleTheme() { _isDarkTheme.value = !_isDarkTheme.value; prefs.edit().putBoolean("dark_mode", _isDarkTheme.value).apply() }
    fun updateUserProfile(n: String, b: String, e: String, p: String, i: String?) {
        viewModelScope.launch {
            _currentUser.value?.let {
                val newU = it.copy(name=n, bio=b, email=e, password=p, profilePictureUri=i)
                repository.updateUser(newU)
                _currentUser.value = newU
            }
        }
    }

    private fun showToast(msg: String) {
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as BookKeeperApplication)
                BookViewModel(app, app.repository)
            }
        }
    }
}