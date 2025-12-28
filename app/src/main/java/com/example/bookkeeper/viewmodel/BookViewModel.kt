package com.example.bookkeeper.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookkeeper.BookKeeperApplication
import com.example.bookkeeper.data.BookRepository
import com.example.bookkeeper.model.Book
import com.example.bookkeeper.model.User
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
                delay(1500)
                val user = repository.getUserById(savedUserId)
                if (user != null) {
                    _currentUser.value = user
                }
            } else {
                delay(1000)
            }

            _isLoading.value = false
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val books: StateFlow<List<Book>> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getBooksForUser(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun login(email: String, pass: String, onResult: (Boolean) -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            onResult(false); return
        }
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500)

            val user = repository.login(email, pass)
            if (user != null) {
                _currentUser.value = user
                saveLoginState(user.id)
                onResult(true)
            } else {
                onResult(false)
            }
            _isLoading.value = false
        }
    }

    fun register(name: String, email: String, pass: String, onResult: (Boolean) -> Unit) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            onResult(false); return
        }
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500) // Simula processamento

            val existingUser = repository.getUserByEmail(email)
            if (existingUser != null) {
                _isLoading.value = false
                onResult(false)
            } else {
                val newUser = User(name = name, email = email, password = pass)
                val registeredUser = repository.registerUser(newUser)
                if (registeredUser != null) {
                    _currentUser.value = registeredUser
                    saveLoginState(registeredUser.id)
                    onResult(true)
                } else {
                    onResult(false)
                }
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        prefs.edit().clear().apply()
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
            val updatedUser = user.copy(
                name = newName, bio = newBio, email = newEmail, password = newPass,
                profilePictureUri = imageUri
            )
            repository.updateUser(updatedUser)
            _currentUser.value = updatedUser
        }
    }

    fun saveBook(book: Book) {
        val user = _currentUser.value ?: return
        viewModelScope.launch { repository.saveBook(book.copy(userId = user.id)) }
    }

    fun deleteBook(book: Book) { viewModelScope.launch { repository.deleteBook(book) } }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as BookKeeperApplication)
                BookViewModel(app, app.repository)
            }
        }
    }
}