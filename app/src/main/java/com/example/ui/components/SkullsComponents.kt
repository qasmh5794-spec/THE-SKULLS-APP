package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlatformType
import com.example.ui.theme.*

@Composable
fun SkullScoreBadge(
    score: Float,
    modifier: Modifier = Modifier,
    size: Int = 54,
    showMax: Boolean = true
) {
    val scoreColor = when {
        score >= 9.5f -> SkullWhite
        score >= 9.0f -> SkullSilver
        score >= 8.0f -> SkullChrome
        else -> SkullGray
    }

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        SkullBorderDark,
                        SkullSurfaceDark
                    )
                )
            )
            .border(
                width = 1.5.dp,
                color = scoreColor.copy(alpha = 0.9f),
                shape = RoundedCornerShape(14.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format("%.1f", score),
                color = SkullWhite,
                fontSize = (size * 0.36f).sp,
                fontWeight = FontWeight.Black
            )
            if (showMax) {
                Text(
                    text = "سكلز",
                    color = scoreColor,
                    fontSize = (size * 0.18f).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PlatformBadge(platform: PlatformType, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (platform) {
        PlatformType.PS5 -> PlatformPS5 to Color.White
        PlatformType.XSX -> PlatformXbox to Color.White
        PlatformType.PC -> PlatformPC to Color.White
        PlatformType.SWITCH -> PlatformSwitch to Color.White
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = bgColor.copy(alpha = 0.25f),
        border = androidx.compose.foundation.BorderStroke(1.dp, bgColor.copy(alpha = 0.6f))
    ) {
        Text(
            text = platform.shortName,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun BreakdownProgressRow(
    label: String,
    score: Float,
    icon: ImageVector,
    color: Color = SkullWhite
) {
    var progressTarget by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 800),
        label = "scoreProgress"
    )

    LaunchedEffect(score) {
        progressTarget = (score / 10f).coerceIn(0f, 1f)
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    color = SkullTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = "${String.format("%.1f", score)} / 10",
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = SkullBorderDark.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun ProsConsCard(pros: List<String>, cons: List<String>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Pros
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = null,
                    tint = SkullWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "الإيجابيات والتميز",
                    color = SkullWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            pros.forEach { pro ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SkullSilver,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = pro,
                        color = SkullTextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = SkullDividerDark)
            Spacer(modifier = Modifier.height(14.dp))

            // Cons
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ThumbDown,
                    contentDescription = null,
                    tint = SkullGray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "السلبيات والملاحظات",
                    color = SkullGray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            cons.forEach { con ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = SkullGrayDark,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = con,
                        color = SkullTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
