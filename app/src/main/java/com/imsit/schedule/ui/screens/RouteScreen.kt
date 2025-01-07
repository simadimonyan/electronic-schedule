package com.imsit.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.imsit.schedule.ui.components.CustomAppBar
import com.imsit.schedule.ui.navigation.AppNavGraph
import com.imsit.schedule.ui.navigation.ScheduleScreen
import com.imsit.schedule.viewmodels.GroupsViewModel
import com.imsit.schedule.viewmodels.RouteViewModel

@Composable
fun RouteScreen(
    viewModel: RouteViewModel = hiltViewModel(),
    globalGraph: NavHostController,
    nestedGraph: NavHostController,
    groupsViewModel: GroupsViewModel
) {

    val screenIndex by viewModel.shared.navigationInvisibility.collectAsState()

    AppNavGraph(navController = nestedGraph, globalGraph = globalGraph, groupsViewModel = groupsViewModel)

//    LaunchedEffect(true) {
//
//        if (screenIndex) {
//            nestedGraph.navigate(ScheduleScreen) {
//                popUpTo(nestedGraph.graph.findStartDestination().id) {
//                    saveState = true
//                }
//                launchSingleTop = true
//                restoreState = true
//            }
//        }
//
//    }

    if (!screenIndex) {
        CustomAppBar(groupsViewModel, nestedGraph)
    }

}