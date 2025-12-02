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

package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.state.ActivityListState
import io.getstream.feeds.android.client.api.state.query.ActivitiesQuery
import io.getstream.feeds.android.client.api.state.query.ActivitiesSort
import io.getstream.feeds.android.client.internal.model.FeedOwnValues
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.model.deleteBookmark
import io.getstream.feeds.android.client.internal.model.removeComment
import io.getstream.feeds.android.client.internal.model.removeCommentReaction
import io.getstream.feeds.android.client.internal.model.removeReaction
import io.getstream.feeds.android.client.internal.model.removeVote
import io.getstream.feeds.android.client.internal.model.update
import io.getstream.feeds.android.client.internal.model.updateFeedOwnValues
import io.getstream.feeds.android.client.internal.model.upsertBookmark
import io.getstream.feeds.android.client.internal.model.upsertComment
import io.getstream.feeds.android.client.internal.model.upsertCommentReaction
import io.getstream.feeds.android.client.internal.model.upsertReaction
import io.getstream.feeds.android.client.internal.model.upsertVote
import io.getstream.feeds.android.client.internal.state.query.ActivitiesQueryConfig
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import io.getstream.feeds.android.client.internal.utils.updateIf
import io.getstream.feeds.android.client.internal.utils.upsertSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An observable state object that manages the activities list and handles real-time updates.
 *
 * [ActivityListState] provides a reactive interface for observing changes to the activities list,
 * including pagination state and real-time updates from WebSocket events. It automatically handles
 * sorting, merging, and updating activities as they change.
 *
 * @property query The query configuration used for fetching activities.
 * @property currentUserId The ID of the current user.
 */
internal class ActivityListStateImpl(
    override val query: ActivitiesQuery,
    private val currentUserId: String,
) : ActivityListMutableState {

    private val _activities: MutableStateFlow<List<ActivityData>> = MutableStateFlow(emptyList())

    internal var queryConfig: ActivitiesQueryConfig? = null
        private set

    private var _pagination: PaginationData? = null

    private val activitiesSorting: List<ActivitiesSort>
        get() = query.sort ?: ActivitiesSort.Default

    override val activities: StateFlow<List<ActivityData>>
        get() = _activities.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onQueryMoreActivities(
        result: PaginationResult<ActivityData>,
        queryConfig: ActivitiesQueryConfig,
    ) {
        _pagination = result.pagination
        // Update the query configuration for future queries
        this.queryConfig = queryConfig
        // Merge the new activities with the existing ones (keeping the sort order)
        _activities.update { current ->
            current.mergeSorted(result.models, ActivityData::id, activitiesSorting)
        }
    }

    override fun onActivityRemoved(activityId: String) {
        _activities.update { current -> current.filter { it.id != activityId } }
    }

    override fun onActivityUpserted(activity: ActivityData) {
        _activities.update { current ->
            current.upsertSorted(
                element = activity,
                idSelector = ActivityData::id,
                sort = activitiesSorting,
                update = { old -> old.update(activity) },
            )
        }
    }

    override fun onActivityHidden(activityId: String, hidden: Boolean) {
        _activities.update { current ->
            current.updateIf({ it.id == activityId }) { activity -> activity.copy(hidden = hidden) }
        }
    }

    override fun onBookmarkRemoved(bookmark: BookmarkData) {
        _activities.update { current ->
            current.updateIf({ it.id == bookmark.activity.id }) { activity ->
                activity.deleteBookmark(bookmark, currentUserId)
            }
        }
    }

    override fun onBookmarkUpserted(bookmark: BookmarkData) {
        _activities.update { current ->
            current.updateIf({ it.id == bookmark.activity.id }) { activity ->
                activity.upsertBookmark(bookmark, currentUserId)
            }
        }
    }

    override fun onCommentUpserted(comment: CommentData) {
        _activities.update { current ->
            current.updateIf({ it.id == comment.objectId }) { activity ->
                activity.upsertComment(comment)
            }
        }
    }

    override fun onCommentRemoved(comment: CommentData) {
        _activities.update { current ->
            current.map { activity ->
                if (activity.id == comment.objectId) {
                    activity.removeComment(comment.id)
                } else {
                    activity
                }
            }
        }
    }

    override fun onCommentReactionRemoved(comment: CommentData, reaction: FeedsReactionData) {
        _activities.update { current ->
            current.updateIf({ it.id == comment.objectId }) { activity ->
                activity.removeCommentReaction(comment, reaction, currentUserId)
            }
        }
    }

    override fun onCommentReactionUpserted(
        comment: CommentData,
        reaction: FeedsReactionData,
        enforceUnique: Boolean,
    ) {
        _activities.update { current ->
            current.updateIf({ it.id == comment.objectId }) { activity ->
                activity.upsertCommentReaction(comment, reaction, currentUserId, enforceUnique)
            }
        }
    }

    override fun onFeedOwnValuesUpdated(map: Map<FeedId, FeedOwnValues>) {
        _activities.update { current ->
            current.map { activity ->
                activity.currentFeed?.fid?.let(map::get)?.let(activity::updateFeedOwnValues)
                    ?: activity
            }
        }
    }

    override fun onPollDeleted(pollId: String) {
        _activities.update { current ->
            current.updateIf({ it.poll?.id == pollId }) { it.copy(poll = null) }
        }
    }

    override fun onPollUpdated(poll: PollData) {
        _activities.update { current ->
            current.updateIf({ it.poll?.id == poll.id }) { activity ->
                activity.copy(poll = activity.poll?.update(poll))
            }
        }
    }

    override fun onPollVoteRemoved(poll: PollData, vote: PollVoteData) {
        _activities.update { current ->
            current.updateIf({ it.poll?.id == poll.id }) { activity ->
                activity.copy(poll = activity.poll?.removeVote(poll, vote, currentUserId))
            }
        }
    }

    override fun onPollVoteUpserted(poll: PollData, vote: PollVoteData) {
        _activities.update { current ->
            current.updateIf({ it.poll?.id == poll.id }) { activity ->
                activity.copy(poll = activity.poll?.upsertVote(poll, vote, currentUserId))
            }
        }
    }

    override fun onReactionUpserted(
        reaction: FeedsReactionData,
        activity: ActivityData,
        enforceUnique: Boolean,
    ) {
        _activities.update { current ->
            current.updateIf({ it.id == reaction.activityId }) {
                it.upsertReaction(activity, reaction, currentUserId, enforceUnique)
            }
        }
    }

    override fun onReactionRemoved(reaction: FeedsReactionData, activity: ActivityData) {
        _activities.update { current ->
            current.updateIf({ it.id == reaction.activityId }) {
                it.removeReaction(activity, reaction, currentUserId)
            }
        }
    }
}

/**
 * A mutable state interface for managing the activities list.
 *
 * This interface combines the [ActivityListState] for read access and [ActivityListStateUpdates]
 * for write access, allowing for both querying and updating the activity list state.
 */
internal interface ActivityListMutableState : ActivityListState, ActivityListStateUpdates

/** An interface that defines the methods for updating the state of an activity list. */
internal interface ActivityListStateUpdates {

    /**
     * Called when more activities are queried and received.
     *
     * @param result The result containing the new activities and pagination information.
     * @param queryConfig The configuration used for the query, including sorting and filtering.
     */
    fun onQueryMoreActivities(
        result: PaginationResult<ActivityData>,
        queryConfig: ActivitiesQueryConfig,
    )

    /**
     * Called when an activity is removed from the list.
     *
     * @param activityId The ID of the activity that was removed.
     */
    fun onActivityRemoved(activityId: String)

    /**
     * Called when an activity is added to or updated in the list.
     *
     * @param activity The activity data.
     */
    fun onActivityUpserted(activity: ActivityData)

    /**
     * Called when an activity is hidden or unhidden.
     *
     * @param activityId The ID of the activity that was hidden or unhidden.
     * @param hidden Whether the activity is hidden (true) or unhidden (false).
     */
    fun onActivityHidden(activityId: String, hidden: Boolean)

    /**
     * Called when a bookmark was removed.
     *
     * @param bookmark The bookmark that was removed.
     */
    fun onBookmarkRemoved(bookmark: BookmarkData)

    /**
     * Called when a bookmark was added or updated.
     *
     * @param bookmark The bookmark that was added or updated.
     */
    fun onBookmarkUpserted(bookmark: BookmarkData)

    /**
     * Called when a comment is added to or updated in the activity.
     *
     * @param comment The comment that was added or updated.
     */
    fun onCommentUpserted(comment: CommentData)

    /**
     * Called when a comment is removed from an activity.
     *
     * @param comment The comment that was removed.
     */
    fun onCommentRemoved(comment: CommentData)

    /**
     * Called when a reaction is removed from a comment.
     *
     * @param comment The comment the reaction belonged to.
     * @param reaction The reaction that was removed.
     */
    fun onCommentReactionRemoved(comment: CommentData, reaction: FeedsReactionData)

    /**
     * Called when a reaction is added to or updated in a comment.
     *
     * @param comment The comment the reaction belongs to.
     * @param reaction The reaction that was added or updated.
     * @param enforceUnique Whether to replace existing reactions by the same user.
     */
    fun onCommentReactionUpserted(
        comment: CommentData,
        reaction: FeedsReactionData,
        enforceUnique: Boolean,
    )

    /**
     * Called when feed own values are updated.
     *
     * @param map A map of feed IDs to their updated set of own values.
     */
    fun onFeedOwnValuesUpdated(map: Map<FeedId, FeedOwnValues>)

    /**
     * Called when a poll is deleted.
     *
     * @param pollId The ID of the deleted poll.
     */
    fun onPollDeleted(pollId: String)

    /**
     * Called when a poll is updated.
     *
     * @param poll The updated poll.
     */
    fun onPollUpdated(poll: PollData)

    /**
     * Called when a vote is removed from a poll.
     *
     * @param poll The poll associated with the vote.
     * @param vote The vote that was removed.
     */
    fun onPollVoteRemoved(poll: PollData, vote: PollVoteData)

    /**
     * Called when a vote is added to or updated in a poll.
     *
     * @param poll The poll associated with the vote.
     * @param vote The vote that was added or updated.
     */
    fun onPollVoteUpserted(poll: PollData, vote: PollVoteData)

    /**
     * Called when a reaction is added to or updated in an activity.
     *
     * @param reaction The reaction that was added.
     * @param activity The activity the reaction belongs to.
     * @param enforceUnique Whether to replace existing reactions by the same user.
     */
    fun onReactionUpserted(
        reaction: FeedsReactionData,
        activity: ActivityData,
        enforceUnique: Boolean,
    )

    /**
     * Called when a reaction is removed from an activity.
     *
     * @param reaction The reaction that was removed.
     * @param activity The activity from which the reaction was removed.
     */
    fun onReactionRemoved(reaction: FeedsReactionData, activity: ActivityData)
}
