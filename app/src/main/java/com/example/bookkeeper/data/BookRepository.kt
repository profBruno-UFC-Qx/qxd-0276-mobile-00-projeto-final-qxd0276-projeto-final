package com.example.bookkeeper.data

import com.example.bookkeeper.model.Book
import com.example.bookkeeper.model.User
import kotlinx.coroutines.flow.Flow

class BookRepository(private val bookDao: BookDao, private val userDao: UserDao) {


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

    fun getBooksForUser(userId: Int): Flow<List<Book>> {
        return bookDao.getBooksByUser(userId)
    }

    suspend fun saveBook(book: Book) {
        bookDao.save(book)
    }

    suspend fun deleteBook(book: Book) {
        bookDao.delete(book)
    }
}