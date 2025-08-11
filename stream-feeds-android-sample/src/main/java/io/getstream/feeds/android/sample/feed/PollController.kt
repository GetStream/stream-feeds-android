package io.getstream.feeds.android.sample.feed

import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feeds.android.core.generated.models.CastPollVoteRequest
import io.getstream.feeds.android.core.generated.models.VoteData
import io.getstream.feeds.android.sample.utils.logResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PollController(
    private val scope: CoroutineScope,
    private val feedsClient: FeedsClient,
    private val fid: FeedId,
) {
    private val activities: MutableMap<String, Activity> = mutableMapOf()

    fun onPollOptionSelected(activityId: String, optionId: String) {
        scope.launch {
            castVote(activityId = activityId, answerText = null, optionId = optionId)
        }
    }

    private suspend fun castVote(activityId: String, answerText: String?, optionId: String?) {
        activity(activityId)
            .castPollVote(CastPollVoteRequest(VoteData(answerText = answerText, optionId = optionId)))
            .logResult(TAG, "Casting vote for $activityId with answer $answerText and optionId $optionId")
    }

    private fun activity(id: String): Activity = activities.getOrPut(id) {
        feedsClient.activity(activityId = id, fid = fid)
    }

    companion object {
        private const val TAG = "PollController"
    }
}
