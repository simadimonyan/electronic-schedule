package com.imsit.schedule.ui.components

import android.content.Context
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imsit.schedule.R
import com.imsit.schedule.data.models.DataClasses
import com.imsit.schedule.viewmodels.GroupsViewModel

@Composable
fun BottomSheet(
    viewModel: GroupsViewModel,
    updateValue: (String) -> Unit,
) {
    val groupState by viewModel.groupState.collectAsState()
    val groups by viewModel.shared.groups.collectAsState()

    when (groupState.selectedIndex) {
        0 -> GroupKeys(groups, updateValue)
        1 -> SpecialityKeys(groups, groupState.course, updateValue)
        else -> GroupListContent(groups, groupState.course, groupState.speciality, updateValue)
    }
}

@Composable
fun GroupKeys(
    groups: Map<String, Map<String, List<DataClasses.Group>>>,
    updateValue: (String) -> Unit
) {
    groups.keys.forEachIndexed { index, key ->
        if (groups[key]?.isEmpty() == true) return@forEachIndexed

        if (index != 0) {
            HorizontalDivider(
                thickness = 0.5.dp,
                modifier = Modifier.padding(25.dp, 0.dp)
            )
        }

        Text(
            text = key,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .clickable { updateValue(key) },
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    }
}

@Composable
fun SpecialityKeys(
    groups: Map<String, Map<String, List<DataClasses.Group>>>,
    courseChosen: String,
    updateValue: (String) -> Unit
) {
    groups[courseChosen]?.keys?.forEachIndexed { index, speciality ->
        if (index != 0) {
            HorizontalDivider(
                thickness = 0.5.dp,
                modifier = Modifier.padding(25.dp, 0.dp)
            )
        }

        Text(
            text = speciality,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .clickable { updateValue(speciality) },
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    }

    HorizontalDivider(
        thickness = 0.5.dp,
        modifier = Modifier.padding(25.dp, 0.dp)
    )
    Text(
        text = LocalContext.current.getString(R.string.all_specialities),
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable { updateValue("Все специальности") },
        textAlign = TextAlign.Center,
        fontSize = 20.sp
    )
}

@Composable
fun GroupListContent(
    groups: Map<String, Map<String, List<DataClasses.Group>>>,
    courseChosen: String,
    specialityChosen: String,
    updateValue: (String) -> Unit
) {
    val context: Context = LocalContext.current
    val groupsToDisplay = remember(courseChosen, specialityChosen) {
        if (specialityChosen != context.getString(R.string.all_specialities)) {
            groups[courseChosen]?.get(specialityChosen)
        } else {
            groups[courseChosen]?.values?.flatten()
        }
    }

    groupsToDisplay?.let {
        LazyColumn {
            itemsIndexed(it, key = { _, group -> group.group }) { index, group ->
                if (index != 0) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(25.dp, 0.dp)
                    )
                }

                Text(
                    text = group.group,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .clickable { updateValue(group.group) },
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
        }
    }
}