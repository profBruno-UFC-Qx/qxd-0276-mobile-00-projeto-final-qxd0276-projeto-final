package com.example.bookkeeper

import android.app.Application
import com.example.bookkeeper.data.BookDatabase
import com.example.bookkeeper.data.BookRepository

class BookKeeperApplication : Application() {
    val database by lazy { BookDatabase.getDatabase(this) }

    val repository by lazy {
        BookRepository(database.bookDao(), database.userDao())
    }
}