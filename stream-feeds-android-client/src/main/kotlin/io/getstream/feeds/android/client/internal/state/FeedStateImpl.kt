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

import io.getstream.android.core.api.sort.Sort
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.ActivityPinData
import io.getstream.feeds.android.client.api.model.AggregatedActivityData
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.state.FeedState
import io.getstream.feeds.android.client.api.state.query.ActivitiesSort
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.model.QueryConfiguration
import io.getstream.feeds.android.client.internal.model.deleteBookmark
import io.getstream.feeds.android.client.internal.model.isFollowRequest
import io.getstream.feeds.android.client.internal.model.isFollowerOf
import io.getstream.feeds.android.client.internal.model.isFollowing
import io.getstream.feeds.android.client.internal.model.removeComment
import io.getstream.feeds.android.client.internal.model.removeCommentReaction
import io.getstream.feeds.android.client.internal.model.removeReaction
import io.getstream.feeds.android.client.internal.model.removeVote
import io.getstream.feeds.android.client.internal.model.update
import io.getstream.feeds.android.client.internal.model.upsertBookmark
import io.getstream.feeds.android.client.internal.model.upsertComment
import io.getstream.feeds.android.client.internal.model.upsertCommentReaction
import io.getstream.feeds.android.client.internal.model.upsertReaction
import io.getstream.feeds.android.client.internal.model.upsertVote
import io.getstream.feeds.android.client.internal.repository.GetOrCreateInfo
import io.getstream.feeds.android.client.internal.state.query.ActivitiesQueryConfig
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import io.getstream.feeds.android.client.internal.utils.updateIf
import io.getstream.feeds.android.client.internal.utils.upsert
import io.getstream.feeds.android.client.internal.utils.upsertAll
import io.getstream.feeds.android.client.internal.utils.upsertSorted
import io.getstream.feeds.android.network.models.NotificationStatusResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Default implementation of [FeedState].
 *
 * This class manages the state of a feed including activities, followers, members, and pagination
 * information. It automatically updates when WebSocket events are received and provides change
 * handlers for state modifications.
 *
 * @property feedQuery The query used to fetch the feed.
 * @property currentUserId The ID of the current user.
 */
internal class FeedStateImpl(
    override val feedQuery: FeedQuery,
    private val currentUserId: String,
    private val memberListState: MemberListMutableState,
) : FeedMutableState {

    private val _activities: MutableStateFlow<List<ActivityData>> = MutableStateFlow(emptyList())
    private val _aggregatedActivities: MutableStateFlow<List<AggregatedActivityData>> =
        MutableStateFlow(emptyList())
    private val _feed: MutableStateFlow<FeedData?> = MutableStateFlow(null)
    private val _followers: MutableStateFlow<List<FollowData>> = MutableStateFlow(emptyList())
    private val _following: MutableStateFlow<List<FollowData>> = MutableStateFlow(emptyList())
    private val _followRequests: MutableStateFlow<List<FollowData>> = MutableStateFlow(emptyList())
    private val _pinnedActivities: MutableStateFlow<List<ActivityPinData>> =
        MutableStateFlow(emptyList())
    private val _notificationStatus: MutableStateFlow<NotificationStatusResponse?> =
        MutableStateFlow(null)

    private var _activitiesPagination: PaginationData? = null

    internal var activitiesQueryConfig: ActivitiesQueryConfig? = null
        private set

    private val activitiesSorting: List<Sort<ActivityData>>
        get() = activitiesQueryConfig?.sort ?: ActivitiesSort.Default

    override val fid: FeedId
        get() = feedQuery.fid

    override val activities: StateFlow<List<ActivityData>>
        get() = _activities.asStateFlow()

    override val aggregatedActivities: StateFlow<List<AggregatedActivityData>>
        get() = _aggregatedActivities.asStateFlow()

    override val feed: StateFlow<FeedData?>
        get() = _feed.asStateFlow()

    override val followers: StateFlow<List<FollowData>>
        get() = _followers.asStateFlow()

    override val following: StateFlow<List<FollowData>>
        get() = _following.asStateFlow()

    override val followRequests: StateFlow<List<FollowData>>
        get() = _followRequests.asStateFlow()

    override val members: StateFlow<List<FeedMemberData>>
        get() = memberListState.members

    override val pinnedActivities: StateFlow<List<ActivityPinData>>
        get() = _pinnedActivities.asStateFlow()

    override val notificationStatus: StateFlow<NotificationStatusResponse?>
        get() = _notificationStatus.asStateFlow()

    override val activitiesPagination: PaginationData?
        get() = _activitiesPagination

    override fun onQueryFeed(result: GetOrCreateInfo) {
        _activities.update { result.activities }
        _aggregatedActivities.update { result.aggregatedActivities }
        _activitiesPagination = result.pagination
        activitiesQueryConfig = result.activitiesQueryConfig
        _feed.update { result.feed }
        _followers.update { result.followers }
        _following.update { result.following }
        _followRequests.update { result.followRequests }
        _pinnedActivities.update { result.pinnedActivities }
        _notificationStatus.update { result.notificationStatus }

        // Members are managed by the paginated list
        memberListState.onQueryMoreMembers(result.members, QueryConfiguration(null, null))
    }

    override fun onQueryMoreActivities(
        activities: List<ActivityData>,
        aggregatedActivities: List<AggregatedActivityData>,
        pagination: PaginationData,
        queryConfig: ActivitiesQueryConfig,
    ) {
        _activitiesPagination = pagination
        activitiesQueryConfig = queryConfig
        // Merge the new activities with the existing ones (keeping the sort order)
        _activities.update { current ->
            current.mergeSorted(activities, ActivityData::id, activitiesSorting)
        }
        _aggregatedActivities.update { current ->
            current.upsertAll(aggregatedActivities, AggregatedActivityData::group)
        }
    }

    override fun onActivityUpserted(activity: ActivityData) {
        if (_activities.value.any { it.id == activity.id }) {
            updateActivitiesWhere({ it.id == activity.id }) { existingActivity ->
                existingActivity.update(activity)
            }
        } else {
            _activities.update { current ->
                current.upsertSorted(activity, ActivityData::id, activitiesSorting)
            }
        }
    }

    override fun onActivityRemoved(activityId: String) {
        _activities.update { current -> current.filter { it.id != activityId } }
        // Also remove the activity from pinned activities if it exists
        _pinnedActivities.update { current -> current.filter { it.activity.id != activityId } }
    }

    override fun onActivityPinned(activityPin: ActivityPinData) {
        _pinnedActivities.update { current -> current.upsert(activityPin, ActivityPinData::id) }
    }

    override fun onActivityUnpinned(activityId: String) {
        _pinnedActivities.update { current -> current.filter { it.activity.id != activityId } }
    }

    override fun onBookmarkRemoved(bookmark: BookmarkData) {
        updateActivitiesWhere({ it.id == bookmark.activity.id }) { activity ->
            activity.deleteBookmark(bookmark, currentUserId)
        }
    }

    override fun onBookmarkUpserted(bookmark: BookmarkData) {
        updateActivitiesWhere({ it.id == bookmark.activity.id }) { activity ->
            activity.upsertBookmark(bookmark, currentUserId)
        }
    }

    override fun onCommentUpserted(comment: CommentData) {
        updateActivitiesWhere({ it.id == comment.objectId }) { activity ->
            activity.upsertComment(comment)
        }
    }

    override fun onCommentRemoved(comment: CommentData) {
        updateActivitiesWhere({ it.id == comment.objectId }) { activity ->
            activity.removeComment(comment.id)
        }
    }

    override fun onCommentReactionRemoved(comment: CommentData, reaction: FeedsReactionData) {
        updateActivitiesWhere({ it.id == comment.objectId }) { activity ->
            activity.removeCommentReaction(comment, reaction, currentUserId)
        }
    }

    override fun onCommentReactionUpserted(
        comment: CommentData,
        reaction: FeedsReactionData,
        enforceUnique: Boolean,
    ) {
        updateActivitiesWhere({ it.id == comment.objectId }) { activity ->
            activity.upsertCommentReaction(comment, reaction, currentUserId, enforceUnique)
        }
    }

    override fun onFeedDeleted() {
        _activities.update { emptyList() }
        _feed.update { null }
        _followers.update { emptyList() }
        _following.update { emptyList() }
        _followRequests.update { emptyList() }
        // Clear the member list state
        memberListState.clear()
    }

    override fun onFeedUpdated(feed: FeedData) {
        _feed.update { feed }
    }

    override fun onFollowAdded(follow: FollowData) {
        addFollow(follow)
    }

    override fun onFollowRemoved(follow: FollowData) {
        removeFollow(follow)
    }

    override fun onFollowUpdated(follow: FollowData) {
        updateFollow(follow)
    }

    override fun onUnfollow(sourceFid: FeedId, targetFid: FeedId) {
        _following.update { current ->
            current.filterNot {
                it.sourceFeed.id == sourceFid.id && it.targetFeed.id == targetFid.id
            }
        }
    }

    override fun onFollowRequestRemoved(id: String) {
        _followRequests.update { current -> current.filter { it.id != id } }
    }

    override fun onReactionUpserted(
        reaction: FeedsReactionData,
        activity: ActivityData,
        enforceUnique: Boolean,
    ) {
        updateActivitiesWhere({ it.id == reaction.activityId }) { currentActivity ->
            currentActivity.upsertReaction(activity, reaction, currentUserId, enforceUnique)
        }
    }

    override fun onReactionRemoved(reaction: FeedsReactionData, activity: ActivityData) {
        updateActivitiesWhere({ it.id == reaction.activityId }) { currentActivity ->
            currentActivity.removeReaction(activity, reaction, currentUserId)
        }
    }

    override fun onPollDeleted(id: String) {
        updateActivitiesWhere({ it.poll?.id == id }) { activity -> activity.copy(poll = null) }
    }

    override fun onPollUpdated(poll: PollData) {
        updateActivitiesWhere({ it.poll?.id == poll.id }) { activity ->
            activity.copy(poll = activity.poll?.update(poll))
        }
    }

    override fun onPollVoteRemoved(vote: PollVoteData, pollId: String) {
        updateActivitiesWhere({ it.poll?.id == pollId }) { activity ->
            activity.copy(poll = activity.poll?.removeVote(vote, currentUserId))
        }
    }

    override fun onPollVoteUpserted(vote: PollVoteData, pollId: String) {
        updateActivitiesWhere({ it.poll?.id == pollId }) { activity ->
            activity.copy(poll = activity.poll?.upsertVote(vote, currentUserId))
        }
    }

    override fun onNotificationFeedUpdated(
        aggregatedActivities: List<AggregatedActivityData>,
        notificationStatus: NotificationStatusResponse?,
    ) {
        updateAggregatedActivities(aggregatedActivities)
        _notificationStatus.update { notificationStatus }
    }

    override fun onStoriesFeedUpdated(
        activities: List<ActivityData>,
        aggregatedActivities: List<AggregatedActivityData>,
    ) {
        updateActivities(activities)
        updateAggregatedActivities(aggregatedActivities)
    }

    private fun updateActivities(activities: List<ActivityData>) {
        val updatedMap = activities.associateBy(ActivityData::id)

        updateActivitiesWhere({ it.id in updatedMap }) { activity ->
            updatedMap[activity.id]?.let(activity::update) ?: activity
        }
    }

    private fun updateAggregatedActivities(aggregatedActivities: List<AggregatedActivityData>) {
        val updatedMap = aggregatedActivities.associateBy(AggregatedActivityData::group)

        _aggregatedActivities.update { current -> current.map { updatedMap[it.group] ?: it } }
    }

    private fun addFollow(follow: FollowData) {
        if (follow.isFollowRequest) {
            _followRequests.update { it.upsert(follow, FollowData::id) }
        } else if (follow.isFollowing(fid)) {
            _following.update { it.upsert(follow, FollowData::id) }
        } else if (follow.isFollowerOf(fid)) {
            _followers.update { it.upsert(follow, FollowData::id) }
            _followRequests.update { current -> current.filter { it.id != follow.id } }
        }

        updateFeedOnFollowChanged(follow)
    }

    private fun removeFollow(follow: FollowData) {
        _following.update { current -> current.filter { it.id != follow.id } }
        _followers.update { current -> current.filter { it.id != follow.id } }
        _followRequests.update { current -> current.filter { it.id != follow.id } }

        updateFeedOnFollowChanged(follow)
    }

    private fun updateFeedOnFollowChanged(follow: FollowData) {
        val updated =
            when (fid) {
                follow.targetFeed.fid -> follow.targetFeed
                follow.sourceFeed.fid -> follow.sourceFeed
                else -> null
            }
        if (updated != null) {
            _feed.update { current -> current?.update(updated) ?: updated }
        }
    }

    private fun updateFollow(follow: FollowData) {
        removeFollow(follow)
        addFollow(follow)
    }

    private fun updateActivitiesWhere(
        filter: (ActivityData) -> Boolean,
        update: (ActivityData) -> ActivityData,
    ) {
        _activities.update { current -> current.updateIf(filter = filter, update = update) }
        _pinnedActivities.update { current ->
            current.updateIf(
                filter = { filter(it.activity) },
                update = { pin -> pin.copy(activity = update(pin.activity)) },
            )
        }
    }
}

/**
 * A mutable state interface for managing feed state updates.
 *
 * This interface combines the [FeedState] for read access and [FeedStateUpdates] for write access
 * to the feed state.
 */
internal interface FeedMutableState : FeedState, FeedStateUpdates

/**
 * An interface for handling updates to the feed state.
 *
 * This interface defines methods for updating the feed state when new activities are queried,
 * activities are added, updated, or removed, and when other feed-related events occur.
 */
internal interface FeedStateUpdates {

    /** Handles the result of a query for the feed. */
    fun onQueryFeed(result: GetOrCreateInfo)

    /** Handles the result of a query for more activities. */
    fun onQueryMoreActivities(
        activities: List<ActivityData>,
        aggregatedActivities: List<AggregatedActivityData>,
        pagination: PaginationData,
        queryConfig: ActivitiesQueryConfig,
    )

    /** Handles updates to the feed state when activity is added or updated. */
    fun onActivityUpserted(activity: ActivityData)

    /** Handles updates to the feed state when an activity is removed. */
    fun onActivityRemoved(activityId: String)

    /** Handles updates to the feed state when an activity is pinned. */
    fun onActivityPinned(activityPin: ActivityPinData)

    /** Handles updates to the feed state when an activity is unpinned. */
    fun onActivityUnpinned(activityId: String)

    /** Handles updates to the feed state when a bookmark is removed. */
    fun onBookmarkRemoved(bookmark: BookmarkData)

    /** Handles updates to the feed state when a bookmark is added or updated. */
    fun onBookmarkUpserted(bookmark: BookmarkData)

    /** Handles updates to the feed state when a comment is added or removed. */
    fun onCommentUpserted(comment: CommentData)

    /** Handles updates to the feed state when a comment is removed. */
    fun onCommentRemoved(comment: CommentData)

    /** Handles updates to the feed state when a comment reaction is removed. */
    fun onCommentReactionRemoved(comment: CommentData, reaction: FeedsReactionData)

    /** Handles updates to the feed state when a comment reaction is added or updated. */
    fun onCommentReactionUpserted(
        comment: CommentData,
        reaction: FeedsReactionData,
        enforceUnique: Boolean,
    )

    /** Handles updates to the feed state when the feed is deleted. */
    fun onFeedDeleted()

    /** Handles updates to the feed state when the feed is updated. */
    fun onFeedUpdated(feed: FeedData)

    /** Handles updates to the feed state when a follow is added. */
    fun onFollowAdded(follow: FollowData)

    /** Handles updates to the feed state when a follow is removed. */
    fun onFollowRemoved(follow: FollowData)

    /** Handles updates to the feed state when a follow is updated. */
    fun onFollowUpdated(follow: FollowData)

    /** Handles updates to the feed state when feed is unfollowed. */
    fun onUnfollow(sourceFid: FeedId, targetFid: FeedId)

    /** Handles updates to the feed state when a follow request is removed. */
    fun onFollowRequestRemoved(id: String)

    /** Handles updates to the feed state when a reaction is added or updated. */
    fun onReactionUpserted(
        reaction: FeedsReactionData,
        activity: ActivityData,
        enforceUnique: Boolean,
    )

    /** Handles updates to the feed state when a reaction is removed. */
    fun onReactionRemoved(reaction: FeedsReactionData, activity: ActivityData)

    /** Handles updates to the feed state when a poll is deleted. */
    fun onPollDeleted(id: String)

    /** Handles updates to the feed state when a poll is updated. */
    fun onPollUpdated(poll: PollData)

    /** Handles updates to the feed state when a poll vote is removed. */
    fun onPollVoteRemoved(vote: PollVoteData, pollId: String)

    /** Handles updates to the feed state when a poll vote is casted or changed. */
    fun onPollVoteUpserted(vote: PollVoteData, pollId: String)

    /**
     * Handles updates to a notification feed.
     *
     * @param aggregatedActivities The list of aggregated activities that were updated in the
     *   notification feed.
     * @param notificationStatus The current notification status.
     */
    fun onNotificationFeedUpdated(
        aggregatedActivities: List<AggregatedActivityData>,
        notificationStatus: NotificationStatusResponse?,
    )

    /**
     * Handles updates to a stories feed.
     *
     * @param activities The list of activities that were updated in the stories feed.
     * @param aggregatedActivities The list of aggregated activities that were updated in the
     *   stories feed.
     */
    fun onStoriesFeedUpdated(
        activities: List<ActivityData>,
        aggregatedActivities: List<AggregatedActivityData>,
    )
}
