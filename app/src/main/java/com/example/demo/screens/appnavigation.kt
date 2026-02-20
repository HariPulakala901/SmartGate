package com.example.demo.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun appnavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "intro", modifier = modifier){

        composable("intro") {
            mainscreen(navController)
        }
        composable(
            route = "register?edit={edit}",
            arguments = listOf(
                navArgument("edit") {
                    defaultValue = "false"
                }
            )
        ) { backStackEntry ->
            val isEdit = backStackEntry.arguments?.getString("edit") == "true"
            registerscreen(navController, isEdit = isEdit)
        }
        composable("login") { 
            LoginScreen(navController)
        }
        composable("pending") {
            pendingscreen(navController)
        }
        composable("rejected") {
            rejectingscreen(navController)
        }
        composable("approved") {
            approvedscreen(navController)
        }
        composable("history") {
            historyscreen(navController)
        }
        composable("qr/{uid}/{requestId}") { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid")!!
            val requestId = backStackEntry.arguments?.getString("requestId")!!
            qrscreen(uid, requestId)
        }
    }
}
