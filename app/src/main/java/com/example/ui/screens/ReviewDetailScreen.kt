package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.firebase.ReviewComment
import com.example.data.firebase.ReviewLike
import com.example.data.local.UserSubmittedReviewEntity
import com.example.model.GameReview
import com.example.ui.components.BreakdownProgressRow
import com.example.ui.components.PlatformBadge
import com.example.ui.components.ProsConsCard
import com.example.ui.components.SkullScoreBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailScreen(
    review: GameReview,
    isBookmarked: Boolean,
    likes: List<ReviewLike>,
    comments: List<ReviewComment>,
    currentUserId: String?,
    communityReviews: List<UserSubmittedReviewEntity>,
    onBack: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleLike: () -> Unit,
    onAddComment: (comment: String, rating: Double) -> Unit,
    onDeleteComment: (commentId: String) -> Unit,
    onSubmitUserReview: (author: String, rating: Float, comment: String, platform: String) -> Unit,
    onRequireAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var quickCommentText by remember { mutableStateOf("") }
    var quickRating by remember { mutableFloatStateOf(9.0f) }

    val isLikedByMe = currentUserId != null && likes.any { it.userId == currentUserId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = review.title,
                        maxLines = 1,
                        color = SkullTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("review_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "الرجوع",
                            tint = SkullTextPrimary
                        )
                    }
                },
                actions = {
                    // Like button in TopBar
                    IconButton(
                        onClick = {
                            if (currentUserId == null) onRequireAuth() else onToggleLike()
                        },
                        modifier = Modifier.testTag("review_detail_like_button")
                    ) {
                        Icon(
                            imageVector = if (isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "إعجاب",
                            tint = if (isLikedByMe) SkullCrimson else SkullTextPrimary
                        )
                    }
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.testTag("review_detail_bookmark_button")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "حفظ",
                            tint = if (isBookmarked) SkullCyan else SkullTextPrimary
                        )
                    }
                    IconButton(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "مراجعة ${review.title} على تطبيق THE SKULLS: تقييم ${review.overallScore}/10 (${review.verdictTag})"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "مشاركة المراجعة"))
                        },
                        modifier = Modifier.testTag("review_detail_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة",
                            tint = SkullTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SkullBgDark)
            )
        },
        containerColor = SkullBgDark,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("review_detail_content"),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Hero Banner with Title & Score
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) {
                    AsyncImage(
                        model = review.imageUrl,
                        contentDescription = review.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        SkullBgDark.copy(alpha = 0.6f),
                                        SkullBgDark
                                    )
                                )
                            )
                    )

                    // Overlay details
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SkullWhite
                        ) {
                            Text(
                                text = review.verdictTag,
                                color = SkullBgDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = review.title,
                            color = SkullTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "${review.arabicTitle} • ${review.genre}",
                            color = SkullTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Big The Skulls Score Showcase Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SkullCrimson.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SkullScoreBadge(
                            score = review.overallScore,
                            size = 72,
                            showMax = true
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تقييم THE SKULLS الرسمي",
                                color = SkullCrimson,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = review.verdictTag,
                                color = SkullTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "مراجعة: ${review.reviewerName} • ${review.reviewDate}",
                                color = SkullTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Interactive Social Bar (Likes & Comments Count)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Like action button
                        Button(
                            onClick = {
                                if (currentUserId == null) onRequireAuth() else onToggleLike()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLikedByMe) SkullCrimson.copy(alpha = 0.2f) else SkullSurfaceDark,
                                contentColor = if (isLikedByMe) SkullCrimson else SkullTextPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isLikedByMe) SkullCrimson else SkullBorderDark
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("like_action_button")
                        ) {
                            Icon(
                                imageVector = if (isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isLikedByMe) SkullCrimson else SkullTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${likes.size} إعجاب",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        // Comments count badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Comment,
                                contentDescription = null,
                                tint = SkullCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${comments.size} تعليق مباشر",
                                color = SkullCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Platforms & Game Specs
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "بطاقة معلومات اللعبة",
                            color = SkullTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpecItem("المطور", review.developer)
                            SpecItem("الناشر", review.publisher)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpecItem("تاريخ الإصدار", review.releaseDate)
                            SpecItem("مدة الإنهاء", review.playtime)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = SkullDividerDark)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "المنصات المتوفرة عليها:",
                            color = SkullTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            review.platforms.forEach { platform ->
                                PlatformBadge(platform = platform)
                            }
                        }
                    }
                }
            }

            // Breakdown Scores
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = SkullCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تفاصيل التقييم (Score Breakdown)",
                                color = SkullCyan,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        BreakdownProgressRow("أسلوب اللعب والتحكم", review.breakdown.gameplay, Icons.Default.SportsEsports, SkullCrimson)
                        BreakdownProgressRow("الرسوميات والمؤثرات البصرية", review.breakdown.graphics, Icons.Default.Image, SkullCyan)
                        BreakdownProgressRow("القصة والسرد والشخصيات", review.breakdown.story, Icons.Default.AutoStories, SkullGold)
                        BreakdownProgressRow("الموسيقى والصوتيات", review.breakdown.audio, Icons.Default.MusicNote, SkullEmerald)
                        BreakdownProgressRow("الأداء التقني والاستقرار", review.breakdown.performance, Icons.Default.Speed, SkullCyanDark)
                    }
                }
            }

            // Summary Callout
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkullCyan.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "خلاصة المراجعة:",
                            color = SkullCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = review.summary,
                            color = SkullTextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Pros and Cons
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    ProsConsCard(pros = review.pros, cons = review.cons)
                }
            }

            // Full Article Analysis
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "التحليل والمراجعة الشاملة",
                            color = SkullTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = review.fullReview,
                            color = SkullTextSecondary,
                            fontSize = 14.sp,
                            lineHeight = 23.sp
                        )
                    }
                }
            }

            // Real-Time Community Comments & Discussion (from Firestore)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Forum,
                                    contentDescription = null,
                                    tint = SkullCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تعليقات ومراجعات اللاعبين (${comments.size})",
                                    color = SkullTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Add Comment Input Box
                        if (currentUserId == null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SkullSurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onRequireAuth() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = SkullTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "سجّل الدخول بحسابك للمشاركة في التعليقات والتقييمات",
                                        color = SkullCyan,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SkullSurfaceDark)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "تقييمك للعبة: ${String.format("%.1f", quickRating)} / 10",
                                    color = SkullGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Slider(
                                    value = quickRating,
                                    onValueChange = { quickRating = it },
                                    valueRange = 1f..10f,
                                    steps = 17,
                                    colors = SliderDefaults.colors(
                                        thumbColor = SkullCrimson,
                                        activeTrackColor = SkullCrimson
                                    ),
                                    modifier = Modifier.fillMaxWidth().height(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = quickCommentText,
                                    onValueChange = { quickCommentText = it },
                                    placeholder = { Text("شاركنا رأيك في اللعبة...", color = SkullTextMuted, fontSize = 13.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = SkullTextPrimary,
                                        unfocusedTextColor = SkullTextPrimary,
                                        focusedBorderColor = SkullCrimson,
                                        unfocusedBorderColor = SkullBorderDark
                                    ),
                                    maxLines = 3,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("comment_input_field")
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = {
                                            if (quickCommentText.isNotBlank()) {
                                                onAddComment(quickCommentText, quickRating.toDouble())
                                                quickCommentText = ""
                                            }
                                        },
                                        enabled = quickCommentText.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SkullWhite,
                                            contentColor = SkullBgDark
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("submit_comment_btn")
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("إرسال التعليق", fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Comments List
                        if (comments.isEmpty()) {
                            Text(
                                text = "كن أول من يعلق ويشارك تقييمه على هذه المراجعة!",
                                color = SkullTextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            comments.forEach { comment ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SkullSurfaceDark)
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (comment.userPhoto.isNotBlank()) {
                                                AsyncImage(
                                                    model = comment.userPhoto,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .clip(CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.AccountCircle,
                                                    contentDescription = null,
                                                    tint = SkullTextSecondary,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = comment.userName,
                                                    color = SkullTextPrimary,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "@${comment.gamerTag}",
                                                    color = SkullCyan,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = SkullGold.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = "★ ${String.format("%.1f", comment.rating)}",
                                                    color = SkullGold,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            if (currentUserId != null && comment.userId == currentUserId) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                IconButton(
                                                    onClick = { onDeleteComment(comment.id) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "حذف التعليق",
                                                        tint = SkullGray,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = comment.comment,
                                        color = SkullTextSecondary,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpecItem(label: String, value: String) {
    Column {
        Text(text = label, color = SkullTextMuted, fontSize = 11.sp)
        Text(text = value, color = SkullTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
