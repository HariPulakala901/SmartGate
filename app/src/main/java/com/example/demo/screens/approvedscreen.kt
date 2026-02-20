package com.example.demo.screens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.demo.R
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.DisposableEffect
import com.google.firebase.auth.FirebaseAuth

@Composable
fun approvedscreen(navController: NavController) {

    val context = LocalContext.current
    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }



    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val wifiScanHelper = remember { WifiScanHelper(context) }



    var isLocationEnabled by remember {
        mutableStateOf(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
    }

    var isWifiEnabled by remember {
        mutableStateOf(wifiManager.isWifiEnabled)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

//    val isLocationEnabled =
//        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//
//    val isWifiEnabled = wifiManager.isWifiEnabled

    val canStartWifiScan =
        hasLocationPermission && isLocationEnabled && isWifiEnabled



    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {

                hasLocationPermission =
                    ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                isLocationEnabled =
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                isWifiEnabled =
                    wifiManager.isWifiEnabled
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }





    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.three)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 0.7f
    )

    val alpha by rememberInfiniteTransition().animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        )
    )

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
        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(300.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Request Approved",
                    color = Color.White.copy(alpha = alpha),
                    fontSize = 28.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ---------- LOCATION PERMISSION ----------
                if (!hasLocationPermission) {
                    GradientButton(
                        text = "Allow Location Access",
                        onClick = {
                            permissionLauncher.launch(
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                    )
                }

                // ---------- LOCATION ENABLE ----------
                if (hasLocationPermission && !isLocationEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    GradientButton(
                        text = "Enable Location",
                        onClick = {
                            context.startActivity(
                                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                    )
                }

                // ---------- WIFI ENABLE ----------
                if (hasLocationPermission && isLocationEnabled && !isWifiEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    GradientButton(
                        text = "Enable Wi-Fi",
                        onClick = {
                            context.startActivity(
                                Intent(Settings.ACTION_WIFI_SETTINGS)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                    )
                }

                // ---------- READY ----------
                DisposableEffect(canStartWifiScan) {
                    if (canStartWifiScan && uid != null) {
                        wifiScanHelper.startScanning(uid)
                    }
                    onDispose {
                        wifiScanHelper.stopScanning()
                    }
                }

                if (canStartWifiScan && uid != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "📡 Wi-Fi zone tracking is active",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                GradientButton(
                    text = "Visit History",
                    onClick = { navController.navigate("history") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                )
            }
        }
    }
}