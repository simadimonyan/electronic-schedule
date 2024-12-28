package com.imsit.schedule.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.imsit.schedule.ui.screens.GroupScreen
import com.imsit.schedule.ui.screens.ScheduleScreen
import com.imsit.schedule.ui.screens.Settings
import com.imsit.schedule.viewmodels.GroupsViewModel
import com.imsit.schedule.viewmodels.ScheduleViewModel
import com.imsit.schedule.viewmodels.SettingsViewModel
import kotlinx.serialization.Serializable

@Serializable
object GroupScreen

@Serializable
object ScheduleScreen

@Serializable
object Settings

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = GroupScreen,
    ) {
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
            val groupsModel: GroupsViewModel = hiltViewModel()
            GroupScreen(groupsModel)
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
            val scheduleModel: ScheduleViewModel = hiltViewModel()
            ScheduleScreen(scheduleModel, navController)
        }
        composable<Settings>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        ) {
            val settingsModel: SettingsViewModel = hiltViewModel()
            Settings(settingsModel, navController)
        }
    }
}


