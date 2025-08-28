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
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.model.addBookmark
import io.getstream.feeds.android.client.api.model.addComment
import io.getstream.feeds.android.client.api.model.addReaction
import io.getstream.feeds.android.client.api.model.deleteBookmark
import io.getstream.feeds.android.client.api.model.removeComment
import io.getstream.feeds.android.client.api.model.removeReaction
import io.getstream.feeds.android.client.api.query.Sort
import io.getstream.feeds.android.client.api.state.FeedState
import io.getstream.feeds.android.client.api.state.query.ActivitiesSort
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.repository.GetOrCreateInfo
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import io.getstream.feeds.android.client.internal.utils.upsert
import io.getstream.feeds.android.client.internal.utils.upsertSorted
import io.getstream.feeds.android.network.models.FeedOwnCapability
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
    private val _ownCapabilities: MutableStateFlow<List<FeedOwnCapability>> =
        MutableStateFlow(emptyList())
    private val _pinnedActivities: MutableStateFlow<List<ActivityPinData>> =
        MutableStateFlow(emptyList())
    private val _notificationStatus: MutableStateFlow<NotificationStatusResponse?> =
        MutableStateFlow(null)

    private var _activitiesPagination: PaginationData? = null

    internal var activitiesQueryConfig: QueryConfiguration<ActivitiesSort>? = null
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

    override val ownCapabilities: StateFlow<List<FeedOwnCapability>>
        get() = _ownCapabilities.asStateFlow()

    override val pinnedActivities: StateFlow<List<ActivityPinData>>
        get() = _pinnedActivities.asStateFlow()

    override val notificationStatus: StateFlow<NotificationStatusResponse?>
        get() = _notificationStatus.asStateFlow()

    override val activitiesPagination: PaginationData?
        get() = _activitiesPagination

    override fun onQueryFeed(result: GetOrCreateInfo) {
        _activities.value = result.activities.models
        _activitiesPagination = result.activities.pagination
        activitiesQueryConfig = result.activitiesQueryConfig
        _aggregatedActivities.value = result.aggregatedActivities
        _feed.value = result.feed
        _followers.value = result.followers
        _following.value = result.following
        _followRequests.value = result.followRequests
        _ownCapabilities.value = result.ownCapabilities
        _pinnedActivities.value = result.pinnedActivities
        _notificationStatus.value = result.notificationStatus

        // Members are managed by the paginated list
        memberListState.onQueryMoreMembers(result.members, QueryConfiguration(null, null))
    }

    override fun onQueryMoreActivities(
        result: PaginationResult<ActivityData>,
        queryConfig: QueryConfiguration<ActivitiesSort>,
    ) {
        _activitiesPagination = result.pagination
        activitiesQueryConfig = queryConfig
        // Merge the new activities with the existing ones (keeping the sort order)
        _activities.value =
            _activities.value.mergeSorted(result.models, ActivityData::id, activitiesSorting)
    }

    override fun onActivityAdded(activity: ActivityData) {
        _activities.value =
            _activities.value.upsertSorted(activity, ActivityData::id, activitiesSorting)
    }

    override fun onActivityUpdated(activity: ActivityData) {
        // Update the activities list
        _activities.value =
            _activities.value.upsertSorted(activity, ActivityData::id, activitiesSorting)
        // Update the pinned activities if the activity is pinned
        _pinnedActivities.value =
            _pinnedActivities.value.map { pin ->
                if (pin.activity.id == activity.id) {
                    pin.copy(activity = activity)
                } else {
                    pin
                }
            }
    }

    override fun onActivityRemoved(activityId: String) {
        _activities.value = _activities.value.filter { it.id != activityId }
        // Also remove the activity from pinned activities if it exists
        _pinnedActivities.value = _pinnedActivities.value.filter { it.activity.id != activityId }
    }

    override fun onActivityPinned(activityPin: ActivityPinData) {
        _pinnedActivities.value = _pinnedActivities.value.upsert(activityPin, ActivityPinData::id)
    }

    override fun onActivityUnpinned(activityId: String) {
        _pinnedActivities.value = _pinnedActivities.value.filter { it.activity.id != activityId }
    }

    override fun onBookmarkAdded(bookmark: BookmarkData) {
        _activities.value =
            _activities.value.map {
                if (it.id == bookmark.activity.id) {
                    it.addBookmark(bookmark, currentUserId)
                } else {
                    it
                }
            }
    }

    override fun onBookmarkRemoved(bookmark: BookmarkData) {
        _activities.value =
            _activities.value.map {
                if (it.id == bookmark.activity.id) {
                    it.deleteBookmark(bookmark, currentUserId)
                } else {
                    it
                }
            }
    }

    override fun onCommentAdded(comment: CommentData) {
        _activities.value =
            _activities.value.map {
                if (it.id == comment.objectId) {
                    it.addComment(comment)
                } else {
                    it
                }
            }
    }

    override fun onCommentRemoved(comment: CommentData) {
        _activities.value =
            _activities.value.map {
                if (it.id == comment.objectId) {
                    it.removeComment(comment)
                } else {
                    it
                }
            }
    }

    override fun onFeedDeleted() {
        _activities.value = emptyList()
        _feed.value = null
        _followers.value = emptyList()
        _following.value = emptyList()
        _followRequests.value = emptyList()
        _ownCapabilities.value = emptyList()
        // Clear the member list state
        memberListState.clear()
    }

    override fun onFeedUpdated(feed: FeedData) {
        _feed.value = feed
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
        _following.value =
            _following.value.filterNot {
                it.sourceFeed.id == sourceFid.id && it.targetFeed.id == targetFid.id
            }
    }

    override fun onFollowRequestRemoved(id: String) {
        _followRequests.value = _followRequests.value.filter { it.id != id }
    }

    override fun onReactionAdded(reaction: FeedsReactionData) {
        _activities.value =
            _activities.value.map { activity ->
                if (activity.id == reaction.activityId) {
                    activity.addReaction(reaction, currentUserId)
                } else {
                    activity
                }
            }
    }

    override fun onReactionRemoved(reaction: FeedsReactionData) {
        _activities.value =
            _activities.value.map { activity ->
                if (activity.id == reaction.activityId) {
                    activity.removeReaction(reaction, currentUserId)
                } else {
                    activity
                }
            }
    }

    override fun onNotificationFeedUpdated(
        aggregatedActivities: List<AggregatedActivityData>,
        notificationStatus: NotificationStatusResponse?,
    ) {
        // TODO: [PV] The enrichWithUserData is a workaround because we don't get the full user data
        //  in the aggregated activities. Remove this method if this is fixed on BE, or remove the
        //  comment if this is the expected behaviour.
        //  See: [FEEDS-684]
        _aggregatedActivities.value = enrichWithUserData(aggregatedActivities)
        _notificationStatus.value = notificationStatus
    }

    private fun addFollow(follow: FollowData) {
        if (follow.isFollowRequest) {
            _followRequests.update { it.upsert(follow, FollowData::id) }
        } else if (follow.isFollowing(fid)) {
            _following.update { it.upsert(follow, FollowData::id) }
        } else if (follow.isFollowerOf(fid)) {
            _followers.update { it.upsert(follow, FollowData::id) }
        }
    }

    private fun removeFollow(follow: FollowData) {
        _following.value = _following.value.filter { it.id != follow.id }
        _followers.value = _followers.value.filter { it.id != follow.id }
        _followRequests.value = _followRequests.value.filter { it.id != follow.id }
    }

    private fun updateFollow(follow: FollowData) {
        removeFollow(follow)
        addFollow(follow)
    }

    private fun enrichWithUserData(
        aggregatedActivities: List<AggregatedActivityData>
    ): List<AggregatedActivityData> {
        // The _activities state flow should contain the full user data for activities, because it
        // will be delivered either via the initial query or via WebSocket events.
        val knownUsers = _activities.value.map { it.user }
        return aggregatedActivities.map {
            val activities =
                it.activities.map {
                    val user = knownUsers.find { user -> user.id == it.user.id } ?: it.user
                    it.copy(user = user)
                }
            it.copy(activities = activities)
        }
    }

    override fun onPollChanged(id: String, data: PollData?) {
        _activities.update { current ->
            current.map { activity ->
                if (activity.poll?.id == id) {
                    activity.copy(poll = data)
                } else {
                    activity
                }
            }
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
        result: PaginationResult<ActivityData>,
        queryConfig: QueryConfiguration<ActivitiesSort>,
    )

    /** Handles updates to the feed state when activity is added. */
    fun onActivityAdded(activity: ActivityData)

    /** Handles updates to the feed state when activity is updated. */
    fun onActivityUpdated(activity: ActivityData)

    /** Handles updates to the feed state when an activity is removed. */
    fun onActivityRemoved(activityId: String)

    /** Handles updates to the feed state when an activity is pinned. */
    fun onActivityPinned(activityPin: ActivityPinData)

    /** Handles updates to the feed state when an activity is unpinned. */
    fun onActivityUnpinned(activityId: String)

    /** Handles updates to the feed state when a bookmark is added or removed. */
    fun onBookmarkAdded(bookmark: BookmarkData)

    /** Handles updates to the feed state when a bookmark is removed. */
    fun onBookmarkRemoved(bookmark: BookmarkData)

    /** Handles updates to the feed state when a comment is added or removed. */
    fun onCommentAdded(comment: CommentData)

    /** Handles updates to the feed state when a comment is removed. */
    fun onCommentRemoved(comment: CommentData)

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

    /** Handles updates to the feed state when a reaction is added. */
    fun onReactionAdded(reaction: FeedsReactionData)

    /** Handles updates to the feed state when a reaction is removed. */
    fun onReactionRemoved(reaction: FeedsReactionData)

    /**
     * Handles updates to the feed state when a poll is changed.
     *
     * @param id The ID of the poll that has changed.
     * @param data The updated poll data, or null if the poll was removed.
     */
    fun onPollChanged(id: String, data: PollData?)

    /**
     * Handles updates to a notification feed.
     *
     * @param aggregatedActivities The list of aggregated activities in the notification feed.
     * @param notificationStatus The current notification status.
     */
    fun onNotificationFeedUpdated(
        aggregatedActivities: List<AggregatedActivityData>,
        notificationStatus: NotificationStatusResponse?,
    )
}
