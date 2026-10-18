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
import com.example.model.GameNews
import com.example.model.NewsCategory
import com.example.ui.theme.*

@Composable
fun NewsScreen(
    newsList: List<GameNews>,
    selectedCategory: NewsCategory,
    onCategorySelect: (NewsCategory) -> Unit,
    onNewsClick: (GameNews) -> Unit,
    onAddNewNews: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredNews = if (selectedCategory == NewsCategory.ALL) {
        newsList
    } else {
        newsList.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("news_screen")
    ) {
        // Category Selector Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(NewsCategory.values()) { category ->
                val isSelected = category == selectedCategory
                Surface(
                    modifier = Modifier
                        .clickable { onCategorySelect(category) }
                        .testTag("news_category_${category.name}"),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) SkullWhite else SkullSurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) SkullWhite else SkullBorderDark
                    )
                ) {
                    Text(
                        text = category.arabicName,
                        color = if (isSelected) SkullBgDark else SkullTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Action header with add news button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "أخبار مجتمع الألعاب (${filteredNews.size})",
                color = SkullTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            FilledTonalButton(
                onClick = onAddNewNews,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = SkullWhite,
                    contentColor = SkullBgDark
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("نشر خبر", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (filteredNews.isEmpty()) {
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
                                imageVector = Icons.Default.Article,
                                contentDescription = null,
                                tint = SkullWhite,
                                modifier = Modifier.size(54.dp).padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "لا توجد أخبار منشورة بعد",
                            color = SkullTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "كن أول من يشارك أحدث الأخبار، التحديثات والتسريبات مع مجتمع THE SKULLS!",
                            color = SkullTextSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = onAddNewNews,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SkullWhite,
                                contentColor = SkullBgDark
                            )
                        ) {
                            Icon(imageVector = Icons.Default.AddComment, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("نشر أول خبر ألعاب", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // News Feed List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
            items(filteredNews) { news ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNewsClick(news) }
                        .testTag("news_item_card_${news.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            AsyncImage(
                                model = news.imageUrl,
                                contentDescription = news.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Gradient overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            listOf(Color.Transparent, SkullSurfaceCard.copy(alpha = 0.9f))
                                        )
                                    )
                            )
                            // Category Tag
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp),
                                shape = RoundedCornerShape(6.dp),
                                color = SkullBgDark.copy(alpha = 0.85f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SkullCyan.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = news.category.arabicName,
                                    color = SkullCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            if (news.isBreaking) {
                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(12.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    color = SkullWhite
                                ) {
                                    Text(
                                        text = "عاجل",
                                        color = SkullBgDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = news.title,
                                color = SkullTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 22.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = news.summary,
                                color = SkullTextMuted,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = SkullTextSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${news.publishDate} • قراءة ${news.readTimeMinutes} دقائق",
                                        color = SkullTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = SkullTextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${news.viewsCount}",
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
}
