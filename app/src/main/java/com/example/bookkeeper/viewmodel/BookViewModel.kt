package com.example.bookkeeper.viewmodel

import androidx.lifecycle.ViewModel
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

class BookViewModel(private val repository: BookRepository) : ViewModel() {

    // --- ESTADO DO USUÁRIO ---
    // Guarda quem é o usuário logado no momento (null se ninguém logou)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // --- ESTADO DOS LIVROS ---
    // A lista de livros muda automaticamente quando o usuário muda!
    @OptIn(ExperimentalCoroutinesApi::class)
    val books: StateFlow<List<Book>> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getBooksForUser(user.id) // Se tem usuário, busca os livros dele
        } else {
            flowOf(emptyList()) // Se não tem usuário, lista vazia
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // --- FUNÇÕES DE AUTENTICAÇÃO ---

    fun login(email: String, pass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.login(email, pass)
            if (user != null) {
                _currentUser.value = user
                onResult(true) // Sucesso
            } else {
                onResult(false) // Falha
            }
        }
    }

    fun register(name: String, email: String, pass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val newUser = User(name = name, email = email, password = pass)
            val success = repository.registerUser(newUser)
            if (success) {
                // Se cadastrou com sucesso, já faz o login automático
                _currentUser.value = newUser
            }
            onResult(success)
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    // --- FUNÇÕES DE LIVROS ---

    fun saveBook(book: Book) {
        // Só salva se tiver alguém logado
        val user = _currentUser.value ?: return

        // Garante que o livro vai ficar com o ID do usuário certo
        val bookWithUser = book.copy(userId = user.id)

        viewModelScope.launch {
            repository.saveBook(bookWithUser)
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BookKeeperApplication)
                BookViewModel(application.repository)
            }
        }
    }
}