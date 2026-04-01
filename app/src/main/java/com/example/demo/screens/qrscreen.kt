package com.example.demo.screens

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.demo.ui.components.GlassButton
import com.example.demo.ui.components.GlassButtonStyle
import com.example.demo.ui.components.GlossyDivider
import com.example.demo.ui.components.LiquidGlassBackground
import com.example.demo.ui.components.LiquidGlassCard
import com.example.demo.ui.theme.AccentBlue
import com.example.demo.ui.theme.AmberGlass12
import com.example.demo.ui.theme.GreenGlass12
import com.example.demo.ui.theme.SemanticAmber
import com.example.demo.ui.theme.SemanticGreen
import com.example.demo.ui.theme.TextPrimary
import com.example.demo.ui.theme.TextSecondary
import com.example.demo.ui.theme.TextTertiary
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

@Composable
fun Qrscreen(uid: String, requestId: String, navController: NavHostController? = null) {

    var visitDate by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(requestId) {
        FirebaseDatabase.getInstance()
            .getReference("visitorRequests")
            .child(requestId).get()
            .addOnSuccessListener { snapshot ->
                visitDate = snapshot.child("visitDate").getValue(String::class.java) ?: ""
                isLoading = false
            }
            .addOnFailureListener { visitDate = ""; isLoading = false }
    }

    val qrData = remember(visitDate) {
        if (visitDate != null) "$uid|$requestId|$visitDate" else null
    }
    val qrBitmap = remember(qrData) {
        if (qrData != null) generateQrCodeLG(qrData) else null
    }

    val today = remember {
        java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
            .format(java.util.Date())
    }
    val isValidToday = visitDate == today

    BackHandler { navController?.popBackStack() }

    LiquidGlassBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick  = { navController?.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp, start = 8.dp)
            ) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text       = "Visitor QR Code",
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text      = "Show this at the college entrance\nfor security verification",
                    fontSize  = 13.sp,
                    color     = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(28.dp))

                // QR card — white surface so QR is scannable
                LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier            = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(260.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                isLoading -> CircularProgressIndicator(
                                    color       = AccentBlue,
                                    modifier    = Modifier.size(36.dp),
                                    strokeWidth = 2.5.dp
                                )
                                qrBitmap != null -> Image(
                                    bitmap             = qrBitmap.asImageBitmap(),
                                    contentDescription = "Visitor QR Code",
                                    modifier           = Modifier.size(240.dp)
                                )
                                else -> Text(
                                    text      = "QR generation failed.\nPlease restart.",
                                    color     = Color(0xFF64748B),
                                    textAlign = TextAlign.Center,
                                    fontSize  = 13.sp,
                                    modifier  = Modifier.padding(16.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))
                        GlossyDivider()
                        Spacer(Modifier.height(10.dp))

                        Text(
                            text       = "SmartGate Visitor Pass",
                            fontSize   = 13.sp,
                            color      = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )

                        if (!isLoading && !visitDate.isNullOrEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text      = "Valid for: $visitDate",
                                fontSize  = 11.sp,
                                color     = TextTertiary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Validity badge
                if (!isLoading && !visitDate.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(if (isValidToday) GreenGlass12 else AmberGlass12)
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text       = if (isValidToday) "✓ Valid today" else "⏰ Valid on $visitDate",
                            color      = if (isValidToday) SemanticGreen else SemanticAmber,
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                GlassButton(
                    text     = "Back to History",
                    style    = GlassButtonStyle.Secondary,
                    onClick  = { navController?.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

fun generateQrCodeLG(text: String, size: Int = 650): Bitmap? {
    return try {
        val bitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK
                else android.graphics.Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) { null }
}
