package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.model.addBookmark
import io.getstream.feeds.android.client.api.model.addComment
import io.getstream.feeds.android.client.api.model.addReaction
import io.getstream.feeds.android.client.api.model.deleteBookmark
import io.getstream.feeds.android.client.api.model.removeComment
import io.getstream.feeds.android.client.api.model.removeReaction
import io.getstream.feeds.android.client.api.state.ActivityListState
import io.getstream.feeds.android.client.api.state.query.ActivitiesQuery
import io.getstream.feeds.android.client.api.state.query.ActivitiesSort
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import io.getstream.feeds.android.client.internal.utils.upsertSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * An observable state object that manages the activities list and handles real-time updates.
 *
 * [ActivityListState] provides a reactive interface for observing changes to the activities list,
 * including pagination state and real-time updates from WebSocket events. It automatically
 * handles sorting, merging, and updating activities as they change.
 *
 * @property query The query configuration used for fetching activities.
 * @property currentUserId The ID of the current user.
 */
internal class ActivityListStateImpl(
    override val query: ActivitiesQuery,
    private val currentUserId: String,
) : ActivityListMutableState {

    private val _activities: MutableStateFlow<List<ActivityData>> = MutableStateFlow(emptyList())

    internal var queryConfig: QueryConfiguration<ActivitiesSort>? = null
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
        queryConfig: QueryConfiguration<ActivitiesSort>
    ) {
        _pagination = result.pagination
        // Update the query configuration for future queries
        this.queryConfig = queryConfig
        // Merge the new activities with the existing ones (keeping the sort order)
        _activities.value =
            _activities.value.mergeSorted(result.models, ActivityData::id, activitiesSorting)
    }

    override fun onActivityRemoved(activity: ActivityData) {
        _activities.value = _activities.value.filter { it.id != activity.id }
    }

    override fun onActivityUpdated(activity: ActivityData) {
        _activities.value =
            _activities.value.upsertSorted(activity, ActivityData::id, activitiesSorting)
    }

    override fun onBookmarkAdded(bookmark: BookmarkData) {
        _activities.value = _activities.value.map { activity ->
            if (activity.id == bookmark.activity.id) {
                // If the activity matches the bookmark, add the bookmark to it
                activity.addBookmark(bookmark, currentUserId)
            } else {
                activity
            }
        }
    }

    override fun onBookmarkRemoved(bookmark: BookmarkData) {
        _activities.value = _activities.value.map { activity ->
            if (activity.id == bookmark.activity.id) {
                // If the activity matches the bookmark, remove the bookmark from it
                activity.deleteBookmark(bookmark, currentUserId)
            } else {
                activity
            }
        }
    }

    override fun onCommentAdded(comment: CommentData) {
        _activities.value = _activities.value.map { activity ->
            if (activity.id == comment.objectId) {
                activity.addComment(comment)
            } else {
                activity
            }
        }
    }

    override fun onCommentRemoved(comment: CommentData) {
        _activities.value = _activities.value.map { activity ->
            if (activity.id == comment.objectId) {
                activity.removeComment(comment)
            } else {
                activity
            }
        }
    }

    override fun onReactionAdded(reaction: FeedsReactionData) {
        _activities.value = _activities.value.map { activity ->
            if (activity.id == reaction.activityId) {
                // Add the reaction to the activity
                activity.addReaction(reaction, currentUserId)
            } else {
                activity
            }
        }
    }

    override fun onReactionRemoved(reaction: FeedsReactionData) {
        _activities.value = _activities.value.map { activity ->
            if (activity.id == reaction.activityId) {
                // Remove the reaction from the activity
                activity.removeReaction(reaction, currentUserId)
            } else {
                activity
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

/**
 * An interface that defines the methods for updating the state of an activity list.
 */
internal interface ActivityListStateUpdates {

    /**
     * Called when more activities are queried and received.
     *
     * @param result The result containing the new activities and pagination information.
     * @param queryConfig The configuration used for the query, including sorting and filtering.
     */
    fun onQueryMoreActivities(
        result: PaginationResult<ActivityData>,
        queryConfig: QueryConfiguration<ActivitiesSort>,
    )

    /**
     * Called when an activity is removed from the list.
     *
     * @param activity The activity that was removed.
     */
    fun onActivityRemoved(activity: ActivityData)

    /**
     * Called when an activity is updated in the list.
     *
     * @param activity The updated activity data.
     */
    fun onActivityUpdated(activity: ActivityData)

    /**
     * Called when a bookmark was added.
     *
     * @param bookmark The bookmark that was added.
     */
    fun onBookmarkAdded(bookmark: BookmarkData)

    /**
     * Called when a bookmark was removed.
     *
     * @param bookmark The bookmark that was removed.
     */
    fun onBookmarkRemoved(bookmark: BookmarkData)

    /**
     * Called when a comment is added to an activity.
     *
     * @param comment The comment that was added.
     */
    fun onCommentAdded(comment: CommentData)

    /**
     * Called when a comment is removed from an activity.
     *
     * @param comment The comment that was removed.
     */
    fun onCommentRemoved(comment: CommentData)

    /**
     * Called when a reaction is added to an activity.
     *
     * @param reaction The reaction that was added.
     */
    fun onReactionAdded(reaction: FeedsReactionData)

    /**
     * Called when a reaction is removed from an activity.
     *
     * @param reaction The reaction that was removed.
     */
    fun onReactionRemoved(reaction: FeedsReactionData)
}
