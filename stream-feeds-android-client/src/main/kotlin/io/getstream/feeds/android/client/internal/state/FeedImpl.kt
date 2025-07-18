package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.FeedQuery
import io.getstream.feeds.android.client.api.state.FeedState
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.core.generated.models.AcceptFollowRequest
import io.getstream.feeds.android.core.generated.models.AddBookmarkRequest
import io.getstream.feeds.android.core.generated.models.AddCommentReactionRequest
import io.getstream.feeds.android.core.generated.models.AddReactionRequest
import io.getstream.feeds.android.core.generated.models.MarkActivityRequest
import io.getstream.feeds.android.core.generated.models.RejectFollowRequest
import io.getstream.feeds.android.core.generated.models.SingleFollowRequest
import io.getstream.feeds.android.core.generated.models.UpdateActivityRequest
import io.getstream.feeds.android.core.generated.models.UpdateBookmarkRequest
import io.getstream.feeds.android.core.generated.models.UpdateCommentRequest
import io.getstream.feeds.android.core.generated.models.UpdateFeedMembersRequest
import io.getstream.feeds.android.core.generated.models.UpdateFeedRequest

/**
 * A feed represents a collection of activities and provides methods to interact with them.
 *
 * The `Feed` class is the primary interface for working with feeds in the Stream Feeds SDK.
 * It provides functionality for:
 * - Creating and managing feed data
 * - Adding, updating, and deleting activities
 * - Managing comments, reactions, and bookmarks
 * - Handling follows and feed memberships
 * - Creating polls and managing poll interactions
 * - Pagination and querying of feed content
 *
 * Each feed instance is associated with a specific feed ID and maintains its own state that can be
 * observed for real-time updates. The feed state includes activities, followers, members, and other
 * feed-related data.
 *
 * Internal implementation of the [Feed] interface.
 *
 * @property query The [FeedQuery] used to create this feed instance.
 * @property feedsRepository The [FeedsRepository] used to manage feed data and operations.
 */
internal class FeedImpl(
    private val query: FeedQuery,
    private val currentUserId: String,
    private val feedsRepository: FeedsRepository,
) : Feed {

    private val _state: FeedStateImpl = FeedStateImpl(
        feedQuery = query,
        currentUserId = currentUserId,
    )

    private val group: String
        get() = fid.group

    private val id: String
        get() = fid.id

    override val fid: FeedId
        get() = query.fid

    override val state: FeedState
        get() = _state

    override suspend fun getOrCreate(): Result<FeedData> {
        return feedsRepository.getOrCreateFeed(query)
            .onSuccess { _state.onQueryFeed(it) }
            .map { it.feed }
    }

    override suspend fun updateFeed(request: UpdateFeedRequest): Result<FeedData> {
        return feedsRepository.updateFeed(feedGroupId = group, feedId = id, request = request)
            .onSuccess { _state.onFeedUpdated(it) }
    }

    override suspend fun deleteFeed(hardDelete: Boolean): Result<Unit> {
        return feedsRepository.deleteFeed(feedGroupId = group, feedId = id, hardDelete = hardDelete)
            .onSuccess { _state.onFeedDeleted() }
    }

    override suspend fun updateActivity(
        id: String,
        request: UpdateActivityRequest
    ): Result<ActivityData> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteActivity(
        id: String,
        hardDelete: Boolean
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun markActivity(request: MarkActivityRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun repost(
        activityId: String,
        text: String?
    ): Result<ActivityData> {
        TODO("Not yet implemented")
    }

    override suspend fun queryMoreActivities(limit: Int?): Result<List<ActivityData>> {
        // Build the query based on the current state(with next cursor) and provided limit.
        val next = _state.activitiesPagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val query = FeedQuery(
            fid = fid,
            activityFilter = _state.activitiesQueryConfig?.filter,
            activityLimit = limit ?: query.activityLimit,
            activityNext = next,
            activitySelectorOptions = null,
            data = null,
            externalRanking = null,
            followerLimit = 0,
            followingLimit = 0,
            interestWeights = null,
            memberLimit = 0,
            view = null,
            watch = query.watch,
        )
        return feedsRepository.getOrCreateFeed(query)
            .onSuccess { _state.onQueryMoreActivities(it.activities, it.activitiesQueryConfig) }
            .map { it.activities.models }
    }

    override suspend fun addBookmark(
        activityId: String,
        request: AddBookmarkRequest
    ): Result<BookmarkData> {
        TODO("Not yet implemented")
    }

    override suspend fun updateBookmark(
        activityId: String,
        request: UpdateBookmarkRequest
    ): Result<BookmarkData> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteBookmark(
        activityId: String,
        folderId: String?
    ): Result<BookmarkData> {
        TODO("Not yet implemented")
    }

    override suspend fun getComment(commentId: String): Result<CommentData> {
        TODO("Not yet implemented")
    }

    override suspend fun updateComment(
        commentId: String,
        request: UpdateCommentRequest
    ): Result<CommentData> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteComment(commentId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun queryFollowSuggestions(limit: Int?): Result<List<FeedData>> {
        return feedsRepository.queryFollowSuggestions(feedGroupId = group, limit = limit)
    }

    override suspend fun follow(
        targetFid: FeedId,
        custom: Map<String, Any>?,
        pushPreference: SingleFollowRequest.PushPreference?
    ): Result<FollowData> {
        val request = SingleFollowRequest(
            custom = custom,
            pushPreference = pushPreference,
            source = fid.rawValue,
            target = targetFid.rawValue,
        )
        return feedsRepository.follow(request)
            .onSuccess { _state.onFollowAdded(it) }
    }

    override suspend fun unfollow(targetFid: FeedId): Result<Unit> {
        return feedsRepository.unfollow(source = fid, target = targetFid)
            .onSuccess {
                // TODO: Remove all following feeds where sourceFeed.fid == fid && targetFeed.fid == targetFid
            }
    }

    override suspend fun acceptFollow(
        sourceFid: FeedId,
        role: String?
    ): Result<FollowData> {
        val request = AcceptFollowRequest(
            followerRole = role,
            sourceFid = sourceFid.rawValue,
            targetFid = fid.rawValue,
        )
        return feedsRepository.acceptFollow(request)
            .onSuccess { follow ->
                // TODO Remove all followRequests where id == follow.id
                _state.onFollowAdded(follow)
            }
    }

    override suspend fun rejectFollow(sourceFid: FeedId): Result<FollowData> {
        val request = RejectFollowRequest(
            sourceFid = sourceFid.rawValue,
            targetFid = fid.rawValue,
        )
        return feedsRepository.rejectFollow(request)
            .onSuccess { follow ->
                // TODO Remove all followRequests where id == follow.id
                _state.onFollowRemoved(follow)
            }
    }

    override suspend fun queryFeedMembers(): Result<List<FeedMemberData>> {
        TODO("Not yet implemented")
    }

    override suspend fun queryMoreFeedMembers(limit: Int?): Result<List<FeedMemberData>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateFeedMembers(request: UpdateFeedMembersRequest): Result<ModelUpdates<FeedMemberData>> {
        return feedsRepository.updateFeedMembers(
            feedGroupId = group,
            feedId = id,
            request = request
        ).onSuccess { updates ->
            // TODO: Update member state in the _state
        }
    }

    override suspend fun acceptFeedMember(): Result<FeedMemberData> {
        return feedsRepository.acceptFeedMember(feedGroupId = group, feedId = id)
    }

    override suspend fun rejectFeedMember(): Result<FeedMemberData> {
        return feedsRepository.rejectFeedMember(feedGroupId = group, feedId = id)
    }

    override suspend fun addReaction(
        activityId: String,
        request: AddReactionRequest
    ): Result<FeedsReactionData> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReaction(
        activityId: String,
        type: String
    ): Result<FeedsReactionData> {
        TODO("Not yet implemented")
    }

    override suspend fun addCommentReaction(
        commentId: String,
        request: AddCommentReactionRequest
    ): Result<FeedsReactionData> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCommentReaction(
        commentId: String,
        type: String
    ): Result<FeedsReactionData> {
        TODO("Not yet implemented")
    }
}
