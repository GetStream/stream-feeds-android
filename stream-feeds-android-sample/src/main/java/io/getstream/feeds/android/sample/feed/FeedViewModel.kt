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
import io.getstream.feeds.android.client.api.FeedsClient
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
import io.getstream.feeds.android.network.models.MarkActivityRequest
import io.getstream.feeds.android.network.models.PollOptionInput
import io.getstream.feeds.android.network.models.UpdateActivityRequest
import io.getstream.feeds.android.sample.login.LoginManager
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

    val viewState =
        flow { emit(AsyncResource.notNull(loginManager.currentClient())) }
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
            timeline.followSelfIfNeeded(ownTimeline.fid)
        }
        viewState.withFirstContent(viewModelScope) {
            stories.getOrCreate().notifyOnFailure { "Error getting the stories" }
            ownStories.getOrCreate()
            stories.followSelfIfNeeded(ownStories.fid)
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
                timeline.addActivityReaction(activity.id, request).notifyOnFailure {
                    "Failed to add reaction"
                }
            }
        } else {
            // Remove reaction
            viewState.withFirstContent(viewModelScope) {
                timeline.deleteActivityReaction(activity.id, reaction.value).notifyOnFailure {
                    "Failed to delete reaction"
                }
            }
        }
    }

    fun onRepostClick(activity: ActivityData, text: String?) {
        viewState.withFirstContent(viewModelScope) {
            ownTimeline.repost(activity.id, text = text).notifyOnFailure {
                "Failed to repost activity"
            }
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

            val postingFeed = if (isStory) ownStories else ownTimeline

            val result =
                postingFeed
                    .addActivity(
                        request =
                            addActivityRequest(postingFeed.fid, text, isStory, attachmentFiles),
                        attachmentUploadProgress = { file, progress ->
                            Log.d(TAG, "Uploading attachment: ${file.type}, progress: $progress")
                        },
                    )
                    .logResult(TAG, "Creating activity with text: $text")
                    .notifyOnFailure { "Failed to create post" }
                    .onSuccess {
                        // Creating a story doesn't trigger an update to aggregated activities
                        // (stories are aggregated by user), so we refetch after posting
                        if (isStory) {
                            stories.getOrCreate()
                        }
                    }

            deleteFiles(attachmentFiles)

            _createContentState.value =
                result.fold(
                    onSuccess = { CreateContentState.Hidden },
                    onFailure = { CreateContentState.Composing },
                )
        }
    }

    fun onStoryWatched(storyId: String) {
        viewState.withFirstContent(viewModelScope) {
            stories
                .markActivity(MarkActivityRequest(markWatched = listOf(storyId)))
                .notifyOnFailure { "Failed to mark story as watched" }
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

            ownTimeline
                .createPoll(request = request, activityType = "activity")
                .logResult(TAG, "Creating poll with question: ${poll.question}")
                .notifyOnFailure { "Failed to create poll" }
        }
    }

    private fun toState(client: FeedsClient): ViewState {
        val userId = client.user.id
        val timelineQuery = feedQuery(Feeds.timeline(userId), userId)
        val storiesQuery = feedQuery(Feeds.stories(userId), userId)
        val ownStoriesQuery = feedQuery(Feeds.story(userId), userId)

        return ViewState(
            userId = userId,
            userImage = client.user.imageURL,
            timeline = client.feed(timelineQuery),
            ownTimeline = client.feed(Feeds.user(userId)),
            stories = client.feed(storiesQuery),
            ownStories = client.feed(ownStoriesQuery),
            notifications = client.feed(Feeds.notifications(userId)),
        )
    }

    private fun feedQuery(feedId: FeedId, userId: String) =
        FeedQuery(
            fid = feedId,
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
        val timeline: Feed,
        val ownTimeline: Feed,
        val stories: Feed,
        val ownStories: Feed,
        val notifications: Feed,
    )

    companion object {
        private const val TAG = "FeedViewModel"
    }
}
