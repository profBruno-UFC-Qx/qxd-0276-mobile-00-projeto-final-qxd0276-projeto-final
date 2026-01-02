package com.example.bookkeeper.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
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
import com.example.bookkeeper.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookViewModel(
    application: Application,
    private val repository: BookRepository
) : AndroidViewModel(application) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val prefs = application.getSharedPreferences("bookkeeper_prefs", Context.MODE_PRIVATE)

    init {
        viewModelScope.launch {
            _isLoading.value = true
            val savedUserId = prefs.getInt("logged_user_id", -1)
            _isDarkTheme.value = prefs.getBoolean("dark_mode", false)

            if (savedUserId != -1) {
                delay(2000)
                val user = repository.getUserById(savedUserId)
                if (user != null) {
                    _currentUser.value = user
                }
            } else {
                delay(1500)
            }
            _isLoading.value = false
        }
    }

    // A lista de livros "viva". Usaremos ela para checar duplicatas.
    @OptIn(ExperimentalCoroutinesApi::class)
    val books: StateFlow<List<Book>> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getBooksForUser(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // --- BUSCAR E SALVAR (COM BLOQUEIO DE DUPLICATAS) ---
    fun searchAndSaveBook(isbn: String, onSuccess: () -> Unit) {
        val user = _currentUser.value
        if (user == null) {
            showToast("Erro: Você precisa estar logado.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Busca na API
                val response = RetrofitClient.api.searchBookByIsbn("isbn:$isbn")
                val item = response.items?.firstOrNull()

                if (item != null) {
                    val info = item.volumeInfo // Pode ser nulo
                    val titleFound = info?.title ?: "Título Desconhecido"

                    // 2. VERIFICAÇÃO DE DUPLICIDADE 🛑
                    // Olha na lista atual (books.value) se alguém tem esse título
                    val jaExiste = books.value.any { livro ->
                        livro.title.equals(titleFound, ignoreCase = true)
                    }

                    if (jaExiste) {
                        showToast("Este livro já está na sua estante!")
                        // Não faz nada (não salva)
                    } else {
                        // 3. Se não existe, cria e salva
                        val newBook = Book(
                            title = titleFound,
                            author = info?.authors?.joinToString(", ") ?: "Autor Desconhecido",
                            totalPages = info?.pageCount ?: 0,
                            currentPage = 0,
                            status = "Quero Ler",
                            review = info?.description ?: "",
                            userId = user.id,
                            coverUrl = info?.imageLinks?.thumbnail?.replace("http://", "https://")
                        )

                        repository.saveBook(newBook)
                        showToast("Livro salvo na estante!")
                        onSuccess()
                    }

                } else {
                    showToast("Google: Livro não encontrado.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Erro de conexão: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun showToast(msg: String) {
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(getApplication(), msg, Toast.LENGTH_LONG).show()
        }
    }

    // --- LOGIN / CADASTRO / LOGOUT ---

    fun login(email: String, pass: String, onResult: (Boolean) -> Unit) {
        if (email.isBlank() || pass.isBlank()) { onResult(false); return }
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500)
            val user = repository.login(email, pass)
            if (user != null) {
                _currentUser.value = user
                saveLoginState(user.id)
                onResult(true)
            } else { onResult(false) }
            _isLoading.value = false
        }
    }

    fun register(name: String, email: String, pass: String, onResult: (Boolean) -> Unit) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) { onResult(false); return }
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500)
            if (repository.getUserByEmail(email) != null) {
                _isLoading.value = false
                onResult(false)
            } else {
                val newUser = User(name = name, email = email, password = pass)
                val registeredUser = repository.registerUser(newUser)
                if (registeredUser != null) {
                    _currentUser.value = registeredUser
                    saveLoginState(registeredUser.id)
                    onResult(true)
                } else { onResult(false) }
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        prefs.edit().clear().apply()
    }

    fun deleteAccount(onResult: () -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteUser(user)
            logout()
            onResult()
            _isLoading.value = false
        }
    }

    private fun saveLoginState(userId: Int) {
        prefs.edit().putInt("logged_user_id", userId).apply()
    }

    fun toggleTheme() {
        val newSetting = !_isDarkTheme.value
        _isDarkTheme.value = newSetting
        prefs.edit().putBoolean("dark_mode", newSetting).apply()
    }

    fun updateUserProfile(newName: String, newBio: String, newEmail: String, newPass: String, imageUri: String?) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val updatedUser = user.copy(name = newName, bio = newBio, email = newEmail, password = newPass, profilePictureUri = imageUri)
            repository.updateUser(updatedUser)
            _currentUser.value = updatedUser
        }
    }

    // --- CRUD MANUAL (Sem validação de duplicata, caso o usuário queira forçar) ---
    fun saveBook(book: Book) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.saveBook(book.copy(userId = user.id))
            _isLoading.value = false
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteBook(book)
            _isLoading.value = false
        }
    }

    // --- ARQUIVOS / IMAGENS ---
    fun saveImageToInternalStorage(uri: android.net.Uri): String? {
        val context = getApplication<Application>().applicationContext
        try {
            val fileName = "cover_${System.currentTimeMillis()}.jpg"
            val file = java.io.File(context.filesDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { input ->
                java.io.FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            return file.absolutePath
        } catch (e: Exception) { e.printStackTrace(); return null }
    }

    fun saveBitmapToInternalStorage(bitmap: android.graphics.Bitmap): String? {
        val context = getApplication<Application>().applicationContext
        try {
            val fileName = "camera_${System.currentTimeMillis()}.jpg"
            val file = java.io.File(context.filesDir, fileName)
            java.io.FileOutputStream(file).use { output ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, output)
            }
            return file.absolutePath
        } catch (e: Exception) { e.printStackTrace(); return null }
    }
    fun updateBookNotes(book: Book, newNotes: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBook(book.copy(userNotes = newNotes))
            _isLoading.value = false
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