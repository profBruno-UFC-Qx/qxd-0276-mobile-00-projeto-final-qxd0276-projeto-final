package com.example.bookkeeper.data

import com.example.bookkeeper.model.Book
import com.example.bookkeeper.model.User
import kotlinx.coroutines.flow.Flow

class BookRepository(private val bookDao: BookDao, private val userDao: UserDao) {

    // --- MÉTODOS DE USUÁRIO (Via UserDao) ---
    // Mantive a lógica que você já tinha para o usuário

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun login(email: String, pass: String): User? {
        return userDao.login(email, pass)
    }

    suspend fun registerUser(user: User): User? {
        return try {
            val newId = userDao.register(user)
            if (newId > 0) {
                user.copy(id = newId.toInt())
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    // --- MÉTODOS DE LIVRO (Via BookDao) ---
    // Agora usando os nomes EXATOS do seu BookDao atualizado

    fun getBooksForUser(userId: Int): Flow<List<Book>> {
        // Antes estava 'getBooksByUser', agora corrigido para:
        return bookDao.getBooksForUser(userId)
    }

    suspend fun saveBook(book: Book) {
        // Antes estava 'save', agora corrigido para:
        bookDao.insertBook(book)
    }

    // A função essencial para salvar suas anotações e edições
    suspend fun updateBook(book: Book) {
        bookDao.updateBook(book)
    }

    suspend fun deleteBook(book: Book) {
        // Antes estava 'delete', agora corrigido para:
        bookDao.deleteBook(book)
    }
}