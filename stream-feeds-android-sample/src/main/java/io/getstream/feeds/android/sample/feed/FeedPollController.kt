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

package io.getstream.feeds.android.sample.feed

import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feeds.android.network.models.CastPollVoteRequest
import io.getstream.feeds.android.network.models.CreatePollOptionRequest
import io.getstream.feeds.android.network.models.VoteData
import io.getstream.feeds.android.sample.login.LoginManager
import io.getstream.feeds.android.sample.util.AsyncResource
import io.getstream.feeds.android.sample.util.Feeds
import io.getstream.feeds.android.sample.util.notNull
import io.getstream.feeds.android.sample.util.withFirstContent
import io.getstream.feeds.android.sample.utils.logResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FeedPollController(private val scope: CoroutineScope, loginManager: LoginManager) {
    private val client =
        flow { emit(AsyncResource.notNull(loginManager.currentClient())) }
            .stateIn(scope, SharingStarted.Eagerly, AsyncResource.Loading)

    private val activities: MutableMap<String, Activity> = mutableMapOf()

    fun onOptionSelected(activityId: String, optionId: String) {
        castVote(activityId = activityId, answerText = null, optionId = optionId)
    }

    fun onAddComment(activityId: String, comment: String) {
        castVote(activityId, answerText = comment, optionId = null)
    }

    private fun castVote(activityId: String, answerText: String?, optionId: String?) {
        scope.launch {
            activity(activityId)
                .castPollVote(
                    CastPollVoteRequest(VoteData(answerText = answerText, optionId = optionId))
                )
                .logResult(
                    TAG,
                    "Casting vote for $activityId with answer $answerText and optionId $optionId",
                )
        }
    }

    fun onClose(activityId: String) {
        scope.launch {
            activity(activityId)
                .closePoll()
                .logResult(TAG, "Closing poll for activity: $activityId")
        }
    }

    fun onSuggestOption(activityId: String, optionText: String) {
        scope.launch {
            activity(activityId)
                .createPollOption(CreatePollOptionRequest(text = optionText))
                .logResult(TAG, "Suggesting option '$optionText' for activity: $activityId")
        }
    }

    private suspend fun activity(id: String): Activity {
        activities[id]?.let {
            return it
        }
        return client.withFirstContent {
            activity(activityId = id, fid = Feeds.timeline(user.id)).also { activities[id] = it }
        }
    }

    companion object {
        private const val TAG = "PollController"
    }
}
