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
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.ActivityCommentListState
import io.getstream.feeds.android.client.api.state.ActivityState
import io.getstream.feeds.android.client.internal.model.deleteBookmark
import io.getstream.feeds.android.client.internal.model.removeComment
import io.getstream.feeds.android.client.internal.model.removeCommentReaction
import io.getstream.feeds.android.client.internal.model.removeReaction
import io.getstream.feeds.android.client.internal.model.removeVote
import io.getstream.feeds.android.client.internal.model.update
import io.getstream.feeds.android.client.internal.model.updateFeedCapabilities
import io.getstream.feeds.android.client.internal.model.upsertBookmark
import io.getstream.feeds.android.client.internal.model.upsertComment
import io.getstream.feeds.android.client.internal.model.upsertCommentReaction
import io.getstream.feeds.android.client.internal.model.upsertReaction
import io.getstream.feeds.android.client.internal.model.upsertVote
import io.getstream.feeds.android.network.models.FeedOwnCapability
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

    override fun onActivityRemoved() {
        _activity.update { null }
        _poll.update { null }
    }

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

    override fun onActivityHidden(activityId: String, hidden: Boolean) {
        _activity.update { current ->
            if (current?.id == activityId) {
                current.copy(hidden = hidden)
            } else {
                current
            }
        }
    }

    override fun onReactionUpserted(
        reaction: FeedsReactionData,
        activity: ActivityData,
        enforceUnique: Boolean,
    ) {
        _activity.update { current ->
            current?.upsertReaction(activity, reaction, currentUserId, enforceUnique)
        }
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

    override fun onCommentRemoved(commentId: String) {
        _activity.update { current -> current?.removeComment(commentId) }
    }

    override fun onCommentUpserted(comment: CommentData) {
        _activity.update { current -> current?.upsertComment(comment) }
    }

    override fun onCommentReactionRemoved(comment: CommentData, reaction: FeedsReactionData) {
        _activity.update { current ->
            current?.removeCommentReaction(comment, reaction, currentUserId)
        }
    }

    override fun onCommentReactionUpserted(
        comment: CommentData,
        reaction: FeedsReactionData,
        enforceUnique: Boolean,
    ) {
        _activity.update { current ->
            current?.upsertCommentReaction(comment, reaction, currentUserId, enforceUnique)
        }
    }

    override fun onPollDeleted(pollId: String) {
        updatePoll(pollId) { null }
    }

    override fun onPollUpdated(poll: PollData) {
        updatePoll(poll.id) { update(poll) }
    }

    override fun onPollVoteRemoved(vote: PollVoteData, pollId: String) {
        updatePoll(pollId) { removeVote(vote, currentUserId) }
    }

    override fun onPollVoteUpserted(vote: PollVoteData, pollId: String) {
        updatePoll(pollId) { upsertVote(vote, currentUserId) }
    }

    override fun onFeedCapabilitiesUpdated(capabilities: Map<FeedId, Set<FeedOwnCapability>>) {
        _activity.update { current ->
            current?.currentFeed?.fid?.let(capabilities::get)?.let(current::updateFeedCapabilities)
                ?: current
        }
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
    /** Called when the activity is removed. */
    fun onActivityRemoved()

    /**
     * Called when the activity is updated.
     *
     * @param activity The updated activity data.
     */
    fun onActivityUpdated(activity: ActivityData)

    /**
     * Called when the activity is hidden or unhidden.
     *
     * @param activityId The ID of the activity that was hidden or unhidden.
     * @param hidden Whether the activity is hidden (true) or unhidden (false).
     */
    fun onActivityHidden(activityId: String, hidden: Boolean)

    /**
     * Called when a reaction is added to or updated in the activity.
     *
     * @param reaction The reaction that was added or updated.
     * @param activity The activity the reaction belongs to.
     * @param enforceUnique Whether to replace existing reactions by the same user.
     */
    fun onReactionUpserted(
        reaction: FeedsReactionData,
        activity: ActivityData,
        enforceUnique: Boolean,
    )

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
     * Called when a comment is removed from the activity.
     *
     * @param commentId The ID of the comment that was removed.
     */
    fun onCommentRemoved(commentId: String)

    /**
     * Called when a comment is added to or updated in the activity.
     *
     * @param comment The comment that was added or updated.
     */
    fun onCommentUpserted(comment: CommentData)

    /**
     * Called when a reaction is removed from a comment.
     *
     * @param comment The comment from which the reaction was removed.
     * @param reaction The reaction that was removed.
     */
    fun onCommentReactionRemoved(comment: CommentData, reaction: FeedsReactionData)

    /**
     * Called when a reaction is added to or updated in a comment.
     *
     * @param comment The comment to which the reaction was added or updated.
     * @param reaction The reaction that was added or updated.
     * @param enforceUnique Whether to replace existing reactions by the same user.
     */
    fun onCommentReactionUpserted(
        comment: CommentData,
        reaction: FeedsReactionData,
        enforceUnique: Boolean,
    )

    /**
     * Called when feed capabilities are updated.
     *
     * @param capabilities A map of feed IDs to their updated capabilities.
     */
    fun onFeedCapabilitiesUpdated(capabilities: Map<FeedId, Set<FeedOwnCapability>>)

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
     * Called when a vote is removed from the poll.
     *
     * @param vote The vote that was removed.
     * @param pollId The ID of the poll associated with the removed vote.
     */
    fun onPollVoteRemoved(vote: PollVoteData, pollId: String)

    /**
     * Called when a vote is casted or changed in the poll.
     *
     * @param vote The vote.
     * @param pollId The ID of the poll associated with the vote.
     */
    fun onPollVoteUpserted(vote: PollVoteData, pollId: String)
}
