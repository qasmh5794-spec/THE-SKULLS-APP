package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.GameNews
import com.example.model.GameReview
import com.example.model.NewsCategory
import com.example.ui.components.PlatformBadge
import com.example.ui.components.SkullScoreBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppNavTab

@Composable
fun HomeScreen(
    featuredReviews: List<GameReview>,
    allReviews: List<GameReview>,
    latestNews: List<GameNews>,
    onReviewClick: (GameReview) -> Unit,
    onNewsClick: (GameNews) -> Unit,
    onNavigateTab: (AppNavTab) -> Unit,
    onAddNewReview: () -> Unit,
    onAddNewNews: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_column"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Branding Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.the_skulls_banner),
                    contentDescription = "THE SKULLS Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient Scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    SkullBgDark.copy(alpha = 0.5f),
                                    SkullBgDark
                                )
                            )
                        )
                )

                // Branding overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.the_skulls_logo),
                        contentDescription = "THE SKULLS Logo",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(2.dp, SkullCrimson, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "THE SKULLS",
                                color = SkullCrimson,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SkullCrimson.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SkullCrimson)
                            ) {
                                Text(
                                    text = "OFFICIAL",
                                    color = SkullCrimson,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "مرجعك العربي الأول لمراجعات وأخبار الألعاب",
                            color = SkullTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Community Quick Actions Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "شارك مع مجتمع اللاعبين 🎮",
                        color = SkullTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "المحتوى هنا من صنعك! أضف مراجعتك للعبة لعبتها أو انشر أحدث خبر وتحديث.",
                        color = SkullTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onAddNewReview,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SkullWhite,
                                contentColor = SkullBgDark
                            )
                        ) {
                            Icon(imageVector = Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("أضف مراجعة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onAddNewNews,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SkullWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
                        ) {
                            Icon(imageVector = Icons.Default.AddComment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("انشر خبراً", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Breaking News Ticker
        val breaking = latestNews.firstOrNull { it.isBreaking } ?: latestNews.firstOrNull()
        if (breaking != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .clickable { onNewsClick(breaking) }
                        .testTag("breaking_news_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkullCrimson.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SkullWhite
                        ) {
                            Text(
                                text = "عاجل",
                                color = SkullBgDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = breaking.title,
                            color = SkullTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowBack, // for RTL direction
                            contentDescription = "قراءة",
                            tint = SkullCrimson,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Section: Featured Reviews
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = SkullGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "أبرز مراجعات سكلز",
                        color = SkullTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(
                    onClick = { onNavigateTab(AppNavTab.REVIEWS) },
                    modifier = Modifier.testTag("see_all_reviews_button")
                ) {
                    Text("عرض الكل", color = SkullCyan, fontSize = 13.sp)
                }
            }
        }

        // Horizontal Carousel or Empty State for Reviews
        if (featuredReviews.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("لا توجد مراجعات منشورة بعد", color = SkullTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("كن أول لاعب يكتب مراجعة وتقييم للعبته المفضلة!", color = SkullTextSecondary, fontSize = 12.sp)
                        }
                        Button(
                            onClick = onAddNewReview,
                            colors = ButtonDefaults.buttonColors(containerColor = SkullWhite, contentColor = SkullBgDark),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("كتابة مراجعة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(featuredReviews) { review ->
                        FeaturedReviewCard(
                            review = review,
                            onClick = { onReviewClick(review) }
                        )
                    }
                }
            }
        }

        // Section: Latest News Highlights
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Article,
                        contentDescription = null,
                        tint = SkullCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "آخر أخبار الألعاب",
                        color = SkullTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(
                    onClick = { onNavigateTab(AppNavTab.NEWS) },
                    modifier = Modifier.testTag("see_all_news_button")
                ) {
                    Text("المزيد", color = SkullCyan, fontSize = 13.sp)
                }
            }
        }

        // News Items in Home or Empty State
        if (latestNews.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("لا توجد أخبار ألعاب مضافة بعد", color = SkullTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("شارك أحدث خبر وتسريب مع مجتمع سكلز!", color = SkullTextSecondary, fontSize = 12.sp)
                        }
                        Button(
                            onClick = onAddNewNews,
                            colors = ButtonDefaults.buttonColors(containerColor = SkullWhite, contentColor = SkullBgDark),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("نشر خبر", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(latestNews.take(3)) { news ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNewsClick(news) }
                    .testTag("home_news_item_${news.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard)
            ) {
                Row(modifier = Modifier.padding(12.dp)) {
                    AsyncImage(
                        model = news.imageUrl,
                        contentDescription = news.title,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SkullCyan.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = news.category.arabicName,
                                    color = SkullCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = news.publishDate,
                                color = SkullTextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = news.title,
                            color = SkullTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

        // Section: Hall of Fame (أعلى الألعاب تقييماً)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SkullSurfaceCardElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, SkullGold.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = SkullGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "صالة العظماء (Hall of Fame)",
                                color = SkullGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(
                            text = "+9.0 سكلز",
                            color = SkullTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    val topRated = allReviews.filter { it.overallScore >= 9.0f }.take(3)
                    topRated.forEachIndexed { index, review ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onReviewClick(review) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#${index + 1}",
                                color = if (index == 0) SkullGold else SkullTextSecondary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.width(28.dp)
                            )
                            AsyncImage(
                                model = review.imageUrl,
                                contentDescription = review.title,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = review.title,
                                    color = SkullTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = review.arabicTitle,
                                    color = SkullTextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            SkullScoreBadge(
                                score = review.overallScore,
                                size = 44,
                                showMax = false
                            )
                        }
                        if (index < topRated.size - 1) {
                            Divider(color = SkullDividerDark.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedReviewCard(
    review: GameReview,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .clickable { onClick() }
            .testTag("featured_review_${review.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
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
                                listOf(Color.Transparent, SkullSurfaceCard)
                            )
                        )
                )
                // Score badge positioned on top corner
                SkullScoreBadge(
                    score = review.overallScore,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    size = 46
                )
                // Genre chip
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = SkullBgDark.copy(alpha = 0.8f)
                ) {
                    Text(
                        text = review.genre,
                        color = SkullCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = review.title,
                    color = SkullTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = review.verdictTag,
                    color = SkullCrimson,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    review.platforms.forEach { platform ->
                        PlatformBadge(platform = platform)
                    }
                }
            }
        }
    }
}
