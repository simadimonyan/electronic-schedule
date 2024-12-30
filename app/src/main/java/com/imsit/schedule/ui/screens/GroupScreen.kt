package com.imsit.schedule.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.imsit.schedule.R
import com.imsit.schedule.events.UIGroupEvent
import com.imsit.schedule.ui.components.BottomSheet
import com.imsit.schedule.ui.theme.ScheduleTheme
import com.imsit.schedule.ui.theme.background
import com.imsit.schedule.ui.theme.buttons
import com.imsit.schedule.viewmodels.GroupsViewModel

@SuppressLint("MutableCollectionMutableState")
@Composable
fun GroupScreen(
    viewModel: GroupsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.handleEvent(UIGroupEvent.RestoreCache) //chosen parameters reveler
    }
    MainFrame(viewModel)
}

@Composable
fun MainFrame(viewModel: GroupsViewModel) {
    val context = LocalContext.current
    val stateContext = remember { context }
    val groupState by viewModel.groupState.collectAsState()

    ScheduleTheme {
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = background) { innerPadding ->
            Column(modifier = Modifier.fillMaxHeight()) {
                Text(
                    stateContext.getString(R.string.choose_group),
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .padding(0.dp, 70.dp, 0.dp, 80.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )

                Body(viewModel)

                ActionButton(
                    text = stateContext.getString(R.string.choose),
                    icon = R.drawable.logo,
                    onClick = {
                        viewModel.handleEvent(UIGroupEvent.CreateSchedule)
                        val result: Boolean = groupState.scheduleCreation
                    }
                )
            }
        }
    }
}

@Composable
fun Body(
    viewModel: GroupsViewModel,
) {
    val context = LocalContext.current
    val stateContext = remember { context }

    val loading by viewModel.shared.loading.collectAsState(true)
    val progress by viewModel.shared.progress.collectAsState(0)
    val groupState by viewModel.groupState.collectAsState()

    Column {
        CardContent(
            icon = R.drawable.study,
            title = stateContext.getString(R.string.course),
            subtitle = groupState.course,
            onClick = {
                viewModel.handleEvent(UIGroupEvent.DisplayCourses)
                viewModel.handleEvent(UIGroupEvent.ShowBottomSheet)
                viewModel.handleEvent(UIGroupEvent.SetSelectedIndex(0))
            }
        )

        CardContent(
            icon = R.drawable.books,
            title = context.getString(R.string.speciality),
            subtitle = groupState.speciality,
            onClick = {
                viewModel.handleEvent(UIGroupEvent.DisplaySpecialities(groupState.course))
                viewModel.handleEvent(UIGroupEvent.ShowBottomSheet)
                viewModel.handleEvent(UIGroupEvent.SetSelectedIndex(1))
            }
        )

        CardContent(
            icon = R.drawable.people,
            title = stateContext.getString(R.string.group),
            subtitle = groupState.group,
            onClick = {
                viewModel.handleEvent(UIGroupEvent.DisplayGroups(groupState.course, groupState.speciality))
                viewModel.handleEvent(UIGroupEvent.ShowBottomSheet)
                viewModel.handleEvent(UIGroupEvent.SetSelectedIndex(2))
            }
        )
    }

    if (groupState.showBottomSheet) {
        BottomSheetContent(
            loading = loading,
            progress = progress,
            viewModel = viewModel,
            selectedIndex = groupState.selectedIndex,
            onDismiss = { viewModel.handleEvent(UIGroupEvent.HideBottomSheet) }
        )
    }
}

@Composable
fun CardContent(icon: Int, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 0.dp, 20.dp, 20.dp)
            .size(width = 0.dp, height = 65.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(15.dp, 0.dp)
                .fillMaxHeight()
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(buttons),
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy((-3).dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(0.dp, 10.dp)
            ) {
                Text(text = title, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(
    loading: Boolean,
    progress: Int,
    viewModel: GroupsViewModel,
    selectedIndex: Int,
    onDismiss: () -> Unit
) {
    val context: Context = LocalContext.current
    val animatedProgress = animateFloatAsState(targetValue = progress / 100f, label = "progress")

    ModalBottomSheet(
        modifier = Modifier.wrapContentHeight(),
        sheetState = rememberModalBottomSheetState(),
        shape = RoundedCornerShape(17.dp),
        contentColor = Color.White,
        containerColor = Color.White,
        onDismissRequest = onDismiss
    ) {
        if (loading) {
            Text(
                text = context.getString(R.string.update_data),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 17.sp,
                color = Color.Black
            )

            LinearProgressIndicator(
                progress = { animatedProgress.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(50.dp, 10.dp),
                color = buttons,
                trackColor = background
            )
            Spacer(modifier = Modifier.height(20.dp))
        } else {
            BottomSheet(viewModel) { newValue ->
                when (selectedIndex) {
                    0 -> viewModel.handleEvent(UIGroupEvent.UpdateCourse(newValue))
                    1 -> viewModel.handleEvent(UIGroupEvent.UpdateSpeciality(newValue))
                    2 -> viewModel.handleEvent(UIGroupEvent.UpdateGroup(newValue))
                }
                onDismiss()
            }
        }
    }
}

@Composable
fun ActionButton(text: String, icon: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 30.dp, 20.dp, 0.dp)
            .size(0.dp, 65.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = buttons),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(15.dp, 0.dp)
                .fillMaxHeight()
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(35.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, Modifier.padding(0.dp, 7.dp), color = Color.White)
        }
    }
}