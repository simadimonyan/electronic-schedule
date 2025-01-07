package com.imsit.schedule.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.imsit.schedule.R
import com.imsit.schedule.events.UISettingsEvent
import com.imsit.schedule.ui.theme.ScheduleTheme
import com.imsit.schedule.ui.theme.background
import com.imsit.schedule.ui.theme.buttons
import com.imsit.schedule.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    ScheduleTheme {
        Scaffold(modifier = Modifier
            .fillMaxSize(), containerColor = background,
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, end = 40.dp, bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val inlineContent = mapOf(
                        "inlineImage" to InlineTextContent(
                            placeholder = Placeholder(
                                width = 16.sp,
                                height = 16.sp,
                                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                            ),
                            children = { altText ->
                                Image(
                                    painter = painterResource(id = R.drawable.telegram),
                                    contentDescription = altText,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .offset(y = 0.7.dp)
                                )
                            }
                        )
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("${LocalContext.current.getString(R.string.contacts)} ")
                            appendInlineContent("inlineImage", "icon")
                            append(" ${LocalContext.current.getString(R.string.tg)}")
                        },
                        color = Color.Gray,
                        lineHeight = 17.sp,
                        textAlign = TextAlign.Center,
                        inlineContent = inlineContent,
                        fontSize = 13.sp,
                    )
                }
            },
            topBar = {
                TopAppBar(
                    title = { Text("Настройки", color = Color.Black, fontWeight = FontWeight.Medium) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = background
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigate(com.imsit.schedule.ui.navigation.Route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Color.Black)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val fullWeekState by viewModel.shared.scheduleFullWeek.collectAsState()
                val navInvisibilityState by viewModel.shared.navigationInvisibility.collectAsState()

                CardSettings(title = "Показать неделю", checkedState = fullWeekState) {
                    viewModel.handleEvent(UISettingsEvent.MakeScheduleWeekFull(it))
                    viewModel.handleEvent(UISettingsEvent.SaveSettings)
                }

                CardSettings(title = "Скрыть навигацию", checkedState = navInvisibilityState) {
                    viewModel.handleEvent(UISettingsEvent.MakeNavigationInvisible(it))
                    viewModel.handleEvent(UISettingsEvent.SaveSettings)
                }
            }
        }
    }
}

@Composable
fun CardSettings(title: String, checkedState: Boolean, onChanged: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 10.dp, 20.dp, 10.dp)
            .size(width = 0.dp, height = 65.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = checkedState,
                onCheckedChange = onChanged,
                modifier = Modifier.padding(10.dp),
                colors = SwitchDefaults.colors(
                    checkedTrackColor = buttons,
                    uncheckedTrackColor = Color.LightGray,
                    uncheckedBorderColor = Color.Transparent,
                    checkedBorderColor = Color.Transparent,
                    checkedThumbColor = Color.White,
                    uncheckedThumbColor = Color.White,
                )
            )
        }
    }
}