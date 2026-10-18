package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.model.GameReview
import com.example.model.PlatformType
import com.example.ui.components.PlatformBadge
import com.example.ui.components.SkullScoreBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    reviews: List<GameReview>,
    searchQuery: String,
    selectedPlatform: PlatformType?,
    onSearchChange: (String) -> Unit,
    onPlatformSelect: (PlatformType?) -> Unit,
    onReviewClick: (GameReview) -> Unit,
    onAddNewReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredReviews = reviews.filter { review ->
        val matchesSearch = searchQuery.isBlank() ||
                review.title.contains(searchQuery, ignoreCase = true) ||
                review.arabicTitle.contains(searchQuery, ignoreCase = true) ||
                review.genre.contains(searchQuery, ignoreCase = true)

        val matchesPlatform = selectedPlatform == null || review.platforms.contains(selectedPlatform)

        matchesSearch && matchesPlatform
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("reviews_screen")
    ) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("ابحث في مراجعات الألعاب والتقييمات...", color = SkullTextMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "بحث", tint = SkullCrimson)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "مسح", tint = SkullTextSecondary)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("review_search_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SkullSurfaceCard,
                unfocusedContainerColor = SkullSurfaceCard,
                focusedBorderColor = SkullCrimson,
                unfocusedBorderColor = SkullBorderDark,
                focusedTextColor = SkullTextPrimary,
                unfocusedTextColor = SkullTextPrimary
            ),
            singleLine = true
        )

        // Platform Filter Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedPlatform == null,
                    onClick = { onPlatformSelect(null) },
                    label = { Text("جميع المنصات") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SkullWhite,
                        selectedLabelColor = SkullBgDark,
                        containerColor = SkullSurfaceCard,
                        labelColor = SkullTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedPlatform == null,
                        borderColor = SkullBorderDark,
                        selectedBorderColor = SkullWhite
                    ),
                    modifier = Modifier.testTag("platform_filter_all")
                )
            }
            items(PlatformType.values()) { platform ->
                FilterChip(
                    selected = selectedPlatform == platform,
                    onClick = { onPlatformSelect(platform) },
                    label = { Text(platform.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SkullWhite,
                        selectedLabelColor = SkullBgDark,
                        containerColor = SkullSurfaceCard,
                        labelColor = SkullTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedPlatform == platform,
                        borderColor = SkullBorderDark,
                        selectedBorderColor = SkullWhite
                    ),
                    modifier = Modifier.testTag("platform_filter_${platform.name}")
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Reviews Count & Add Review Action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "مراجعات اللاعبين (${filteredReviews.size})",
                color = SkullTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            FilledTonalButton(
                onClick = onAddNewReview,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = SkullWhite,
                    contentColor = SkullBgDark
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة مراجعة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Reviews List or Community Empty State
        if (filteredReviews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SkullSurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = SkullWhite,
                                modifier = Modifier.size(54.dp).padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "لا توجد نتائج مطابقة لبحثك" else "لا توجد مراجعات مضافة بعد",
                            color = SkullTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty())
                                "جرّب تغيير كلمات البحث أو إزالة تصفية المنصات"
                            else
                                "المحتوى هنا من صنعكم! كن أول لاعب يشارك مراجعة وتقييم للعبته المفضلة مع مجتمع THE SKULLS.",
                            color = SkullTextSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = onAddNewReview,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SkullWhite,
                                contentColor = SkullBgDark
                            )
                        ) {
                            Icon(imageVector = Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("كتابة أول مراجعة لعبة", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredReviews) { review ->
                    ReviewCard(
                        review = review,
                        onClick = { onReviewClick(review) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewCard(
    review: GameReview,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("review_card_${review.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
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
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color.Transparent, SkullSurfaceCard)
                            )
                        )
                )
                // Score Badge
                SkullScoreBadge(
                    score = review.overallScore,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    size = 50
                )
                // Verdict chip
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = SkullBgDark.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkullCrimson.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = review.verdictTag,
                        color = SkullCrimson,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = review.title,
                    color = SkullTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = review.arabicTitle,
                    color = SkullTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = review.summary,
                    color = SkullTextMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        review.platforms.forEach { platform ->
                            PlatformBadge(platform = platform)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = SkullTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = review.playtime,
                            color = SkullTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
