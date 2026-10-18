package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TheSkullsDao {
    @Query("SELECT * FROM bookmarked_items ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_items WHERE itemId = :id)")
    fun isItemBookmarked(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarked_items WHERE itemId = :id")
    suspend fun removeBookmark(id: String)

    // Community Reviews created by users
    @Query("SELECT * FROM community_reviews ORDER BY createdAt DESC")
    fun getAllCommunityReviews(): Flow<List<CommunityReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityReview(review: CommunityReviewEntity)

    @Query("DELETE FROM community_reviews WHERE id = :id")
    suspend fun deleteCommunityReview(id: String)

    // Community News created by users
    @Query("SELECT * FROM community_news ORDER BY createdAt DESC")
    fun getAllCommunityNews(): Flow<List<CommunityNewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityNews(news: CommunityNewsEntity)

    @Query("DELETE FROM community_news WHERE id = :id")
    suspend fun deleteCommunityNews(id: String)

    // User ratings / comments
    @Query("SELECT * FROM user_submitted_reviews ORDER BY createdAt DESC")
    fun getAllUserReviews(): Flow<List<UserSubmittedReviewEntity>>

    @Query("SELECT * FROM user_submitted_reviews WHERE gameId = :gameId ORDER BY createdAt DESC")
    fun getUserReviewsForGame(gameId: String): Flow<List<UserSubmittedReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserReview(review: UserSubmittedReviewEntity)
}
