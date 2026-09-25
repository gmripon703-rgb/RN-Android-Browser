package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val url: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "whitelist")
data class WhitelistItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val domain: String,
    val addedAt: Long = System.currentTimeMillis()
)
