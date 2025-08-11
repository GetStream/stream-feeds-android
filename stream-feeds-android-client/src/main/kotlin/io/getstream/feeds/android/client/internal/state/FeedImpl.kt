package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.websocket.WebSocketConnectionState
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedAddActivityRequest
import io.getstream.feeds.android.client.api.model.FeedAddCommentRequest
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.FeedState
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.api.state.query.MembersQuery
import io.getstream.feeds.android.client.internal.common.StreamSubscriptionManager
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.repository.BookmarksRepository
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.repository.PollsRepository
import io.getstream.feeds.android.client.internal.socket.FeedsSocketListener
import io.getstream.feeds.android.client.internal.state.event.handler.FeedEventHandler
import io.getstream.feeds.android.client.internal.utils.flatMap
import io.getstream.feeds.android.core.generated.models.AcceptFollowRequest
import io.getstream.feeds.android.core.generated.models.AddActivityRequest
import io.getstream.feeds.android.core.generated.models.AddBookmarkRequest
import io.getstream.feeds.android.core.generated.models.AddCommentReactionRequest
import io.getstream.feeds.android.core.generated.models.AddReactionRequest
import io.getstream.feeds.android.core.generated.models.CreatePollRequest
import io.getstream.feeds.android.core.generated.models.FollowRequest
import io.getstream.feeds.android.core.generated.models.MarkActivityRequest
import io.getstream.feeds.android.core.generated.models.RejectFollowRequest
import io.getstream.feeds.android.core.generated.models.UpdateActivityRequest
import io.getstream.feeds.android.core.generated.models.UpdateBookmarkRequest
import io.getstream.feeds.android.core.generated.models.UpdateCommentRequest
import io.getstream.feeds.android.core.generated.models.UpdateFeedMembersRequest
import io.getstream.feeds.android.core.generated.models.UpdateFeedRequest
import io.getstream.feeds.android.core.generated.models.WSEvent

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
 * @property currentUserId The ID of the current user.
 * @property activitiesRepository The [ActivitiesRepository] used to manage activities in the feed.
 * @property bookmarksRepository The [BookmarksRepository] used to manage bookmarks in the feed.
 * @property commentsRepository The [CommentsRepository] used to manage comments in the feed.
 * @property feedsRepository The [FeedsRepository] used to manage feed data and operations.
 * @property pollsRepository The [PollsRepository] used to manage polls in the feed.
 * @property subscriptionManager The [StreamSubscriptionManager] used to manage WebSocket
 * subscriptions for feed events.
 */
internal class FeedImpl(
    private val query: FeedQuery,
    private val currentUserId: String,
    private val activitiesRepository: ActivitiesRepository,
    private val bookmarksRepository: BookmarksRepository,
    private val commentsRepository: CommentsRepository,
    private val feedsRepository: FeedsRepository,
    private val pollsRepository: PollsRepository,
    private val subscriptionManager: StreamSubscriptionManager<FeedsSocketListener>,
) : Feed {

    init {
        subscriptionManager.subscribe(object : FeedsSocketListener {
            override fun onState(state: WebSocketConnectionState) {
                // Not relevant, rethink this
            }

            override fun onEvent(event: WSEvent) {
                eventHandler.handleEvent(event)
            }
        })
    }

    private val memberList: MemberListImpl = MemberListImpl(
        query = MembersQuery(fid = query.fid),
        feedsRepository = feedsRepository,
        subscriptionManager = subscriptionManager,
    )

    private val _state: FeedStateImpl = FeedStateImpl(
        feedQuery = query,
        currentUserId = currentUserId,
        memberListState = memberList.mutableState,
    )

    private val eventHandler = FeedEventHandler(fid = fid, state = _state)

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

    override suspend fun stopWatching(): Result<Unit> {
        return feedsRepository.stopWatching(groupId = group, feedId = id)
    }

    override suspend fun updateFeed(request: UpdateFeedRequest): Result<FeedData> {
        return feedsRepository.updateFeed(feedGroupId = group, feedId = id, request = request)
            .onSuccess { _state.onFeedUpdated(it) }
    }

    override suspend fun deleteFeed(hardDelete: Boolean): Result<Unit> {
        return feedsRepository.deleteFeed(feedGroupId = group, feedId = id, hardDelete = hardDelete)
            .onSuccess { _state.onFeedDeleted() }
    }

    override suspend fun addActivity(
        request: FeedAddActivityRequest,
        attachmentUploadProgress: ((FeedUploadPayload, Double) -> Unit)?
    ): Result<ActivityData> {
        return activitiesRepository.addActivity(request, attachmentUploadProgress)
            .onSuccess { _state.onActivityAdded(it) }
    }

    override suspend fun updateActivity(
        id: String,
        request: UpdateActivityRequest
    ): Result<ActivityData> {
        return activitiesRepository.updateActivity(id, request)
            .onSuccess { _state.onActivityUpdated(it) }
    }

    override suspend fun deleteActivity(
        id: String,
        hardDelete: Boolean
    ): Result<Unit> {
        return activitiesRepository.deleteActivity(id, hardDelete)
            .onSuccess { _state.onActivityRemoved(id) }
    }

    override suspend fun markActivity(request: MarkActivityRequest): Result<Unit> {
        return activitiesRepository.markActivity(
            feedGroupId = group,
            feedId = id,
            request = request,
        )
    }

    override suspend fun repost(
        activityId: String,
        text: String?
    ): Result<ActivityData> {
        val request = AddActivityRequest(
            type = "post",
            text = text,
            feeds = listOf(fid.rawValue),
            parentId = activityId,
        )
        return activitiesRepository.addActivity(FeedAddActivityRequest(request))
            .onSuccess { _state.onActivityAdded(it) }
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
        return bookmarksRepository.addBookmark(activityId, request)
            .onSuccess { _state.onBookmarkAdded(it) }
    }

    override suspend fun updateBookmark(
        activityId: String,
        request: UpdateBookmarkRequest
    ): Result<BookmarkData> {
        return bookmarksRepository.updateBookmark(activityId, request)
    }

    override suspend fun deleteBookmark(
        activityId: String,
        folderId: String?
    ): Result<BookmarkData> {
        return bookmarksRepository.deleteBookmark(activityId = activityId, folderId = folderId)
            .onSuccess { _state.onBookmarkRemoved(it) }
    }

    override suspend fun getComment(commentId: String): Result<CommentData> {
        return commentsRepository.getComment(commentId)
    }

    override suspend fun addComment(
        request: FeedAddCommentRequest,
        attachmentUploadProgress: ((FeedUploadPayload, Double) -> Unit)?
    ): Result<CommentData> {
        return commentsRepository.addComment(request, attachmentUploadProgress)
    }

    override suspend fun updateComment(
        commentId: String,
        request: UpdateCommentRequest
    ): Result<CommentData> {
        return commentsRepository.updateComment(commentId, request)
    }

    override suspend fun deleteComment(commentId: String, hardDelete: Boolean?): Result<Unit> {
        return commentsRepository.deleteComment(commentId, hardDelete)
    }

    override suspend fun queryFollowSuggestions(limit: Int?): Result<List<FeedData>> {
        return feedsRepository.queryFollowSuggestions(feedGroupId = group, limit = limit)
    }

    override suspend fun follow(
        targetFid: FeedId,
        createNotificationActivity: Boolean?,
        custom: Map<String, Any>?,
        pushPreference: FollowRequest.PushPreference?
    ): Result<FollowData> {
        val request = FollowRequest(
            createNotificationActivity = createNotificationActivity,
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
            .onSuccess { _state.onUnfollow(sourceFid = fid, targetFid = targetFid) }
    }

    override suspend fun acceptFollow(
        sourceFid: FeedId,
        role: String?
    ): Result<FollowData> {
        val request = AcceptFollowRequest(
            followerRole = role,
            source = sourceFid.rawValue,
            target = fid.rawValue,
        )
        return feedsRepository.acceptFollow(request)
            .onSuccess { follow ->
                _state.onFollowRequestRemoved(follow.id)
                _state.onFollowAdded(follow)
            }
    }

    override suspend fun rejectFollow(sourceFid: FeedId): Result<FollowData> {
        val request = RejectFollowRequest(
            source = sourceFid.rawValue,
            target = fid.rawValue,
        )
        return feedsRepository.rejectFollow(request)
            .onSuccess { follow ->
                _state.onFollowRequestRemoved(follow.id)
                _state.onFollowRemoved(follow)
            }
    }

    override suspend fun queryFeedMembers(): Result<List<FeedMemberData>> {
        return memberList.get()
    }

    override suspend fun queryMoreFeedMembers(limit: Int?): Result<List<FeedMemberData>> {
        return memberList.queryMoreMembers(limit)
    }

    override suspend fun updateFeedMembers(request: UpdateFeedMembersRequest): Result<ModelUpdates<FeedMemberData>> {
        return feedsRepository.updateFeedMembers(
            feedGroupId = group,
            feedId = id,
            request = request,
        ).onSuccess { updates ->
            memberList.mutableState.onMembersUpdated(updates)
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
        return activitiesRepository.addReaction(activityId, request)
    }

    override suspend fun deleteReaction(
        activityId: String,
        type: String
    ): Result<FeedsReactionData> {
        return activitiesRepository.deleteReaction(activityId = activityId, type = type)
    }

    override suspend fun addCommentReaction(
        commentId: String,
        request: AddCommentReactionRequest
    ): Result<FeedsReactionData> {
        return commentsRepository.addCommentReaction(commentId, request)
            .map { it.first }
    }

    override suspend fun deleteCommentReaction(
        commentId: String,
        type: String
    ): Result<FeedsReactionData> {
        return commentsRepository.deleteCommentReaction(commentId = commentId, type = type)
            .map { it.first }
    }

    override suspend fun createPoll(
        request: CreatePollRequest,
        activityType: String
    ): Result<ActivityData> {
        return pollsRepository.createPoll(request).flatMap { poll ->
            val request = AddActivityRequest(
                feeds = listOf(fid.rawValue),
                pollId = poll.id,
                type = activityType,
            )
            activitiesRepository.addActivity(FeedAddActivityRequest(request))
        }
    }
}
