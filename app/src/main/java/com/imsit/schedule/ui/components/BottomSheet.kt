package com.imsit.schedule.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imsit.schedule.R
import com.imsit.schedule.viewmodels.GroupsViewModel

@Composable
fun BottomSheet(
    viewModel: GroupsViewModel,
    updateValue: (String) -> Unit,
) {
    val groupState by viewModel.groupState.collectAsState()

    when (groupState.selectedIndex) {
        0 -> CourseKeys(groupState.coursesToDisplay, updateValue)
        1 -> SpecialityKeys(groupState.specialitiesToDisplay, updateValue)
        else -> GroupListContent(groupState.groupsToDisplay, updateValue)
    }
}

@Composable
fun CourseKeys(
    coursesToDisplay: List<String>,
    updateValue: (String) -> Unit
) {
    coursesToDisplay.forEachIndexed { index, key ->
        if (coursesToDisplay.isEmpty()) return@forEachIndexed

        if (index != 0) {
            HorizontalDivider(
                thickness = 0.5.dp,
                modifier = Modifier.padding(25.dp, 0.dp),
                color = Color.LightGray
            )
        }

        Text(
            text = key,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .clickable { updateValue(key) },
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = Color.Black
        )
    }
}

@Composable
fun SpecialityKeys(
    specialitiesToDisplay: List<String>,
    updateValue: (String) -> Unit
) {
    specialitiesToDisplay.forEachIndexed { index, speciality ->
        if (index != 0) {
            HorizontalDivider(
                thickness = 0.5.dp,
                modifier = Modifier.padding(25.dp, 0.dp),
                color = Color.LightGray
            )
        }

        Text(
            text = speciality,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .clickable { updateValue(speciality) },
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = Color.Black
        )
    }

    HorizontalDivider(
        thickness = 0.5.dp,
        modifier = Modifier.padding(25.dp, 0.dp),
        color = Color.LightGray
    )
    Text(
        text = LocalContext.current.getString(R.string.all_specialities),
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable { updateValue("Все специальности") },
        textAlign = TextAlign.Center,
        fontSize = 20.sp,
        color = Color.Black
    )
}

@Composable
fun GroupListContent(
    groupsToDisplay: List<String>,
    updateValue: (String) -> Unit
) {
    groupsToDisplay.let {
        LazyColumn {
            itemsIndexed(it, key = { _, group -> group }) { index, group ->
                if (index != 0) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(25.dp, 0.dp),
                        color = Color.LightGray
                    )
                }

                Text(
                    text = group,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .clickable { updateValue(group) },
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }
        }
    }
}