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
package io.getstream.feeds.android.sample.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import io.getstream.feeds.android.client.api.model.AggregatedActivityData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.FeedState
import io.getstream.feeds.android.sample.components.LoadingScreen
import io.getstream.feeds.android.sample.components.UserAvatar
import io.getstream.feeds.android.sample.util.AsyncResource

data class NotificationsScreenArgs(val feedId: String) {
    val fid = FeedId(feedId)
}

@Destination<RootGraph>(
    style = DestinationStyleBottomSheet::class,
    navArgs = NotificationsScreenArgs::class,
)
@Composable
fun NotificationsScreen(navigator: DestinationsNavigator) {
    val viewModel = hiltViewModel<NotificationsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val state = state) {
        AsyncResource.Loading -> LoadingScreen()
        AsyncResource.Error -> {
            LaunchedEffect(Unit) { navigator.popBackStack() }
            return
        }
        is AsyncResource.Content ->
            NotificationsScreen(
                state = state.data,
                onMarkAllSeen = viewModel::onMarkAllSeen,
                onMarkAggregatedActivityRead = viewModel::onMarkAggregatedActivityRead,
                onMarkAllRead = viewModel::onMarkAllRead,
            )
    }
}

@Composable
private fun NotificationsScreen(
    state: FeedState,
    onMarkAllSeen: () -> Unit,
    onMarkAggregatedActivityRead: (AggregatedActivityData) -> Unit,
    onMarkAllRead: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(16.dp)) {
        // Title
        Text(
            text = "Notifications",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        val notificationStatus by state.notificationStatus.collectAsStateWithLifecycle()
        // Mark all read button
        if ((notificationStatus?.unread ?: 0) > 0) {
            Button(onClick = onMarkAllRead, modifier = Modifier.padding(bottom = 16.dp)) {
                Text("Mark all read")
            }
        }

        // Notifications
        val activities by state.aggregatedActivities.collectAsStateWithLifecycle()
        LazyColumn {
            items(activities) {
                val isActivityRead = notificationStatus?.readActivities?.contains(it.group) == true
                NotificationItem(
                    data = it,
                    isActivityRead = isActivityRead,
                    onMarkRead = { onMarkAggregatedActivityRead(it) },
                )
            }
        }

        // Mark all notifications (aggregated activities) as seen when the screen is opened
        val unseen = notificationStatus?.unseen ?: 0
        LaunchedEffect(unseen) {
            if (unseen > 0) {
                onMarkAllSeen()
            }
        }
    }
}

@Composable
private fun NotificationItem(
    data: AggregatedActivityData,
    isActivityRead: Boolean,
    onMarkRead: () -> Unit,
) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .clickable(enabled = !isActivityRead, onClick = onMarkRead)
                .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UserAvatar(data.activities.lastOrNull()?.user?.image)
        Spacer(Modifier.width(8.dp))
        Text(text = data.displayText, modifier = Modifier.weight(1f))
        if (!isActivityRead) {
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape))
        }
    }
}

private val AggregatedActivityData.displayText: String
    get() {
        if (activities.isEmpty()) return ""
        val firstUser = activities.last().user.name ?: "Someone"
        val actionText = displayTextForAggregationType(activities.first().type)
        val otherUsers = if (userCount == 2) "other" else "others"
        val text =
            if (userCount > 1) {
                "$firstUser and ${userCount - 1} $otherUsers $actionText"
            } else {
                "$firstUser $actionText"
            }
        return text
    }

private fun displayTextForAggregationType(type: String): String {
    return when (type) {
        "comment" -> "commented on your activity"
        "comment_reaction" -> "reacted on your comment"
        "reaction" -> "reacted on your activity"
        "follow" -> "followed you"
        else -> ""
    }
}
