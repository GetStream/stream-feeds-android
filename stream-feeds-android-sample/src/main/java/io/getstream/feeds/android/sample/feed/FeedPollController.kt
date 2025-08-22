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
package io.getstream.feeds.android.sample.feed

import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feeds.android.network.models.CastPollVoteRequest
import io.getstream.feeds.android.network.models.VoteData
import io.getstream.feeds.android.sample.utils.logResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FeedPollController(
    private val scope: CoroutineScope,
    private val feedsClient: FeedsClient,
    private val fid: FeedId,
) {
    private val activities: MutableMap<String, Activity> = mutableMapOf()

    fun onPollOptionSelected(activityId: String, optionId: String) {
        scope.launch { castVote(activityId = activityId, answerText = null, optionId = optionId) }
    }

    private suspend fun castVote(activityId: String, answerText: String?, optionId: String?) {
        activity(activityId)
            .castPollVote(
                CastPollVoteRequest(VoteData(answerText = answerText, optionId = optionId))
            )
            .logResult(
                TAG,
                "Casting vote for $activityId with answer $answerText and optionId $optionId",
            )
    }

    private fun activity(id: String): Activity =
        activities.getOrPut(id) { feedsClient.activity(activityId = id, fid = fid) }

    companion object {
        private const val TAG = "PollController"
    }
}
