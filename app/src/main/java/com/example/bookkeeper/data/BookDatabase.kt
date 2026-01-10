package com.example.bookkeeper.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bookkeeper.data.BookDao
import com.example.bookkeeper.data.UserDao
import com.example.bookkeeper.data.ReadingSessionDao
import com.example.bookkeeper.model.Book
import com.example.bookkeeper.model.User
import com.example.bookkeeper.model.ReadingSession

@Database(
    entities = [Book::class, User::class, ReadingSession::class],
    version = 8,
    exportSchema = false
)
abstract class BookDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun userDao(): UserDao

    abstract fun readingSessionDao(): ReadingSessionDao

    companion object {
        @Volatile
        private var Instance: BookDatabase? = null

        fun getDatabase(context: Context): BookDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, BookDatabase::class.java, "book_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}