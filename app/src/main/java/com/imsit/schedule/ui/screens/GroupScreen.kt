package com.imsit.schedule.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.imsit.schedule.R
import com.imsit.schedule.data.models.DataClasses
import com.imsit.schedule.ui.components.BottomSheet
import com.imsit.schedule.ui.components.CustomAppBar
import com.imsit.schedule.ui.navigation.AppNavGraph
import com.imsit.schedule.ui.theme.ScheduleTheme
import com.imsit.schedule.ui.theme.background
import com.imsit.schedule.ui.theme.buttons
import com.imsit.schedule.viewmodels.GroupScreenViewModel
import kotlinx.coroutines.launch


@SuppressLint("MutableCollectionMutableState")
@Composable
fun GroupScreen(
    viewModel: GroupScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val context = LocalContext.current

    // ViewModel State
    val groups by viewModel.groups.collectAsState(HashMap())
    val loading by viewModel.loading.collectAsState(true)
    val progress by viewModel.progress.collectAsState(0)
    val course by viewModel.course.collectAsState(context.getString(R.string.first_course))
    val speciality by viewModel.speciality.collectAsState(context.getString(R.string.all_specialities))
    val group by viewModel.group.collectAsState(context.getString(R.string.choose))
    val showBottomSheet by viewModel.showBottomSheet.collectAsState(false)
    val selectedIndex by viewModel.selectedIndex.collectAsState(0)

    // UI
    MainFrame(
        groups = groups,
        loading = loading,
        progress = progress,
        course = course,
        speciality = speciality,
        group = group,
        showBottomSheet = showBottomSheet,
        selectedIndex = selectedIndex,
        onItemSelected = { con, index, newValue ->
            viewModel.onSelectItem(con, index, newValue)
        },
        setToggleBottomSheet = { toggle ->
            viewModel.toggleBottomSheet(toggle)
        },
        setSelectedIndex = { index ->
            viewModel.setSelectedIndex(index)
        }
    )

}

@Composable
fun MainFrame(
    groups: HashMap<String, java.util.HashMap<String, java.util.ArrayList<DataClasses.Group>>>,
    loading: Boolean,
    progress: Int,
    course: String,
    speciality: String,
    group: String,
    showBottomSheet: Boolean,
    selectedIndex: Int,
    onItemSelected: (Context, Int, String) -> Unit,
    setToggleBottomSheet: (Boolean) -> Unit,
    setSelectedIndex: (Int) -> Unit,
) {

    ScheduleTheme {
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = background) { innerPadding ->
            Column (
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    LocalContext.current.getString(R.string.choose_group),
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .padding(0.dp, 70.dp, 0.dp, 80.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )

                // Body
                Body(
                    groups = groups ,
                    loading = loading,
                    progress = progress,
                    course = course,
                    speciality = speciality,
                    group = group,
                    showBottomSheet = showBottomSheet,
                    selectedIndex = selectedIndex,
                    onItemSelected,
                    setToggleBottomSheet,
                    setSelectedIndex
                )

                Button(onClick = { /* TODO */},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 0.dp, 20.dp, 0.dp)
                        .size(0.dp, 65.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttons
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(15.dp, 0.dp)
                            .fillMaxHeight()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "study",
                            modifier = Modifier
                                .size(35.dp)
                        )
                        Spacer(modifier = Modifier.width(0.dp))
                        Text(
                            text = LocalContext.current.getString(R.string.choose),
                            Modifier.padding(0.dp, 7.dp)
                        )
                    }
                }

            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Body(
    groups: HashMap<String, HashMap<String, ArrayList<DataClasses.Group>>>,
    loading: Boolean,
    progress: Int,
    course: String,
    speciality: String,
    group: String,
    showBottomSheet: Boolean,
    selectedIndex: Int,
    onItemSelected: (Context, Int, String) -> Unit,
    setToggleBottomSheet: (Boolean) -> Unit,
    setSelectedIndex: (Int) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 0.dp, 20.dp, 20.dp)
            .size(width = 0.dp, height = 65.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        onClick = {
            setToggleBottomSheet(true)
            setSelectedIndex(0)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(15.dp, 0.dp)
                    .fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.study),
                    contentDescription = "study",
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
                    Text(
                        text =  LocalContext.current.getString(R.string.course),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = course,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 0.dp, 20.dp, 20.dp)
            .size(width = 0.dp, height = 65.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        onClick = {
            setToggleBottomSheet(true)
            setSelectedIndex(1)
        }
    ) {
        Row(
            modifier = Modifier
                .padding(15.dp, 0.dp)
                .fillMaxHeight()
        ) {
            Image(
                painter = painterResource(id = R.drawable.books),
                contentDescription = "study",
                colorFilter = ColorFilter.tint(buttons),
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Column (
                verticalArrangement = Arrangement.spacedBy((-3).dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(0.dp, 10.dp)
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.speciality),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = speciality,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 0.dp, 20.dp, 80.dp)
            .size(width = 0.dp, height = 65.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        onClick = {
            setToggleBottomSheet(true)
            setSelectedIndex(2)
        }
    ) {
        Row(
            modifier = Modifier
                .padding(15.dp, 0.dp)
                .fillMaxHeight()
        ) {
            Image(
                painter = painterResource(id = R.drawable.people),
                contentDescription = "study",
                colorFilter = ColorFilter.tint(buttons),
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Column (
                verticalArrangement = Arrangement.spacedBy((-3).dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(0.dp, 10.dp)
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.group),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = group,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier
                .wrapContentHeight(),
            sheetState = sheetState,
            shape = RoundedCornerShape(17.dp),
            onDismissRequest = { setToggleBottomSheet(false) }
        ) {
            if (loading) {
                Text(
                    LocalContext.current.getString(R.string.update_data),
                    modifier = Modifier
                        .padding(10.dp, 0.dp, 10.dp, 10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp
                )

                val animatedProgress = remember { Animatable(0f) }

                // Animate the progress value
                LaunchedEffect(progress) {
                    animatedProgress.animateTo(progress / 100f,
                        animationSpec = tween(durationMillis = 500,
                            easing = LinearEasing
                        )
                    )
                }

                LinearProgressIndicator(
                    progress = {
                        animatedProgress.value
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(50.dp, 10.dp),
                )
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                val context = LocalContext.current
                BottomSheet(selectedIndex, groups, { newValue ->
                    onItemSelected(context, selectedIndex, newValue)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            setToggleBottomSheet(false)
                        }
                    }
                }, course, speciality)
            }
        }
    }
}


