package io.getstream.feeds.android.sample.poll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.sample.R
import io.getstream.feeds.android.sample.components.LoadingScreen
import io.getstream.feeds.android.sample.components.UserAvatar
import io.getstream.feeds.android.sample.ui.util.ScrolledToBottomEffect
import io.getstream.feeds.android.sample.util.AsyncResource

data class PollCommentsScreenArgs(val pollId: String)

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(navArgs = PollCommentsScreenArgs::class)
@Composable
fun PollCommentsScreen(navigator: DestinationsNavigator) {
    val viewModel = hiltViewModel<PollCommentsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comments") },
                navigationIcon = {
                    IconButton(onClick = navigator::popBackStack) {
                        Icon(painterResource(R.drawable.close), contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = state) {
            AsyncResource.Loading -> {
                LoadingScreen(Modifier.padding(padding))
            }

            AsyncResource.Error -> {
                Box(Modifier.padding(padding), contentAlignment = Alignment.Center) {
                    Text("There was an error loading the poll comments")
                }
            }

            is AsyncResource.Content -> {
                val votes by state.data.votes.collectAsStateWithLifecycle()

                PollCommentsScreen(
                    votes = votes,
                    onLoadMore = viewModel::onLoadMore,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun PollCommentsScreen(
    votes: List<PollVoteData>,
    onLoadMore: () -> Unit,
    modifier: Modifier,
) {
    val listState = rememberLazyListState()

    ScrolledToBottomEffect(listState, action = onLoadMore)

    LazyColumn(
        modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(votes) { vote ->
            PollCommentItem(vote)
        }
    }
}

@Composable
private fun PollCommentItem(vote: PollVoteData) {
    Column(
        Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(vote.answerText.orEmpty(), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

        vote.user?.let { user ->
            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    user.image,
                    Modifier
                        .padding(end = 8.dp)
                        .size(32.dp)
                )

                Text(
                    text = user.name.orEmpty(),
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
