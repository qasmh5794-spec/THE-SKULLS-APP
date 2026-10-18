package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.NewsCategory
import com.example.model.PlatformType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (
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
        cons: List<String>
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var arabicTitle by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("أكشن ومغامرات") }
    var selectedPlatforms by remember { mutableStateOf(setOf(PlatformType.PS5)) }

    var overallScore by remember { mutableFloatStateOf(9.0f) }
    var gameplayScore by remember { mutableFloatStateOf(9.0f) }
    var graphicsScore by remember { mutableFloatStateOf(9.0f) }
    var storyScore by remember { mutableFloatStateOf(8.5f) }
    var soundScore by remember { mutableFloatStateOf(9.0f) }

    var verdict by remember { mutableStateOf("تجربة ممتازة تستحق التجربة") }
    var reviewBody by remember { mutableStateOf("") }
    var prosText by remember { mutableStateOf("أسلوب لعب ممتع وسلس\nرسوميات مبهرة") }
    var consText by remember { mutableStateOf("بعض التكرار في المهام الجانبية") }

    var errorMsg by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(20.dp)),
            color = SkullSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SkullBorderDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            tint = SkullWhite,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "إضافة مراجعة لعبة جديدة",
                            color = SkullWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = SkullTextSecondary)
                    }
                }

                Divider(color = SkullDividerDark, modifier = Modifier.padding(vertical = 12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (errorMsg != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SkullSlate.copy(alpha = 0.3f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkullWhite)
                        ) {
                            Text(
                                text = errorMsg!!,
                                color = SkullWhite,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Game Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("اسم اللعبة (بالإنجليزية أو بالعربية) *") },
                        placeholder = { Text("مثال: Elden Ring / إلدن رينغ") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SkullSurfaceCard,
                            unfocusedContainerColor = SkullSurfaceCard,
                            focusedBorderColor = SkullWhite,
                            unfocusedBorderColor = SkullBorderDark,
                            focusedTextColor = SkullTextPrimary,
                            unfocusedTextColor = SkullTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Arabic Title
                    OutlinedTextField(
                        value = arabicTitle,
                        onValueChange = { arabicTitle = it },
                        label = { Text("الاسم بالعربية أو الوصف المختصر") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SkullSurfaceCard,
                            unfocusedContainerColor = SkullSurfaceCard,
                            focusedBorderColor = SkullWhite,
                            unfocusedBorderColor = SkullBorderDark,
                            focusedTextColor = SkullTextPrimary,
                            unfocusedTextColor = SkullTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Platform Selection
                    Text(
                        text = "المنصات المتوفرة عليها اللعبة *",
                        color = SkullTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PlatformType.values().forEach { platform ->
                            val isSelected = selectedPlatforms.contains(platform)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) SkullWhite else SkullSurfaceCard,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) SkullWhite else SkullBorderDark
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedPlatforms = if (isSelected) {
                                            if (selectedPlatforms.size > 1) selectedPlatforms - platform else selectedPlatforms
                                        } else {
                                            selectedPlatforms + platform
                                        }
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = platform.shortName,
                                        color = if (isSelected) SkullBgDark else SkullTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    // Genre
                    OutlinedTextField(
                        value = genre,
                        onValueChange = { genre = it },
                        label = { Text("نوع وتصنيف اللعبة") },
                        placeholder = { Text("مثال: أكشن RPG / عالم مفتوح / شوتر") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SkullSurfaceCard,
                            unfocusedContainerColor = SkullSurfaceCard,
                            focusedBorderColor = SkullWhite,
                            unfocusedBorderColor = SkullBorderDark,
                            focusedTextColor = SkullTextPrimary,
                            unfocusedTextColor = SkullTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Overall Score Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("التقييم الكلي (سكلز)", color = SkullTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(String.format("%.1f / 10", overallScore), color = SkullWhite, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                        Slider(
                            value = overallScore,
                            onValueChange = { overallScore = Math.round(it * 10f) / 10f },
                            valueRange = 1.0f..10.0f,
                            steps = 89,
                            colors = SliderDefaults.colors(
                                thumbColor = SkullWhite,
                                activeTrackColor = SkullWhite,
                                inactiveTrackColor = SkullBorderDark
                            )
                        )
                    }

                    // Breakdown Scores
                    Text("تفاصيل التقييم (من 1 إلى 10)", color = SkullTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    ScoreSliderRow(label = "أسلوب اللعب (Gameplay)", value = gameplayScore) { gameplayScore = it }
                    ScoreSliderRow(label = "الرسوميات والجرافيكس (Graphics)", value = graphicsScore) { graphicsScore = it }
                    ScoreSliderRow(label = "القصة والعالم (Story)", value = storyScore) { storyScore = it }
                    ScoreSliderRow(label = "الصوتيات والموسيقى (Audio)", value = soundScore) { soundScore = it }

                    // Verdict
                    OutlinedTextField(
                        value = verdict,
                        onValueChange = { verdict = it },
                        label = { Text("الخلاصة بعبارة واحدة") },
                        placeholder = { Text("مثال: تحفة فنية / لعبة لا تفوت") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SkullSurfaceCard,
                            unfocusedContainerColor = SkullSurfaceCard,
                            focusedBorderColor = SkullWhite,
                            unfocusedBorderColor = SkullBorderDark,
                            focusedTextColor = SkullTextPrimary,
                            unfocusedTextColor = SkullTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Review Body
                    OutlinedTextField(
                        value = reviewBody,
                        onValueChange = { reviewBody = it },
                        label = { Text("تفاصيل المراجعة ورأيك الكامل *") },
                        placeholder = { Text("اكتب تجربتك، إيجابيات اللعبة وسلبياتها، ونصيحتك للاعبين...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        maxLines = 10,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SkullSurfaceCard,
                            unfocusedContainerColor = SkullSurfaceCard,
                            focusedBorderColor = SkullWhite,
                            unfocusedBorderColor = SkullBorderDark,
                            focusedTextColor = SkullTextPrimary,
                            unfocusedTextColor = SkullTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Pros
                    OutlinedTextField(
                        value = prosText,
                        onValueChange = { prosText = it },
                        label = { Text("الإيجابيات (اكتب كل إيجابية في سطر)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SkullSurfaceCard,
                            unfocusedContainerColor = SkullSurfaceCard,
                            focusedBorderColor = SkullWhite,
                            unfocusedBorderColor = SkullBorderDark,
                            focusedTextColor = SkullTextPrimary,
                            unfocusedTextColor = SkullTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Cons
                    OutlinedTextField(
                        value = consText,
                        onValueChange = { consText = it },
                        label = { Text("السلبيات والملاحظات (اكتب كل سلبية في سطر)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SkullSurfaceCard,
                            unfocusedContainerColor = SkullSurfaceCard,
                            focusedBorderColor = SkullWhite,
                            unfocusedBorderColor = SkullBorderDark,
                            focusedTextColor = SkullTextPrimary,
                            unfocusedTextColor = SkullTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SkullTextSecondary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
                    ) {
                        Text("إلغاء")
                    }

                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                errorMsg = "يرجى كتابة اسم اللعبة أولاً"
                                return@Button
                            }
                            if (reviewBody.isBlank()) {
                                errorMsg = "يرجى كتابة نص المراجعة ورأيك في اللعبة"
                                return@Button
                            }
                            val prosList = prosText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
                            val consList = consText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

                            onSubmit(
                                title.trim(),
                                arabicTitle.trim(),
                                genre.trim(),
                                selectedPlatforms.toList(),
                                overallScore,
                                gameplayScore,
                                graphicsScore,
                                storyScore,
                                soundScore,
                                verdict.trim(),
                                reviewBody.trim(),
                                prosList,
                                consList
                            )
                        },
                        modifier = Modifier.weight(2f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkullWhite,
                            contentColor = SkullBgDark
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("نشر المراجعة الآن", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreSliderRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = SkullTextSecondary, fontSize = 11.sp)
            Text(String.format("%.1f", value), color = SkullWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = { onValueChange(Math.round(it * 10f) / 10f) },
            valueRange = 1.0f..10.0f,
            steps = 89,
            colors = SliderDefaults.colors(
                thumbColor = SkullSilver,
                activeTrackColor = SkullSilver,
                inactiveTrackColor = SkullBorderDark.copy(alpha = 0.5f)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewsDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        title: String,
        category: NewsCategory,
        summary: String,
        content: String,
        isBreaking: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(NewsCategory.PLAYSTATION) }
    var summary by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isBreaking by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(20.dp)),
            color = SkullSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SkullBorderDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AddComment,
                            contentDescription = null,
                            tint = SkullWhite,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "نشر خبر ألعاب جديد",
                            color = SkullWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = SkullTextSecondary)
                    }
                }

                Divider(color = SkullDividerDark, modifier = Modifier.padding(vertical = 12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (errorMsg != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SkullSlate.copy(alpha = 0.3f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkullWhite)
                        ) {
                            Text(
                                text = errorMsg!!,
                                color = SkullWhite,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // News Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان الخبر الرئيسي *") },
                        placeholder = { Text("مثال: الإعلان الرسمي عن مواصفات وموعد إطلاق...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SkullSurfaceCard,
                            unfocusedContainerColor = SkullSurfaceCard,
                            focusedBorderColor = SkullWhite,
                            unfocusedBorderColor = SkullBorderDark,
                            focusedTextColor = SkullTextPrimary,
                            unfocusedTextColor = SkullTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Category Selection
                    Text(
                        text = "قسم الخبر أو المنصة *",
                        color = SkullTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val categories = listOf(
                            NewsCategory.PLAYSTATION,
                            NewsCategory.XBOX,
                            NewsCategory.PC,
                            NewsCategory.NINTENDO,
                            NewsCategory.LEAKS
                        )
                        categories.forEach { cat ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) SkullWhite else SkullSurfaceCard,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) SkullWhite else SkullBorderDark
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedCategory = cat }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat.arabicName,
                                        color = if (isSelected) SkullBgDark else SkullTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    // Breaking News Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SkullSurfaceCard)
                            .clickable { isBreaking = !isBreaking }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = SkullWhite)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("خبر عاجل (Breaking News)", color = SkullTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("تمييز الخبر كحدث رئيسي في واجهة التطبيق", color = SkullTextSecondary, fontSize = 11.sp)
                            }
                        }
                        Switch(
                            checked = isBreaking,
                            onCheckedChange = { isBreaking = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SkullBgDark,
                                checkedTrackColor = SkullWhite,
                                uncheckedThumbColor = SkullGray,
                                uncheckedTrackColor = SkullSurfaceDark
                            )
                        )
                    }

                    // Summary
                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text("ملخص الخبر (سطر أو سطرين)") },
                        placeholder = { Text("موجز سريع يظهر في قائمة الأخبار...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SkullSurfaceCard,
                            unfocusedContainerColor = SkullSurfaceCard,
                            focusedBorderColor = SkullWhite,
                            unfocusedBorderColor = SkullBorderDark,
                            focusedTextColor = SkullTextPrimary,
                            unfocusedTextColor = SkullTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Full Content
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("تفاصيل ومحتوى الخبر الكامل *") },
                        placeholder = { Text("اكتب كامل تفاصيل الخبر والمصادر والمعلومات التي تهم مجتمع اللاعبين...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 140.dp),
                        maxLines = 12,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SkullSurfaceCard,
                            unfocusedContainerColor = SkullSurfaceCard,
                            focusedBorderColor = SkullWhite,
                            unfocusedBorderColor = SkullBorderDark,
                            focusedTextColor = SkullTextPrimary,
                            unfocusedTextColor = SkullTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SkullTextSecondary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
                    ) {
                        Text("إلغاء")
                    }

                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                errorMsg = "يرجى كتابة عنوان الخبر أولاً"
                                return@Button
                            }
                            if (content.isBlank()) {
                                errorMsg = "يرجى كتابة تفاصيل ومحتوى الخبر"
                                return@Button
                            }
                            onSubmit(
                                title.trim(),
                                selectedCategory,
                                summary.trim(),
                                content.trim(),
                                isBreaking
                            )
                        },
                        modifier = Modifier.weight(2f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkullWhite,
                            contentColor = SkullBgDark
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("نشر الخبر الآن", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
