package com.imsit.schedule.ui

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imsit.schedule.R
import com.imsit.schedule.ui.theme.ScheduleTheme
import com.imsit.schedule.ui.theme.background
import com.imsit.schedule.ui.theme.buttons

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GroupPreview()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupFields(
    course: Int,
    speciality: Int,
    group: Int
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
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
                        text = "1 курс",
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
                    text = "СПО",
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
                    text = "Выбрать",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false }
        ) {
            BottomSheet(selectedIndex)
        }
    }

}

@Composable
fun BottomSheet(index: Int) {
    if (index == 0) {
        Text(
            "Курсы",
            modifier = Modifier.padding(16.dp)
        )
    }
    else if (index == 1) {
        Text(
            "Специальности",
            modifier = Modifier.padding(16.dp)
        )
    }
    else {
        Text(
            "Группы",
            modifier = Modifier.padding(16.dp)
        )
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

@Preview
@Composable
fun GroupPreview() {
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

                GroupFields(0,0 , 0)

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

