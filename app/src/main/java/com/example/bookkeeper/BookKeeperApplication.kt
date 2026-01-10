package com.example.bookkeeper

import android.app.Application
import androidx.work.* // Import do WorkManager
import com.example.bookkeeper.data.BookDatabase
import com.example.bookkeeper.data.BookRepository
import com.example.bookkeeper.worker.ReminderWorker
import java.util.concurrent.TimeUnit

class BookKeeperApplication : Application() {
    val database by lazy { BookDatabase.getDatabase(this) }

    val repository by lazy {
        BookRepository(
            database.bookDao(),
            database.userDao(),
            database.readingSessionDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        setupRecurringWork()
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyReadingReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }
}