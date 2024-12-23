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
import com.imsit.schedule.R
import com.imsit.schedule.ui.components.BottomSheet
import com.imsit.schedule.ui.theme.ScheduleTheme
import com.imsit.schedule.ui.theme.background
import com.imsit.schedule.ui.theme.buttons
import com.imsit.schedule.viewmodels.MainViewModel


@SuppressLint("MutableCollectionMutableState")
@Composable
fun GroupScreen(
    viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    MainFrame(viewModel)
}

@Composable
fun MainFrame(viewModel: MainViewModel) {
    val context = LocalContext.current
    val stateContext = remember { context }

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
                    text = LocalContext.current.getString(R.string.choose),
                    icon = R.drawable.logo,
                    onClick = {

                    }
                )
            }
        }
    }
}

@Composable
fun Body(viewModel: MainViewModel) {
    val context = LocalContext.current
    val stateContext = remember { context }

    val loading by viewModel.loading.collectAsState(true)
    val progress by viewModel.progress.collectAsState(0)
    val course by viewModel.course.collectAsState(context.getString(R.string.first_course))
    val speciality by viewModel.speciality.collectAsState(context.getString(R.string.all_specialities))
    val group by viewModel.group.collectAsState(context.getString(R.string.choose))
    val showBottomSheet by viewModel.showBottomSheet.collectAsState(false)
    val selectedIndex by viewModel.selectedIndex.collectAsState(0)

    Column {
        CardContent(
            icon = R.drawable.study,
            title = stateContext.getString(R.string.course),
            subtitle = course,
            onClick = {
                viewModel.toggleBottomSheet(true)
                viewModel.setSelectedIndex(0)
            }
        )

        CardContent(
            icon = R.drawable.books,
            title = stateContext.getString(R.string.speciality),
            subtitle = speciality,
            onClick = {
                viewModel.toggleBottomSheet(true)
                viewModel.setSelectedIndex(1)
            }
        )

        CardContent(
            icon = R.drawable.people,
            title = stateContext.getString(R.string.group),
            subtitle = group,
            onClick = {
                viewModel.toggleBottomSheet(true)
                viewModel.setSelectedIndex(2)
            }
        )
    }

    if (showBottomSheet) {
        BottomSheetContent(
            loading = loading,
            progress = progress,
            viewModel = viewModel,
            selectedIndex = selectedIndex,
            onDismiss = { viewModel.toggleBottomSheet(false) }
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
    viewModel: MainViewModel,
    selectedIndex: Int,
    onDismiss: () -> Unit
) {
    val context: Context = LocalContext.current
    val stateContext = remember { context }
    val animatedProgress = animateFloatAsState(targetValue = progress / 100f, label = "progress")

    ModalBottomSheet(
        modifier = Modifier.wrapContentHeight(),
        sheetState = rememberModalBottomSheetState(),
        shape = RoundedCornerShape(17.dp),
        onDismissRequest = onDismiss
    ) {
        if (loading) {
            Text(
                text = stateContext.getString(R.string.update_data),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 17.sp
            )

            LinearProgressIndicator(
                progress = { animatedProgress.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(50.dp, 10.dp),
            )
            Spacer(modifier = Modifier.height(20.dp))
        } else {
            BottomSheet(viewModel) { newValue ->
                viewModel.onSelectItem(
                    stateContext,
                    selectedIndex,
                    newValue
                )
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
            Text(text = text, Modifier.padding(0.dp, 7.dp))
        }
    }
}