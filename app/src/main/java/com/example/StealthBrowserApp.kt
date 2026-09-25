package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.BookmarkRepository

class StealthBrowserApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { BookmarkRepository(database.bookmarkDao(), database.whitelistDao()) }

    override fun onCreate() {
        super.onCreate()
    }
}
