package com.example.demo.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

@Composable
fun qrscreen(uid: String, requestId: String) {

    val qrData = "$uid|$requestId"

    val qrBitmap = remember {
        generateQrCode(qrData)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2D30E3),
                        Color(0xFF8D6AAB),
                        Color(0xFF135DC4)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Visitor QR Code",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            qrBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Visitor QR",
                    modifier = Modifier
                        .size(340.dp)
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Verifiy the QR CODE",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 25.sp
            )
        }
    }
}


fun generateQrCode(text: String, size: Int = 650): Bitmap? {
    return try {
        val bitMatrix = MultiFormatWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            size,
            size
        )

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap[x, y] =
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK
                    else android.graphics.Color.WHITE
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}
