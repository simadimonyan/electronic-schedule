package com.mycollege.schedule.presentation.screens.start.components

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mycollege.schedule.presentation.navigation.AppPager
import com.mycollege.schedule.presentation.ui.components.CustomAppBar
import com.mycollege.schedule.presentation.screens.groups.data.GroupsViewModel
import com.mycollege.schedule.presentation.screens.schedule.data.ScheduleViewModel
import com.mycollege.schedule.presentation.screens.start.data.StartViewModel

@Composable
fun StartScreen(
    viewModel: StartViewModel = hiltViewModel(),
    globalGraph: NavHostController,
    groupsViewModel: GroupsViewModel,
    scheduleViewModel: ScheduleViewModel
) {
    val barVisibility by viewModel.shared.navigationInvisibility.collectAsState()
    val screenIndex by viewModel.shared.screenIndex.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = if (screenIndex == 1) 1 else 0
    ) { 2 }

    AppPager(
        pagerState,
        groupsViewModel = groupsViewModel,
        scheduleViewModel = scheduleViewModel,
        globalNavHostController = globalGraph
    )

    if (!barVisibility) {
        CustomAppBar(groupsViewModel, pagerState)
    }

}