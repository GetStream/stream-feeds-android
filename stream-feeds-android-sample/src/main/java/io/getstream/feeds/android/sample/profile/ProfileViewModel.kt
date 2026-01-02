/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedSuggestionData
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.network.models.FollowBatchRequest
import io.getstream.feeds.android.network.models.FollowPair
import io.getstream.feeds.android.network.models.FollowRequest
import io.getstream.feeds.android.network.models.UnfollowBatchRequest
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
            val requests =
                listOf(
                    FollowRequest(
                        source = feed.fid.rawValue,
                        target = feedId.rawValue,
                        createNotificationActivity = true,
                    ),
                    FollowRequest(
                        source = Feeds.stories(client.user.id).rawValue,
                        target = Feeds.story(feedId.id).rawValue,
                    ),
                )
            client
                .getOrCreateFollows(FollowBatchRequest(requests))
                .onSuccess {
                    // Update the follow suggestions after following a feed
                    _followSuggestions.update {
                        it.filter { suggestion -> suggestion.fid != feedId }
                    }
                }
                .logResult(TAG, "Following: $feedId")
        }
    }

    fun unfollow(feedId: FeedId) {
        state.withFirstContent(viewModelScope) {
            val followPairs =
                listOf(
                    FollowPair(source = feed.fid.rawValue, target = feedId.rawValue),
                    FollowPair(
                        source = Feeds.stories(client.user.id).rawValue,
                        target = Feeds.story(feedId.id).rawValue,
                    ),
                )

            client
                .getOrCreateUnfollows(UnfollowBatchRequest(followPairs))
                .logResult(TAG, "Unfollowing: $feedId")
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
