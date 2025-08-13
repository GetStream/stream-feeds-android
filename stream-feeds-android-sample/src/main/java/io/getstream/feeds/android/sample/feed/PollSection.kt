package io.getstream.feeds.android.sample.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollOptionData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.sample.ui.theme.LighterGray

@Composable
fun PollSection(activityId: String, poll: PollData, controller: FeedPollController) {
    Column(
        modifier = Modifier
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
        if (poll.description.isNotBlank()) {
            Text(
                text = poll.description,
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
                onClick = { optionId -> controller.onPollOptionSelected(activityId, optionId) },
            )
        }

        if (poll.allowUserSuggestedOptions) {
            PollTextButton(
                text = "Suggest an option",
            )
        }

        if (poll.allowAnswers) {
            PollTextButton(
                text = "Add a comment",
            )
        }

        if (poll.answersCount > 0) {
            PollTextButton(
                text = "View comments",
            )
        }

        PollTextButton(
            text = "View Results",
        )

        // TODO [G.] show end vote button when appropriate
    }
}

@Composable
fun PollTextButton(text: String, onClick: () -> Unit = {}) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        TextButton(onClick) {
            Text(text = text)
        }
    }
}

@Composable
private fun PollOption(
    option: PollOptionData,
    voteData: PollVoteData?,
    ratio: Float,
    votes: Int,
    onClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            RadioButton(
                selected = voteData != null,
                onClick = { onClick(option.id) },
            )
        }

        Column(Modifier.padding(start = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = option.text,
                )
                Text(
                    text = votes.toString(),
                )
            }
            LinearProgressIndicator(
                progress = { ratio },
                drawStopIndicator = {}
            )
        }
    }
}
