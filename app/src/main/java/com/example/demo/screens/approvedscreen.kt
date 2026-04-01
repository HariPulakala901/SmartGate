//package com.example.demo.screens
//
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.location.LocationManager
//import android.net.wifi.WifiManager
//import android.os.Build
//import android.provider.Settings
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.annotation.RequiresApi
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.core.RepeatMode
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.infiniteRepeatable
//import androidx.compose.animation.core.rememberInfiniteTransition
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.animation.slideInHorizontally
//import androidx.compose.animation.slideOutHorizontally
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.rounded.CheckCircle
//import androidx.compose.material.icons.rounded.LocationOn
//import androidx.compose.material.icons.rounded.Wifi
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleEventObserver
//import androidx.lifecycle.compose.LocalLifecycleOwner
//import androidx.navigation.NavController
//import com.airbnb.lottie.compose.LottieAnimation
//import com.airbnb.lottie.compose.LottieCompositionSpec
//import com.airbnb.lottie.compose.LottieConstants
//import com.airbnb.lottie.compose.animateLottieCompositionAsState
//import com.airbnb.lottie.compose.rememberLottieComposition
//import com.example.demo.R
//import com.example.demo.ui.components.GlassButton
//import com.example.demo.ui.components.GlassButtonStyle
//import com.example.demo.ui.components.GlossyDivider
//import com.example.demo.ui.components.LiquidGlassBackground
//import com.example.demo.ui.components.LiquidGlassCard
//import com.example.demo.ui.components.RadarWidget
//import com.example.demo.ui.components.SectionLabel
//import com.example.demo.ui.components.StatusBadge
//import com.example.demo.ui.components.TrackingStatusRow
//import com.example.demo.ui.theme.SemanticGreen
//import com.example.demo.ui.theme.SemanticRed
//import com.example.demo.ui.theme.TextPrimary
//import com.example.demo.ui.theme.TextSecondary
//import com.example.demo.ui.theme.TextTertiary
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun Approvedscreen(navController: NavController) {
//
//    val context         = LocalContext.current
//    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//    val wifiManager     = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//    val uid             = FirebaseAuth.getInstance().currentUser?.uid
//
//    var visitorStatus  by remember { mutableStateOf("APPROVED") }
//    var visitDate      by remember { mutableStateOf("") }
//    var currentZone    by remember { mutableStateOf("") }
//    var lastZoneUpdate by remember { mutableStateOf("") }
//
//    // Real-time Firebase listener
//    DisposableEffect(uid) {
//        if (uid == null) return@DisposableEffect onDispose {}
//        val query = FirebaseDatabase.getInstance()
//            .getReference("visitorRequests")
//            .orderByChild("uid").equalTo(uid)
//
//        val listener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val latest = snapshot.children.maxByOrNull {
//                    it.child("timestamp").getValue(Long::class.java) ?: 0L
//                } ?: return
//                visitorStatus = latest.child("status").getValue(String::class.java) ?: return
//                visitDate     = latest.child("visitDate").getValue(String::class.java) ?: ""
//                val zone      = latest.child("currentZone").getValue(String::class.java) ?: ""
//                val zoneTs    = latest.child("lastZoneUpdate").getValue(Long::class.java)
//                currentZone   = if (zone == "Unknown Zone") "" else zone
//                lastZoneUpdate = if (zoneTs != null && zoneTs > 0)
//                    java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
//                        .format(java.util.Date(zoneTs)) else ""
//            }
//            override fun onCancelled(error: DatabaseError) {}
//        }
//        query.addValueEventListener(listener)
//        onDispose { query.removeEventListener(listener) }
//    }
//
//    var hasLocationPermission by remember {
//        mutableStateOf(
//            ContextCompat.checkSelfPermission(
//                context, android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        )
//    }
//    var hasNotificationPermission by remember {
//        mutableStateOf(
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                ContextCompat.checkSelfPermission(
//                    context, android.Manifest.permission.POST_NOTIFICATIONS
//                ) == PackageManager.PERMISSION_GRANTED
//            } else true
//        )
//    }
//    var isLocationEnabled by remember {
//        mutableStateOf(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
//    }
//    var isWifiEnabled by remember { mutableStateOf(wifiManager.isWifiEnabled) }
//
//    val permissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { granted -> hasLocationPermission = granted }
//
//    val notificationPermissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { granted -> hasNotificationPermission = granted }
//
//    val canStartWifiScan = hasLocationPermission && isLocationEnabled && isWifiEnabled
//
//    val lifecycleOwner = LocalLifecycleOwner.current
//    DisposableEffect(lifecycleOwner) {
//        val observer = LifecycleEventObserver { _, event ->
//            if (event == Lifecycle.Event.ON_RESUME) {
//                hasLocationPermission = ContextCompat.checkSelfPermission(
//                    context, android.Manifest.permission.ACCESS_FINE_LOCATION
//                ) == PackageManager.PERMISSION_GRANTED
//                isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                isWifiEnabled     = wifiManager.isWifiEnabled
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    hasNotificationPermission = ContextCompat.checkSelfPermission(
//                        context, android.Manifest.permission.POST_NOTIFICATIONS
//                    ) == PackageManager.PERMISSION_GRANTED
//                }
//            }
//        }
//        lifecycleOwner.lifecycle.addObserver(observer)
//        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
//    }
//
//    DisposableEffect(visitorStatus, canStartWifiScan) {
//        when {
//            visitorStatus == "INSIDE" && canStartWifiScan && uid != null -> {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
//                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
//                }
//                context.startForegroundService(
//                    Intent(context, WifiTrackingService::class.java).apply { putExtra("uid", uid) }
//                )
//            }
//            visitorStatus == "EXITED" -> {
//                context.stopService(Intent(context, WifiTrackingService::class.java))
//            }
//        }
//        onDispose {}
//    }
//
//    var visible by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { visible = true }
//
//    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.three))
//    val progress by animateLottieCompositionAsState(
//        composition = composition,
//        iterations  = LottieConstants.IterateForever,
//        speed       = 0.7f
//    )
//
//    val pulsAlpha by rememberInfiniteTransition(label = "pulse").animateFloat(
//        initialValue  = 0.5f,
//        targetValue   = 1f,
//        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
//        label         = "alpha"
//    )
//
//    LiquidGlassBackground {
//        AnimatedVisibility(
//            visible = visible,
//            enter   = slideInHorizontally { it } + fadeIn(),
//            exit    = slideOutHorizontally { it } + fadeOut()
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .verticalScroll(rememberScrollState())
//                    .padding(horizontal = 24.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Spacer(Modifier.height(40.dp))
//
//                // ── Lottie animation ──────────────────────────────────────
//                LottieAnimation(
//                    composition = composition,
//                    progress    = { progress },
//                    modifier    = Modifier.size(180.dp)
//                )
//
//                Spacer(Modifier.height(8.dp))
//
//                // ── Status badge ──────────────────────────────────────────
//                StatusBadge(status = visitorStatus)
//
//                Spacer(Modifier.height(12.dp))
//
//                Text(
//                    text       = "Request Approved!",
//                    color      = SemanticGreen.copy(alpha = pulsAlpha),
//                    fontSize   = 26.sp,
//                    fontWeight = FontWeight.Bold
//                )
//
//                if (visitDate.isNotEmpty()) {
//                    Spacer(Modifier.height(4.dp))
//                    Text(
//                        text  = "Visit Date: $visitDate",
//                        color = TextSecondary,
//                        fontSize = 13.sp
//                    )
//                }
//
//                Spacer(Modifier.height(10.dp))
//
//                Text(
//                    text = when (visitorStatus) {
//                        "APPROVED" -> "Please arrive on your registered date.\nScan the QR at the entrance to check in."
//                        "INSIDE"   -> "📡 You are inside campus.\nZone tracking is now active."
//                        "EXITED"   -> "✅ Your visit is complete.\nThank you for visiting!"
//                        else       -> "Your visit is scheduled."
//                    },
//                    color      = TextSecondary,
//                    fontSize   = 14.sp,
//                    textAlign  = TextAlign.Center,
//                    lineHeight = 20.sp
//                )
//
//                Spacer(Modifier.height(20.dp))
//
//                // ── Live Zone card — only when INSIDE ─────────────────────
//                if (visitorStatus == "INSIDE") {
//                    LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
//                        Column(
//                            modifier            = Modifier.padding(20.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            SectionLabel(text = "Current Location")
//                            Spacer(Modifier.height(16.dp))
//                            RadarWidget(zoneName = currentZone)
//                            if (lastZoneUpdate.isNotEmpty()) {
//                                Spacer(Modifier.height(6.dp))
//                                Text(
//                                    text     = "Last updated at $lastZoneUpdate",
//                                    color    = TextTertiary,
//                                    fontSize = 11.sp
//                                )
//                            }
//                            if (currentZone.isEmpty()) {
//                                Spacer(Modifier.height(4.dp))
//                                Text(
//                                    text      = "Move closer to a Wi-Fi access point",
//                                    color     = TextTertiary,
//                                    fontSize  = 11.sp,
//                                    textAlign = TextAlign.Center
//                                )
//                            }
//                        }
//                    }
//                    Spacer(Modifier.height(14.dp))
//                }
//
//                // ── Tracking setup card ───────────────────────────────────
//                LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
//                    Column(modifier = Modifier.padding(20.dp)) {
//                        SectionLabel(text = "Tracking Setup")
//                        Spacer(Modifier.height(12.dp))
//                        GlossyDivider()
//                        Spacer(Modifier.height(8.dp))
//
//                        TrackingStatusRow(
//                            icon  = { Icon(Icons.Rounded.LocationOn, null, tint = if (hasLocationPermission) SemanticGreen else SemanticRed, modifier = Modifier.size(18.dp)) },
//                            label = "Location Permission",
//                            isOk  = hasLocationPermission
//                        )
//                        TrackingStatusRow(
//                            icon  = { Icon(Icons.Rounded.LocationOn, null, tint = if (isLocationEnabled) SemanticGreen else SemanticRed, modifier = Modifier.size(18.dp)) },
//                            label = "Location Enabled",
//                            isOk  = isLocationEnabled
//                        )
//                        TrackingStatusRow(
//                            icon  = { Icon(Icons.Rounded.Wifi, null, tint = if (isWifiEnabled) SemanticGreen else SemanticRed, modifier = Modifier.size(18.dp)) },
//                            label = "Wi-Fi Enabled",
//                            isOk  = isWifiEnabled
//                        )
//                        TrackingStatusRow(
//                            icon  = { Icon(Icons.Rounded.CheckCircle, null, tint = if (visitorStatus == "INSIDE" && canStartWifiScan) SemanticGreen else SemanticRed, modifier = Modifier.size(18.dp)) },
//                            label = when (visitorStatus) {
//                                "APPROVED" -> "Zone Tracking (starts at check-in)"
//                                "INSIDE"   -> "Zone Tracking (active)"
//                                "EXITED"   -> "Zone Tracking (session ended)"
//                                else       -> "Zone Tracking"
//                            },
//                            isOk  = visitorStatus == "INSIDE" && canStartWifiScan
//                        )
//                    }
//                }
//
//                Spacer(Modifier.height(16.dp))
//
//                // ── Permission / enable buttons ───────────────────────────
//                if (visitorStatus == "APPROVED") {
//                    when {
//                        !hasLocationPermission -> {
//                            GlassButton(
//                                text     = "Allow Location Access",
//                                style    = GlassButtonStyle.Primary,
//                                onClick  = { permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION) },
//                                modifier = Modifier.fillMaxWidth()
//                            )
//                            Spacer(Modifier.height(12.dp))
//                        }
//                        !isLocationEnabled -> {
//                            GlassButton(
//                                text    = "Enable Location",
//                                style   = GlassButtonStyle.Primary,
//                                onClick = { context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) },
//                                modifier = Modifier.fillMaxWidth()
//                            )
//                            Spacer(Modifier.height(12.dp))
//                        }
//                        !isWifiEnabled -> {
//                            GlassButton(
//                                text    = "Enable Wi-Fi",
//                                style   = GlassButtonStyle.Primary,
//                                onClick = { context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) },
//                                modifier = Modifier.fillMaxWidth()
//                            )
//                            Spacer(Modifier.height(12.dp))
//                        }
//                    }
//                }
//
//                GlassButton(
//                    text     = "Visit History & QR Code",
//                    style    = GlassButtonStyle.Secondary,
//                    onClick  = { navController.navigate("history") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(Modifier.height(40.dp))
//            }
//        }
//    }
//}



//package com.example.demo.screens
//
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.location.LocationManager
//import android.net.wifi.WifiManager
//import android.os.Build
//import android.provider.Settings
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.annotation.RequiresApi
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.core.RepeatMode
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.infiniteRepeatable
//import androidx.compose.animation.core.rememberInfiniteTransition
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.animation.slideInHorizontally
//import androidx.compose.animation.slideOutHorizontally
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.rounded.CheckCircle
//import androidx.compose.material.icons.rounded.LocationOn
//import androidx.compose.material.icons.rounded.Wifi
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.drawWithContent
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleEventObserver
//import androidx.lifecycle.compose.LocalLifecycleOwner
//import androidx.navigation.NavController
//import com.example.demo.R
//import com.example.demo.ui.components.GlassButton
//import com.example.demo.ui.components.GlassButtonStyle
//import com.example.demo.ui.components.GlossyDivider
//import com.example.demo.ui.components.LiquidGlassBackground
//import com.example.demo.ui.components.LiquidGlassCard
//import com.example.demo.ui.components.RadarWidget
//import com.example.demo.ui.components.SectionLabel
//import com.example.demo.ui.components.StatusBadge
//import com.example.demo.ui.components.TrackingStatusRow
//import com.example.demo.ui.theme.BgDeep
//import com.example.demo.ui.theme.SemanticGreen
//import com.example.demo.ui.theme.SemanticRed
//import com.example.demo.ui.theme.SemanticBlue
//import com.example.demo.ui.theme.TextPrimary
//import com.example.demo.ui.theme.TextSecondary
//import com.example.demo.ui.theme.TextTertiary
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun Approvedscreen(navController: NavController) {
//
//    val context         = LocalContext.current
//    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//    val wifiManager     = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//    val uid             = FirebaseAuth.getInstance().currentUser?.uid
//
//    var visitorStatus  by remember { mutableStateOf("APPROVED") }
//    var visitDate      by remember { mutableStateOf("") }
//    var currentZone    by remember { mutableStateOf("") }
//    var lastZoneUpdate by remember { mutableStateOf("") }
//
//    // ── Real-time Firebase listener ──────────────────────────────────────────
//    DisposableEffect(uid) {
//        if (uid == null) return@DisposableEffect onDispose {}
//        val query = FirebaseDatabase.getInstance()
//            .getReference("visitorRequests")
//            .orderByChild("uid").equalTo(uid)
//
//        val listener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val latest = snapshot.children.maxByOrNull {
//                    it.child("timestamp").getValue(Long::class.java) ?: 0L
//                } ?: return
//                visitorStatus  = latest.child("status").getValue(String::class.java) ?: return
//                visitDate      = latest.child("visitDate").getValue(String::class.java) ?: ""
//                val zone       = latest.child("currentZone").getValue(String::class.java) ?: ""
//                val zoneTs     = latest.child("lastZoneUpdate").getValue(Long::class.java)
//                currentZone    = if (zone == "Unknown Zone") "" else zone
//                lastZoneUpdate = if (zoneTs != null && zoneTs > 0)
//                    java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
//                        .format(java.util.Date(zoneTs)) else ""
//            }
//            override fun onCancelled(error: DatabaseError) {}
//        }
//        query.addValueEventListener(listener)
//        onDispose { query.removeEventListener(listener) }
//    }
//
//    // ── Permission states ────────────────────────────────────────────────────
//    var hasLocationPermission by remember {
//        mutableStateOf(
//            ContextCompat.checkSelfPermission(
//                context, android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        )
//    }
//    var hasNotificationPermission by remember {
//        mutableStateOf(
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                ContextCompat.checkSelfPermission(
//                    context, android.Manifest.permission.POST_NOTIFICATIONS
//                ) == PackageManager.PERMISSION_GRANTED
//            else true
//        )
//    }
//    var isLocationEnabled by remember {
//        mutableStateOf(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
//    }
//    var isWifiEnabled by remember { mutableStateOf(wifiManager.isWifiEnabled) }
//
//    val permissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { granted -> hasLocationPermission = granted }
//
//    val notificationPermissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { granted -> hasNotificationPermission = granted }
//
//    val canStartWifiScan = hasLocationPermission && isLocationEnabled && isWifiEnabled
//
//    val lifecycleOwner = LocalLifecycleOwner.current
//    DisposableEffect(lifecycleOwner) {
//        val observer = LifecycleEventObserver { _, event ->
//            if (event == Lifecycle.Event.ON_RESUME) {
//                hasLocationPermission = ContextCompat.checkSelfPermission(
//                    context, android.Manifest.permission.ACCESS_FINE_LOCATION
//                ) == PackageManager.PERMISSION_GRANTED
//                isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                isWifiEnabled     = wifiManager.isWifiEnabled
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    hasNotificationPermission = ContextCompat.checkSelfPermission(
//                        context, android.Manifest.permission.POST_NOTIFICATIONS
//                    ) == PackageManager.PERMISSION_GRANTED
//                }
//            }
//        }
//        lifecycleOwner.lifecycle.addObserver(observer)
//        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
//    }
//
//    DisposableEffect(visitorStatus, canStartWifiScan) {
//        when {
//            visitorStatus == "INSIDE" && canStartWifiScan && uid != null -> {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
//                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
//                }
//                context.startForegroundService(
//                    Intent(context, WifiTrackingService::class.java).apply { putExtra("uid", uid) }
//                )
//            }
//            visitorStatus == "EXITED" -> {
//                context.stopService(Intent(context, WifiTrackingService::class.java))
//            }
//        }
//        onDispose {}
//    }
//
//    // ── Pulsing alpha for the title ──────────────────────────────────────────
//    val pulsAlpha by rememberInfiniteTransition(label = "pulse").animateFloat(
//        initialValue  = 0.6f,
//        targetValue   = 1f,
//        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
//        label         = "alpha"
//    )
//
//    // ── Status-driven tint colour for the image overlay ─────────────────────
//    // Each status gets its own colour so the screen "feels" different
//    val statusTint: Color = when (visitorStatus) {
//        "INSIDE"  -> SemanticBlue.copy(alpha = 0.22f)
//        "EXITED"  -> Color(0xFF8B8BFF).copy(alpha = 0.18f)  // soft indigo for completed
//        else      -> SemanticGreen.copy(alpha = 0.20f)       // green for APPROVED
//    }
//
//    // ── Status icon + emoji shown in the hero band ───────────────────────────
//    val statusEmoji = when (visitorStatus) {
//        "INSIDE"  -> "📡"
//        "EXITED"  -> "✅"
//        else      -> "✅"
//    }
//
//    var visible by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { visible = true }
//
//    // ── Root: three-layer Box ────────────────────────────────────────────────
//    // Layer 1 — background image (top 32% of screen)
//    // Layer 2 — LiquidGlassBackground ambient blobs (full screen, transparent base)
//    // Layer 3 — AnimatedVisibility scrollable content
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        // ── LAYER 1: Hero image band ─────────────────────────────────────────
//        // Narrow top band — just enough to be scenic without
//        // fighting the cards below. Crop to show the lobby scene.
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight(0.32f)           // 32% of screen — tight band
//                .align(Alignment.TopCenter)
//        ) {
//            Image(
//                painter            = painterResource(id = R.drawable.bg_smartgate),
//                contentDescription = null,
//                contentScale       = ContentScale.Crop,
//                modifier           = Modifier.fillMaxSize()
//            )
//
//            // Status-coloured tint overlay — tints the image to match
//            // the visitor's current status (green = approved, cyan = inside)
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(statusTint)
//            )
//
//            // Dark gradient at bottom — fades image into the app background
//            // so there's no hard edge between image and cards
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .drawWithContent {
//                        drawContent()
//                        drawRect(
//                            brush = Brush.verticalGradient(
//                                colorStops = arrayOf(
//                                    0.00f to Color.Transparent,
//                                    0.25f to Color.Transparent,
//                                    0.65f to BgDeep.copy(alpha = 0.5f),
//                                    0.85f to BgDeep.copy(alpha = 0.88f),
//                                    1.00f to BgDeep
//                                )
//                            )
//                        )
//                    }
//            )
//
//            // Status badge floats at the bottom of the image band,
//            // centered — acts as the "headline" emerging from the scene
//            Column(
//                modifier            = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(bottom = 14.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.spacedBy(6.dp)
//            ) {
//                // Big emoji icon — clean, no Lottie conflict
//                Box(
//                    modifier = Modifier
//                        .size(56.dp)
//                        .clip(CircleShape)
//                        .background(
//                            when (visitorStatus) {
//                                "INSIDE" -> SemanticBlue.copy(alpha = 0.25f)
//                                "EXITED" -> Color(0xFF8B8BFF).copy(alpha = 0.20f)
//                                else     -> SemanticGreen.copy(alpha = 0.22f)
//                            }
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(text = statusEmoji, fontSize = 26.sp)
//                }
//
//                StatusBadge(status = visitorStatus)
//            }
//        }
//
//        // ── LAYER 2: Ambient blobs ───────────────────────────────────────────
//        LiquidGlassBackground(modifier = Modifier.fillMaxSize()) {
//
//            // ── LAYER 3: Scrollable content ──────────────────────────────────
//            AnimatedVisibility(
//                visible = visible,
//                enter   = slideInHorizontally { it } + fadeIn(),
//                exit    = slideOutHorizontally { it } + fadeOut()
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .verticalScroll(rememberScrollState())
//                        .padding(horizontal = 24.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//
//                    // Spacer that clears the image band so content starts
//                    // just below the hero — no overlap
//                    Spacer(Modifier.height(195.dp))
//
//                    // ── Title block ───────────────────────────────────────
//                    Text(
//                        text = when (visitorStatus) {
//                            "INSIDE"  -> "You're Inside Campus"
//                            "EXITED"  -> "Visit Complete"
//                            else      -> "Request Approved!"
//                        },
//                        color      = when (visitorStatus) {
//                            "INSIDE" -> SemanticBlue.copy(alpha = pulsAlpha)
//                            "EXITED" -> Color(0xFFB0B0FF).copy(alpha = pulsAlpha)
//                            else     -> SemanticGreen.copy(alpha = pulsAlpha)
//                        },
//                        fontSize   = 26.sp,
//                        fontWeight = FontWeight.Bold,
//                        textAlign  = TextAlign.Center
//                    )
//
//                    Spacer(Modifier.height(4.dp))
//
//                    // Visit date pill
//                    if (visitDate.isNotEmpty()) {
//                        Box(
//                            modifier = Modifier
//                                .clip(RoundedCornerShape(100.dp))
//                                .background(Color.White.copy(alpha = 0.08f))
//                                .padding(horizontal = 14.dp, vertical = 5.dp)
//                        ) {
//                            Text(
//                                text     = "📅  $visitDate",
//                                color    = TextSecondary,
//                                fontSize = 13.sp
//                            )
//                        }
//                        Spacer(Modifier.height(8.dp))
//                    }
//
//                    Text(
//                        text = when (visitorStatus) {
//                            "APPROVED" -> "Please arrive on your registered date.\nScan the QR at the entrance to check in."
//                            "INSIDE"   -> "Zone tracking is active.\nYour movement is being monitored."
//                            "EXITED"   -> "Thank you for your visit.\nWe hope to see you again."
//                            else       -> "Your visit is scheduled."
//                        },
//                        color      = TextSecondary,
//                        fontSize   = 14.sp,
//                        textAlign  = TextAlign.Center,
//                        lineHeight = 21.sp
//                    )
//
//                    Spacer(Modifier.height(20.dp))
//
//                    // ── Live Zone card (only INSIDE) ──────────────────────
//                    if (visitorStatus == "INSIDE") {
//                        LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
//                            Column(
//                                modifier            = Modifier.padding(20.dp),
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                SectionLabel(text = "Current Location")
//                                Spacer(Modifier.height(16.dp))
//                                RadarWidget(zoneName = currentZone)
//                                if (lastZoneUpdate.isNotEmpty()) {
//                                    Spacer(Modifier.height(6.dp))
//                                    Text(
//                                        text     = "Last updated at $lastZoneUpdate",
//                                        color    = TextTertiary,
//                                        fontSize = 11.sp
//                                    )
//                                }
//                                if (currentZone.isEmpty()) {
//                                    Spacer(Modifier.height(4.dp))
//                                    Text(
//                                        text      = "Move closer to a Wi-Fi access point",
//                                        color     = TextTertiary,
//                                        fontSize  = 11.sp,
//                                        textAlign = TextAlign.Center
//                                    )
//                                }
//                            }
//                        }
//                        Spacer(Modifier.height(12.dp))
//                    }
//
//                    // ── Tracking setup card ───────────────────────────────
//                    LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
//                        Column(modifier = Modifier.padding(20.dp)) {
//                            SectionLabel(text = "Tracking Setup")
//                            Spacer(Modifier.height(10.dp))
//                            GlossyDivider()
//                            Spacer(Modifier.height(6.dp))
//
//                            TrackingStatusRow(
//                                icon  = {
//                                    Icon(
//                                        Icons.Rounded.LocationOn, null,
//                                        tint     = if (hasLocationPermission) SemanticGreen else SemanticRed,
//                                        modifier = Modifier.size(18.dp)
//                                    )
//                                },
//                                label = "Location Permission",
//                                isOk  = hasLocationPermission
//                            )
//                            TrackingStatusRow(
//                                icon  = {
//                                    Icon(
//                                        Icons.Rounded.LocationOn, null,
//                                        tint     = if (isLocationEnabled) SemanticGreen else SemanticRed,
//                                        modifier = Modifier.size(18.dp)
//                                    )
//                                },
//                                label = "Location Enabled",
//                                isOk  = isLocationEnabled
//                            )
//                            TrackingStatusRow(
//                                icon  = {
//                                    Icon(
//                                        Icons.Rounded.Wifi, null,
//                                        tint     = if (isWifiEnabled) SemanticGreen else SemanticRed,
//                                        modifier = Modifier.size(18.dp)
//                                    )
//                                },
//                                label = "Wi-Fi Enabled",
//                                isOk  = isWifiEnabled
//                            )
//                            TrackingStatusRow(
//                                icon  = {
//                                    Icon(
//                                        Icons.Rounded.CheckCircle, null,
//                                        tint     = if (visitorStatus == "INSIDE" && canStartWifiScan)
//                                            SemanticGreen else SemanticRed,
//                                        modifier = Modifier.size(18.dp)
//                                    )
//                                },
//                                label = when (visitorStatus) {
//                                    "APPROVED" -> "Zone Tracking (starts at check-in)"
//                                    "INSIDE"   -> "Zone Tracking (active)"
//                                    "EXITED"   -> "Zone Tracking (session ended)"
//                                    else       -> "Zone Tracking"
//                                },
//                                isOk  = visitorStatus == "INSIDE" && canStartWifiScan
//                            )
//                        }
//                    }
//
//                    Spacer(Modifier.height(14.dp))
//
//                    // ── Permission / enable buttons ───────────────────────
//                    if (visitorStatus == "APPROVED") {
//                        when {
//                            !hasLocationPermission -> {
//                                GlassButton(
//                                    text     = "Allow Location Access",
//                                    style    = GlassButtonStyle.Primary,
//                                    onClick  = {
//                                        permissionLauncher.launch(
//                                            android.Manifest.permission.ACCESS_FINE_LOCATION
//                                        )
//                                    },
//                                    modifier = Modifier.fillMaxWidth()
//                                )
//                                Spacer(Modifier.height(10.dp))
//                            }
//                            !isLocationEnabled -> {
//                                GlassButton(
//                                    text     = "Enable Location",
//                                    style    = GlassButtonStyle.Primary,
//                                    onClick  = {
//                                        context.startActivity(
//                                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                                        )
//                                    },
//                                    modifier = Modifier.fillMaxWidth()
//                                )
//                                Spacer(Modifier.height(10.dp))
//                            }
//                            !isWifiEnabled -> {
//                                GlassButton(
//                                    text     = "Enable Wi-Fi",
//                                    style    = GlassButtonStyle.Primary,
//                                    onClick  = {
//                                        context.startActivity(
//                                            Intent(Settings.ACTION_WIFI_SETTINGS)
//                                        )
//                                    },
//                                    modifier = Modifier.fillMaxWidth()
//                                )
//                                Spacer(Modifier.height(10.dp))
//                            }
//                        }
//                    }
//
//                    GlassButton(
//                        text     = "Visit History & QR Code",
//                        style    = GlassButtonStyle.Secondary,
//                        onClick  = { navController.navigate("history") },
//                        modifier = Modifier.fillMaxWidth()
//                    )
//
//                    Spacer(Modifier.height(40.dp))
//                }
//            }
//        }
//    }
//}



package com.example.demo.screens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.demo.R
import com.example.demo.ui.components.GlassButton
import com.example.demo.ui.components.GlassButtonStyle
import com.example.demo.ui.components.GlossyDivider
import com.example.demo.ui.components.LiquidGlassBackground
import com.example.demo.ui.components.LiquidGlassCard
import com.example.demo.ui.components.RadarWidget
import com.example.demo.ui.components.SectionLabel
import com.example.demo.ui.components.StatusBadge
import com.example.demo.ui.components.TrackingStatusRow
import com.example.demo.ui.theme.BgDeep
import com.example.demo.ui.theme.SemanticGreen
import com.example.demo.ui.theme.SemanticRed
import com.example.demo.ui.theme.TextPrimary
import com.example.demo.ui.theme.TextSecondary
import com.example.demo.ui.theme.TextTertiary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Approvedscreen(navController: NavController) {

    val context         = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val wifiManager     = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val uid             = FirebaseAuth.getInstance().currentUser?.uid

    var visitorStatus  by remember { mutableStateOf("APPROVED") }
    var visitDate      by remember { mutableStateOf("") }
    var currentZone    by remember { mutableStateOf("") }
    var lastZoneUpdate by remember { mutableStateOf("") }

    // Real-time Firebase listener
    DisposableEffect(uid) {
        if (uid == null) return@DisposableEffect onDispose {}
        val query = FirebaseDatabase.getInstance()
            .getReference("visitorRequests")
            .orderByChild("uid").equalTo(uid)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val latest = snapshot.children.maxByOrNull {
                    it.child("timestamp").getValue(Long::class.java) ?: 0L
                } ?: return
                visitorStatus  = latest.child("status").getValue(String::class.java) ?: return
                visitDate      = latest.child("visitDate").getValue(String::class.java) ?: ""
                val zone       = latest.child("currentZone").getValue(String::class.java) ?: ""
                val zoneTs     = latest.child("lastZoneUpdate").getValue(Long::class.java)
                currentZone    = if (zone == "Unknown Zone") "" else zone
                lastZoneUpdate = if (zoneTs != null && zoneTs > 0)
                    java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                        .format(java.util.Date(zoneTs)) else ""
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        query.addValueEventListener(listener)
        onDispose { query.removeEventListener(listener) }
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }
    var isLocationEnabled by remember {
        mutableStateOf(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
    }
    var isWifiEnabled by remember { mutableStateOf(wifiManager.isWifiEnabled) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasLocationPermission = granted }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasNotificationPermission = granted }

    val canStartWifiScan = hasLocationPermission && isLocationEnabled && isWifiEnabled

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasLocationPermission = ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isWifiEnabled     = wifiManager.isWifiEnabled
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    hasNotificationPermission = ContextCompat.checkSelfPermission(
                        context, android.Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(visitorStatus, canStartWifiScan) {
        when {
            visitorStatus == "INSIDE" && canStartWifiScan && uid != null -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
                context.startForegroundService(
                    Intent(context, WifiTrackingService::class.java).apply { putExtra("uid", uid) }
                )
            }
            visitorStatus == "EXITED" -> {
                context.stopService(Intent(context, WifiTrackingService::class.java))
            }
        }
        onDispose {}
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

//    // Lottie animation
//    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.three))
//    val progress    by animateLottieCompositionAsState(
//        composition = composition,
//        iterations  = LottieConstants.IterateForever,
//        speed       = 0.7f
//    )

    // Pulse for "Approved" title
    val pulsAlpha by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue  = 0.5f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label         = "alpha"
    )

    val bgColor = BgDeep

    // ── Root layer: image drawn first ──────────────────────────────────────
    Box(modifier = Modifier
        .fillMaxSize()
        .background(bgColor)
    ) {

        // ── Layer 1: Hero image — top 55%, gradient fades into bg ──────────
        Image(
            painter            = painterResource(id = R.drawable.approve_bg),
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.55f)
                .align(Alignment.TopCenter)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.00f to Color.Transparent,
                                0.35f to Color.Transparent,
                                0.70f to bgColor.copy(alpha = 0.60f),
                                0.88f to bgColor.copy(alpha = 0.88f),
                                1.00f to bgColor
                            )
                        )
                    )
                }
        )

        // ── Layer 1.5: Emerald Stardust Animation ───────────────────────────
        EmeraldStardustBackground()

        // ── Layer 2: Glass blobs + scrollable content ───────────────────────
        Box(modifier = Modifier.fillMaxSize()) {
            LiquidGlassBackground(
                modifier        = Modifier.fillMaxSize(),
                backgroundColor = Color.Transparent
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter   = slideInHorizontally { it } + fadeIn(),
                    exit    = slideOutHorizontally { it } + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Push content down so image is visible above and text doesn't overlap faces
                        Spacer(Modifier.height(390.dp))

                        // ── Lottie animation ──────────────────────────────────
//                        LottieAnimation(
//                            composition = composition,
//                            progress    = { progress },
//                            modifier    = Modifier.size(160.dp)
//                        )

//                        Spacer(Modifier.height(8.dp))

                        // ── Status badge ──────────────────────────────────────
                        StatusBadge(status = visitorStatus)

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text       = "Request Approved!",
                            color      = SemanticGreen.copy(alpha = pulsAlpha),
                            fontSize   = 26.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (visitDate.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text     = "Visit Date: $visitDate",
                                color    = TextSecondary,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = when (visitorStatus) {
                                "APPROVED" -> "Please arrive on your registered date.\nScan the QR at the entrance to check in."
                                "INSIDE"   -> "📡 You are inside campus.\nZone tracking is now active."
                                "EXITED"   -> "✅ Your visit is complete.\nThank you for visiting!"
                                else       -> "Your visit is scheduled."
                            },
                            color      = TextSecondary,
                            fontSize   = 14.sp,
                            textAlign  = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(Modifier.height(20.dp))

                        // ── Live Zone card — only when INSIDE ─────────────────
                        if (visitorStatus == "INSIDE") {
                            GlassButton(
                                text     = "Get Live Zones",
                                style    = GlassButtonStyle.Primary,
                                onClick  = { navController.navigate("live_zones") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(14.dp))
                        }

                        // ── Tracking setup card ───────────────────────────────
                        LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                SectionLabel(text = "Tracking Setup")
                                Spacer(Modifier.height(12.dp))
                                GlossyDivider()
                                Spacer(Modifier.height(8.dp))

                                TrackingStatusRow(
                                    icon  = { Icon(Icons.Rounded.LocationOn, null, tint = if (hasLocationPermission) SemanticGreen else SemanticRed, modifier = Modifier.size(18.dp)) },
                                    label = "Location Permission",
                                    isOk  = hasLocationPermission
                                )
                                TrackingStatusRow(
                                    icon  = { Icon(Icons.Rounded.LocationOn, null, tint = if (isLocationEnabled) SemanticGreen else SemanticRed, modifier = Modifier.size(18.dp)) },
                                    label = "Location Enabled",
                                    isOk  = isLocationEnabled
                                )
                                TrackingStatusRow(
                                    icon  = { Icon(Icons.Rounded.Wifi, null, tint = if (isWifiEnabled) SemanticGreen else SemanticRed, modifier = Modifier.size(18.dp)) },
                                    label = "Wi-Fi Enabled",
                                    isOk  = isWifiEnabled
                                )
                                TrackingStatusRow(
                                    icon  = { Icon(Icons.Rounded.CheckCircle, null, tint = if (visitorStatus == "INSIDE" && canStartWifiScan) SemanticGreen else SemanticRed, modifier = Modifier.size(18.dp)) },
                                    label = when (visitorStatus) {
                                        "APPROVED" -> "Zone Tracking (starts at check-in)"
                                        "INSIDE"   -> "Zone Tracking (active)"
                                        "EXITED"   -> "Zone Tracking (session ended)"
                                        else       -> "Zone Tracking"
                                    },
                                    isOk  = visitorStatus == "INSIDE" && canStartWifiScan
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // ── Permission / enable buttons ───────────────────────
                        if (visitorStatus == "APPROVED") {
                            when {
                                !hasLocationPermission -> {
                                    GlassButton(
                                        text     = "Allow Location Access",
                                        style    = GlassButtonStyle.Primary,
                                        onClick  = { permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(Modifier.height(12.dp))
                                }
                                !isLocationEnabled -> {
                                    GlassButton(
                                        text     = "Enable Location",
                                        style    = GlassButtonStyle.Primary,
                                        onClick  = { context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(Modifier.height(12.dp))
                                }
                                !isWifiEnabled -> {
                                    GlassButton(
                                        text     = "Enable Wi-Fi",
                                        style    = GlassButtonStyle.Primary,
                                        onClick  = { context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(Modifier.height(12.dp))
                                }
                            }
                        }

                        GlassButton(
                            text     = "Visit History & QR Code",
                            style    = GlassButtonStyle.Secondary,
                            onClick  = { navController.navigate("history") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EmeraldStardustBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "stardust")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing)
        ),
        label = "time"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val particles = remember {
        List(40) {
            Triple(
                Math.random().toFloat(),
                (Math.random() * 4f + 2f).toFloat(),
                Math.random().toFloat() * 2f + 1f
            ) to Math.random().toFloat()
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        val emerald = Color(0xFF00FF88)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    emerald.copy(alpha = glowAlpha * 0.2f),
                    emerald.copy(alpha = glowAlpha * 0.6f)
                ),
                startY = height * 0.35f,
                endY = height
            )
        )
        
        particles.forEach { (props, phase) ->
            val (relX, pSize, speed) = props
            val x = relX * width
            
            val rawY = phase + (time * speed)
            val wrappedY = 1f - (rawY % 1f)
            val y = wrappedY * height
            
            val alpha = when {
                wrappedY > 0.8f -> (1f - wrappedY) / 0.2f
                wrappedY < 0.2f -> wrappedY / 0.2f
                else -> 1f
            }
            
            if (alpha > 0f) {
                drawCircle(
                    color = emerald.copy(alpha = alpha * 0.6f),
                    radius = pSize,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = Color.White.copy(alpha = alpha * 0.8f),
                    radius = pSize * 0.4f,
                    center = Offset(x, y)
                )
            }
        }
    }
}