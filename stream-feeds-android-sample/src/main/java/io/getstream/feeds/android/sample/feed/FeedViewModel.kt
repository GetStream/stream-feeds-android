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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.generated.destinations.FeedsScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedAddActivityRequest
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedInputData
import io.getstream.feeds.android.client.api.model.FeedMemberRequestData
import io.getstream.feeds.android.client.api.model.FeedVisibility
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.network.models.AddReactionRequest
import io.getstream.feeds.android.network.models.CreatePollRequest
import io.getstream.feeds.android.network.models.CreatePollRequest.VotingVisibility.Anonymous
import io.getstream.feeds.android.network.models.CreatePollRequest.VotingVisibility.Public
import io.getstream.feeds.android.network.models.PollOptionInput
import io.getstream.feeds.android.network.models.UpdateActivityRequest
import io.getstream.feeds.android.sample.login.LoginManager
import io.getstream.feeds.android.sample.util.AsyncResource
import io.getstream.feeds.android.sample.util.copyToCache
import io.getstream.feeds.android.sample.util.deleteFiles
import io.getstream.feeds.android.sample.util.getOrNull
import io.getstream.feeds.android.sample.util.map
import io.getstream.feeds.android.sample.util.notNull
import io.getstream.feeds.android.sample.util.withFirstContent
import io.getstream.feeds.android.sample.utils.logResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val application: Application,
    loginManager: LoginManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val args = FeedsScreenDestination.argsFrom(savedStateHandle)

    private val userState = flow {
        emit(AsyncResource.notNull(loginManager.currentState()))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    private val feed = userState
        .map { it.map(::getFeed) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    val state = feed
        .map { asyncResource -> asyncResource.map(Feed::state) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    val pollController = FeedPollController(
        scope = viewModelScope,
        feedsClient = { userState.value.getOrNull()?.client },
        fid = args.fid
    )

    // Notification feed
    private val notificationFid = FeedId("notification", args.userId)
    private val notificationFeed = userState
        .map { asyncResource ->
            asyncResource.map { userState -> userState.client.feed(FeedQuery(notificationFid)) }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    val notificationStatus = notificationFeed
        .flatMapLatest { it.getOrNull()?.state?.notificationStatus ?: emptyFlow() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        feed.withFirstContent(viewModelScope) {
            getOrCreate()
        }
        notificationFeed.withFirstContent(viewModelScope) {
            getOrCreate()
        }
    }

    fun onLoadMore() {
        feed.withFirstContent(viewModelScope) {
            if (!state.canLoadMoreActivities) return@withFirstContent
            queryMoreActivities()
                .logResult(TAG, "Loading more activities for feed: $fid")
        }
    }

    fun onHeartClick(activity: ActivityData) {
        if (activity.ownReactions.isEmpty()) {
            // Add 'heart' reaction
            feed.withFirstContent(viewModelScope) {
                val request = AddReactionRequest("heart", createNotificationActivity = true)
                addReaction(activity.id, request)
            }
        } else {
            // Remove 'heart' reaction
            feed.withFirstContent(viewModelScope) {
                deleteReaction(activity.id, "heart")
            }
        }
    }

    fun onRepostClick(activity: ActivityData, text: String?) {
        feed.withFirstContent(viewModelScope) {
            repost(activity.id, text = text)
        }
    }

    fun onBookmarkClick(activity: ActivityData) {
        if (activity.ownBookmarks.isEmpty()) {
            // Add bookmark
            feed.withFirstContent(viewModelScope) {
                addBookmark(activity.id)
            }
        } else {
            // Remove bookmark
            feed.withFirstContent(viewModelScope) {
                deleteBookmark(activity.id)
            }
        }
    }

    fun onDeleteClick(activityId: String) {
        feed.withFirstContent(viewModelScope) {
            deleteActivity(activityId)
                .logResult(TAG, "Deleting activity: $activityId")
        }
    }

    fun onEditActivity(activityId: String, text: String) {
        feed.withFirstContent(viewModelScope) {
            updateActivity(activityId, UpdateActivityRequest(text = text))
                .logResult(TAG, "Updating activity: $activityId with text: $text")
        }
    }

    fun onCreatePost(text: String, attachments: List<Uri>) {
        feed.withFirstContent(viewModelScope) {
            val attachmentFiles = application.copyToCache(attachments).getOrElse { error ->
                Log.e(TAG, "Failed to copy attachments", error)
                return@withFirstContent
            }

            addActivity(
                FeedAddActivityRequest(
                    type = "activity",
                    text = text,
                    feeds = listOf(fid.rawValue),
                    attachmentUploads = attachmentFiles.map {
                        FeedUploadPayload(it, FileType.Image("jpeg"))
                    }
                ),
                attachmentUploadProgress = { file, progress ->
                    Log.d(TAG, "Uploading attachment: ${file.type}, progress: $progress")
                }
            ).logResult(TAG, "Creating activity with text: $text")

            deleteFiles(attachmentFiles)
        }
    }

    fun onCreatePoll(poll: PollFormData) {
        feed.withFirstContent(viewModelScope) {
            val request = CreatePollRequest(
                name = poll.question,
                options = poll.options.map(::PollOptionInput),
                allowAnswers = poll.allowComments,
                allowUserSuggestedOptions = poll.allowSuggestingOptions,
                enforceUniqueVote = !poll.allowMultipleAnswers,
                maxVotesAllowed = poll.maxVotesPerPerson.toIntOrNull()
                    .takeIf { poll.constrainMaxVotesPerPerson },
                votingVisibility = if (poll.anonymousPoll) Anonymous else Public,
            )

            createPoll(request = request, activityType = "activity")
                .logResult(TAG, "Creating poll with question: ${poll.question}")
        }
    }

    private fun getFeed(userState: LoginManager.UserState): Feed {
        val query = FeedQuery(
            fid = args.fid,
            data = FeedInputData(
                members = listOf(FeedMemberRequestData(userState.user.id)),
                visibility = FeedVisibility.Public,
            )
        )

        return userState.client.feed(query)
    }

    companion object {
        private const val TAG = "FeedViewModel"
    }
}
