package com.example.data

import kotlinx.coroutines.flow.Flow

class BookmarkRepository(
    private val bookmarkDao: BookmarkDao,
    private val whitelistDao: WhitelistDao
) {
    val allBookmarks: Flow<List<Bookmark>> = bookmarkDao.getAllBookmarks()
    val allWhitelisted: Flow<List<WhitelistItem>> = whitelistDao.getAllWhitelisted()

    suspend fun addBookmark(title: String, url: String) {
        bookmarkDao.insertBookmark(Bookmark(title = title.ifBlank { url }, url = url))
    }

    suspend fun removeBookmark(bookmark: Bookmark) {
        bookmarkDao.deleteBookmark(bookmark)
    }

    suspend fun removeBookmarkByUrl(url: String) {
        bookmarkDao.deleteByUrl(url)
    }

    fun isBookmarked(url: String): Flow<Int> {
        return bookmarkDao.isBookmarked(url)
    }

    suspend fun addWhitelist(domain: String) {
        whitelistDao.insertWhitelist(WhitelistItem(domain = domain))
    }

    suspend fun removeWhitelist(domain: String) {
        whitelistDao.deleteByDomain(domain)
    }

    suspend fun isDomainWhitelisted(domain: String): Boolean {
        return whitelistDao.isWhitelisted(domain) > 0
    }
}
