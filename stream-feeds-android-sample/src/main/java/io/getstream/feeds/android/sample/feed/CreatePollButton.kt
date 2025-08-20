/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.getstream.feeds.android.sample.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.feeds.android.sample.R
import io.getstream.feeds.android.sample.ui.theme.LighterGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePollButton(onCreatePoll: (PollFormData) -> Unit) {
    var showPollBottomSheet by remember { mutableStateOf(false) }

    IconButton(onClick = { showPollBottomSheet = true }) {
        Icon(
            painter = painterResource(R.drawable.poll),
            contentDescription = "Create Poll",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp),
        )
    }

    if (showPollBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPollBottomSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            contentWindowInsets = { WindowInsets(16.dp, 16.dp, 16.dp, 16.dp) },
        ) {
            val scrollState = rememberScrollState()

            Column(Modifier.verticalScroll(scrollState)) {
                CreatePollBottomSheetContent(
                    onCreateClicked = {
                        onCreatePoll(it)
                        showPollBottomSheet = false
                    },
                    onCancelClick = { showPollBottomSheet = false },
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.CreatePollBottomSheetContent(
    onCreateClicked: (PollFormData) -> Unit,
    onCancelClick: () -> Unit,
) {
    var question by remember { mutableStateOf("") }
    val options = remember { mutableStateListOf("", "") }
    var allowMultipleAnswers by remember { mutableStateOf(false) }
    var constrainMaxVotesPerPerson by remember { mutableStateOf(false) }
    var maxVotesPerPerson by remember { mutableStateOf("") }
    var anonymousPoll by remember { mutableStateOf(false) }
    var allowSuggestingOptions by remember { mutableStateOf(false) }
    var allowComments by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onCancelClick) { Text("Cancel") }

        Text(text = "Create Poll", fontSize = 16.sp, fontWeight = FontWeight.Medium)

        TextButton(
            onClick = {
                val filteredOptions = options.filter(String::isNotBlank)
                if (filteredOptions.size < 2) {
                    errorText = "Please provide at least two non-blank options"
                    return@TextButton
                }

                onCreateClicked(
                    PollFormData(
                        question = question,
                        options = filteredOptions,
                        allowMultipleAnswers = allowMultipleAnswers,
                        constrainMaxVotesPerPerson = constrainMaxVotesPerPerson,
                        maxVotesPerPerson = maxVotesPerPerson,
                        anonymousPoll = anonymousPoll,
                        allowSuggestingOptions = allowSuggestingOptions,
                        allowComments = allowComments,
                    )
                )
            }
        ) {
            Text("Create")
        }
    }

    Text("Question", modifier = Modifier.padding(vertical = 8.dp))
    OutlinedTextField(
        value = question,
        onValueChange = { question = it },
        placeholder = { Text("Ask a question") },
        modifier = Modifier.fillMaxWidth(),
    )

    Text("Options", modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))

    if (errorText.isNotEmpty()) {
        Text(
            text = errorText,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.error,
        )
    }

    options.forEachIndexed { index, option -> PollOptionField(option, options, index) }

    val defaultSwitchModifier =
        Modifier.padding(vertical = 8.dp)
            .background(LighterGray, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)

    Column(defaultSwitchModifier) {
        TextSwitchRow(
            text = "Allow multiple answers",
            checked = allowMultipleAnswers,
            onCheckedChange = { allowMultipleAnswers = it },
        )
        AnimatedVisibility(allowMultipleAnswers) {
            SwitchRow(
                content = {
                    OutlinedTextField(
                        value = maxVotesPerPerson,
                        onValueChange = { newText ->
                            if (newText.all(Char::isDigit)) {
                                maxVotesPerPerson = newText
                            }
                        },
                        placeholder = { Text("Max votes") },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(2f).padding(bottom = 8.dp),
                        singleLine = true,
                    )
                },
                checked = constrainMaxVotesPerPerson,
                onCheckedChange = { constrainMaxVotesPerPerson = it },
            )
        }
    }

    TextSwitchRow(
        text = "Anonymous poll",
        checked = anonymousPoll,
        onCheckedChange = { anonymousPoll = it },
        modifier = defaultSwitchModifier,
    )

    TextSwitchRow(
        text = "Allow suggesting options",
        checked = allowSuggestingOptions,
        onCheckedChange = { allowSuggestingOptions = it },
        modifier = defaultSwitchModifier,
    )

    TextSwitchRow(
        text = "Allow comments",
        checked = allowComments,
        onCheckedChange = { allowComments = it },
        modifier = defaultSwitchModifier,
    )

    Spacer(Modifier.weight(1f))
}

@Composable
private fun TextSwitchRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) = SwitchRow({ Text(text) }, checked, onCheckedChange, modifier)

@Composable
private fun SwitchRow(
    content: @Composable RowScope.() -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        content()

        Spacer(modifier = Modifier.weight(0.1f))

        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun PollOptionField(option: String, options: SnapshotStateList<String>, index: Int) {
    Row(
        modifier = Modifier.padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = option,
            onValueChange = {
                options[index] = it
                // Add a new option input field if the user types something in the last field
                if (index == options.lastIndex && it.isNotBlank()) {
                    options.add("")
                }
            },
            placeholder = { Text("Add option") },
            modifier = Modifier.weight(1f),
        )
        if (options.size > 1) {
            IconButton(
                onClick = { options.removeAt(index) },
                modifier = Modifier.padding(start = 8.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "Delete Option",
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreatePollBottomSheetContentPreview() {
    Column(Modifier.size(width = 400.dp, height = 800.dp).padding(16.dp)) {
        CreatePollBottomSheetContent({}, {})
    }
}

data class PollFormData(
    val question: String,
    val options: List<String>,
    val allowMultipleAnswers: Boolean,
    val constrainMaxVotesPerPerson: Boolean,
    val maxVotesPerPerson: String,
    val anonymousPoll: Boolean,
    val allowSuggestingOptions: Boolean,
    val allowComments: Boolean,
)
