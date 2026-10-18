package com.example

import com.example.model.GameNews
import com.example.model.GameReview
import com.example.model.NewsCategory
import com.example.model.PlatformType
import com.example.model.ScoreBreakdown
import com.example.model.UpcomingGame
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testScoreBreakdownCalculations() {
        val breakdown = ScoreBreakdown(
            gameplay = 9.5f,
            graphics = 9.0f,
            story = 8.5f,
            audio = 9.0f,
            performance = 9.0f
        )
        val average = (breakdown.gameplay + breakdown.graphics + breakdown.story + breakdown.audio + breakdown.performance) / 5f
        assertEquals(9.0f, average, 0.01f)
        assertTrue("Gameplay score should be valid", breakdown.gameplay in 1.0f..10.0f)
    }

    @Test
    fun testGameReviewCreation() {
        val review = GameReview(
            id = "test_review_1",
            title = "Elden Ring",
            arabicTitle = "إلدن رينغ",
            genre = "Action RPG",
            overallScore = 9.8f,
            verdictTag = "تحفة فنية",
            summary = "مغامرة أسطورية في الأراضي الواقعة بين العالمين",
            fullReview = "مراجعة كاملة لعالم إلدن رينغ الساحر والصعب.",
            pros = listOf("عالم مفتوح مذهل", "قتال زعماء ملحمي"),
            cons = listOf("هبوط طفيف في الإطارات"),
            breakdown = ScoreBreakdown(10f, 9.5f, 9.5f, 10f, 9f),
            platforms = listOf(PlatformType.PS5, PlatformType.PC, PlatformType.XSX),
            developer = "FromSoftware",
            publisher = "Bandai Namco",
            releaseDate = "2022",
            playtime = "100+ ساعة",
            reviewerName = "فريق سكلز",
            reviewDate = "18 أكتوبر 2026",
            imageUrl = "https://example.com/image.jpg",
            isFeatured = true
        )
        assertEquals("test_review_1", review.id)
        assertTrue(review.isFeatured)
        assertTrue(review.platforms.contains(PlatformType.PS5))
        assertEquals(2, review.pros.size)
    }

    @Test
    fun testNewsCategories() {
        val categories = NewsCategory.values()
        assertTrue("News categories should not be empty", categories.isNotEmpty())
        assertTrue("Should include PlayStation category", categories.contains(NewsCategory.PLAYSTATION))
        assertTrue("Should include Xbox category", categories.contains(NewsCategory.XBOX))
        assertTrue("Should include PC category", categories.contains(NewsCategory.PC))
        assertEquals("بلايستيشن", NewsCategory.PLAYSTATION.arabicName)
    }

    @Test
    fun testUpcomingGameModel() {
        val upcoming = UpcomingGame(
            id = "gta_vi",
            title = "Grand Theft Auto VI",
            arabicTitle = "جراند ثفت أوتو 6",
            releaseDateStr = "خريف 2025 / 2026",
            countdownDays = 120,
            platforms = listOf(PlatformType.PS5, PlatformType.XSX),
            genre = "عالم مفتوح وأكشن",
            description = "الجزء المنتظر بشدة من روكستار جيمز",
            hypeScorePercent = 99,
            imageUrl = "https://example.com/gta.jpg"
        )
        assertTrue("Countdown days must be positive", upcoming.countdownDays > 0)
        assertTrue("Hype score must be in percentage", upcoming.hypeScorePercent in 0..100)
        assertEquals(99, upcoming.hypeScorePercent)
    }
}

