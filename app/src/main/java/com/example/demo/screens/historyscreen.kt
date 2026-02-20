package com.example.demo.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

@Composable
fun historyscreen(navController: NavHostController) {

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val historyList = remember { mutableStateListOf<VisitorHistory>() }
    var isLoading by remember { mutableStateOf(true) }


//    LaunchedEffect(uid) {
//        if (uid == null) return@LaunchedEffect
//
//        FirebaseDatabase.getInstance()
//            .getReference("visitorRequests")
//            .orderByChild("uid")
//            .equalTo(uid)
//            .get()
//            .addOnSuccessListener { snapshot ->
//                historyList.clear()
//
//                snapshot.children.forEach { child ->
//                    val item = child.getValue(VisitorHistory::class.java)
//                    if (item != null) {
//                        historyList.add(
//                            item.copy(
//                                requestId = child.key ?: "",
//                                uid = uid
//                            )
//                        )
//                    }
//                }
//
//                historyList.sortByDescending { it.timestamp }
//                isLoading = false
//            }
//            .addOnFailureListener {
//                isLoading = false
//            }
//    }


    DisposableEffect(uid) {
        if (uid == null) {
            onDispose { }
        } else {

            val ref = FirebaseDatabase.getInstance()
                .getReference("visitorRequests")

            val query = ref.orderByChild("uid").equalTo(uid)

            val listener = object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    historyList.clear()

                    snapshot.children.forEach { child ->
                        val item = child.getValue(VisitorHistory::class.java)
                        if (item != null) {
                            historyList.add(
                                item.copy(
                                    requestId = child.key ?: "",
                                    uid = uid
                                )
                            )
                        }
                    }

                    historyList.sortByDescending { it.timestamp }
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    isLoading = false
                }
            }

            query.addValueEventListener(listener)

            onDispose {
                query.removeEventListener(listener)
            }
        }
    }


    // 🌈 GRADIENT BACKGROUND
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
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            // ✅ CENTERED TITLE
            Text(
                text = "Visit History",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                isLoading -> {
                    Text(
                        text = "Loading history...",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                historyList.isEmpty() -> {
                    Text(
                        text = "No visit history found",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(historyList) { index, item ->
                            AnimatedHistoryCard(
                                item = item,
                                index = index,
                                onClick = {
                                    when (item.status) {
                                        "PENDING" -> {
                                            navController.navigate("pending")
                                        }

                                        "APPROVED" -> {
                                            navController.navigate(
                                                "qr/${item.uid}/${item.requestId}"
                                            )
                                        }

                                        "REJECTED" -> {
                                            // optional: do nothing or show toast
                                            navController.navigate("rejected")
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


data class VisitorHistory(
    val requestId: String = "",
    val uid: String = "",
    val name: String = "",
    val personToMeet: String = "",
    val purpose: String = "",
    val status: String = "",
    val timestamp: Long = 0
)


@Composable
fun AnimatedHistoryCard(item: VisitorHistory, index: Int, onClick:()->Unit) {
    val visible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 80L) // stagger animation
        visible.value = true
    }

    AnimatedVisibility(
        visible = visible.value,
        enter = slideInVertically(
            initialOffsetY = { it }
        ) + fadeIn(),
        exit = fadeOut()
    ) {
        HistoryCard(item, onClick)
    }
}



@Composable
fun HistoryCard(item: VisitorHistory, onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White.copy(alpha = 0.12f),
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {

            Text(
                text = item.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Meeting: ${item.personToMeet}",
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Purpose: ${item.purpose}",
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.status,
                color = when (item.status) {
                    "APPROVED" -> Color(0xFF03C222)
                    "REJECTED" -> Color(0xFFEA1102)
                    else -> Color(0xFFDEDC09)
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}