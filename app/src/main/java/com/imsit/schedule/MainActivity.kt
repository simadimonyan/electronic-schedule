package com.imsit.schedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.imsit.schedule.ui.components.CustomAppBar
import com.imsit.schedule.ui.navigation.AppNavGraph
import com.imsit.schedule.viewmodels.GroupScreenViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val viewModel: GroupScreenViewModel = viewModel()
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                viewModel.restoreCache(context)
                viewModel.fetchData(context)
                viewModel.setupCacheUpdater(context)
            }

            val navController = rememberNavController()
            AppNavGraph(navController = navController, viewModel)
            CustomAppBar(navController)
        }
    }

}


