package com.imsit.schedule.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.imsit.schedule.ui.screens.GroupScreen
import com.imsit.schedule.ui.screens.ScheduleScreen
import com.imsit.schedule.viewmodels.GroupScreenViewModel
import kotlinx.serialization.Serializable

@Serializable
object GroupScreen

@Serializable
object ScheduleScreen

@SuppressLint("RestrictedApi")
@Composable
fun AppNavGraph(navController: NavHostController, viewModel: GroupScreenViewModel) {

    NavHost(
        navController = navController,
        startDestination = GroupScreen,
    ) {
        composable<GroupScreen>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                )
            }
        ) {
            GroupScreen(viewModel)
        }
        composable<ScheduleScreen>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400, easing = LinearEasing)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400, easing = LinearEasing)
                )
            }
        ){
            ScheduleScreen()
        }
    }
}


