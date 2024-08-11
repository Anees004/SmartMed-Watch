//// NavGraph.kt
//package com.medbytes.smartwatch.presentation.views
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavHost
//import androidx.navigation.NavHostController
//import com.medbytes.smartwatch.presentation.viewmodels.AuthViewModel
//
//@Composable
//fun NavGraph(navController: NavHostController, authViewModel: AuthViewModel) {
//    NavHost(navController = navController, startDestination = "login") {
//        composable("login") {
//            LoginScreen(authViewModel, navController)
//        }
//        composable("home") {
//            HomeScreen()
//        }
//    }
//}
