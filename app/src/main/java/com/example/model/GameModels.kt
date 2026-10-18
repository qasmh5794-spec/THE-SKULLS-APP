package com.example.model

enum class PlatformType(val displayName: String, val shortName: String) {
    PS5("PlayStation 5", "PS5"),
    XSX("Xbox Series X|S", "XSX"),
    PC("PC Gaming", "PC"),
    SWITCH("Nintendo Switch", "NSW")
}

data class ScoreBreakdown(
    val gameplay: Float,      // أسلوب اللعب
    val graphics: Float,      // الرسوميات
    val story: Float,         // القصة
    val audio: Float,         // المؤثرات والموسيقى
    val performance: Float    // الأداء والاستقرار
)

data class GameReview(
    val id: String,
    val title: String,
    val arabicTitle: String,
    val genre: String,
    val overallScore: Float,
    val verdictTag: String,
    val summary: String,
    val fullReview: String,
    val pros: List<String>,
    val cons: List<String>,
    val breakdown: ScoreBreakdown,
    val platforms: List<PlatformType>,
    val developer: String,
    val publisher: String,
    val releaseDate: String,
    val playtime: String,
    val reviewerName: String,
    val reviewDate: String,
    val imageUrl: String,
    val userScore: Float = 0f,
    val userReviewsCount: Int = 0,
    val isFeatured: Boolean = false,
    val isHallOfFame: Boolean = false
)

enum class NewsCategory(val arabicName: String, val iconName: String) {
    ALL("الكل", "all"),
    PLAYSTATION("بلايستيشن", "ps"),
    XBOX("إكس بوكس", "xbox"),
    PC("الكمبيوتر", "pc"),
    NINTENDO("نينتندو", "nintendo"),
    INDUSTRY("صناعة الألعاب", "industry"),
    LEAKS("تسريبات وإشاعات", "leaks")
}

data class GameNews(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val category: NewsCategory,
    val imageUrl: String,
    val author: String,
    val publishDate: String,
    val readTimeMinutes: Int,
    val isBreaking: Boolean = false,
    val likesCount: Int = 124,
    val viewsCount: Int = 1850
)

data class UpcomingGame(
    val id: String,
    val title: String,
    val arabicTitle: String,
    val releaseDateStr: String,
    val countdownDays: Int,
    val platforms: List<PlatformType>,
    val genre: String,
    val description: String,
    val hypeScorePercent: Int,
    val imageUrl: String
)

data class UserCommunityReview(
    val id: String,
    val gameId: String,
    val authorName: String,
    val rating: Float,
    val comment: String,
    val date: String,
    val platformPlayed: String
)
