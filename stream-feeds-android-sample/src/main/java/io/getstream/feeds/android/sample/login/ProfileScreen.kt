package io.getstream.feeds.android.sample.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.UserData

@Composable
fun ProfileScreen(
    fid: FeedId,
    client: FeedsClient,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(fid, client))
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
    ) {
        // Feed members
        Text(
            text = "Feed members:",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
        val members by viewModel.state.members.collectAsStateWithLifecycle()
        members.forEach { member ->
            Text(
                text = "${member.user.name ?: member.user.id} (${member.role})",
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Follow requests
        Text(
            text = "Follow requests:",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        val followRequests by viewModel.state.followRequests.collectAsStateWithLifecycle()
        if (followRequests.isNotEmpty()) {
            followRequests.forEach { request ->
                Text(
                    text = request.sourceFeed.createdBy.name ?: request.sourceFeed.createdBy.id,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        } else {
            Text(
                text = "-No follow requests-",
            )
        }

        // Following
        Text(
            text = "Following:",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        val following by viewModel.state.following.collectAsStateWithLifecycle()
        if (following.isNotEmpty()) {
            following.forEach { feed ->
                Text(
                    text = feed.targetFeed.createdBy.name ?: feed.targetFeed.createdBy.id,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        } else {
            Text(
                text = "-Not following any feeds-",
            )
        }

        // Followers
        Text(
            text = "Followers:",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        val followers by viewModel.state.followers.collectAsStateWithLifecycle()
        if (followers.isNotEmpty()) {
            followers.forEach { feed ->
                Text(
                    text = feed.sourceFeed.createdBy.name ?: feed.sourceFeed.createdBy.id,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        } else {
            Text(
                text = "-No followers-",
            )
        }

        // Follow suggestions
        val followSuggestions by viewModel.followSuggestions.collectAsStateWithLifecycle()
        Text(
            text = "Follow suggestions:",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        if (followSuggestions.isNotEmpty()) {
            LazyColumn {
                items(followSuggestions) {
                    FollowSuggestionItem(
                        owner = it.createdBy,
                        fid = it.fid,
                        onFollowClick = { feedId ->
                            viewModel.follow(feedId)
                        }
                    )
                }
            }
        } else {
            Text(
                text = "-No follow suggestions-",
            )
        }
    }
}

@Composable
fun FollowSuggestionItem(
    owner: UserData,
    fid: FeedId,
    onFollowClick: (FeedId) -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            model = ImageRequest.Builder(context)
                .data(owner.image)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = owner.name ?: owner.id,
                modifier = Modifier.padding(start = 8.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            if (!owner.name.isNullOrEmpty()) {
                Text(
                    text = "@${owner.id}",
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        Card(
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically),
            shape = CircleShape,
            border = BorderStroke(1.dp, Color.Gray),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            onClick = { onFollowClick(fid) }
        ) {
            Text(
                text = "Follow",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 14.sp,
                color = Color.Blue
            )
        }
    }
}