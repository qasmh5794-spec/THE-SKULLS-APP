package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TheSkullsRepository
import com.example.data.firebase.FirestoreTheSkullsRepository
import com.example.data.firebase.ReviewComment
import com.example.data.firebase.ReviewLike
import com.example.data.firebase.UserProfile
import com.example.data.local.BookmarkEntity
import com.example.data.local.TheSkullsDatabase
import com.example.data.local.UserSubmittedReviewEntity
import com.example.model.GameNews
import com.example.model.GameReview
import com.example.model.NewsCategory
import com.example.model.PlatformType
import com.example.model.UpcomingGame
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppNavTab(val labelArabic: String, val iconKey: String) {
    HOME("الرئيسية", "home"),
    REVIEWS("المراجعات", "reviews"),
    NEWS("الأخبار", "news"),
    RADAR("الرادار", "radar"),
    SAVED("حسابي", "saved")
}

data class SkullsUiState(
    val currentTab: AppNavTab = AppNavTab.HOME,
    val selectedReview: GameReview? = null,
    val selectedNews: GameNews? = null,
    val searchQuery: String = "",
    val selectedPlatformFilter: PlatformType? = null,
    val selectedNewsCategory: NewsCategory = NewsCategory.ALL,
    val showReviewDialog: Boolean = false,
    val showAddReviewDialog: Boolean = false,
    val showAddNewsDialog: Boolean = false,
    val showAuthPromptDialog: Boolean = false,
    val userFeedbackMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class TheSkullsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = TheSkullsDatabase.getDatabase(application)
    private val localRepository = TheSkullsRepository(database.theSkullsDao())
    private val firestoreRepository = FirestoreTheSkullsRepository(application)
    private val auth = Firebase.auth

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _uiState = MutableStateFlow(SkullsUiState())
    val uiState: StateFlow<SkullsUiState> = _uiState.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    val userProfile: StateFlow<UserProfile?> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            firestoreRepository.observeUserProfile(user.uid)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Observe likes for currently selected review
    val currentReviewLikes: StateFlow<List<ReviewLike>> = _uiState
        .map { it.selectedReview?.id }
        .distinctUntilChanged()
        .flatMapLatest { reviewId ->
            if (reviewId != null && _currentUser.value != null) {
                firestoreRepository.observeReviewLikes(reviewId)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Observe comments for currently selected review
    val currentReviewComments: StateFlow<List<ReviewComment>> = _uiState
        .map { it.selectedReview?.id }
        .distinctUntilChanged()
        .flatMapLatest { reviewId ->
            if (reviewId != null && _currentUser.value != null) {
                firestoreRepository.observeReviewComments(reviewId)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedItems: StateFlow<List<BookmarkEntity>> = localRepository.bookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSubmittedReviews: StateFlow<List<UserSubmittedReviewEntity>> = localRepository.userReviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Community reviews and news (only added by users)
    val allReviews: StateFlow<List<GameReview>> = localRepository.allReviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredReviews: StateFlow<List<GameReview>> = allReviews.map { reviews ->
        reviews.filter { it.isFeatured || it.overallScore >= 9.0f }.ifEmpty { reviews.take(5) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNews: StateFlow<List<GameNews>> = localRepository.allNews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingGames: StateFlow<List<UpcomingGame>> = flowOf(emptyList<UpcomingGame>())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: AppNavTab) {
        _uiState.value = _uiState.value.copy(
            currentTab = tab,
            selectedReview = null,
            selectedNews = null,
            searchQuery = ""
        )
    }

    fun openReview(review: GameReview) {
        _uiState.value = _uiState.value.copy(selectedReview = review, selectedNews = null)
    }

    fun closeReviewDetail() {
        _uiState.value = _uiState.value.copy(selectedReview = null)
    }

    fun openNews(news: GameNews) {
        _uiState.value = _uiState.value.copy(selectedNews = news, selectedReview = null)
    }

    fun closeNewsDetail() {
        _uiState.value = _uiState.value.copy(selectedNews = null)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun selectNewsCategory(category: NewsCategory) {
        _uiState.value = _uiState.value.copy(selectedNewsCategory = category)
    }

    fun selectPlatformFilter(platform: PlatformType?) {
        _uiState.value = _uiState.value.copy(
            selectedPlatformFilter = if (_uiState.value.selectedPlatformFilter == platform) null else platform
        )
    }

    fun isBookmarked(id: String): Boolean {
        return bookmarkedItems.value.any { it.itemId == id }
    }

    fun toggleReviewBookmark(review: GameReview) {
        viewModelScope.launch {
            if (isBookmarked(review.id)) {
                localRepository.removeBookmark(review.id)
                _uiState.value = _uiState.value.copy(userFeedbackMessage = "تمت الإزالة من المفضلة")
            } else {
                localRepository.toggleBookmark(
                    id = review.id,
                    type = "REVIEW",
                    title = review.title,
                    subtitle = "${review.arabicTitle} • تقييم ${review.overallScore}",
                    imageUrl = review.imageUrl
                )
                _uiState.value = _uiState.value.copy(userFeedbackMessage = "تم حفظ المراجعة في المفضلة")
            }
        }
    }

    fun toggleNewsBookmark(news: GameNews) {
        viewModelScope.launch {
            if (isBookmarked(news.id)) {
                localRepository.removeBookmark(news.id)
                _uiState.value = _uiState.value.copy(userFeedbackMessage = "تمت إزالة الخبر من المحفوظات")
            } else {
                localRepository.toggleBookmark(
                    id = news.id,
                    type = "NEWS",
                    title = news.title,
                    subtitle = news.category.arabicName,
                    imageUrl = news.imageUrl
                )
                _uiState.value = _uiState.value.copy(userFeedbackMessage = "تم حفظ الخبر للقراءة لاحقاً")
            }
        }
    }

    // Toggle Review Like in Firestore
    fun toggleReviewLike(reviewId: String) {
        val user = _currentUser.value
        if (user == null) {
            _uiState.value = _uiState.value.copy(showAuthPromptDialog = true)
            return
        }

        viewModelScope.launch {
            try {
                val isLikedNow = firestoreRepository.toggleLike(reviewId)
                _uiState.value = _uiState.value.copy(
                    userFeedbackMessage = if (isLikedNow) "تم الإعجاب بالمراجعة ❤️" else "تم إلغاء الإعجاب"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    userFeedbackMessage = "تعذر تحديث الإعجاب: ${e.localizedMessage}"
                )
            }
        }
    }

    // Add Comment in Firestore
    fun addCommentToReview(reviewId: String, commentText: String, rating: Double) {
        val user = _currentUser.value
        if (user == null) {
            _uiState.value = _uiState.value.copy(showAuthPromptDialog = true)
            return
        }

        val tag = userProfile.value?.gamerTag ?: "SkullPlayer"
        viewModelScope.launch {
            try {
                firestoreRepository.addComment(
                    reviewId = reviewId,
                    commentText = commentText,
                    rating = rating,
                    gamerTag = tag
                )
                _uiState.value = _uiState.value.copy(
                    userFeedbackMessage = "تم نشر تعليقك بنجاح!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    userFeedbackMessage = "فشل نشر التعليق: ${e.localizedMessage}"
                )
            }
        }
    }

    // Delete Comment from Firestore
    fun deleteComment(reviewId: String, commentId: String) {
        viewModelScope.launch {
            try {
                firestoreRepository.deleteComment(reviewId, commentId)
                _uiState.value = _uiState.value.copy(userFeedbackMessage = "تم حذف التعليق")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(userFeedbackMessage = "فشل حذف التعليق")
            }
        }
    }

    // Save gamer profile
    fun saveUserProfile(gamerTag: String, favoritePlatform: String) {
        val user = _currentUser.value ?: return
        val currentProfile = userProfile.value
        viewModelScope.launch {
            try {
                firestoreRepository.saveUserProfile(
                    UserProfile(
                        userId = user.uid,
                        displayName = user.displayName ?: "لاعب سكلز",
                        email = user.email ?: "",
                        photoUrl = user.photoUrl?.toString() ?: "",
                        gamerTag = gamerTag,
                        favoritePlatform = favoritePlatform,
                        createdAt = currentProfile?.createdAt
                    )
                )
                _uiState.value = _uiState.value.copy(userFeedbackMessage = "تم تحديث الملف الشخصي بنجاح")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(userFeedbackMessage = "فشل حفظ الملف الشخصي")
            }
        }
    }

    // Dialog controls for adding reviews & news
    fun openAddReviewDialog() {
        _uiState.value = _uiState.value.copy(showAddReviewDialog = true)
    }

    fun closeAddReviewDialog() {
        _uiState.value = _uiState.value.copy(showAddReviewDialog = false)
    }

    fun openAddNewsDialog() {
        _uiState.value = _uiState.value.copy(showAddNewsDialog = true)
    }

    fun closeAddNewsDialog() {
        _uiState.value = _uiState.value.copy(showAddNewsDialog = false)
    }

    fun publishNewReview(
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
        developer: String = "",
        publisher: String = "",
        playtime: String = "",
        imageUrl: String = ""
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            val authorName = user?.displayName ?: (userProfile.value?.displayName ?: "لاعب سكلز")
            val authorId = user?.uid ?: "user_${System.currentTimeMillis()}"
            val authorPhoto = user?.photoUrl?.toString() ?: ""

            localRepository.publishGameReview(
                title = title,
                arabicTitle = arabicTitle,
                genre = genre,
                platforms = platforms,
                overallScore = overallScore,
                gameplayScore = gameplayScore,
                graphicsScore = graphicsScore,
                storyScore = storyScore,
                soundScore = soundScore,
                verdict = verdict,
                reviewBody = reviewBody,
                pros = pros,
                cons = cons,
                authorName = authorName,
                authorId = authorId,
                authorPhoto = authorPhoto,
                developer = developer,
                publisher = publisher,
                playtime = playtime,
                imageUrl = imageUrl
            )
            _uiState.value = _uiState.value.copy(
                showAddReviewDialog = false,
                userFeedbackMessage = "تم نشر مراجعتك بنجاح في مجتمع THE SKULLS! 🎮"
            )
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            localRepository.deleteGameReview(reviewId)
            _uiState.value = _uiState.value.copy(
                selectedReview = null,
                userFeedbackMessage = "تم حذف المراجعة"
            )
        }
    }

    fun publishNewNews(
        title: String,
        category: NewsCategory,
        summary: String,
        content: String,
        imageUrl: String = "",
        isBreaking: Boolean = false
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            val authorName = user?.displayName ?: (userProfile.value?.displayName ?: "محرر سكلز")
            val authorId = user?.uid ?: "user_${System.currentTimeMillis()}"
            val authorPhoto = user?.photoUrl?.toString() ?: ""

            localRepository.publishGameNews(
                title = title,
                category = category,
                summary = summary,
                content = content,
                authorName = authorName,
                authorId = authorId,
                authorPhoto = authorPhoto,
                imageUrl = imageUrl,
                isBreaking = isBreaking
            )
            _uiState.value = _uiState.value.copy(
                showAddNewsDialog = false,
                userFeedbackMessage = "تم نشر الخبر بنجاح! 📰"
            )
        }
    }

    fun deleteNews(newsId: String) {
        viewModelScope.launch {
            localRepository.deleteGameNews(newsId)
            _uiState.value = _uiState.value.copy(
                selectedNews = null,
                userFeedbackMessage = "تم حذف الخبر"
            )
        }
    }

    fun setShowAuthPromptDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAuthPromptDialog = show)
    }

    fun submitUserReview(
        gameId: String,
        gameTitle: String,
        author: String,
        rating: Float,
        comment: String,
        platform: String
    ) {
        viewModelScope.launch {
            localRepository.addCommunityRating(
                gameId = gameId,
                gameTitle = gameTitle,
                author = author,
                rating = rating,
                comment = comment,
                platform = platform
            )
            // Also submit to Firestore comments if logged in
            if (_currentUser.value != null) {
                try {
                    firestoreRepository.addComment(
                        reviewId = gameId,
                        commentText = comment,
                        rating = rating.toDouble(),
                        gamerTag = author
                    )
                } catch (_: Exception) {}
            }
            _uiState.value = _uiState.value.copy(
                showReviewDialog = false,
                userFeedbackMessage = "شكراً لمشاركتك! تم نشر تقييمك بنجاح."
            )
        }
    }

    fun setShowReviewDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showReviewDialog = show)
    }

    fun clearFeedbackMessage() {
        _uiState.value = _uiState.value.copy(userFeedbackMessage = null)
    }
}
