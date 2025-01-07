package com.imsit.schedule.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.imsit.schedule.ui.screens.GroupScreen
import com.imsit.schedule.ui.screens.RouteScreen
import com.imsit.schedule.ui.screens.ScheduleScreen
import com.imsit.schedule.ui.screens.Settings
import com.imsit.schedule.viewmodels.GroupsViewModel
import kotlinx.serialization.Serializable

@Serializable object AppMainRoute

@Serializable object GroupScreen
@Serializable object ScheduleScreen

@Serializable object Route

@Serializable object Settings

@Composable
fun AppNavGraph(globalGraph: NavHostController, navController: NavHostController, groupsViewModel: GroupsViewModel) {

    val screenIndex by groupsViewModel.shared.screenIndex.collectAsState()

    NavHost(
        navController = navController,
        startDestination = AppMainRoute,
    ) {
        navigation<AppMainRoute>(startDestination = if (screenIndex == 1) ScheduleScreen else GroupScreen) {
            composable<GroupScreen>(
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(250)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(250)
                    )
                }
            ) {
                GroupScreen(groupsViewModel)
            }
            composable<ScheduleScreen>(
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(250)

                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(250)
                    )
                }
            ){
                ScheduleScreen(groupsViewModel, globalGraph)
            }
        }
    }
}

@Composable
fun AddNavGraph(navController: NavHostController) {

    val viewModel: GroupsViewModel = hiltViewModel()
    val nestedController = rememberNavController()

    NavHost(navController = navController, startDestination = Route) {
        composable<Route> {
            RouteScreen(hiltViewModel(), navController, nestedController, viewModel)
        }
        composable<Settings>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(
                        durationMillis = 250
                    )
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(
                        durationMillis = 250
                    )
                )
            }
        ) {
            Settings(hiltViewModel(), navController)
        }
    }
}


