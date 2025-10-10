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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.model.AggregatedActivityData
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.network.models.MarkActivityRequest
import io.getstream.feeds.android.sample.login.LoginManager
import io.getstream.feeds.android.sample.util.AsyncResource
import io.getstream.feeds.android.sample.util.Feeds
import io.getstream.feeds.android.sample.util.map
import io.getstream.feeds.android.sample.util.notNull
import io.getstream.feeds.android.sample.util.withFirstContent
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class NotificationsViewModel @Inject constructor(loginManager: LoginManager) : ViewModel() {

    private val feed =
        flow { emit(AsyncResource.notNull(loginManager.currentClient()?.let(::getFeed))) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    val state =
        feed
            .map { state -> state.map(Feed::state) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    init {
        feed.withFirstContent(viewModelScope) { getOrCreate() }
    }

    fun onMarkAllSeen() {
        feed.withFirstContent(viewModelScope) {
            // Check if the notification status is already set to seen
            val notificationStatus = state.notificationStatus.value
            if ((notificationStatus?.unseen ?: 0) == 0) {
                return@withFirstContent
            }
            // Mark all notifications as seen
            val request = MarkActivityRequest(markAllSeen = true)
            markActivity(request)
        }
    }

    fun onMarkAggregatedActivityRead(activity: AggregatedActivityData) {
        feed.withFirstContent(viewModelScope) {
            // Check that the activity is not already read
            val notificationStatus = state.notificationStatus.value
            if (notificationStatus?.readActivities?.contains(activity.group) == true) {
                return@withFirstContent
            }
            // Mark the aggregated activity as read
            val request = MarkActivityRequest(markRead = listOf(activity.group))
            markActivity(request)
        }
    }

    fun onMarkAllRead() {
        feed.withFirstContent(viewModelScope) {
            val request = MarkActivityRequest(markAllRead = true)
            markActivity(request)
        }
    }

    private fun getFeed(client: FeedsClient): Feed =
        client.feed(Feeds.notifications(client.user.id))
}
