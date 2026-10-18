package com.example.data

import com.example.data.local.BookmarkEntity
import com.example.data.local.CommunityNewsEntity
import com.example.data.local.CommunityReviewEntity
import com.example.data.local.TheSkullsDao
import com.example.data.local.UserSubmittedReviewEntity
import com.example.model.GameNews
import com.example.model.GameReview
import com.example.model.NewsCategory
import com.example.model.PlatformType
import com.example.model.ScoreBreakdown
import com.example.model.UpcomingGame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class TheSkullsRepository(private val dao: TheSkullsDao) {

    val bookmarks: Flow<List<BookmarkEntity>> = dao.getAllBookmarks()
    val userReviews: Flow<List<UserSubmittedReviewEntity>> = dao.getAllUserReviews()

    // Community-driven game reviews flow directly from user submissions
    val allReviews: Flow<List<GameReview>> = dao.getAllCommunityReviews().map { list ->
        list.map { entity ->
            val platforms = entity.platformsString.split(",")
                .mapNotNull { name ->
                    try {
                        PlatformType.valueOf(name.trim())
                    } catch (e: Exception) {
                        null
                    }
                }.ifEmpty { listOf(PlatformType.PS5) }

            val pros = entity.prosString.split("||").map { it.trim() }.filter { it.isNotEmpty() }
            val cons = entity.consString.split("||").map { it.trim() }.filter { it.isNotEmpty() }

            val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale("ar")).format(Date(entity.createdAt))

            GameReview(
                id = entity.id,
                title = entity.title,
                arabicTitle = entity.arabicTitle.ifBlank { entity.title },
                genre = entity.genre.ifBlank { "أكشن ومغامرات" },
                overallScore = entity.overallScore,
                verdictTag = entity.verdict.ifBlank { if (entity.overallScore >= 9.0f) "تحفة فنية" else "تقييم مجتمع سكلز" },
                summary = entity.reviewBody.take(150),
                fullReview = entity.reviewBody,
                pros = pros.ifEmpty { listOf("أسلوب لعب متقن وممتع") },
                cons = cons.ifEmpty { listOf("لا توجد سلبيات رئيسية مسجلة") },
                breakdown = ScoreBreakdown(
                    gameplay = entity.gameplayScore,
                    graphics = entity.graphicsScore,
                    story = entity.storyScore,
                    audio = entity.soundScore,
                    performance = entity.performanceScore
                ),
                platforms = platforms,
                developer = entity.developer.ifBlank { "مجتمع اللاعبين" },
                publisher = entity.publisher.ifBlank { "THE SKULLS Community" },
                releaseDate = "2026",
                playtime = entity.playtime.ifBlank { "مفتوح" },
                reviewerName = entity.authorName.ifBlank { "عضو في سكلز" },
                reviewDate = dateStr,
                imageUrl = entity.imageUrl.ifBlank { "android.resource://com.aistudio.theskulls.vkmq/drawable/the_skulls_banner" },
                userScore = entity.overallScore,
                userReviewsCount = 1,
                isFeatured = entity.overallScore >= 9.0f
            )
        }
    }

    // Community-driven game news flow directly from user submissions
    val allNews: Flow<List<GameNews>> = dao.getAllCommunityNews().map { list ->
        list.map { entity ->
            val category = try {
                NewsCategory.valueOf(entity.categoryName.trim())
            } catch (e: Exception) {
                NewsCategory.ALL
            }

            val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale("ar")).format(Date(entity.createdAt))

            GameNews(
                id = entity.id,
                title = entity.title,
                summary = entity.summary,
                content = entity.content,
                category = category,
                imageUrl = entity.imageUrl.ifBlank { "android.resource://com.aistudio.theskulls.vkmq/drawable/the_skulls_banner" },
                author = entity.authorName.ifBlank { "محرر سكلز" },
                publishDate = dateStr,
                readTimeMinutes = 3,
                isBreaking = entity.isBreaking,
                likesCount = entity.likesCount,
                viewsCount = 1
            )
        }
    }

    fun isBookmarked(id: String): Flow<Boolean> = dao.isItemBookmarked(id)

    suspend fun toggleBookmark(
        id: String,
        type: String,
        title: String,
        subtitle: String,
        imageUrl: String
    ) {
        dao.insertBookmark(
            BookmarkEntity(
                itemId = id,
                itemType = type,
                title = title,
                subtitle = subtitle,
                imageUrl = imageUrl
            )
        )
    }

    suspend fun removeBookmark(id: String) {
        dao.removeBookmark(id)
    }

    // User creates a new game review
    suspend fun publishGameReview(
        title: String,
        arabicTitle: String,
        genre: String,
        platforms: List<PlatformType>,
        overallScore: Float,
        gameplayScore: Float,
        graphicsScore: Float,
        storyScore: Float,
        soundScore: Float,
        verdict: String,
        reviewBody: String,
        pros: List<String>,
        cons: List<String>,
        authorName: String,
        authorId: String,
        authorPhoto: String = "",
        imageUrl: String = "",
        developer: String = "",
        publisher: String = "",
        playtime: String = ""
    ): String {
        val reviewId = "review_${UUID.randomUUID()}"
        val entity = CommunityReviewEntity(
            id = reviewId,
            title = title,
            arabicTitle = arabicTitle.ifBlank { title },
            genre = genre.ifBlank { "ألعاب متنوعة" },
            platformsString = platforms.joinToString(",") { it.name },
            overallScore = overallScore.coerceIn(1.0f, 10.0f),
            gameplayScore = gameplayScore.coerceIn(1.0f, 10.0f),
            graphicsScore = graphicsScore.coerceIn(1.0f, 10.0f),
            storyScore = storyScore.coerceIn(1.0f, 10.0f),
            soundScore = soundScore.coerceIn(1.0f, 10.0f),
            verdict = verdict.ifBlank { "تقييم اللاعب" },
            reviewBody = reviewBody,
            prosString = pros.filter { it.isNotBlank() }.joinToString("||"),
            consString = cons.filter { it.isNotBlank() }.joinToString("||"),
            authorName = authorName.ifBlank { "لاعب THE SKULLS" },
            authorId = authorId,
            authorPhoto = authorPhoto,
            imageUrl = imageUrl,
            developer = developer,
            publisher = publisher,
            playtime = playtime,
            createdAt = System.currentTimeMillis()
        )
        dao.insertCommunityReview(entity)
        return reviewId
    }

    suspend fun deleteGameReview(reviewId: String) {
        dao.deleteCommunityReview(reviewId)
    }

    // User publishes a new news post
    suspend fun publishGameNews(
        title: String,
        category: NewsCategory,
        summary: String,
        content: String,
        authorName: String,
        authorId: String,
        authorPhoto: String = "",
        imageUrl: String = "",
        isBreaking: Boolean = false
    ): String {
        val newsId = "news_${UUID.randomUUID()}"
        val entity = CommunityNewsEntity(
            id = newsId,
            title = title,
            categoryName = category.name,
            summary = summary.ifBlank { content.take(120) },
            content = content,
            authorName = authorName.ifBlank { "ناشر في سكلز" },
            authorId = authorId,
            authorPhoto = authorPhoto,
            imageUrl = imageUrl,
            isBreaking = isBreaking,
            createdAt = System.currentTimeMillis()
        )
        dao.insertCommunityNews(entity)
        return newsId
    }

    suspend fun deleteGameNews(newsId: String) {
        dao.deleteCommunityNews(newsId)
    }

    suspend fun addCommunityRating(
        gameId: String,
        gameTitle: String,
        author: String,
        rating: Float,
        comment: String,
        platform: String
    ) {
        dao.insertUserReview(
            UserSubmittedReviewEntity(
                gameId = gameId,
                gameTitle = gameTitle,
                authorName = author.ifBlank { "لاعب The Skulls" },
                rating = rating,
                reviewComment = comment,
                platformPlayed = platform,
                createdAt = System.currentTimeMillis()
            )
        )
    }
}
