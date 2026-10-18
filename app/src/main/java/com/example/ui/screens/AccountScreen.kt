package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import coil.compose.AsyncImage
import com.example.R
import com.example.data.firebase.UserProfile
import com.example.model.PlatformType
import com.example.ui.auth.GoogleAuthHelper
import com.example.ui.auth.GoogleSignInButton
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.*
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

@Composable
fun AccountScreen(
    currentUser: FirebaseUser?,
    userProfile: UserProfile?,
    onSaveProfile: (gamerTag: String, platform: String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var gamerTagInput by remember(userProfile) { mutableStateOf(userProfile?.gamerTag ?: "SkullPlayer") }
    var selectedPlatform by remember(userProfile) { mutableStateOf(userProfile?.favoritePlatform ?: "PS5") }
    var isEditing by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("account_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (currentUser == null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SkullCrimson.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.the_skulls_logo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .border(2.dp, SkullCrimson, CircleShape)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "انضم إلى مجتمع THE SKULLS",
                            color = SkullTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "سجّل الدخول بحساب Google للتفاعل مع مراجعات الألعاب، إضافة اللايكات والتعليقات، وإبداء رأيك وتقييمك الشخصي.",
                            color = SkullTextSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        GoogleSignInButton(
                            onAuthSuccess = {},
                            onAuthError = {}
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "مميزات حساب عضوية THE SKULLS:",
                            color = SkullCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FeatureBenefitRow(Icons.Default.ThumbUp, "إمكانية الإعجاب بالمراجعات المفضلة وحفظها")
                        FeatureBenefitRow(Icons.Default.RateReview, "كتابة التعليقات والمراجعات ومناقشة تفاصيل الألعاب")
                        FeatureBenefitRow(Icons.Default.Badge, "تخصيص GamerTag خاص بك ومنصتك المفضلة")
                        FeatureBenefitRow(Icons.Default.CloudSync, "مزامنة سحابية مستمرة لتقييماتك عبر قاعدة بيانات سكلز")
                    }
                }
            }
        } else {
            // User is signed in!
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            if (currentUser.photoUrl != null) {
                                AsyncImage(
                                    model = currentUser.photoUrl,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, SkullCrimson, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = SkullCrimson,
                                    modifier = Modifier.size(80.dp)
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = SkullEmerald,
                                modifier = Modifier.size(18.dp).border(2.dp, SkullBgDark, CircleShape)
                            ) {}
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = currentUser.displayName ?: "لاعب سكلز",
                            color = SkullTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentUser.email ?: "",
                            color = SkullTextMuted,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SkullCrimson.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkullCrimson)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsEsports,
                                    contentDescription = null,
                                    tint = SkullCrimson,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "لقب اللاعب: ${gamerTagInput.ifBlank { "SkullPlayer" }}",
                                    color = SkullCrimson,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Edit Gamer Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SkullSurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "بيانات الجيمنج الخاصة بك",
                                color = SkullTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = { isEditing = !isEditing }) {
                                Text(
                                    text = if (isEditing) "إلغاء" else "تعديل",
                                    color = SkullCyan,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        if (isEditing) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = gamerTagInput,
                                onValueChange = { gamerTagInput = it },
                                label = { Text("لقب الجيمر (GamerTag)") },
                                placeholder = { Text("مثال: CyberViper99") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = SkullTextPrimary,
                                    unfocusedTextColor = SkullTextPrimary
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("gamer_tag_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "منصتك الأساسية:",
                                color = SkullTextSecondary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("PS5", "XSX", "PC", "NSW").forEach { plat ->
                                    FilterChip(
                                        selected = selectedPlatform == plat,
                                        onClick = { selectedPlatform = plat },
                                        label = { Text(plat) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = SkullWhite,
                                            selectedLabelColor = SkullBgDark
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    onSaveProfile(gamerTagInput, selectedPlatform)
                                    isEditing = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SkullWhite,
                                    contentColor = SkullBgDark
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("save_gamer_profile_btn")
                            ) {
                                Text("حفظ البيانات", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "المنصة الأساسية:", color = SkullTextSecondary, fontSize = 13.sp)
                                Text(text = selectedPlatform, color = SkullCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Sign Out Button
            item {
                Button(
                    onClick = {
                        GoogleAuthHelper.signOut(
                            credentialManager = credentialManager,
                            onSignOutComplete = onSignOut,
                            scope = scope
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SkullSurfaceCardElevated,
                        contentColor = SkullWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkullBorderDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("sign_out_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تسجيل الخروج من الحساب", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FeatureBenefitRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SkullEmerald,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = SkullTextPrimary, fontSize = 12.sp)
    }
}
