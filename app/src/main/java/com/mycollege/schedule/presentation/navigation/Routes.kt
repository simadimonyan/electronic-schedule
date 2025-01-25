package com.mycollege.schedule.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mycollege.schedule.presentation.screens.groups.GroupScreen
import com.mycollege.schedule.presentation.screens.groups.data.GroupsViewModel
import com.mycollege.schedule.presentation.screens.onboarding.OnboardingScreen
import com.mycollege.schedule.presentation.screens.start.components.StartScreen
import com.mycollege.schedule.presentation.screens.schedule.ScheduleScreen
import com.mycollege.schedule.presentation.screens.settings.Settings
import com.mycollege.schedule.presentation.screens.schedule.data.ScheduleViewModel
import com.mycollege.schedule.presentation.screens.start.data.MainViewModel
import com.mycollege.schedule.presentation.screens.start.data.StartViewModel

@Composable
fun AppPager(
    pagerState: PagerState,
    groupsViewModel: GroupsViewModel,
    scheduleViewModel: ScheduleViewModel,
    globalNavHostController: NavHostController
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false
    ) { page ->
        when (page) {
            0 -> GroupScreen(groupsViewModel, pagerState)
            1 -> ScheduleScreen(scheduleViewModel, globalNavHostController)
        }
    }
}

@Composable
fun AddNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    groupsViewModel: GroupsViewModel,
    scheduleViewModel: ScheduleViewModel
) {

    val firstStartup by mainViewModel.shared.firstStartup.collectAsState()
    val startViewModel: StartViewModel = hiltViewModel()

    // restore cache event
    startViewModel.init()
    
    NavHost(navController = navController, startDestination = Start) { //if (firstStartup) Onboarding else Start
        composable<Onboarding>(
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(
                        durationMillis = 350
                    )
                )
            }
        ) {
            OnboardingScreen(hiltViewModel())
        }
        composable<Start> {
            StartScreen(startViewModel, navController, groupsViewModel, scheduleViewModel)
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


