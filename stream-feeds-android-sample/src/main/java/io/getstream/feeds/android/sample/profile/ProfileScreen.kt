package io.getstream.feeds.android.sample.profile

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.bottomsheet.spec.DestinationStyleBottomSheet
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.UserData

data class ProfileScreenArgs(val feedId: String) {
    val fid = FeedId(feedId)
}

@Destination<RootGraph>(style = DestinationStyleBottomSheet::class, navArgs = ProfileScreenArgs::class)
@Composable
fun ProfileScreen() {
    val viewModel = hiltViewModel<ProfileViewModel>()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
    ) {
        Text("Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // Feed members
        ProfileSection(
            title = "Members:",
            emptyText = "-No members-",
            items = viewModel.state.members.collectAsStateWithLifecycle().value,
            itemText = { member -> "${member.user.name ?: member.user.id} (${member.role})" },
        )

        // Follow requests
        ProfileSection(
            title = "Follow requests:",
            emptyText = "-No follow requests-",
            items = viewModel.state.followRequests.collectAsStateWithLifecycle().value,
            itemText = { it.sourceFeed.createdBy.run { name ?: id } },
        )

        // Following
        ProfileSection(
            title = "Following:",
            emptyText = "-Not following any feeds-",
            items = viewModel.state.following.collectAsStateWithLifecycle().value,
            itemText = { it.targetFeed.createdBy.run { name ?: id } },
        )

        // Followers
        ProfileSection(
            title = "Followers:",
            emptyText = "-No followers-",
            items = viewModel.state.followers.collectAsStateWithLifecycle().value,
            itemText = { it.sourceFeed.createdBy.run { name ?: id } },
        )

        // Follow suggestions
        val followSuggestions by viewModel.followSuggestions.collectAsStateWithLifecycle()
        SectionTitle("Who to follow")
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
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
}

@Composable
private fun <T> ProfileSection(
    title: String,
    emptyText: String,
    items: List<T>,
    itemText: (T) -> String,
) {
    SectionTitle(title)

    if (items.isNotEmpty()) {
        items.forEach { item ->
            Text(
                text = itemText(item),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    } else {
        Text(text = emptyText)
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
