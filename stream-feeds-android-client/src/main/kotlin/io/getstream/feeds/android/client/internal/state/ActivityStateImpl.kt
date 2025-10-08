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
package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.castVote
import io.getstream.feeds.android.client.api.model.deleteBookmark
import io.getstream.feeds.android.client.api.model.removeReaction
import io.getstream.feeds.android.client.api.model.removeVote
import io.getstream.feeds.android.client.api.model.setClosed
import io.getstream.feeds.android.client.api.model.update
import io.getstream.feeds.android.client.api.model.upsertBookmark
import io.getstream.feeds.android.client.api.model.upsertReaction
import io.getstream.feeds.android.client.api.state.ActivityCommentListState
import io.getstream.feeds.android.client.api.state.ActivityState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An observable object representing the current state of an activity.
 *
 * This class manages the state of a single activity including its comments, poll data, and
 * real-time updates. It automatically updates when WebSocket events are received and provides
 * change handlers for state modifications.
 *
 * @property activityCommentListState The mutable state for the list of comments associated with the
 *   activity.
 */
internal class ActivityStateImpl(
    private val currentUserId: String,
    private val activityCommentListState: ActivityCommentListState,
) : ActivityMutableState {

    private val _activity: MutableStateFlow<ActivityData?> = MutableStateFlow(null)
    private val _poll: MutableStateFlow<PollData?> = MutableStateFlow(null)

    override val activity: StateFlow<ActivityData?>
        get() = _activity.asStateFlow()

    override val comments: StateFlow<List<ThreadedCommentData>>
        get() = activityCommentListState.comments

    override val poll: StateFlow<PollData?>
        get() = _poll.asStateFlow()

    override fun onActivityUpdated(activity: ActivityData) {
        _activity.update { current -> current?.update(activity) ?: activity }
        _poll.update { current ->
            if (activity.poll == null) {
                null
            } else {
                current?.update(activity.poll) ?: activity.poll
            }
        }
    }

    override fun onReactionUpserted(reaction: FeedsReactionData, activity: ActivityData) {
        _activity.update { current -> current?.upsertReaction(activity, reaction, currentUserId) }
    }

    override fun onReactionRemoved(reaction: FeedsReactionData, activity: ActivityData) {
        _activity.update { current -> current?.removeReaction(activity, reaction, currentUserId) }
    }

    override fun onBookmarkRemoved(bookmark: BookmarkData) {
        _activity.update { current -> current?.deleteBookmark(bookmark, currentUserId) }
    }

    override fun onBookmarkUpserted(bookmark: BookmarkData) {
        _activity.update { current -> current?.upsertBookmark(bookmark, currentUserId) }
    }

    override fun onPollClosed(poll: PollData) {
        updatePoll(poll.id, PollData::setClosed)
    }

    override fun onPollDeleted(pollId: String) {
        updatePoll(pollId) { null }
    }

    override fun onPollUpdated(poll: PollData) {
        updatePoll(poll.id) { update(poll) }
    }

    override fun onPollVoteCasted(vote: PollVoteData, pollId: String) {
        updatePoll(pollId) { castVote(vote, currentUserId) }
    }

    override fun onPollVoteChanged(vote: PollVoteData, pollId: String) {
        updatePoll(pollId) { castVote(vote, currentUserId) }
    }

    override fun onPollVoteRemoved(vote: PollVoteData, pollId: String) {
        updatePoll(pollId) { removeVote(vote, currentUserId) }
    }

    private fun updatePoll(pollId: String, update: PollData.() -> PollData?) {
        if (_poll.value?.id != pollId) return

        var updated: PollData? = null
        _poll.update { current -> current?.let(update).also { updated = it } }
        _activity.update { current -> current?.copy(poll = updated) }
    }
}

/**
 * Mutable state interface for activity state management.
 *
 * This interface extends the [ActivityState] and [ActivityStateUpdates] interfaces to provide a
 * mutable state representation of an activity, allowing updates to the activity and its associated
 * poll data.
 */
internal interface ActivityMutableState : ActivityState, ActivityStateUpdates

/**
 * Interface for handling updates to the activity state.
 *
 * This interface defines methods that are called when the activity or its associated poll is
 * updated, closed, deleted, or when votes are casted, changed, or removed.
 */
internal interface ActivityStateUpdates {

    /**
     * Called when the activity is updated.
     *
     * @param activity The updated activity data.
     */
    fun onActivityUpdated(activity: ActivityData)

    /**
     * Called when a reaction is added to or updated in the activity.
     *
     * @param reaction The reaction that was added or updated.
     * @param activity The activity the reaction belongs to.
     */
    fun onReactionUpserted(reaction: FeedsReactionData, activity: ActivityData)

    /**
     * Called when a reaction is removed from the activity.
     *
     * @param reaction The reaction that was removed.
     * @param activity The activity from which the reaction was removed.
     */
    fun onReactionRemoved(reaction: FeedsReactionData, activity: ActivityData)

    /**
     * Called when a bookmark is removed from the activity.
     *
     * @param bookmark The bookmark that was deleted.
     */
    fun onBookmarkRemoved(bookmark: BookmarkData)

    /**
     * Called when a bookmark is added to or updated in an activity.
     *
     * @param bookmark The bookmark that was added or updated.
     */
    fun onBookmarkUpserted(bookmark: BookmarkData)

    /**
     * Called when the associated poll is closed.
     *
     * @param poll The updated poll data.
     */
    fun onPollClosed(poll: PollData)

    /**
     * Called when the associated poll is deleted.
     *
     * @param pollId The ID of the deleted poll.
     */
    fun onPollDeleted(pollId: String)

    /**
     * Called when the associated poll is updated.
     *
     * @param poll The updated poll data.
     */
    fun onPollUpdated(poll: PollData)

    /**
     * Called when a vote is casted on the poll.
     *
     * @param vote The vote that was casted.
     * @param pollId The ID of the poll associated with the vote.
     */
    fun onPollVoteCasted(vote: PollVoteData, pollId: String)

    /**
     * Called when a vote is changed on the poll.
     *
     * @param vote The updated vote data.
     * @param pollId The ID of the poll associated with the changed vote.
     */
    fun onPollVoteChanged(vote: PollVoteData, pollId: String)

    /**
     * Called when a vote is removed from the poll.
     *
     * @param vote The vote that was removed.
     * @param pollId The ID of the poll associated with the removed vote.
     */
    fun onPollVoteRemoved(vote: PollVoteData, pollId: String)
}
