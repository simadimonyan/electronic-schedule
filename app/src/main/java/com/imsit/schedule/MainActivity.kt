package com.imsit.schedule

import android.app.Application
import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.imsit.schedule.events.DataEvent
import com.imsit.schedule.ui.components.CustomAppBar
import com.imsit.schedule.ui.navigation.AppNavGraph
import com.imsit.schedule.ui.theme.background
import com.imsit.schedule.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val viewModel: MainViewModel = hiltViewModel()

            // true - only once | does not start when recomposes
            LaunchedEffect(true) {
                viewModel.handleEvent(DataEvent.FetchData)
                viewModel.handleEvent(DataEvent.SetupCacheUpdater)
            }

            Box(modifier = Modifier
                .fillMaxSize()
                .background(background)) {
                AppNavGraph(navController = navController)
                CustomAppBar(viewModel, navController)
            }
        }
    }

}


