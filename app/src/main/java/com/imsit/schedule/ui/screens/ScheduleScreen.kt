package com.imsit.schedule.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.imsit.schedule.R
import com.imsit.schedule.data.models.DataClasses
import com.imsit.schedule.ui.theme.ScheduleTheme
import com.imsit.schedule.ui.theme.background
import com.imsit.schedule.ui.theme.buttons
import com.imsit.schedule.viewmodels.MainViewModel

@Composable
fun ScheduleScreen(
    viewModel: MainViewModel = viewModel(),
    navController: NavHostController
) {
    ScheduleTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 20.dp)) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(background)
            ) {
                TodayScheduleRender(viewModel)
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                SettingsButton(navController)
            }
        }
    }
}

@Composable
fun SettingsButton(navController: NavHostController) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        shape = RoundedCornerShape(10.dp)
    ) {

        val navigationLink = {
            navController.navigate(com.imsit.schedule.ui.navigation.Settings) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        IconButton(
                onClick = navigationLink,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "settings",
                tint = buttons
            )
        }
    }
}

@Composable
fun WeekScheduleRender(viewModel: MainViewModel) {
    val lessons: HashMap<Int, ArrayList<DataClasses.Lesson>>? = viewModel.getWeekLessonsByGroup()
}

@Composable
fun TodayScheduleRender(viewModel: MainViewModel) {
    val lessons = remember { viewModel.getTodayLessons() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp, 45.dp, 80.dp, 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = viewModel.getTodayDate(),
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }

    if (lessons.isEmpty()) {
        WeekendUnit()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(lessons) { _, lesson ->
                ScheduleUnit(lesson = lesson)
            }
        }
    }
}

@Composable
fun WeekendUnit() {
    Card(
        modifier = Modifier
            .padding(20.dp, 0.dp, 20.dp, 7.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = buttons)
    ) {
        Card(
            modifier = Modifier
                .padding(4.dp, 0.dp, 0.dp, 0.dp),
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Loader(resource = R.raw.weekend)
        }
    }
}

@Composable
fun ScheduleUnit(lesson: DataClasses.Lesson) {
    Card(
        modifier = Modifier
            .padding(20.dp, 0.dp, 20.dp, 7.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = buttons)
    ) {
        Card(
            modifier = Modifier
                .padding(4.dp, 0.dp, 0.dp, 0.dp),
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            ScheduleUnitContent(lesson)
        }
    }
}

@Composable
private fun ScheduleUnitContent(lesson: DataClasses.Lesson) {
    Column(modifier = Modifier.padding(20.dp, 5.dp, 20.dp, 5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "№${lesson.count}",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = lesson.type,
                color = Color.Black,
                fontSize = 15.sp,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = lesson.time,
                color = Color.Black,
                fontSize = 15.sp,
                textAlign = TextAlign.End
            )
        }
        lesson.name?.let {
            Text(
                text = it,
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
        lesson.teacher?.let {
            Text(
                text = it,
                color = Color.Black,
                fontSize = 15.sp,
                fontStyle = FontStyle.Italic
            )
        }
        lesson.location?.let {
            Text(
                text = it,
                modifier = Modifier.padding(top = 10.dp),
                color = Color.Black,
                fontSize = 15.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
fun Loader(resource: Int) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resource))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    val isAnimationReady = composition != null

    Column(
        modifier = Modifier
            .height(130.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isAnimationReady) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp)
            )
        } else {
            Text(text = "Загрузка анимации...", color = Color.Gray)
        }
    }
}

@Composable
fun LessonTest() {
    ScheduleUnit(DataClasses.Lesson(1,
        "12:30 - 14:40",
        "Практика",
        "Физика",
        "Иванов И.И.",
        "1-23"))
}

