package com.example.bookkeeper.data

import com.example.bookkeeper.model.Book
import com.example.bookkeeper.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class BookRepository(
    private val bookDao: BookDao,
    private val userDao: UserDao
) {

    // --- ÁREA DE USUÁRIOS (Login/Cadastro) ---

    suspend fun login(email: String, pass: String): User? {
        return userDao.login(email, pass)
    }

    suspend fun registerUser(user: User): Boolean {
        // Verifica se já existe
        val existing = userDao.getUserByEmail(user.email)
        if (existing != null) return false // Já existe, falha

        try {
            userDao.register(user)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    // --- ÁREA DE LIVROS ---

    // Agora precisamos do ID do usuário para buscar os livros DELE
    fun getBooksForUser(userId: String): Flow<List<Book>> {
        return bookDao.getBooksByUser(userId)
    }

    suspend fun saveBook(book: Book) {
        bookDao.save(book)
    }

    suspend fun deleteBook(book: Book) {
        bookDao.delete(book)
    }
}