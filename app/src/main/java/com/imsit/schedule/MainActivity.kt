package com.imsit.schedule

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.imsit.schedule.ui.screens.SettingMain

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SettingMain()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.cancel(2)
    }
}


