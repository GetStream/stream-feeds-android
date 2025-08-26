package io.getstream.feeds.android.sample.poll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollOptionData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.sample.R
import io.getstream.feeds.android.sample.components.UserAvatar
import io.getstream.feeds.android.sample.ui.theme.LighterGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollResultsScreen(poll: PollData, onCloseClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Results") },
                navigationIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(painterResource(R.drawable.close), contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = poll.name,
                modifier = Modifier
                    .background(LighterGray, RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .fillMaxWidth(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )

            poll.options.forEach { option ->
                OptionResultItem(
                    option = option,
                    votesCount = poll.voteCountsByOption[option.id] ?: 0,
                    latestVotes = poll.latestVotesByOption[option.id].orEmpty(),
                )
            }
        }
    }
}

@Composable
private fun OptionResultItem(
    option: PollOptionData,
    votesCount: Int,
    latestVotes: List<PollVoteData>
) {
    Column(
        Modifier
            .background(LighterGray, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row {
            Text(
                text = option.text,
                modifier = Modifier.weight(1f),
                fontSize = 18.sp
            )
            Text(votesCount.toString())
        }

        latestVotes.forEach { vote ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                UserAvatar(
                    vote.user?.image,
                    Modifier.size(24.dp)
                )
                Text(vote.user?.name ?: vote.userId ?: "anonymous")
            }
        }
    }
}
