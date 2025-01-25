package com.mycollege.schedule.presentation.screens.start

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.mycollege.schedule.presentation.screens.start.data.DataEvent
import com.mycollege.schedule.presentation.navigation.AddNavGraph
import com.mycollege.schedule.presentation.screens.groups.data.GroupsViewModel
import com.mycollege.schedule.presentation.screens.schedule.data.ScheduleViewModel
import com.mycollege.schedule.presentation.ui.theme.background
import com.mycollege.schedule.presentation.screens.start.data.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // initializing all required viewModels on app startup
    private val mainViewModel: MainViewModel by viewModels()
    private val groupsViewModel: GroupsViewModel by viewModels()
    private val scheduleViewModel: ScheduleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        groupsViewModel.init()
        scheduleViewModel.init()

        enableEdgeToEdge()
        setContent {
            Box(modifier = Modifier.fillMaxSize().background(background)) {

                val scope = rememberCoroutineScope()
                val navController = rememberNavController()

                // true - only once | does not start when recomposes
                LaunchedEffect(true) {
                    scope.launch {
                        mainViewModel.handleEvent(DataEvent.RestoreCache)
                        mainViewModel.handleEvent(DataEvent.FetchData)
                        mainViewModel.handleEvent(DataEvent.SetupCacheUpdater)
                    }
                }

                // hide system ui navigation panel
                WindowCompat.setDecorFitsSystemWindows(window, false)
                val insetsController = WindowInsetsControllerCompat(window, window.decorView)
                insetsController.hide(WindowInsetsCompat.Type.navigationBars())
                insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

                AddNavGraph(
                    navController = navController,
                    mainViewModel = mainViewModel,
                    groupsViewModel = groupsViewModel,
                    scheduleViewModel = scheduleViewModel
                )

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.destroyNotifications()
    }

}


