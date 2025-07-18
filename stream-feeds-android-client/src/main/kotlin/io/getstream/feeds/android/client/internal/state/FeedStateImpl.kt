package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.query.Sort
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.ActivityPinData
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.model.addBookmark
import io.getstream.feeds.android.client.api.model.addComment
import io.getstream.feeds.android.client.api.model.addReaction
import io.getstream.feeds.android.client.api.model.deleteBookmark
import io.getstream.feeds.android.client.api.model.removeComment
import io.getstream.feeds.android.client.api.model.removeReaction
import io.getstream.feeds.android.client.api.state.ActivitiesSort
import io.getstream.feeds.android.client.api.state.FeedQuery
import io.getstream.feeds.android.client.api.state.FeedState
import io.getstream.feeds.android.client.internal.repository.GetOrCreateInfo
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import io.getstream.feeds.android.client.internal.utils.upsert
import io.getstream.feeds.android.client.internal.utils.upsertSorted
import io.getstream.feeds.android.core.generated.models.FeedOwnCapability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Default implementation of [FeedState].
 *
 * This class manages the state of a feed including activities, followers, members, and pagination
 * information. It automatically updates when WebSocket events are received and provides change
 * handlers for state modifications.
 */
internal class FeedStateImpl(
    override val feedQuery: FeedQuery,
    private val currentUserId: String,
) : FeedState, FeedStateUpdate {

    private val _activities: MutableStateFlow<List<ActivityData>> = MutableStateFlow(emptyList())
    private val _feed: MutableStateFlow<FeedData?> = MutableStateFlow(null)
    private val _followers: MutableStateFlow<List<FollowData>> = MutableStateFlow(emptyList())
    private val _following: MutableStateFlow<List<FollowData>> = MutableStateFlow(emptyList())
    private val _followRequests: MutableStateFlow<List<FollowData>> = MutableStateFlow(emptyList())
    private val _members: MutableStateFlow<List<FeedMemberData>> = MutableStateFlow(emptyList())
    private val _ownCapabilities: MutableStateFlow<List<FeedOwnCapability>> =
        MutableStateFlow(emptyList())
    private val _pinnedActivities: MutableStateFlow<List<ActivityPinData>> =
        MutableStateFlow(emptyList())

    private var _activitiesPagination: PaginationData? = null

    internal var activitiesQueryConfig: QueryConfiguration<ActivityData>? = null
        private set

    private val activitiesSorting: List<Sort<ActivityData>>
        get() = activitiesQueryConfig?.sort ?: ActivitiesSort.Default

    override val fid: FeedId
        get() = feedQuery.fid

    override val activities: StateFlow<List<ActivityData>>
        get() = _activities

    override val feed: StateFlow<FeedData?>
        get() = _feed

    override val followers: StateFlow<List<FollowData>>
        get() = _followers

    override val following: StateFlow<List<FollowData>>
        get() = _following

    override val followRequests: StateFlow<List<FollowData>>
        get() = _followRequests

    // TODO: Handle updates to members
    override val members: StateFlow<List<FeedMemberData>>
        get() = _members

    override val ownCapabilities: StateFlow<List<FeedOwnCapability>>
        get() = _ownCapabilities

    override val pinnedActivities: StateFlow<List<ActivityPinData>>
        get() = _pinnedActivities

    override val activitiesPagination: PaginationData?
        get() = _activitiesPagination

    override fun onQueryFeed(result: GetOrCreateInfo) {
        _activities.value = result.activities.models
        _activitiesPagination = result.activities.pagination
        activitiesQueryConfig = result.activitiesQueryConfig
        _feed.value = result.feed
        _followers.value = result.followers
        _following.value = result.following
        _followRequests.value = result.followRequests
        // TODO: Handle members with a delegated state
        _ownCapabilities.value = result.ownCapabilities
        _pinnedActivities.value = result.pinnedActivities
    }

    override fun onQueryMoreActivities(
        result: PaginationResult<ActivityData>,
        queryConfig: QueryConfiguration<ActivityData>,
    ) {
        _activitiesPagination = result.pagination
        activitiesQueryConfig = queryConfig
        // Merge the new activities with the existing ones (keeping the sort order)
        _activities.value =
            _activities.value.mergeSorted(result.models, ActivityData::id, activitiesSorting)
    }

    override fun onActivityAdded(activity: ActivityData) {
        _activities.value = _activities.value
            .upsertSorted(activity, ActivityData::id, activitiesSorting)
    }

    override fun onActivityUpdated(activity: ActivityData) {
        // Update the activities list
        _activities.value = _activities.value
            .upsertSorted(activity, ActivityData::id, activitiesSorting)
        // Update the pinned activities if the activity is pinned
        _pinnedActivities.value = _pinnedActivities.value.map { pin ->
            if (pin.activity.id == activity.id) {
                pin.copy(activity = activity)
            } else {
                pin
            }
        }
    }

    override fun onActivityRemoved(activity: ActivityData) {
        _activities.value = _activities.value.filter { it.id != activity.id }
    }

    override fun onActivityPinned(activityPin: ActivityPinData) {
        _pinnedActivities.value = _pinnedActivities.value.upsert(activityPin, ActivityPinData::id)
    }

    override fun onActivityUnpinned(activityId: String) {
        _pinnedActivities.value = _pinnedActivities.value.filter { it.activity.id != activityId }
    }

    override fun onBookmarkAdded(bookmark: BookmarkData) {
        _activities.value = _activities.value.map {
            if (it.id == bookmark.activity.id) { // todo generalize this in a common operations
                it.addBookmark(bookmark, currentUserId)
            } else {
                it
            }
        }
    }

    override fun onBookmarkRemoved(bookmark: BookmarkData) {
        _activities.value = _activities.value.map {
            if (it.id == bookmark.activity.id) {
                it.deleteBookmark(bookmark, currentUserId)
            } else {
                it
            }
        }
    }

    override fun onCommentAdded(comment: CommentData) {
        _activities.value = _activities.value.map {
            if (it.id == comment.objectId) {
                it.addComment(comment)
            } else {
                it
            }
        }
    }

    override fun onCommentRemoved(comment: CommentData) {
        _activities.value = _activities.value.map {
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
        _members.value = emptyList()
        _ownCapabilities.value = emptyList()
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

    override fun onReactionAdded(reaction: FeedsReactionData) {
        _activities.value = _activities.value.map { activity ->
            if (activity.id == reaction.activityId) {
                activity.addReaction(reaction, currentUserId)
            } else {
                activity
            }
        }
    }

    override fun onReactionRemoved(reaction: FeedsReactionData) {
        _activities.value = _activities.value.map { activity ->
            if (activity.id == reaction.activityId) {
                activity.removeReaction(reaction, currentUserId)
            } else {
                activity
            }
        }
    }

    private fun addFollow(follow: FollowData) {
        if (follow.isFollowRequest) {
            _followRequests.value = _followRequests.value.plus(follow)
        } else if (follow.isFollowing(fid)) {
            _following.value = _following.value.plus(follow)
        } else if (follow.isFollowerOf(fid)) {
            _followers.value = _followers.value.plus(follow)
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
}

internal interface FeedStateUpdate {
    fun onQueryFeed(result: GetOrCreateInfo)
    fun onQueryMoreActivities(
        result: PaginationResult<ActivityData>,
        queryConfig: QueryConfiguration<ActivityData>,
    )
    fun onActivityAdded(activity: ActivityData)
    fun onActivityUpdated(activity: ActivityData)
    fun onActivityRemoved(activity: ActivityData)
    fun onActivityPinned(activityPin: ActivityPinData)
    fun onActivityUnpinned(activityId: String)
    fun onBookmarkAdded(bookmark: BookmarkData)
    fun onBookmarkRemoved(bookmark: BookmarkData)
    fun onCommentAdded(comment: CommentData)
    fun onCommentRemoved(comment: CommentData)
    fun onFeedDeleted()
    fun onFeedUpdated(feed: FeedData)
    fun onFollowAdded(follow: FollowData)
    fun onFollowRemoved(follow: FollowData)
    fun onFollowUpdated(follow: FollowData)
    fun onReactionAdded(reaction: FeedsReactionData)
    fun onReactionRemoved(reaction: FeedsReactionData)
}