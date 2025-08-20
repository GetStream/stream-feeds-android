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

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedAddActivityRequest
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedInputData
import io.getstream.feeds.android.client.api.model.FeedMemberRequestData
import io.getstream.feeds.android.client.api.model.FeedVisibility
import io.getstream.feeds.android.client.api.state.FeedState
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.core.generated.models.AddReactionRequest
import io.getstream.feeds.android.core.generated.models.CreatePollRequest
import io.getstream.feeds.android.core.generated.models.CreatePollRequest.VotingVisibility.Anonymous
import io.getstream.feeds.android.core.generated.models.CreatePollRequest.VotingVisibility.Public
import io.getstream.feeds.android.core.generated.models.PollOptionInput
import io.getstream.feeds.android.core.generated.models.UpdateActivityRequest
import io.getstream.feeds.android.sample.util.copyToCache
import io.getstream.feeds.android.sample.util.deleteFiles
import io.getstream.feeds.android.sample.utils.logResult
import kotlinx.coroutines.launch

class FeedViewModel(
    private val currentUserId: String,
    private val fid: FeedId,
    private val feedsClient: FeedsClient,
    private val application: Application,
) : ViewModel() {

    val pollController = FeedPollController(viewModelScope, feedsClient, fid)

    // User feed
    private val query =
        FeedQuery(
            fid = fid,
            data =
                FeedInputData(
                    members = listOf(FeedMemberRequestData(currentUserId)),
                    visibility = FeedVisibility.Public,
                ),
        )
    private val feed = feedsClient.feed(query)

    // Notification feed
    private val notificationFid = FeedId("notification", currentUserId)
    private val notificationFeed = feedsClient.feed(notificationFid)

    val state: FeedState
        get() = feed.state

    val notificationState: FeedState
        get() = notificationFeed.state

    init {
        viewModelScope.launch {
            feed.getOrCreate()
        }
        viewModelScope.launch {
            notificationFeed.getOrCreate()
        }
    }

    fun onLoadMore() {
        if (!state.canLoadMoreActivities) return
        viewModelScope.launch {
            feed.queryMoreActivities().logResult(TAG, "Loading more activities for feed: $fid")
        }
    }

    fun onHeartClick(activity: ActivityData) {
        if (activity.ownReactions.isEmpty()) {
            // Add 'heart' reaction
            viewModelScope.launch {
                val request = AddReactionRequest("heart", createNotificationActivity = true)
                feed.addReaction(activity.id, request) }
        } else {
            // Remove 'heart' reaction
            viewModelScope.launch { feed.deleteReaction(activity.id, "heart") }
        }
    }

    fun onRepostClick(activity: ActivityData, text: String?) {
        viewModelScope.launch { feed.repost(activity.id, text = text) }
    }

    fun onBookmarkClick(activity: ActivityData) {
        if (activity.ownBookmarks.isEmpty()) {
            // Add bookmark
            viewModelScope.launch { feed.addBookmark(activity.id) }
        } else {
            // Remove bookmark
            viewModelScope.launch { feed.deleteBookmark(activity.id) }
        }
    }

    fun onDeleteClick(activityId: String) {
        viewModelScope.launch {
            feed.deleteActivity(activityId).logResult(TAG, "Deleting activity: $activityId")
        }
    }

    fun onEditActivity(activityId: String, text: String) {
        viewModelScope.launch {
            feed
                .updateActivity(activityId, UpdateActivityRequest(text = text))
                .logResult(TAG, "Updating activity: $activityId with text: $text")
        }
    }

    fun onCreatePost(text: String, attachments: List<Uri>) {
        viewModelScope.launch {
            val attachmentFiles =
                application.copyToCache(attachments).getOrElse { error ->
                    Log.e(TAG, "Failed to copy attachments", error)
                    return@launch
                }

            feed
                .addActivity(
                    FeedAddActivityRequest(
                        type = "activity",
                        text = text,
                        feeds = listOf(fid.rawValue),
                        attachmentUploads =
                            attachmentFiles.map { FeedUploadPayload(it, FileType.Image("jpeg")) },
                    ),
                    attachmentUploadProgress = { file, progress ->
                        Log.d(TAG, "Uploading attachment: ${file.type}, progress: $progress")
                    },
                )
                .logResult(TAG, "Creating activity with text: $text")

            deleteFiles(attachmentFiles)
        }
    }

    fun onCreatePoll(poll: PollFormData) {
        viewModelScope.launch {
            val request =
                CreatePollRequest(
                    name = poll.question,
                    options = poll.options.map(::PollOptionInput),
                    allowAnswers = poll.allowComments,
                    allowUserSuggestedOptions = poll.allowSuggestingOptions,
                    enforceUniqueVote = !poll.allowMultipleAnswers,
                    maxVotesAllowed =
                        poll.maxVotesPerPerson.toIntOrNull().takeIf {
                            poll.constrainMaxVotesPerPerson
                        },
                    votingVisibility = if (poll.anonymousPoll) Anonymous else Public,
                )

            feed
                .createPoll(request = request, activityType = "activity")
                .logResult(TAG, "Creating poll with question: ${poll.question}")
        }
    }

    companion object {
        private const val TAG = "FeedViewModel"
    }
}

fun feedViewModelFactory(currentUserId: String, fid: FeedId, feedsClient: FeedsClient) =
    viewModelFactory {
        initializer {
            FeedViewModel(
                currentUserId = currentUserId,
                fid = fid,
                feedsClient = feedsClient,
                application = checkNotNull(this[APPLICATION_KEY]),
            )
        }
    }
