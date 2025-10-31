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

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.android.core.api.utils.flatMap
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedSuggestionData
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.sample.login.LoginManager
import io.getstream.feeds.android.sample.util.AsyncResource
import io.getstream.feeds.android.sample.util.Feeds
import io.getstream.feeds.android.sample.util.map
import io.getstream.feeds.android.sample.util.notNull
import io.getstream.feeds.android.sample.util.withFirstContent
import io.getstream.feeds.android.sample.utils.logResult
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class ProfileViewModel @Inject constructor(loginManager: LoginManager) : ViewModel() {

    val state =
        flow { emit(AsyncResource.notNull(loginManager.currentClient())) }
            .map { loadingState -> loadingState.map(::toState) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    private val _followSuggestions: MutableStateFlow<List<FeedData>> = MutableStateFlow(emptyList())
    val followSuggestions: StateFlow<List<FeedData>> = _followSuggestions.asStateFlow()

    init {
        state.withFirstContent(viewModelScope) {
            feed.getOrCreate().logResult(TAG, "Getting the profile feed")
            _followSuggestions.value =
                // We query suggestions from a user feed because we want to follow those, not other
                // timelines.
                client
                    .feed(Feeds.user(client.user.id))
                    .queryFollowSuggestions(10)
                    .map { suggestions -> suggestions.map(FeedSuggestionData::feed) }
                    .logResult(TAG, "Getting follow suggestions")
                    .getOrDefault(emptyList())
        }
    }

    fun follow(feedId: FeedId) {
        state.withFirstContent(viewModelScope) {
            feed
                .follow(feedId, createNotificationActivity = true)
                .onSuccess {
                    // Update the follow suggestions after following a feed
                    _followSuggestions.update {
                        it.filter { suggestion -> suggestion.fid != feedId }
                    }
                }
                .onFailure { Log.e(TAG, "Failed to follow feed: $feedId", it) }
                .flatMap {
                    // Also make `stories:user_id` follow `story:their_id` to follow stories.
                    client.feed(Feeds.stories(client.user.id)).follow(Feeds.story(feedId.id))
                }
                .onFailure { Log.e(TAG, "Failed to follow stories feed for: ${feedId.id}", it) }
        }
    }

    fun unfollow(feedId: FeedId) {
        state.withFirstContent(viewModelScope) {
            feed
                .unfollow(feedId)
                .logResult(TAG, "Unfollowing feed: $feedId")
                .flatMap {
                    client.feed(Feeds.stories(client.user.id)).unfollow(Feeds.story(feedId.id))
                }
                .logResult(TAG, "Unfollowing stories feed for: ${feedId.id}")
        }
    }

    private fun toState(client: FeedsClient): State {
        val profileFeedQuery =
            FeedQuery(
                fid = Feeds.timeline(client.user.id),
                activityLimit = 0, // We don't need activities for the profile feed
                followerLimit = 10, // Load first 10 followers
                followingLimit = 10, // Load first 10 followings
            )
        return State(client, client.feed(profileFeedQuery))
    }

    data class State(val client: FeedsClient, val feed: Feed)

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
