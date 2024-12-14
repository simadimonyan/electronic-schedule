package com.imsit.schedule

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imsit.schedule.models.CacheManager
import com.imsit.schedule.models.CacheUpdater
import com.imsit.schedule.models.Schedule
import com.imsit.schedule.system.NotificationsManager
import com.imsit.schedule.ui.theme.ScheduleTheme
import com.imsit.schedule.ui.theme.background
import com.imsit.schedule.ui.theme.buttons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PreviewWrapper()
        }
    }
}

@Preview
@Composable
fun PreviewWrapper() {
    val context = LocalContext.current
    val cacheManager = remember { CacheManager(context) }
    val coroutineScope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    var groups by remember { mutableStateOf<HashMap<String, ArrayList<Schedule.Group>>?>(HashMap()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            NotificationsManager().createNotificationChannel(context)
            try {
                if (cacheManager.shouldUpdateCache()) {
                    val schedule = Schedule()
                    val loadedGroups = withContext(Dispatchers.IO) {
                        schedule.loadData()
                    }
                    loading = true
                    groups = loadedGroups

                    cacheManager.saveGroupsToCache(groups!!)
                    cacheManager.saveLastUpdatedTime(System.currentTimeMillis())
                    loading = false
                } else {
                    groups = cacheManager.loadGroupsFromCache()
                    loading = false
                }
            }
            catch (e: Exception) {
                loading = true
                groups = cacheManager.loadGroupsFromCache()
                if (groups?.size!! > 1)
                    loading = false
            }
            CacheUpdater().setupPeriodicWork(context,
                cacheManager.getLastUpdatedTime())
        }
    }
    groups?.let { GroupPreview(it, loading) }
}

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupFields(groups: HashMap<String, ArrayList<Schedule.Group>>, loading: Boolean) {
    val scope = rememberCoroutineScope()

    var course: String by remember { mutableStateOf("1 курс") }
    var speciality: String by remember { mutableStateOf("СПО") }
    var group: String by remember { mutableStateOf("Выбрать") }

    val sheetState = rememberModalBottomSheetState()
    var selectedIndex by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }

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
            showBottomSheet = true
            selectedIndex = 0
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
                        text = "Курс",
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
            showBottomSheet = true
            selectedIndex = 1
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
                    text = "Специальность",
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
            showBottomSheet = true
            selectedIndex = 2
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
                    text = "Группа",
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

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier
                .wrapContentHeight(),
            sheetState = sheetState,
            shape = RoundedCornerShape(17.dp),
            onDismissRequest = { showBottomSheet = false }
        ) {
            if (loading) {
                Text(
                    "Обновляем данные... ",
                    modifier = Modifier
                        .padding(10.dp, 10.dp, 0.dp, 10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp
                )
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(50.dp, 10.dp),
                )
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                BottomSheet(selectedIndex, groups) { newValue ->
                    when (selectedIndex) {
                        0 -> course = newValue
                        1 -> speciality = newValue
                        2 -> group = newValue
                    }
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun BottomSheet(
    index: Int,
    groups: HashMap<String, ArrayList<Schedule.Group>>,
    updateValue: (String) -> Unit
) {
    when (index) {
        0 -> {
            groups.let { loadedGroups ->
                for ((k, i) in loadedGroups.keys.withIndex()) {

                    if (loadedGroups[i]?.isEmpty() == true) {
                        Spacer(modifier = Modifier.height(7.dp))
                        continue
                    }

                    if (k != 0)
                        HorizontalDivider(thickness = 0.5.dp,
                            modifier = Modifier.padding(25.dp, 0.dp))

                    Text(
                        i,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clickable(
                                onClick = { updateValue(i) }
                            ),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }
            }
        }
        1 -> {
            Text(
                "Специальности",
                modifier = Modifier.padding(16.dp)
            )
        }
        else -> {
            Text(
                "Группы",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun CustomAppBar() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    val image = 62.dp

    val indicatorOffset by animateDpAsState(
        targetValue = if (selectedIndex == 1) image else -image,
        animationSpec = tween(durationMillis = 300),
        label = "indicatorOffset"
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(60.dp, 0.dp, 60.dp, 55.dp)
                .size(width = 0.dp, height = 80.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Card(
                    modifier = Modifier
                        .size(44.dp, 4.dp)
                        .align(Alignment.BottomCenter)
                        .offset {
                            IntOffset(
                                x = indicatorOffset.roundToPx(),
                                y = -15.dp.roundToPx()
                            )
                        },
                    shape = RoundedCornerShape(100.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = buttons
                    )
                ) {}

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.list),
                        contentDescription = "study",
                        colorFilter = ColorFilter.tint(if (selectedIndex == 0) buttons else Color.Gray),
                        modifier = Modifier
                            .size(55.dp)
                            .padding(0.dp, 15.dp, 0.dp, 0.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { selectedIndex = 0 }
                            )
                    )
                    Spacer(modifier = Modifier.width(70.dp))
                    Image(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "study",
                        colorFilter = ColorFilter.tint(if (selectedIndex == 1) buttons else Color.Gray),
                        modifier = Modifier
                            .size(55.dp)
                            .padding(0.dp, 15.dp, 0.dp, 0.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { selectedIndex = 1 }
                            )
                    )
                }
            }
        }
    }
}


@Composable
fun GroupPreview(groups: HashMap<String, ArrayList<Schedule.Group>>, loading: Boolean) {
    ScheduleTheme {

        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = background) { innerPadding ->
            Column (
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    "Выбор группы",
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .padding(0.dp, 70.dp, 0.dp, 80.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )

                GroupFields(groups, loading)

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
                            text = "Выбрать",
                            Modifier.padding(0.dp, 7.dp)
                        )
                    }
                }

                CustomAppBar()
            }
        }
    }
}

