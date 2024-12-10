package com.imsit.schedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imsit.schedule.ui.theme.ScheduleTheme
import com.imsit.schedule.ui.theme.background

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GroupPreview()
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun GroupPreview() {
    ScheduleTheme {
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = background) { innerPadding ->
            Text(
                "Выбор группы",
                modifier = Modifier.padding(innerPadding)
                    .fillMaxWidth().padding(70.dp),
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}