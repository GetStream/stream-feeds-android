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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollOptionData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.sample.ui.theme.LighterGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollSection(activityId: String, currentUserId: String, poll: PollData, controller: FeedPollController) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(LighterGray, shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (poll.name.isNotBlank()) {
            Text(
                text = poll.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
            )
        }
        if (poll.isClosed) {
            Text(
                text = "Vote ended",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
            )
        }

        val ownVotes = remember(poll.ownVotes) { poll.ownVotes.associateBy(PollVoteData::optionId) }

        poll.options.forEach { option ->
            val votes = poll.voteCountsByOption.getOrDefault(option.id, 0)
            val ratio = votes.toFloat() / poll.voteCount.coerceAtLeast(1)

            PollOption(
                option = option,
                voteData = ownVotes[option.id],
                ratio = ratio,
                votes = votes,
                enabled = !poll.isClosed,
                onClick = { optionId -> controller.onOptionSelected(activityId, optionId) },
            )
        }

        if (!poll.isClosed && poll.allowUserSuggestedOptions) {
            SuggestOptionButton { option -> controller.onSuggestOption(activityId, option) }
        }

        if (!poll.isClosed && poll.allowAnswers) {
            AddCommentButton { comment -> controller.onAddComment(activityId, comment) }
        }

        if (poll.answersCount > 0) {
            PollTextButton(text = "View comments")
        }

        PollTextButton(text = "View results")

        if (!poll.isClosed && poll.createdById == currentUserId) {
            PollTextButton(
                text = "Close Poll",
                onClick = { controller.onClose(activityId) }
            )
        }
    }
}

@Composable
fun PollTextButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        TextButton(onClick, modifier = modifier) { Text(text = text) }
    }
}

@Composable
private fun PollOption(
    option: PollOptionData,
    voteData: PollVoteData?,
    ratio: Float,
    votes: Int,
    enabled: Boolean,
    onClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if (enabled) {
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                RadioButton(selected = voteData != null, onClick = { onClick(option.id) })
            }
        }

        Column(Modifier.padding(start = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = option.text)
                Text(text = votes.toString())
            }
            LinearProgressIndicator(
                progress = { ratio },
                drawStopIndicator = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AddCommentButton(onSubmit: (String) -> Unit) {
    var showCommentDialog by remember { mutableStateOf(false) }

    PollTextButton(
        text = "Add a comment",
        onClick = { showCommentDialog = true }
    )

    if (showCommentDialog) {
        InputAlertDialog(
            title = "Add a comment",
            onDismissRequest = { showCommentDialog = false },
            onSubmit = { comment ->
                onSubmit(comment)
                showCommentDialog = false
            }
        )
    }
}

@Composable
fun SuggestOptionButton(onSubmit: (String) -> Unit) {
    var showOptionSuggestDialog by remember { mutableStateOf(false) }

    PollTextButton(
        text = "Suggest an option",
        onClick = { showOptionSuggestDialog = true },
    )

    if (showOptionSuggestDialog) {
        InputAlertDialog(
            title = "Suggest an option",
            onDismissRequest = { showOptionSuggestDialog = false },
            onSubmit = { option ->
                onSubmit(option)
                showOptionSuggestDialog = false
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun InputAlertDialog(
    title: String,
    onDismissRequest: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    var input by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest,
        title = { Text(title) },
        text = {
            OutlinedTextField(value = input, onValueChange = { input = it })
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSubmit(input) },
                enabled = input.isNotBlank()
            ) {
                Text("Submit")
            }
        },
    )
}

@Preview
@Composable
private fun InputAlertDialogPreview() {
    InputAlertDialog("Title", {}, {})
}
