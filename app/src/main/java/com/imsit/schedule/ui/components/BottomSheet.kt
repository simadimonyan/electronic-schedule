package com.imsit.schedule.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imsit.schedule.R
import com.imsit.schedule.data.models.DataClasses

@Composable
fun BottomSheet(
    index: Int,
    groups: HashMap<String, HashMap<String, ArrayList<DataClasses.Group>>>,
    updateValue: (String) -> Unit,
    courseChosen: String,
    specialityChosen: String
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
            groups.let { loadedGroups ->
                for ((k, speciality) in loadedGroups[courseChosen]?.keys!!.withIndex()) {

                    if (k != 0)
                        HorizontalDivider(thickness = 0.5.dp,
                            modifier = Modifier.padding(25.dp, 0.dp))

                    Text(
                        speciality,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clickable(
                                onClick = { updateValue(speciality) }
                            ),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }
            }
            HorizontalDivider(thickness = 0.5.dp,
                modifier = Modifier.padding(25.dp, 0.dp))
            Text(
                LocalContext.current.getString(R.string.all_specialities),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .clickable(
                        onClick = { updateValue("Все специальности") }
                    ),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }
        else -> {

            @Composable
            fun GroupList(groups: List<DataClasses.Group>, updateValue: (String) -> Unit) {
                LazyColumn {
                    itemsIndexed(groups) { index, group ->
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
            groups[courseChosen]?.let { courseGroups ->
                val groupsToDisplay = if (specialityChosen != LocalContext.current.getString(R.string.all_specialities)) {
                    courseGroups[specialityChosen]
                } else {
                    courseGroups.values.flatten()
                }

                groupsToDisplay?.let { GroupList(it, updateValue) }
            }
        }
    }
}