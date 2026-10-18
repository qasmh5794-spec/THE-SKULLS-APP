package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.BookmarkEntity
import com.example.data.local.UserSubmittedReviewEntity
import com.example.model.GameNews
import com.example.model.GameReview
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppNavTab

@Composable
fun BookmarksScreen(
    bookmarks: List<BookmarkEntity>,
    userReviews: List<UserSubmittedReviewEntity>,
    allReviews: List<GameReview>,
    allNews: List<GameNews>,
    onReviewClick: (GameReview) -> Unit,
    onNewsClick: (GameNews) -> Unit,
    onNavigateTab: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("bookmarks_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = SkullSurfaceDark,
            contentColor = SkullCrimson,
            divider = { Divider(color = SkullBorderDark) }
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = {
                    Text(
                        text = "المحفوظات (${bookmarks.size})",
                        fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.testTag("tab_saved_items")
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = {
                    Text(
                        text = "تقييماتي ومشاركاتي (${userReviews.size})",
                        fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.testTag("tab_user_reviews")
            )
        }

        if (selectedSubTab == 0) {
            if (bookmarks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = SkullTextMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "لا توجد عناصر محفوظة حتى الآن",
                            color = SkullTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "يمكنك حفظ المراجعات الإستراتيجية والأخبار للرجوع إليها في أي وقت دون إنترنت.",
                            color = SkullTextSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { onNavigateTab(AppNavTab.REVIEWS) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SkullWhite,
                                contentColor = SkullBgDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("browse_reviews_from_empty")
                        ) {
                            Text("تصفح المراجعات الآن", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookmarks) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (item.itemType == "REVIEW") {
                                        val review = allReviews.find { it.id == item.itemId }
                                        if (review != null) onReviewClick(review)
                                    } else {
                                        val news = allNews.find { it.id == item.itemId }
                                        if (news != null) onNewsClick(news)
                                    }
                                }
                                .testTag("bookmark_card_${item.itemId}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.title,
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (item.itemType == "REVIEW") SkullCrimson.copy(alpha = 0.2f) else SkullCyan.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = if (item.itemType == "REVIEW") "مراجعة لعبة" else "خبر صحفي",
                                            color = if (item.itemType == "REVIEW") SkullCrimson else SkullCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.title,
                                        color = SkullTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = item.subtitle,
                                        color = SkullTextSecondary,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = null,
                                    tint = SkullTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // User submitted reviews tab
            if (userReviews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            tint = SkullTextMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "لم تقم بإضافة تقييمات بعد",
                            color = SkullTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "شارك رأيك وتقييمك للألعاب من خلال فتح أي مراجعة والضغط على 'أضف تقييمك'.",
                            color = SkullTextSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(userReviews) { review ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = review.gameTitle,
                                        color = SkullTextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SkullGold.copy(alpha = 0.2f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, SkullGold)
                                    ) {
                                        Text(
                                            text = "★ ${review.rating} / 10",
                                            color = SkullGold,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = review.reviewComment,
                                    color = SkullTextSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "المنصة: ${review.platformPlayed}",
                                        color = SkullCyan,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "بواسطة: ${review.authorName}",
                                        color = SkullTextMuted,
                                        fontSize = 11.sp
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
