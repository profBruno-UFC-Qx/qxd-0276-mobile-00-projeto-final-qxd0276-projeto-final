package com.example.bookkeeper.data

import com.example.bookkeeper.model.Book
import com.example.bookkeeper.model.ReadingSession
import com.example.bookkeeper.model.User
import com.example.bookkeeper.model.Note
import kotlinx.coroutines.flow.Flow

class BookRepository(
    private val bookDao: BookDao,
    private val userDao: UserDao,
    private val sessionDao: ReadingSessionDao,
    private val noteDao: NoteDao
) {
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
        return bookDao.getBooksForUser(userId)
    }

    suspend fun saveBook(book: Book) {
        bookDao.insertBook(book)
    }

    suspend fun updateBook(book: Book) {
        bookDao.updateBook(book)
    }

    suspend fun deleteBook(book: Book) {
        bookDao.deleteBook(book)
    }

    suspend fun insertReadingSession(session: ReadingSession) {
        sessionDao.insertSession(session)
    }

    fun getSessionsForBook(bookId: Int): Flow<List<ReadingSession>> {
        return sessionDao.getSessionsForBook(bookId)
    }

    fun getAverageSecondsPerPage(bookId: Int): Flow<Float?> {
        return sessionDao.getAverageSecondsPerPage(bookId)
    }


    suspend fun insertNote(note: Note) {
        noteDao.insert(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.update(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    fun getNotesForBook(bookId: Int): Flow<List<Note>> {
        return noteDao.getNotesByBookId(bookId)
    }

    fun getTotalPagesRead(userId: Int): Flow<Int> {
        return bookDao.getTotalPagesRead(userId)
    }
}