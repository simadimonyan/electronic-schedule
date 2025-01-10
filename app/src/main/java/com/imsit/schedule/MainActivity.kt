package com.imsit.schedule

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.imsit.schedule.data.cache.CacheManager
import com.imsit.schedule.events.DataEvent
import com.imsit.schedule.ui.navigation.AddNavGraph
import com.imsit.schedule.ui.theme.background
import com.imsit.schedule.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch

@HiltAndroidApp
class App : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val viewModel: MainViewModel = hiltViewModel()
            val scope = rememberCoroutineScope()
            val navController = rememberNavController()

            // true - only once | does not start when recomposes
            LaunchedEffect(true) {
                scope.launch {
                    viewModel.handleEvent(DataEvent.RestoreCache)
                    viewModel.handleEvent(DataEvent.FetchData)
                    viewModel.handleEvent(DataEvent.SetupCacheUpdater)
                }
            }

            // hide system ui navigation panel
            WindowCompat.setDecorFitsSystemWindows(window, false)

            val insetsController = WindowInsetsControllerCompat(window, window.decorView)
            insetsController.hide(WindowInsetsCompat.Type.navigationBars())
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            Box(modifier = Modifier
                .fillMaxSize()
                .background(background)) {
                val context = LocalContext.current
                val cacheManager = CacheManager(context)
                viewModel.shared.updatingFirstStartup(cacheManager.isFirstStartup())

                AddNavGraph(navController = navController, mainViewModel = viewModel)
            }
        }
    }

}


