package com.example.demo.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.demo.ui.components.StatusBadge
import com.example.demo.ui.theme.AccentBlue
import com.example.demo.ui.theme.SemanticGreen
import com.example.demo.ui.theme.TextPrimary
import com.example.demo.ui.theme.TextSecondary
import com.example.demo.ui.theme.TextTertiary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun historyscreen(navController: NavHostController) {

    val uid           = FirebaseAuth.getInstance().currentUser?.uid
    val historyList   = remember { androidx.compose.runtime.mutableStateListOf<VisitorHistory>() }
    var isLoading     by remember { mutableStateOf(true) }
    var isRefreshing  by remember { mutableStateOf(false) }

    fun loadHistory() {
        if (uid == null) { isLoading = false; return }
        val db         = FirebaseDatabase.getInstance()
        val activeRef  = db.getReference("visitorRequests")
        val historyRef = db.getReference("visitHistory").child(uid)
        var activeDone = false; var historyDone = false
        val active     = mutableListOf<VisitorHistory>()
        val archived   = mutableListOf<VisitorHistory>()

        fun merge() {
            if (!activeDone || !historyDone) return
            historyList.clear()
            historyList.addAll(active); historyList.addAll(archived)
            historyList.sortByDescending { it.timestamp as? Long ?: 0L }
            isLoading = false; isRefreshing = false
        }

        activeRef.orderByChild("uid").equalTo(uid).get()
            .addOnSuccessListener { s ->
                active.clear()
                s.children.forEach { c -> c.getValue(VisitorHistory::class.java)?.let { active.add(it.copy(requestId = c.key ?: "", uid = uid)) } }
                activeDone = true; merge()
            }.addOnFailureListener { activeDone = true; merge() }

        historyRef.get()
            .addOnSuccessListener { s ->
                archived.clear()
                s.children.forEach { c -> c.getValue(VisitorHistory::class.java)?.let { archived.add(it.copy(requestId = c.key ?: "", uid = uid)) } }
                historyDone = true; merge()
            }.addOnFailureListener { historyDone = true; merge() }
    }

    DisposableEffect(uid) {
        if (uid == null) { isLoading = false; return@DisposableEffect onDispose {} }
        val db = FirebaseDatabase.getInstance()
        val activeRef  = db.getReference("visitorRequests")
        val historyRef = db.getReference("visitHistory").child(uid)
        var activeDone = false; var histDone = false
        val active = mutableListOf<VisitorHistory>(); val archived = mutableListOf<VisitorHistory>()

        fun merge() {
            if (!activeDone || !histDone) return
            historyList.clear()
            historyList.addAll(active); historyList.addAll(archived)
            historyList.sortByDescending { it.timestamp as? Long ?: 0L }
            isLoading = false
        }

        val aListener = object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                active.clear()
                s.children.forEach { c -> c.getValue(VisitorHistory::class.java)?.let { active.add(it.copy(requestId = c.key ?: "", uid = uid)) } }
                activeDone = true; merge()
            }
            override fun onCancelled(e: DatabaseError) { activeDone = true; merge() }
        }
        val hListener = object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                archived.clear()
                s.children.forEach { c -> c.getValue(VisitorHistory::class.java)?.let { archived.add(it.copy(requestId = c.key ?: "", uid = uid)) } }
                histDone = true; merge()
            }
            override fun onCancelled(e: DatabaseError) { histDone = true; merge() }
        }
        activeRef.orderByChild("uid").equalTo(uid).addValueEventListener(aListener)
        historyRef.addValueEventListener(hListener)
        onDispose {
            activeRef.orderByChild("uid").equalTo(uid).removeEventListener(aListener)
            historyRef.removeEventListener(hListener)
        }
    }

    LiquidGlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Text(
                text       = "Visit History",
                fontSize   = 30.sp,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary,
                letterSpacing = (-0.3).sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text     = if (historyList.isNotEmpty())
                    "${historyList.size} visit${if (historyList.size > 1) "s" else ""} · Tap approved to view QR"
                else "Tap an approved visit to view your QR code",
                fontSize = 12.sp,
                color    = TextTertiary
            )

            Spacer(Modifier.height(20.dp))

            when {
                isLoading -> {
                    Spacer(Modifier.weight(1f))
                    Text("Loading history…", color = TextSecondary)
                    Spacer(Modifier.weight(1f))
                }

                historyList.isEmpty() -> {
                    Spacer(Modifier.weight(1f))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("📋", fontSize = 48.sp)
                        Text(
                            text       = "No visit history yet",
                            color      = TextPrimary,
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text      = "Your visits will appear here\nonce you register",
                            color     = TextSecondary,
                            fontSize  = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        GlassButton(
                            text     = "Register a Visit",
                            style    = GlassButtonStyle.Primary,
                            onClick  = { navController.navigate("register") },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                }

                else -> {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh    = { isRefreshing = true; loadHistory() },
                        modifier     = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier            = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(historyList) { index, item ->
                                AnimatedHistoryCardLG(
                                    item    = item,
                                    index   = index,
                                    onClick = {
                                        when (item.status) {
                                            "PENDING"                        -> navController.navigate("pending")
                                            "APPROVED", "INSIDE", "EXITED"  -> navController.navigate("qr/${item.uid}/${item.requestId}")
                                            "REJECTED"                       -> navController.navigate("rejected")
                                            "FLAGGED"                        -> navController.navigate("flagged")
                                        }
                                    }
                                )
                            }
                            item { Spacer(Modifier.height(24.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedHistoryCardLG(item: VisitorHistory, index: Int, onClick: () -> Unit) {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 80L)
        visible.value = true
    }
    AnimatedVisibility(
        visible = visible.value,
        enter   = slideInVertically(
            animationSpec    = tween(350),
            initialOffsetY   = { it / 2 }
        ) + fadeIn(tween(350))
    ) {
        HistoryCardLG(item, onClick)
    }
}

@Composable
fun HistoryCardLG(item: VisitorHistory, onClick: () -> Unit) {
    val statusLabel = when (item.status) {
        "APPROVED" -> "✅ Approved — Tap to view QR"
        "INSIDE"   -> "📍 Inside Campus"
        "EXITED"   -> "🚪 Visit Completed"
        "REJECTED" -> "❌ Rejected"
        "PENDING"  -> "⏳ Pending Approval"
        "FLAGGED"  -> "🚩 Flagged — Under Admin Review"
        else       -> item.status
    }

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Text(
                    text       = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 17.sp,
                    color      = TextPrimary,
                    modifier   = Modifier.weight(1f)
                )
                StatusBadge(status = item.status)
            }

            Spacer(Modifier.height(8.dp))
            GlossyDivider()
            Spacer(Modifier.height(8.dp))

            if (item.visitDate.isNotEmpty()) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Rounded.CalendarMonth, null, tint = TextTertiary, modifier = Modifier.size(13.dp))
                    Text(text = item.visitDate, color = TextSecondary, fontSize = 13.sp)
                }
                Spacer(Modifier.height(4.dp))
            }

            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Rounded.Person, null, tint = TextTertiary, modifier = Modifier.size(13.dp))
                Text(text = "Meeting: ${item.personToMeet}", color = TextSecondary, fontSize = 13.sp)
            }

            Spacer(Modifier.height(4.dp))
            Text(text = "Purpose: ${item.purpose}", color = TextTertiary, fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))
            Text(text = statusLabel, color = AccentBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}