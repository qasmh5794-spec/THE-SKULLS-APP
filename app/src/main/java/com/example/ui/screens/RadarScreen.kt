package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.UpcomingGame
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.*

@Composable
fun RadarScreen(
    upcomingGames: List<UpcomingGame>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("radar_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SkullSurfaceCardElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, SkullCrimson.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = null,
                        tint = SkullCrimson,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "رادار الإصدارات المرتقبة",
                            color = SkullTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "متابعة العد التنازلي ونسبة الحماس لأضخم عناوين الألعاب القادمة",
                            color = SkullTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        if (upcomingGames.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = SkullWhite,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد ألعاب مرتقبة مسجلة حالياً",
                            color = SkullTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "سيتم تحديث الرادار مع مشاركات وتحديثات مجتمع اللاعبين",
                            color = SkullTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(upcomingGames) { game ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upcoming_game_${game.id}"),
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
                            model = game.imageUrl,
                            contentDescription = game.title,
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
                        // Countdown badge
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = SkullBgDark.copy(alpha = 0.85f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkullGold)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = SkullGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "باقي ~${game.countdownDays} يوم",
                                    color = SkullGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Release Date string
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = SkullWhite
                        ) {
                            Text(
                                text = game.releaseDateStr,
                                color = SkullBgDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = game.title,
                            color = SkullTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = game.arabicTitle,
                            color = SkullTextSecondary,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = game.description,
                            color = SkullTextMuted,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Hype Meter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = SkullCrimson,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "مؤشر الحماس (Hype):",
                                    color = SkullTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "${game.hypeScorePercent}%",
                                color = SkullCrimson,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        LinearProgressIndicator(
                            progress = { game.hypeScorePercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = SkullCrimson,
                            trackColor = SkullBorderDark
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            game.platforms.forEach { platform ->
                                PlatformBadge(platform = platform)
                            }
                        }
                    }
                }
            }
        }
    }
}
}
