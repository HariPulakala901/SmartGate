package com.example.demo.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Appnavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context       = LocalContext.current

    // Check SharedPreferences — true means onboarding already seen
    val hasSeenOnboarding = remember {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getBoolean("onboarding_complete", false)
    }

    // Start at onboarding if first launch, otherwise go straight to intro (home)
    val startDestination = if (hasSeenOnboarding) "intro" else "onboarding"

    NavHost(navController, startDestination = startDestination, modifier = modifier) {

        composable("intro") {
            Mainscreen(navController)
        }

        composable("onboarding") {
            OnboardingScreen(
                navController     = navController,
                onOnboardingComplete = {
                    // Mark onboarding as done so it never shows again
                    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("onboarding_complete", true)
                        .apply()
                    navController.navigate("intro") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route     = "register?edit={edit}",
            arguments = listOf(navArgument("edit") { defaultValue = "false" })
        ) { backStackEntry ->
            val isEdit = backStackEntry.arguments?.getString("edit") == "true"
            Registerscreen(navController, isEdit = isEdit)
        }

        composable("login") {
            LoginScreen(navController)
        }

        composable("pending") {
            Pendingscreen(navController)
        }

        composable("rejected") {
            Rejectingscreen(navController)
        }

        composable("approved") {
            Approvedscreen(navController)
        }

        composable("history") {
            historyscreen(navController)
        }

        composable("flagged") {
            Flaggedscreen(navController)
        }

        composable("live_zones") {
            LiveZonesScreen(navController)
        }

        composable("qr/{uid}/{requestId}") { backStackEntry ->
            val uid       = backStackEntry.arguments?.getString("uid") ?: ""
            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            Qrscreen(uid = uid, requestId = requestId, navController = navController)
        }
    }
}