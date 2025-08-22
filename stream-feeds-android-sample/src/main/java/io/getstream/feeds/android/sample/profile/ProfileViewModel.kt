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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.generated.destinations.ProfileScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.sample.login.LoginManager
import io.getstream.feeds.android.sample.util.AsyncResource
import io.getstream.feeds.android.sample.util.map
import io.getstream.feeds.android.sample.util.notNull
import io.getstream.feeds.android.sample.util.withFirstContent
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
class ProfileViewModel
@Inject
constructor(loginManager: LoginManager, savedStateHandle: SavedStateHandle) : ViewModel() {

    private val fid = ProfileScreenDestination.argsFrom(savedStateHandle).fid

    private val profileFeedQuery =
        FeedQuery(
            fid = fid,
            activityLimit = 0, // We don't need activities for the profile feed
            followerLimit = 10, // Load first 10 followers
            followingLimit = 10, // Load first 10 followings
        )

    private val feed =
        flow {
                emit(
                    AsyncResource.notNull(
                        loginManager.currentState()?.client?.feed(profileFeedQuery)
                    )
                )
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    val state =
        feed
            .map { loadingState -> loadingState.map(Feed::state) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    private val _followSuggestions: MutableStateFlow<List<FeedData>> = MutableStateFlow(emptyList())
    val followSuggestions: StateFlow<List<FeedData>> = _followSuggestions.asStateFlow()

    init {
        feed.withFirstContent(viewModelScope) {
            getOrCreate()
                .onSuccess {
                    queryFollowSuggestions(limit = 10).onSuccess { _followSuggestions.value = it }
                }
                .onFailure { Log.e(TAG, "Failed to get or create feed: $fid", it) }
        }
    }

    fun follow(feedId: FeedId) {
        feed.withFirstContent(viewModelScope) {
            follow(feedId)
                .onSuccess {
                    // Update the follow suggestions after following a feed
                    _followSuggestions.update {
                        it.filter { suggestion -> suggestion.fid != feedId }
                    }
                }
                .onFailure { Log.e(TAG, "Failed to follow feed: $feedId", it) }
        }
    }

    fun unfollow(feedId: FeedId) {
        feed.withFirstContent(viewModelScope) {
            unfollow(feedId)
                .onSuccess { Log.d(TAG, "Successfully unfollowed feed: $it") }
                .onFailure { Log.e(TAG, "Failed to unfollow feed: $feedId", it) }
        }
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
