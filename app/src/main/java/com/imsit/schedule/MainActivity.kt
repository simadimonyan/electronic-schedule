package com.imsit.schedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.imsit.schedule.ui.components.CustomAppBar
import com.imsit.schedule.ui.navigation.AppNavGraph
import com.imsit.schedule.ui.theme.background
import com.imsit.schedule.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()
            val viewModel: MainViewModel = viewModel()

            val contextState = remember { context }

            LaunchedEffect(Unit) {
                viewModel.restoreCache(contextState)
                viewModel.fetchData(contextState)
                viewModel.setupCacheUpdater(contextState)
            }

            Box(modifier = Modifier.fillMaxSize().background(background)) {
                AppNavGraph(navController = navController, viewModel)
                CustomAppBar(navController)
            }
        }
    }

}


