package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<Bookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark): Long

    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)

    @Query("DELETE FROM bookmarks WHERE url = :url")
    suspend fun deleteByUrl(url: String)

    @Query("SELECT COUNT(*) FROM bookmarks WHERE url = :url")
    fun isBookmarked(url: String): Flow<Int>
}

@Dao
interface WhitelistDao {
    @Query("SELECT * FROM whitelist ORDER BY addedAt DESC")
    fun getAllWhitelisted(): Flow<List<WhitelistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWhitelist(item: WhitelistItem): Long

    @Query("DELETE FROM whitelist WHERE domain = :domain")
    suspend fun deleteByDomain(domain: String)

    @Query("SELECT COUNT(*) FROM whitelist WHERE domain = :domain")
    suspend fun isWhitelisted(domain: String): Int
}
