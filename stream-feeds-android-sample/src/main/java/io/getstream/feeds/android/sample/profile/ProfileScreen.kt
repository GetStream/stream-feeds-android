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
package io.getstream.feeds.android.sample.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.bottomsheet.spec.DestinationStyleBottomSheet
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.UserData
import io.getstream.feeds.android.client.api.state.FeedState
import io.getstream.feeds.android.sample.components.LoadingScreen
import io.getstream.feeds.android.sample.components.UserAvatar
import io.getstream.feeds.android.sample.util.AsyncResource
import kotlinx.coroutines.flow.StateFlow

@Destination<RootGraph>(style = DestinationStyleBottomSheet::class)
@Composable
fun ProfileScreen(navigator: DestinationsNavigator) {
    val viewModel = hiltViewModel<ProfileViewModel>()

    val feed by viewModel.feed.collectAsStateWithLifecycle()

    Surface {
        when (val feed = feed) {
            AsyncResource.Loading -> LoadingScreen()

            AsyncResource.Error -> {
                LaunchedEffect(Unit) { navigator.popBackStack() }
                return@Surface
            }

            is AsyncResource.Content ->
                ProfileScreen(
                    state = feed.data.state,
                    followSuggestions = viewModel.followSuggestions,
                    onFollowClick = viewModel::follow,
                    onUnfollowClick = viewModel::unfollow,
                )
        }
    }
}

@Composable
fun ProfileScreen(
    state: FeedState,
    followSuggestions: StateFlow<List<FeedData>>,
    onFollowClick: (FeedId) -> Unit,
    onUnfollowClick: (FeedId) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier =
            Modifier.fillMaxSize().systemBarsPadding().verticalScroll(scrollState).padding(16.dp)
    ) {
        Text(
            text = "Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // Feed members
        ProfileSection(
            title = "Members:",
            emptyText = "No members",
            items = state.members.collectAsStateWithLifecycle().value,
            itemContent = { member ->
                Text(
                    text = "${member.user.name ?: member.user.id} (${member.role})",
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            },
        )

        // Follow requests
        ProfileSection(
            title = "Follow requests:",
            emptyText = "No follow requests",
            items = state.followRequests.collectAsStateWithLifecycle().value,
            itemContent = {
                Text(
                    text = it.sourceFeed.createdBy.run { name ?: id },
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            },
        )

        // Following
        ProfileSection(
            title = "Following:",
            emptyText = "Not following any feeds",
            items = state.following.collectAsStateWithLifecycle().value,
            itemContent = { FollowingItem(follow = it, onUnfollowClick = onUnfollowClick) },
        )

        // Followers
        ProfileSection(
            title = "Followers:",
            emptyText = "No followers",
            items = state.followers.collectAsStateWithLifecycle().value,
            itemContent = {
                Text(
                    text = it.sourceFeed.createdBy.run { name ?: id },
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            },
        )

        // Follow suggestions
        val followSuggestions by followSuggestions.collectAsStateWithLifecycle()
        SectionTitle("Who to follow")
        if (followSuggestions.isNotEmpty()) {
            followSuggestions.forEach {
                FollowSuggestionItem(
                    owner = it.createdBy,
                    fid = it.fid,
                    onFollowClick = onFollowClick,
                )
            }
        } else {
            Text(text = "-No follow suggestions-")
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun <T> ProfileSection(
    title: String,
    emptyText: String,
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
) {
    SectionTitle(title)

    if (items.isNotEmpty()) {
        items.forEach { item -> itemContent(item) }
    } else {
        Text(text = emptyText, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }

    HorizontalDivider(Modifier.fillMaxWidth().padding(vertical = 16.dp))
}

@Composable
fun FollowingItem(follow: FollowData, onUnfollowClick: (FeedId) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = follow.targetFeed.createdBy.run { name ?: id },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            TextButton(onClick = { onUnfollowClick(follow.targetFeed.fid) }) {
                Text(text = "Unfollow")
            }
        }
    }
}

@Composable
fun FollowSuggestionItem(owner: UserData, fid: FeedId, onFollowClick: (FeedId) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        UserAvatar(owner.image)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = owner.name ?: owner.id,
                modifier = Modifier.padding(start = 8.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            if (!owner.name.isNullOrEmpty()) {
                Text(
                    text = "@${owner.id}",
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 14.sp,
                    color = Color.Gray,
                )
            }
        }
        OutlinedButton(
            modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically),
            shape = CircleShape,
            border = BorderStroke(1.dp, Color.Gray),
            onClick = { onFollowClick(fid) },
        ) {
            Text(text = "Follow")
        }
    }
}
