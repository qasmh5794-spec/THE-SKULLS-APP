package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ui.auth.GoogleSignInButton
import com.example.ui.dialogs.AddNewsDialog
import com.example.ui.dialogs.AddReviewDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.TheSkullsViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TheSkullsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    TheSkullsApp(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheSkullsApp(viewModel: TheSkullsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val currentReviewLikes by viewModel.currentReviewLikes.collectAsStateWithLifecycle()
    val currentReviewComments by viewModel.currentReviewComments.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarkedItems.collectAsStateWithLifecycle()
    val userReviews by viewModel.userSubmittedReviews.collectAsStateWithLifecycle()
    val allReviews by viewModel.allReviews.collectAsStateWithLifecycle()
    val featuredReviews by viewModel.featuredReviews.collectAsStateWithLifecycle()
    val allNews by viewModel.allNews.collectAsStateWithLifecycle()
    val upcomingGames by viewModel.upcomingGames.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAboutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.userFeedbackMessage) {
        val msg = uiState.userFeedbackMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    // Detail screens handle their own top bars & scaffold
    if (uiState.selectedReview != null) {
        val review = uiState.selectedReview!!
        val isBookmarked = viewModel.isBookmarked(review.id)
        val gameUserReviews = userReviews.filter { it.gameId == review.id }

        ReviewDetailScreen(
            review = review,
            isBookmarked = isBookmarked,
            likes = currentReviewLikes,
            comments = currentReviewComments,
            currentUserId = currentUser?.uid,
            communityReviews = gameUserReviews,
            onBack = { viewModel.closeReviewDetail() },
            onToggleBookmark = { viewModel.toggleReviewBookmark(review) },
            onToggleLike = { viewModel.toggleReviewLike(review.id) },
            onAddComment = { comment, rating -> viewModel.addCommentToReview(review.id, comment, rating) },
            onDeleteComment = { commentId -> viewModel.deleteComment(review.id, commentId) },
            onSubmitUserReview = { author, rating, comment, platform ->
                viewModel.submitUserReview(review.id, review.title, author, rating, comment, platform)
            },
            onRequireAuth = { viewModel.setShowAuthPromptDialog(true) }
        )
        return
    }

    if (uiState.selectedNews != null) {
        val news = uiState.selectedNews!!
        val isBookmarked = viewModel.isBookmarked(news.id)

        NewsDetailScreen(
            news = news,
            isBookmarked = isBookmarked,
            onBack = { viewModel.closeNewsDetail() },
            onToggleBookmark = { viewModel.toggleNewsBookmark(news) }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.selectTab(AppNavTab.HOME) }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.the_skulls_logo),
                            contentDescription = "THE SKULLS Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, SkullCrimson, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "THE SKULLS",
                                color = SkullCrimson,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "مراجعات وأخبار الألعاب",
                                color = SkullTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.selectTab(AppNavTab.REVIEWS) },
                        modifier = Modifier.testTag("topbar_search_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = SkullTextPrimary
                        )
                    }

                    // Account Profile Avatar / Login shortcut
                    IconButton(
                        onClick = { viewModel.selectTab(AppNavTab.SAVED) },
                        modifier = Modifier.testTag("topbar_account_action")
                    ) {
                        if (currentUser?.photoUrl != null) {
                            AsyncImage(
                                model = currentUser?.photoUrl,
                                contentDescription = "الحساب",
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, SkullCrimson, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = if (currentUser != null) Icons.Default.AccountCircle else Icons.Outlined.AccountCircle,
                                contentDescription = "الحساب",
                                tint = if (currentUser != null) SkullCrimson else SkullTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = { showAboutDialog = true },
                        modifier = Modifier.testTag("topbar_info_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "حول التطبيق",
                            tint = SkullTextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SkullBgDark,
                    titleContentColor = SkullTextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SkullSurfaceDark,
                contentColor = SkullTextPrimary,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = uiState.currentTab == AppNavTab.HOME,
                    onClick = { viewModel.selectTab(AppNavTab.HOME) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == AppNavTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "الرئيسية"
                        )
                    },
                    label = { Text("الرئيسية", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SkullCrimson,
                        selectedTextColor = SkullCrimson,
                        unselectedIconColor = SkullTextMuted,
                        unselectedTextColor = SkullTextMuted,
                        indicatorColor = SkullCrimson.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_home")
                )
                NavigationBarItem(
                    selected = uiState.currentTab == AppNavTab.REVIEWS,
                    onClick = { viewModel.selectTab(AppNavTab.REVIEWS) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == AppNavTab.REVIEWS) Icons.Filled.SportsEsports else Icons.Outlined.SportsEsports,
                            contentDescription = "المراجعات"
                        )
                    },
                    label = { Text("المراجعات", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SkullCrimson,
                        selectedTextColor = SkullCrimson,
                        unselectedIconColor = SkullTextMuted,
                        unselectedTextColor = SkullTextMuted,
                        indicatorColor = SkullCrimson.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_reviews")
                )
                NavigationBarItem(
                    selected = uiState.currentTab == AppNavTab.NEWS,
                    onClick = { viewModel.selectTab(AppNavTab.NEWS) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == AppNavTab.NEWS) Icons.Filled.Article else Icons.Outlined.Article,
                            contentDescription = "الأخبار"
                        )
                    },
                    label = { Text("الأخبار", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SkullCyan,
                        selectedTextColor = SkullCyan,
                        unselectedIconColor = SkullTextMuted,
                        unselectedTextColor = SkullTextMuted,
                        indicatorColor = SkullCyan.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_news")
                )
                NavigationBarItem(
                    selected = uiState.currentTab == AppNavTab.RADAR,
                    onClick = { viewModel.selectTab(AppNavTab.RADAR) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == AppNavTab.RADAR) Icons.Filled.Whatshot else Icons.Outlined.Whatshot,
                            contentDescription = "الرادار"
                        )
                    },
                    label = { Text("الرادار", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SkullGold,
                        selectedTextColor = SkullGold,
                        unselectedIconColor = SkullTextMuted,
                        unselectedTextColor = SkullTextMuted,
                        indicatorColor = SkullGold.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_radar")
                )
                NavigationBarItem(
                    selected = uiState.currentTab == AppNavTab.SAVED,
                    onClick = { viewModel.selectTab(AppNavTab.SAVED) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == AppNavTab.SAVED) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "حسابي والمحفوظات"
                        )
                    },
                    label = { Text("حسابي", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SkullCrimson,
                        selectedTextColor = SkullCrimson,
                        unselectedIconColor = SkullTextMuted,
                        unselectedTextColor = SkullTextMuted,
                        indicatorColor = SkullCrimson.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_saved")
                )
            }
        },
        floatingActionButton = {
            when (uiState.currentTab) {
                AppNavTab.REVIEWS -> {
                    ExtendedFloatingActionButton(
                        onClick = { viewModel.openAddReviewDialog() },
                        containerColor = SkullWhite,
                        contentColor = SkullBgDark,
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("إضافة مراجعة", fontWeight = FontWeight.Bold) }
                    )
                }
                AppNavTab.NEWS -> {
                    ExtendedFloatingActionButton(
                        onClick = { viewModel.openAddNewsDialog() },
                        containerColor = SkullWhite,
                        contentColor = SkullBgDark,
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("نشر خبر", fontWeight = FontWeight.Bold) }
                    )
                }
                else -> Unit
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SkullBgDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                AppNavTab.HOME -> {
                    HomeScreen(
                        featuredReviews = featuredReviews,
                        allReviews = allReviews,
                        latestNews = allNews,
                        onReviewClick = { viewModel.openReview(it) },
                        onNewsClick = { viewModel.openNews(it) },
                        onNavigateTab = { viewModel.selectTab(it) },
                        onAddNewReview = { viewModel.openAddReviewDialog() },
                        onAddNewNews = { viewModel.openAddNewsDialog() }
                    )
                }

                AppNavTab.REVIEWS -> {
                    ReviewsScreen(
                        reviews = allReviews,
                        searchQuery = uiState.searchQuery,
                        selectedPlatform = uiState.selectedPlatformFilter,
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        onPlatformSelect = { viewModel.selectPlatformFilter(it) },
                        onReviewClick = { viewModel.openReview(it) },
                        onAddNewReview = { viewModel.openAddReviewDialog() }
                    )
                }

                AppNavTab.NEWS -> {
                    NewsScreen(
                        newsList = allNews,
                        selectedCategory = uiState.selectedNewsCategory,
                        onCategorySelect = { viewModel.selectNewsCategory(it) },
                        onNewsClick = { viewModel.openNews(it) },
                        onAddNewNews = { viewModel.openAddNewsDialog() }
                    )
                }

                AppNavTab.RADAR -> {
                    RadarScreen(
                        upcomingGames = upcomingGames
                    )
                }

                AppNavTab.SAVED -> {
                    var accountTabSelected by remember { mutableIntStateOf(0) }
                    Column(modifier = Modifier.fillMaxSize()) {
                        TabRow(
                            selectedTabIndex = accountTabSelected,
                            containerColor = SkullSurfaceDark,
                            contentColor = SkullCrimson,
                            divider = { Divider(color = SkullBorderDark) }
                        ) {
                            Tab(
                                selected = accountTabSelected == 0,
                                onClick = { accountTabSelected = 0 },
                                text = {
                                    Text(
                                        text = "الملف الشخصي والحساب",
                                        fontWeight = if (accountTabSelected == 0) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.testTag("tab_account_profile")
                            )
                            Tab(
                                selected = accountTabSelected == 1,
                                onClick = { accountTabSelected = 1 },
                                text = {
                                    Text(
                                        text = "المحفوظات (${bookmarks.size})",
                                        fontWeight = if (accountTabSelected == 1) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.testTag("tab_account_bookmarks")
                            )
                        }

                        if (accountTabSelected == 0) {
                            AccountScreen(
                                currentUser = currentUser,
                                userProfile = userProfile,
                                onSaveProfile = { tag, plat -> viewModel.saveUserProfile(tag, plat) },
                                onSignOut = {}
                            )
                        } else {
                            BookmarksScreen(
                                bookmarks = bookmarks,
                                userReviews = userReviews,
                                allReviews = allReviews,
                                allNews = allNews,
                                onReviewClick = { viewModel.openReview(it) },
                                onNewsClick = { viewModel.openNews(it) },
                                onNavigateTab = { viewModel.selectTab(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Google Sign-In Prompt Modal when user tries to like/comment while signed out
    if (uiState.showAuthPromptDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowAuthPromptDialog(false) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = SkullCrimson,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "تسجيل الدخول مطلوب",
                        color = SkullTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "لإضافة إعجابك أو كتابة تعليقات ومراجعات حية على ألعاب THE SKULLS، يرجى تسجيل الدخول بحساب Google الخاص بك.",
                        color = SkullTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    GoogleSignInButton(
                        onAuthSuccess = {
                            viewModel.setShowAuthPromptDialog(false)
                        },
                        onAuthError = {}
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { viewModel.setShowAuthPromptDialog(false) }) {
                    Text("إلغاء", color = SkullTextSecondary)
                }
            },
            containerColor = SkullSurfaceCardElevated
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.the_skulls_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("عن تطبيق THE SKULLS", color = SkullTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "THE SKULLS هو التطبيق العربي المتخصص في مراجعات الألعاب وتحليلاتها الدقيقة، وتغطية أحدث أخبار منصات PlayStation وXbox وPC وNintendo.",
                        color = SkullTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "نظام تقييم سكلز (Skull Score):",
                        color = SkullCrimson,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• 9.5 - 10: تحفة أسطورية لا تُفوّت\n• 9.0 - 9.4: لعبة ممتازة جداً ومرشحة للجوائز\n• 8.0 - 8.9: لعبة رائعة تستحق التجربة\n• أقل من 8.0: تجربة متوسطة مع بعض الملاحظات",
                        color = SkullTextMuted,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SkullWhite,
                        contentColor = SkullBgDark
                    )
                ) {
                    Text("حسناً", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = SkullSurfaceCardElevated
        )
    }

    if (uiState.showAddReviewDialog) {
        AddReviewDialog(
            onDismiss = { viewModel.closeAddReviewDialog() },
            onSubmit = { title, arabicTitle, genre, platforms, overall, gameplay, graphics, story, sound, verdict, body, pros, cons ->
                viewModel.publishNewReview(
                    title = title,
                    arabicTitle = arabicTitle,
                    genre = genre,
                    platforms = platforms,
                    overallScore = overall,
                    gameplayScore = gameplay,
                    graphicsScore = graphics,
                    storyScore = story,
                    soundScore = sound,
                    verdict = verdict,
                    reviewBody = body,
                    pros = pros,
                    cons = cons
                )
            }
        )
    }

    if (uiState.showAddNewsDialog) {
        AddNewsDialog(
            onDismiss = { viewModel.closeAddNewsDialog() },
            onSubmit = { title, category, summary, content, isBreaking ->
                viewModel.publishNewNews(
                    title = title,
                    category = category,
                    summary = summary,
                    content = content,
                    isBreaking = isBreaking
                )
            }
        )
    }
}
