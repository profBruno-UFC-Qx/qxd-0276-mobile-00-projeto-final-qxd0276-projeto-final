package com.example.bookkeeper.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookViewModel(
    application: Application,
    private val repository: BookRepository
) : AndroidViewModel(application) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val prefs = application.getSharedPreferences("bookkeeper_prefs", Context.MODE_PRIVATE)

    init {
        val savedUserId = prefs.getInt("logged_user_id", -1)
        if (savedUserId != -1) {
            viewModelScope.launch {
                val user = repository.getUserById(savedUserId)
                if (user != null) {
                    _currentUser.value = user
                }
            }
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


    // --- AUTENTICAÇÃO ---

    fun login(email: String, pass: String, onResult: (Boolean) -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            onResult(false); return
        }
        viewModelScope.launch {
            val user = repository.login(email, pass)
            if (user != null) {
                _currentUser.value = user
                saveLoginState(user.id)
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun register(name: String, email: String, pass: String, onResult: (Boolean) -> Unit) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            onResult(false); return
        }
        viewModelScope.launch {
            val newUser = User(name = name, email = email, password = pass)
            val registeredUser = repository.registerUser(newUser)
            if (registeredUser != null) {
                _currentUser.value = registeredUser
                saveLoginState(registeredUser.id)
                onResult(true)
            } else {
                onResult(false)
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


    fun updateUserProfile(newName: String, newBio: String, newEmail: String, newPass: String, imageUri: String?) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val updatedUser = user.copy(
                name = newName, bio = newBio, email = newEmail, password = newPass,
                profilePictureUri = imageUri // Atualiza a foto
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