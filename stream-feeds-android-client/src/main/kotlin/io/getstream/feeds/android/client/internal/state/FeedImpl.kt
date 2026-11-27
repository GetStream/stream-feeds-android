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

import io.getstream.android.core.api.filter.Filters
import io.getstream.android.core.api.filter.equal
import io.getstream.android.core.api.model.exceptions.StreamEndpointException
import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.AggregatedActivityData
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedAddActivityRequest
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.FeedSuggestionData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.FollowStatus
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.FeedState
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.api.state.query.FollowsFilterField
import io.getstream.feeds.android.client.api.state.query.FollowsQuery
import io.getstream.feeds.android.client.api.state.query.MembersQuery
import io.getstream.feeds.android.client.internal.client.reconnect.FeedWatchHandler
import io.getstream.feeds.android.client.internal.client.reconnect.PaginatedRefetchable
import io.getstream.feeds.android.client.internal.client.reconnect.ReconnectionRefetchHandler
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.repository.BookmarksRepository
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.repository.FeedOwnValuesRepository
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.repository.GetOrCreateInfo
import io.getstream.feeds.android.client.internal.repository.PollsRepository
import io.getstream.feeds.android.client.internal.repository.cache
import io.getstream.feeds.android.client.internal.state.event.FidScope
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.handler.FeedEventHandler
import io.getstream.feeds.android.client.internal.state.event.handler.OnNewActivity
import io.getstream.feeds.android.client.internal.state.query.toRequest
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.subscribe.onEvent
import io.getstream.feeds.android.client.internal.utils.flatMap
import io.getstream.feeds.android.client.internal.utils.refetchPages
import io.getstream.feeds.android.client.internal.utils.refetchPagesAround
import io.getstream.feeds.android.network.models.AcceptFollowRequest
import io.getstream.feeds.android.network.models.AddActivityRequest
import io.getstream.feeds.android.network.models.AddBookmarkRequest
import io.getstream.feeds.android.network.models.AddCommentReactionRequest
import io.getstream.feeds.android.network.models.AddReactionRequest
import io.getstream.feeds.android.network.models.CreatePollRequest
import io.getstream.feeds.android.network.models.FollowPushPreference
import io.getstream.feeds.android.network.models.FollowRequest
import io.getstream.feeds.android.network.models.MarkActivityRequest
import io.getstream.feeds.android.network.models.RejectFollowRequest
import io.getstream.feeds.android.network.models.UpdateActivityPartialRequest
import io.getstream.feeds.android.network.models.UpdateActivityRequest
import io.getstream.feeds.android.network.models.UpdateBookmarkRequest
import io.getstream.feeds.android.network.models.UpdateCommentRequest
import io.getstream.feeds.android.network.models.UpdateFeedMembersRequest
import io.getstream.feeds.android.network.models.UpdateFeedRequest
import io.getstream.feeds.android.network.models.UpdateFollowRequest
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

/**
 * A feed represents a collection of activities and provides methods to interact with them.
 *
 * The `Feed` class is the primary interface for working with feeds in the Stream Feeds SDK. It
 * provides functionality for:
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
 * @property subscriptionManager The manager for state update subscriptions.
 */
internal class FeedImpl(
    private val query: FeedQuery,
    private val currentUserId: String,
    private val onNewActivity: OnNewActivity,
    private val activitiesRepository: ActivitiesRepository,
    private val bookmarksRepository: BookmarksRepository,
    private val commentsRepository: CommentsRepository,
    private val feedsRepository: FeedsRepository,
    private val pollsRepository: PollsRepository,
    private val feedOwnValuesRepository: FeedOwnValuesRepository,
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener>,
    private val feedWatchHandler: FeedWatchHandler,
    reconnectionRefetchHandler: ReconnectionRefetchHandler,
) : Feed, PaginatedRefetchable<FeedImpl.FeedItem> {

    private val memberList: MemberListImpl =
        MemberListImpl(
            query = MembersQuery(fid = query.fid),
            feedsRepository = feedsRepository,
            subscriptionManager = subscriptionManager,
        )

    /** The pending follow requests sent to this feed. */
    private val followRequestsQuery =
        FollowsQuery(
            filter =
                Filters.and(
                    FollowsFilterField.status.equal(FollowStatus.Pending.value),
                    FollowsFilterField.targetFeed.equal(query.fid.rawValue),
                )
        )

    private val _state: FeedStateImpl =
        FeedStateImpl(
            feedQuery = query,
            currentUserId = currentUserId,
            memberListState = memberList.mutableState,
        )

    private val eventHandler =
        FeedEventHandler(
            query = query,
            currentUserId = currentUserId,
            onNewActivity = onNewActivity,
            state = _state,
        )

    init {
        subscriptionManager.subscribe(eventHandler)
        reconnectionRefetchHandler.register(this)
    }

    /**
     * The activity that the loaded activities were last loaded around, which the refetch stays
     * around. It is null when they were loaded from the top of the feed.
     */
    @Volatile private var aroundActivityId: String? = null

    private val group: String
        get() = fid.group

    private val id: String
        get() = fid.id

    override val fid: FeedId
        get() = query.fid

    override val state: FeedState
        get() = _state

    override suspend fun getOrCreate(): Result<FeedData> {
        if (query.watch) {
            feedWatchHandler.onStartWatching(query.fid)
        }
        return feedsRepository
            .getOrCreateFeed(query)
            .onSuccess {
                aroundActivityId = query.activityIdAround
                _state.onQueryFeed(it)
                feedOwnValuesRepository.cache(it.feed)
            }
            .map { it.feed }
    }

    override val rewatchedFeed: FeedId?
        get() = fid.takeIf { query.watch && _state.feed.value != null }

    override suspend fun refetch() {
        val anchor = refetchAnchor()
        if (anchor == null) {
            super.refetch()
        } else {
            val (anchorId, index) = anchor
            val result =
                refetchPagesAround(
                    anchorId = anchorId,
                    loadedBefore = index,
                    loadedAfter = _state.activities.value.size - index - 1,
                    idSelector = ::idOf,
                ) { previous, next ->
                    fetchPage(anchorId, previous, next)
                }
            onRefetched(result)
        }
        if (_state.followRequestsPagination != null) {
            refetchFollowRequests()
        }
    }

    override fun loadedCount(): Int? =
        _state.feed.value?.let {
            _state.activities.value.size + _state.aggregatedActivities.value.size
        }

    override fun idOf(model: FeedItem): Any = model.id

    override suspend fun fetchPage(next: String?): PaginationResult<FeedItem> =
        fetchPage(aroundId = null, previous = null, next = next)

    /**
     * The activity to refetch around and its position in the loaded activities, or null to refetch
     * from the top of the feed: nothing was loaded around an activity, the loaded activities start
     * at the top of the feed anyway, or the activity is not loaded anymore.
     */
    private fun refetchAnchor(): Pair<String, Int>? {
        val anchorId = aroundActivityId ?: return null
        if (_state.feed.value == null || !_state.canLoadPreviousActivities) return null
        val index = _state.activities.value.indexOfFirst { it.id == anchorId }
        return if (index >= 0) anchorId to index else null
    }

    /**
     * Loads the page at [previous] or [next], or the first page of the refetch if both are null:
     * the page around [aroundId], or the top of the feed if it is null.
     */
    private suspend fun fetchPage(
        aroundId: String?,
        previous: String?,
        next: String?,
    ): PaginationResult<FeedItem> {
        val firstPage = previous == null && next == null
        val info =
            if (firstPage) {
                refetchFirstPage(aroundId)
            } else {
                // The first page handles the watch, so another page never starts it again
                val page =
                    activitiesPageQuery(limit = null)
                        .copy(activityPrevious = previous, activityNext = next, watch = false)
                feedsRepository.getOrCreateFeed(page).getOrThrow()
            }
        if (firstPage) {
            // Only the first page has the rest of the feed data
            currentCoroutineContext().ensureActive()
            _state.onRefetchFeed(info)
            feedOwnValuesRepository.cache(info.feed)
        }
        val items =
            info.activities.map(FeedItem::Single) + info.aggregatedActivities.map(FeedItem::Group)
        return PaginationResult(items, info.pagination)
    }

    override fun onRefetched(result: PaginationResult<FeedItem>) {
        _state.onRefetchActivities(
            activities = result.models.mapNotNull { (it as? FeedItem.Single)?.activity },
            aggregatedActivities = result.models.mapNotNull { (it as? FeedItem.Group)?.group },
            pagination = result.pagination,
        )
    }

    /**
     * The refetch starts around [aroundId], or at the top of the feed if it is null. For a watched
     * feed this request is also what starts watching it again after a reconnection.
     */
    private suspend fun refetchFirstPage(aroundId: String?): GetOrCreateInfo {
        val watching = query.watch && feedWatchHandler.isWatching(fid)
        // Members are not refetched: they are managed by the member list
        val topOfFeed = query.copy(activityIdAround = null, memberLimit = 0, watch = watching)
        val fetch: suspend () -> GetOrCreateInfo = {
            if (aroundId == null) {
                feedsRepository.getOrCreateFeed(topOfFeed).getOrThrow()
            } else {
                fetchAround(aroundId, topOfFeed)
            }
        }
        return if (watching) feedWatchHandler.rewatch(fid, fetch).getOrThrow() else fetch()
    }

    /**
     * Loads the page around [aroundId]. The backend answers with a not found error when the
     * activity is not in the feed anymore: the refetch then starts at the top of the feed, with
     * [topOfFeed], and stays there.
     */
    private suspend fun fetchAround(aroundId: String, topOfFeed: FeedQuery): GetOrCreateInfo {
        // An around request can't carry page tokens
        val around =
            topOfFeed.copy(
                activityIdAround = aroundId,
                activityNext = null,
                activityPrevious = null,
            )
        val result = feedsRepository.getOrCreateFeed(around)
        val error = result.exceptionOrNull() as? StreamEndpointException
        if (error?.apiError?.statusCode != HTTP_NOT_FOUND) {
            return result.getOrThrow()
        }
        aroundActivityId = null
        return feedsRepository.getOrCreateFeed(topOfFeed).getOrThrow()
    }

    private suspend fun refetchFollowRequests() {
        val result =
            refetchPages(_state.followRequests.value.size, FollowData::id) { next ->
                feedsRepository
                    .queryFollows(followRequestsQuery.copy(next = next).toRequest())
                    .getOrThrow()
            }
        _state.onQueryFollowRequests(result, replace = true)
    }

    /** An entry of the feed: an activity, or a group of them in an aggregated feed. */
    sealed interface FeedItem {
        val id: Any

        data class Single(val activity: ActivityData) : FeedItem {
            override val id: Any
                get() = activity.id
        }

        data class Group(val group: AggregatedActivityData) : FeedItem {
            override val id: Any
                // Kept apart from activity ids
                get() = Group::class to group.group
        }
    }

    override suspend fun stopWatching(): Result<Unit> {
        feedWatchHandler.onStopWatching(query.fid)
        return feedsRepository.stopWatching(groupId = group, feedId = id)
    }

    override suspend fun updateFeed(request: UpdateFeedRequest): Result<FeedData> {
        return feedsRepository
            .updateFeed(feedGroupId = group, feedId = id, request = request)
            .onSuccess { subscriptionManager.onEvent(StateUpdateEvent.FeedUpdated(it)) }
    }

    override suspend fun deleteFeed(
        hardDelete: Boolean,
        purgeUserActivities: Boolean?,
    ): Result<Unit> {
        return feedsRepository
            .deleteFeed(
                feedGroupId = group,
                feedId = id,
                hardDelete = hardDelete,
                purgeUserActivities = purgeUserActivities,
            )
            .onSuccess { subscriptionManager.onEvent(StateUpdateEvent.FeedDeleted(fid.rawValue)) }
    }

    override suspend fun addActivity(
        request: FeedAddActivityRequest,
        attachmentUploadProgress: ((FeedUploadPayload, Double) -> Unit)?,
    ): Result<ActivityData> {
        val feeds = request.request.feeds.ifEmpty { listOf(fid.rawValue) }
        val requestWithFeeds = request.copy(request = request.request.copy(feeds = feeds))
        return activitiesRepository
            .addActivity(requestWithFeeds, attachmentUploadProgress)
            .onSuccess {
                subscriptionManager.onEvent(StateUpdateEvent.ActivityAdded(FidScope.unknown, it))
            }
    }

    override suspend fun updateActivity(
        id: String,
        request: UpdateActivityRequest,
    ): Result<ActivityData> {
        return activitiesRepository.updateActivity(id, request).onSuccess {
            subscriptionManager.onEvent(StateUpdateEvent.ActivityUpdated(FidScope.unknown, it))
        }
    }

    override suspend fun updateActivityPartial(
        id: String,
        request: UpdateActivityPartialRequest,
    ): Result<ActivityData> {
        return activitiesRepository.updateActivityPartial(id, request).onSuccess {
            subscriptionManager.onEvent(StateUpdateEvent.ActivityUpdated(FidScope.unknown, it))
        }
    }

    override suspend fun deleteActivity(
        id: String,
        hardDelete: Boolean,
        deleteNotificationActivity: Boolean?,
    ): Result<Unit> {
        return activitiesRepository
            .deleteActivity(
                activityId = id,
                hardDelete = hardDelete,
                deleteNotificationActivity = deleteNotificationActivity,
            )
            .onSuccess {
                subscriptionManager.onEvent(StateUpdateEvent.ActivityDeleted(FidScope.unknown, id))
            }
    }

    override suspend fun restoreActivity(id: String): Result<ActivityData> {
        return activitiesRepository.restoreActivity(id).onSuccess {
            subscriptionManager.onEvent(
                StateUpdateEvent.ActivityAdded(FidScope.unknown, it, restored = true)
            )
        }
    }

    override suspend fun markActivity(request: MarkActivityRequest): Result<Unit> {
        return activitiesRepository.markActivity(
            feedGroupId = group,
            feedId = id,
            request = request,
        )
    }

    override suspend fun repost(activityId: String, text: String?): Result<ActivityData> {
        val request =
            AddActivityRequest(
                type = "post",
                text = text,
                feeds = listOf(fid.rawValue),
                parentId = activityId,
            )
        return activitiesRepository.addActivity(FeedAddActivityRequest(request)).onSuccess {
            subscriptionManager.onEvent(StateUpdateEvent.ActivityAdded(FidScope.unknown, it))
        }
    }

    override suspend fun queryMoreActivities(limit: Int?): Result<List<ActivityData>> {
        val next = _state.activitiesPagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        return feedsRepository
            .getOrCreateFeed(activitiesPageQuery(limit).copy(activityNext = next))
            .onSuccess {
                _state.onQueryMoreActivities(
                    activities = it.activities,
                    aggregatedActivities = it.aggregatedActivities,
                    pagination = it.pagination,
                )
            }
            .map(GetOrCreateInfo::activities)
    }

    override suspend fun queryPreviousActivities(limit: Int?): Result<List<ActivityData>> {
        val previous = _state.activitiesPagination?.previous
        if (previous == null) {
            // If there is no previous cursor, return an empty list.
            return Result.success(emptyList())
        }
        return feedsRepository
            .getOrCreateFeed(activitiesPageQuery(limit).copy(activityPrevious = previous))
            .onSuccess {
                _state.onQueryPreviousActivities(
                    activities = it.activities,
                    aggregatedActivities = it.aggregatedActivities,
                    pagination = it.pagination,
                )
            }
            .map(GetOrCreateInfo::activities)
    }

    override suspend fun queryActivitiesAround(
        activityId: String,
        limit: Int?,
    ): Result<List<ActivityData>> {
        return feedsRepository
            .getOrCreateFeed(activitiesPageQuery(limit).copy(activityIdAround = activityId))
            .onSuccess {
                aroundActivityId = activityId
                _state.onQueryActivitiesAround(
                    activities = it.activities,
                    aggregatedActivities = it.aggregatedActivities,
                    pagination = it.pagination,
                )
            }
            .map(GetOrCreateInfo::activities)
    }

    /** The query for a page of activities only, without the rest of the feed data. */
    private fun activitiesPageQuery(limit: Int?): FeedQuery =
        FeedQuery(
            fid = fid,
            activityFilter = query.activityFilter,
            activityLimit = limit ?: query.activityLimit,
            data = null,
            enrichmentOptions = query.enrichmentOptions,
            externalRanking = null,
            followerLimit = 0,
            followingLimit = 0,
            interestWeights = null,
            memberLimit = 0,
            view = query.view,
            watch = query.watch,
            language = query.language,
            friendReactionsOptions = query.friendReactionsOptions,
            viewerLocation = query.viewerLocation,
        )

    override suspend fun addBookmark(
        activityId: String,
        request: AddBookmarkRequest,
    ): Result<BookmarkData> {
        return bookmarksRepository.addBookmark(activityId, request).onSuccess {
            subscriptionManager.onEvent(StateUpdateEvent.BookmarkAdded(it))
        }
    }

    override suspend fun updateBookmark(
        activityId: String,
        request: UpdateBookmarkRequest,
    ): Result<BookmarkData> {
        return bookmarksRepository.updateBookmark(activityId, request).onSuccess {
            subscriptionManager.onEvent(StateUpdateEvent.BookmarkUpdated(it))
        }
    }

    override suspend fun deleteBookmark(
        activityId: String,
        folderId: String?,
    ): Result<BookmarkData> {
        return bookmarksRepository
            .deleteBookmark(activityId = activityId, folderId = folderId)
            .onSuccess { subscriptionManager.onEvent(StateUpdateEvent.BookmarkDeleted(it)) }
    }

    override suspend fun getComment(commentId: String): Result<CommentData> {
        return commentsRepository.getComment(commentId, query.language).onSuccess {
            subscriptionManager.onEvent(StateUpdateEvent.CommentUpdated(FidScope.unknown, it))
        }
    }

    override suspend fun addComment(
        request: ActivityAddCommentRequest,
        attachmentUploadProgress: ((FeedUploadPayload, Double) -> Unit)?,
    ): Result<CommentData> {
        return commentsRepository.addComment(request, attachmentUploadProgress).onSuccess {
            subscriptionManager.onEvent(StateUpdateEvent.CommentAdded(FidScope.unknown, it))
        }
    }

    override suspend fun translateActivity(
        activityId: String,
        language: String,
    ): Result<ActivityData> {
        return activitiesRepository.translateActivity(activityId, language).onSuccess {
            subscriptionManager.onEvent(StateUpdateEvent.ActivityUpdated(FidScope.unknown, it))
        }
    }

    override suspend fun translateComment(
        commentId: String,
        language: String,
    ): Result<CommentData> {
        return commentsRepository.translateComment(commentId, language).onSuccess {
            subscriptionManager.onEvent(StateUpdateEvent.CommentUpdated(FidScope.unknown, it))
        }
    }

    override suspend fun updateComment(
        commentId: String,
        request: UpdateCommentRequest,
    ): Result<CommentData> {
        return commentsRepository.updateComment(commentId, request).onSuccess {
            subscriptionManager.onEvent(StateUpdateEvent.CommentUpdated(FidScope.unknown, it))
        }
    }

    override suspend fun deleteComment(
        commentId: String,
        hardDelete: Boolean?,
        deleteNotificationActivity: Boolean?,
    ): Result<Unit> {
        return commentsRepository
            .deleteComment(
                commentId = commentId,
                hardDelete = hardDelete,
                deleteNotificationActivity = deleteNotificationActivity,
            )
            .onSuccess { (comment, activity) ->
                subscriptionManager.onEvent(
                    StateUpdateEvent.CommentDeleted(FidScope.unknown, comment)
                )
                subscriptionManager.onEvent(
                    StateUpdateEvent.ActivityUpdated(FidScope.unknown, activity)
                )
            }
            .map {}
    }

    override suspend fun queryFollowSuggestions(limit: Int?): Result<List<FeedSuggestionData>> {
        return feedsRepository.queryFollowSuggestions(feedGroupId = group, limit = limit)
    }

    override suspend fun follow(
        targetFid: FeedId,
        createNotificationActivity: Boolean?,
        custom: Map<String, Any>?,
        pushPreference: FollowPushPreference?,
        activityCopyLimit: Int?,
        enrichOwnFields: Boolean?,
        skipPush: Boolean?,
    ): Result<FollowData> {
        val request =
            FollowRequest(
                activityCopyLimit = activityCopyLimit,
                createNotificationActivity = createNotificationActivity,
                custom = custom,
                enrichOwnFields = enrichOwnFields,
                pushPreference = pushPreference,
                skipPush = skipPush,
                source = fid.rawValue,
                target = targetFid.rawValue,
            )
        return feedsRepository.follow(request).onSuccess {
            subscriptionManager.onEvent(StateUpdateEvent.FollowAdded(it))
        }
    }

    override suspend fun updateFollow(
        targetFid: FeedId,
        custom: Map<String, Any>?,
        pushPreference: FollowPushPreference?,
    ): Result<FollowData> {
        val request =
            UpdateFollowRequest(
                source = fid.rawValue,
                target = targetFid.rawValue,
                custom = custom,
                pushPreference = pushPreference,
            )
        return feedsRepository.updateFollow(request).onSuccess {
            subscriptionManager.onEvent(StateUpdateEvent.FollowUpdated(it))
        }
    }

    override suspend fun unfollow(
        targetFid: FeedId,
        deleteNotificationActivity: Boolean?,
        keepHistory: Boolean?,
    ): Result<Unit> {
        return feedsRepository
            .unfollow(
                source = fid,
                target = targetFid,
                deleteNotificationActivity = deleteNotificationActivity,
                keepHistory = keepHistory,
            )
            .onSuccess { subscriptionManager.onEvent(StateUpdateEvent.FollowDeleted(it)) }
            .map {}
    }

    override suspend fun acceptFollow(sourceFid: FeedId, role: String?): Result<FollowData> {
        val request =
            AcceptFollowRequest(
                followerRole = role,
                source = sourceFid.rawValue,
                target = fid.rawValue,
            )
        return feedsRepository.acceptFollow(request).onSuccess { follow ->
            subscriptionManager.onEvent(StateUpdateEvent.FollowUpdated(follow))
        }
    }

    override suspend fun rejectFollow(sourceFid: FeedId): Result<FollowData> {
        val request = RejectFollowRequest(source = sourceFid.rawValue, target = fid.rawValue)
        return feedsRepository.rejectFollow(request).onSuccess { follow ->
            subscriptionManager.onEvent(StateUpdateEvent.FollowUpdated(follow))
        }
    }

    override suspend fun queryFollowRequests(): Result<List<FollowData>> {
        return queryFollowRequests(followRequestsQuery, replace = true)
    }

    override suspend fun queryMoreFollowRequests(limit: Int?): Result<List<FollowData>> {
        val next = _state.followRequestsPagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        return queryFollowRequests(followRequestsQuery.copy(limit = limit, next = next))
    }

    private suspend fun queryFollowRequests(
        query: FollowsQuery,
        replace: Boolean = false,
    ): Result<List<FollowData>> {
        return feedsRepository
            .queryFollows(query.toRequest())
            .onSuccess { _state.onQueryFollowRequests(it, replace) }
            .map { it.models }
    }

    override suspend fun queryFeedMembers(): Result<List<FeedMemberData>> {
        return memberList.get()
    }

    override suspend fun queryMoreFeedMembers(limit: Int?): Result<List<FeedMemberData>> {
        return memberList.queryMoreMembers(limit)
    }

    override suspend fun updateFeedMembers(
        request: UpdateFeedMembersRequest
    ): Result<ModelUpdates<FeedMemberData>> {
        return feedsRepository
            .updateFeedMembers(feedGroupId = group, feedId = id, request = request)
            .onSuccess { updates ->
                subscriptionManager.onEvent(
                    StateUpdateEvent.FeedMemberBatchUpdate(fid.rawValue, updates)
                )
            }
    }

    override suspend fun acceptFeedMember(): Result<FeedMemberData> {
        return feedsRepository.acceptFeedMember(feedGroupId = group, feedId = id).onSuccess {
            subscriptionManager.onEvent(StateUpdateEvent.FeedMemberUpdated(fid.rawValue, it))
        }
    }

    override suspend fun rejectFeedMember(): Result<FeedMemberData> {
        return feedsRepository.rejectFeedMember(feedGroupId = group, feedId = id).onSuccess {
            subscriptionManager.onEvent(StateUpdateEvent.FeedMemberUpdated(fid.rawValue, it))
        }
    }

    override suspend fun addActivityReaction(
        activityId: String,
        request: AddReactionRequest,
    ): Result<FeedsReactionData> {
        return activitiesRepository
            .addActivityReaction(activityId, request)
            .onSuccess { (reaction, activity) ->
                subscriptionManager.onEvent(
                    StateUpdateEvent.ActivityReactionUpserted(
                        scope = FidScope.unknown,
                        activity = activity,
                        reaction = reaction,
                        enforceUnique = request.enforceUnique == true,
                    )
                )
            }
            .map { it.first }
    }

    override suspend fun deleteActivityReaction(
        activityId: String,
        type: String,
        deleteNotificationActivity: Boolean?,
    ): Result<FeedsReactionData> {
        return activitiesRepository
            .deleteActivityReaction(
                activityId = activityId,
                type = type,
                deleteNotificationActivity = deleteNotificationActivity,
            )
            .onSuccess { (reaction, activity) ->
                subscriptionManager.onEvent(
                    StateUpdateEvent.ActivityReactionDeleted(FidScope.unknown, activity, reaction)
                )
            }
            .map { it.first }
    }

    override suspend fun addCommentReaction(
        commentId: String,
        request: AddCommentReactionRequest,
    ): Result<FeedsReactionData> {
        return commentsRepository
            .addCommentReaction(commentId, request)
            .onSuccess { (reaction, comment) ->
                subscriptionManager.onEvent(
                    StateUpdateEvent.CommentReactionUpserted(
                        scope = FidScope.unknown,
                        comment = comment,
                        reaction = reaction,
                        enforceUnique = request.enforceUnique == true,
                    )
                )
            }
            .map { it.first }
    }

    override suspend fun deleteCommentReaction(
        commentId: String,
        type: String,
        deleteNotificationActivity: Boolean?,
    ): Result<FeedsReactionData> {
        return commentsRepository
            .deleteCommentReaction(
                commentId = commentId,
                type = type,
                deleteNotificationActivity = deleteNotificationActivity,
            )
            .onSuccess { (reaction, comment) ->
                subscriptionManager.onEvent(
                    StateUpdateEvent.CommentReactionDeleted(FidScope.unknown, comment, reaction)
                )
            }
            .map { it.first }
    }

    override suspend fun createPoll(
        request: CreatePollRequest,
        activityType: String,
    ): Result<ActivityData> {
        return pollsRepository.createPoll(request).flatMap { poll ->
            val request =
                AddActivityRequest(
                    feeds = listOf(fid.rawValue),
                    pollId = poll.id,
                    type = activityType,
                )
            activitiesRepository.addActivity(FeedAddActivityRequest(request)).onSuccess {
                subscriptionManager.onEvent(StateUpdateEvent.ActivityAdded(FidScope.unknown, it))
            }
        }
    }
}
