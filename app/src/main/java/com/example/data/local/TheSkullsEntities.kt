package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarked_items")
data class BookmarkEntity(
    @PrimaryKey val itemId: String,
    val itemType: String, // "REVIEW" or "NEWS"
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "community_reviews")
data class CommunityReviewEntity(
    @PrimaryKey val id: String,
    val title: String,
    val arabicTitle: String,
    val genre: String,
    val platformsString: String, // "PS5,PC,XSX"
    val overallScore: Float,
    val gameplayScore: Float,
    val graphicsScore: Float,
    val storyScore: Float,
    val soundScore: Float,
    val performanceScore: Float = 9.0f,
    val verdict: String,
    val reviewBody: String,
    val prosString: String, // separated by "||"
    val consString: String, // separated by "||"
    val authorName: String,
    val authorId: String,
    val authorPhoto: String = "",
    val imageUrl: String = "",
    val developer: String = "",
    val publisher: String = "",
    val playtime: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val likesCount: Int = 0
)

@Entity(tableName = "community_news")
data class CommunityNewsEntity(
    @PrimaryKey val id: String,
    val title: String,
    val categoryName: String, // "PLAYSTATION", "XBOX", "PC", "NINTENDO", "INDUSTRY", "LEAKS"
    val summary: String,
    val content: String,
    val authorName: String,
    val authorId: String,
    val authorPhoto: String = "",
    val imageUrl: String = "",
    val isBreaking: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val likesCount: Int = 0
)

@Entity(tableName = "user_submitted_reviews")
data class UserSubmittedReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameId: String,
    val gameTitle: String,
    val authorName: String,
    val rating: Float,
    val reviewComment: String,
    val platformPlayed: String,
    val createdAt: Long = System.currentTimeMillis()
)
