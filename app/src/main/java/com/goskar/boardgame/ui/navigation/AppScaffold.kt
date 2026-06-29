package com.goskar.boardgame.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.goskar.boardgame.ui.home.HomeScreen
import com.goskar.boardgame.ui.theme.BgBottomNavBar

//@Composable
//fun AppScaffold(
//    navController: NavHostController,
//    currentRoute: String?,
//) {
//    Scaffold(
//        bottomBar = {
//            BgBottomNavBar(
//                items    = bottomNavItems,
//                selected = ...,
//            onSelect = { navController.navigate(...) }
//            )
//        }
//    ) { innerPadding ->
//        NavHost(
//            navController    = navController,
//            startDestination = "home",
//            modifier         = Modifier.padding(innerPadding)
//        ) {
//            composable("home")       { HomeScreen() }
//            composable("collection") { CollectionScreen() }
//            composable("history")    { HistoryScreen() }
//            composable("players")    { PlayersScreen() }
//        }
//    }
//}