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
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.android.core.api.filter.doesNotExist
import io.getstream.android.core.api.filter.exists
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedAddActivityRequest
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedInputData
import io.getstream.feeds.android.client.api.model.FeedMemberRequestData
import io.getstream.feeds.android.client.api.model.FeedVisibility
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.query.ActivitiesFilter
import io.getstream.feeds.android.client.api.state.query.ActivitiesFilterField
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.network.models.AddReactionRequest
import io.getstream.feeds.android.network.models.CreatePollRequest
import io.getstream.feeds.android.network.models.CreatePollRequest.VotingVisibility.Anonymous
import io.getstream.feeds.android.network.models.CreatePollRequest.VotingVisibility.Public
import io.getstream.feeds.android.network.models.PollOptionInput
import io.getstream.feeds.android.network.models.UpdateActivityRequest
import io.getstream.feeds.android.sample.login.LoginManager
import io.getstream.feeds.android.sample.login.LoginManager.UserState
import io.getstream.feeds.android.sample.util.AsyncResource
import io.getstream.feeds.android.sample.util.Feeds
import io.getstream.feeds.android.sample.util.copyToCache
import io.getstream.feeds.android.sample.util.deleteFiles
import io.getstream.feeds.android.sample.util.map
import io.getstream.feeds.android.sample.util.notNull
import io.getstream.feeds.android.sample.util.withFirstContent
import io.getstream.feeds.android.sample.utils.logResult
import java.io.File
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class FeedViewModel
@Inject
constructor(private val application: Application, loginManager: LoginManager) : ViewModel() {
    private val userState =
        flow { emit(AsyncResource.notNull(loginManager.currentState())) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    val viewState =
        userState
            .map { it.map(::toState) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    private val _createContentState = MutableStateFlow(CreateContentState.Hidden)
    val createContentState: StateFlow<CreateContentState> = _createContentState.asStateFlow()

    val pollController = FeedPollController(viewModelScope, loginManager)

    private val errorChannel = Channel<String>()
    val error = errorChannel.receiveAsFlow()

    init {
        viewState.withFirstContent(viewModelScope) {
            timeline.getOrCreate().notifyOnFailure { "Error getting the timeline" }
            timeline.followSelfIfNeeded(ownFeed.fid)
        }
        viewState.withFirstContent(viewModelScope) {
            stories.getOrCreate().notifyOnFailure { "Error getting the stories" }
        }
        viewState.withFirstContent(viewModelScope) { notifications.getOrCreate() }
    }

    // By default, `timeline:user_id` does not follow `user:user_id`, i.e. the timeline wouldn't
    // show the user's own posts. So we add the follow ourselves. Awkward in clients, this is
    // usually done in the backend.
    private suspend fun Feed.followSelfIfNeeded(userFeedId: FeedId) {
        // Check if we are already following our own user feed
        if (state.following.first().none { it.targetFeed.fid == userFeedId }) {
            follow(userFeedId, createNotificationActivity = false).logResult(TAG, "Following self")
        }
    }

    fun onLoadMore() {
        viewState.withFirstContent(viewModelScope) {
            if (!timeline.state.canLoadMoreActivities) return@withFirstContent
            timeline
                .queryMoreActivities()
                .logResult(TAG, "Loading more activities for feed: ${timeline.fid}")
                .notifyOnFailure { "Failed to load more activities" }
        }
    }

    fun onReactionClick(activity: ActivityData, reaction: Reaction) {
        if (activity.ownReactions.none { it.type == reaction.value }) {
            // Add reaction
            viewState.withFirstContent(viewModelScope) {
                val request = AddReactionRequest(reaction.value, createNotificationActivity = true)
                timeline.addReaction(activity.id, request).notifyOnFailure {
                    "Failed to add reaction"
                }
            }
        } else {
            // Remove reaction
            viewState.withFirstContent(viewModelScope) {
                timeline.deleteReaction(activity.id, reaction.value).notifyOnFailure {
                    "Failed to delete reaction"
                }
            }
        }
    }

    fun onRepostClick(activity: ActivityData, text: String?) {
        viewState.withFirstContent(viewModelScope) {
            ownFeed.repost(activity.id, text = text).notifyOnFailure { "Failed to repost activity" }
        }
    }

    fun onBookmarkClick(activity: ActivityData) {
        if (activity.ownBookmarks.isEmpty()) {
            // Add bookmark
            viewState.withFirstContent(viewModelScope) {
                timeline.addBookmark(activity.id).notifyOnFailure { "Failed to add bookmark" }
            }
        } else {
            // Remove bookmark
            viewState.withFirstContent(viewModelScope) {
                timeline.deleteBookmark(activity.id).notifyOnFailure { "Failed to delete bookmark" }
            }
        }
    }

    fun onDeleteClick(activityId: String) {
        viewState.withFirstContent(viewModelScope) {
            timeline
                .deleteActivity(activityId)
                .logResult(TAG, "Deleting activity: $activityId")
                .notifyOnFailure { "Failed to delete activity" }
        }
    }

    fun onEditActivity(activityId: String, text: String) {
        viewState.withFirstContent(viewModelScope) {
            timeline
                .updateActivity(activityId, UpdateActivityRequest(text = text))
                .logResult(TAG, "Updating activity: $activityId with text: $text")
                .notifyOnFailure { "Failed to edit activity" }
        }
    }

    fun onCreatePostClick() {
        _createContentState.value = CreateContentState.Composing
    }

    fun onContentCreateDismiss() {
        _createContentState.value = CreateContentState.Hidden
    }

    fun onCreatePost(text: String, attachments: List<Uri>, isStory: Boolean) {
        _createContentState.value = CreateContentState.Posting

        viewState.withFirstContent(viewModelScope) {
            val attachmentFiles =
                application
                    .copyToCache(attachments)
                    .notifyOnFailure { "Failed to copy attachments" }
                    .getOrElse { error ->
                        Log.e(TAG, "Failed to copy attachments", error)
                        _createContentState.value = CreateContentState.Composing
                        return@withFirstContent
                    }

            val result =
                ownFeed
                    .addActivity(
                        request = addActivityRequest(ownFeed.fid, text, isStory, attachmentFiles),
                        attachmentUploadProgress = { file, progress ->
                            Log.d(TAG, "Uploading attachment: ${file.type}, progress: $progress")
                        },
                    )
                    .logResult(TAG, "Creating activity with text: $text")
                    .notifyOnFailure { "Failed to create post" }

            deleteFiles(attachmentFiles)

            _createContentState.value =
                result.fold(
                    onSuccess = { CreateContentState.Hidden },
                    onFailure = { CreateContentState.Composing },
                )
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun addActivityRequest(
        feedId: FeedId,
        text: String,
        isStory: Boolean,
        attachments: List<File>,
    ) =
        FeedAddActivityRequest(
            type = "activity",
            text = text,
            feeds = listOf(feedId.rawValue),
            expiresAt = if (isStory) Clock.System.now().plus(1.days).toString() else null,
            attachmentUploads = attachments.map { FeedUploadPayload(it, FileType.Image("jpeg")) },
        )

    fun onCreatePoll(poll: PollFormData) {
        viewState.withFirstContent(viewModelScope) {
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

            ownFeed
                .createPoll(request = request, activityType = "activity")
                .logResult(TAG, "Creating poll with question: ${poll.question}")
                .notifyOnFailure { "Failed to create poll" }
        }
    }

    private fun toState(userState: UserState): ViewState {
        val userId = userState.user.id
        val timelineQuery = feedQuery(userId, ActivitiesFilterField.expiresAt.doesNotExist())
        val storiesQuery = feedQuery(userId, ActivitiesFilterField.expiresAt.exists())

        return ViewState(
            userId = userId,
            userImage = userState.user.imageURL,
            ownFeed = userState.client.feed(Feeds.user(userId)),
            timeline = userState.client.feed(timelineQuery),
            stories = userState.client.feed(storiesQuery),
            notifications = userState.client.feed(Feeds.notifications(userId)),
        )
    }

    private fun feedQuery(userId: String, filter: ActivitiesFilter) =
        FeedQuery(
            fid = Feeds.timeline(userId),
            activityFilter = filter,
            followingLimit = 10,
            data =
                FeedInputData(
                    members = listOf(FeedMemberRequestData(userId)),
                    visibility = FeedVisibility.Public,
                ),
        )

    private suspend inline fun <T> Result<T>.notifyOnFailure(message: () -> String) = onFailure {
        errorChannel.send("${message()}: ${it.message}")
    }

    data class ViewState(
        val userId: String,
        val userImage: String?,
        val ownFeed: Feed,
        val timeline: Feed,
        val stories: Feed,
        val notifications: Feed,
    )

    companion object {
        private const val TAG = "FeedViewModel"
    }
}
