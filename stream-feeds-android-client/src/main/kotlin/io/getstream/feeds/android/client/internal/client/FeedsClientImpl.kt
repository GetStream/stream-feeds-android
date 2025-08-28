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
package io.getstream.feeds.android.client.internal.client

import io.getstream.android.core.api.StreamClient
import io.getstream.android.core.api.model.connection.StreamConnectedUser
import io.getstream.android.core.api.model.connection.StreamConnectionState
import io.getstream.android.core.api.model.value.StreamApiKey
import io.getstream.android.core.api.socket.listeners.StreamClientListener
import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.Moderation
import io.getstream.feeds.android.client.api.file.FeedUploader
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.AppData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.PushNotificationsProvider
import io.getstream.feeds.android.client.api.model.User
import io.getstream.feeds.android.client.api.model.UserAuthType
import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feeds.android.client.api.state.ActivityCommentList
import io.getstream.feeds.android.client.api.state.ActivityList
import io.getstream.feeds.android.client.api.state.ActivityReactionList
import io.getstream.feeds.android.client.api.state.BookmarkFolderList
import io.getstream.feeds.android.client.api.state.BookmarkList
import io.getstream.feeds.android.client.api.state.CommentList
import io.getstream.feeds.android.client.api.state.CommentReactionList
import io.getstream.feeds.android.client.api.state.CommentReplyList
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.FeedList
import io.getstream.feeds.android.client.api.state.FollowList
import io.getstream.feeds.android.client.api.state.MemberList
import io.getstream.feeds.android.client.api.state.ModerationConfigList
import io.getstream.feeds.android.client.api.state.PollList
import io.getstream.feeds.android.client.api.state.PollVoteList
import io.getstream.feeds.android.client.api.state.query.ActivitiesQuery
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsQuery
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQuery
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery
import io.getstream.feeds.android.client.api.state.query.CommentReactionsQuery
import io.getstream.feeds.android.client.api.state.query.CommentRepliesQuery
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.client.api.state.query.FollowsQuery
import io.getstream.feeds.android.client.api.state.query.MembersQuery
import io.getstream.feeds.android.client.api.state.query.ModerationConfigsQuery
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import io.getstream.feeds.android.client.api.state.query.PollsQuery
import io.getstream.feeds.android.client.internal.client.reconnect.ConnectionRecoveryHandler
import io.getstream.feeds.android.client.internal.log.provideLogger
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.repository.AppRepository
import io.getstream.feeds.android.client.internal.repository.BookmarksRepository
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.repository.DevicesRepository
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.repository.FilesRepository
import io.getstream.feeds.android.client.internal.repository.ModerationRepository
import io.getstream.feeds.android.client.internal.repository.PollsRepository
import io.getstream.feeds.android.client.internal.state.ActivityCommentListImpl
import io.getstream.feeds.android.client.internal.state.ActivityImpl
import io.getstream.feeds.android.client.internal.state.ActivityListImpl
import io.getstream.feeds.android.client.internal.state.ActivityReactionListImpl
import io.getstream.feeds.android.client.internal.state.BookmarkFolderListImpl
import io.getstream.feeds.android.client.internal.state.BookmarkListImpl
import io.getstream.feeds.android.client.internal.state.CommentListImpl
import io.getstream.feeds.android.client.internal.state.CommentReactionListImpl
import io.getstream.feeds.android.client.internal.state.CommentReplyListImpl
import io.getstream.feeds.android.client.internal.state.FeedImpl
import io.getstream.feeds.android.client.internal.state.FeedListImpl
import io.getstream.feeds.android.client.internal.state.FollowListImpl
import io.getstream.feeds.android.client.internal.state.MemberListImpl
import io.getstream.feeds.android.client.internal.state.ModerationConfigListImpl
import io.getstream.feeds.android.client.internal.state.PollListImpl
import io.getstream.feeds.android.client.internal.state.PollVoteListImpl
import io.getstream.feeds.android.client.internal.subscribe.FeedsEventListener
import io.getstream.feeds.android.network.models.ActivityRequest
import io.getstream.feeds.android.network.models.AddActivityRequest
import io.getstream.feeds.android.network.models.DeleteActivitiesRequest
import io.getstream.feeds.android.network.models.DeleteActivitiesResponse
import io.getstream.feeds.android.network.models.ListDevicesResponse
import io.getstream.feeds.android.network.models.WSEvent
import io.getstream.log.TaggedLogger
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class FeedsClientImpl(
    private val coreClient: StreamClient,
    private val feedsEventsSubscriptionManager: StreamSubscriptionManager<FeedsEventListener>,
    override val apiKey: StreamApiKey,
    override val user: User,
    private val connectionRecoveryHandler: ConnectionRecoveryHandler,
    private val activitiesRepository: ActivitiesRepository,
    private val appRepository: AppRepository,
    private val bookmarksRepository: BookmarksRepository,
    private val commentsRepository: CommentsRepository,
    private val devicesRepository: DevicesRepository,
    private val feedsRepository: FeedsRepository,
    private val filesRepository: FilesRepository,
    private val moderationRepository: ModerationRepository,
    private val pollsRepository: PollsRepository,
    override val uploader: FeedUploader,
    override val moderation: Moderation,
    private val logger: TaggedLogger = provideLogger(tag = "Client"),
) : FeedsClient {

    override val state: StateFlow<StreamConnectionState>
        get() = coreClient.connectionState

    private val _events =
        MutableSharedFlow<WSEvent>(
            1,
            extraBufferCapacity = 100,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    override val events: Flow<WSEvent>
        get() = _events.asSharedFlow()

    private val clientSubscription =
        object : StreamClientListener {

            override fun onEvent(event: Any) {
                if (event is WSEvent) {
                    logger.v { "[onEvent] Received event from core: $event" }
                    _events.tryEmit(event)
                    feedsEventsSubscriptionManager.forEach { it.onEvent(event) }
                } else {
                    logger.e { "[onEvent] Received non-WSEvent: $event" }
                }
            }
        }

    override suspend fun connect(): Result<StreamConnectedUser> {
        if (user.type == UserAuthType.ANONYMOUS) {
            logger.e { "[connect] Attempting to connect an anonymous user, returning an error." }
            return Result.failure(IllegalArgumentException("Anonymous users cannot connect."))
        }
        coreClient.subscribe(clientSubscription)
        return coreClient.connect()
    }

    override suspend fun disconnect(): Result<Unit> {
        connectionRecoveryHandler.stop()
        return coreClient.disconnect()
    }

    override fun feed(group: String, id: String): Feed = feed(FeedId(group, id))

    override fun feed(fid: FeedId): Feed = feed(FeedQuery(fid))

    override fun feed(query: FeedQuery): Feed =
        FeedImpl(
            query = query,
            currentUserId = user.id,
            activitiesRepository = activitiesRepository,
            bookmarksRepository = bookmarksRepository,
            commentsRepository = commentsRepository,
            feedsRepository = feedsRepository,
            pollsRepository = pollsRepository,
            subscriptionManager = feedsEventsSubscriptionManager,
        )

    override fun feedList(query: FeedsQuery): FeedList =
        FeedListImpl(
            query = query,
            feedsRepository = feedsRepository,
            subscriptionManager = feedsEventsSubscriptionManager,
        )

    override fun followList(query: FollowsQuery): FollowList =
        FollowListImpl(
            query = query,
            feedsRepository = feedsRepository,
            subscriptionManager = feedsEventsSubscriptionManager,
        )

    override fun activity(activityId: String, fid: FeedId): Activity =
        ActivityImpl(
            activityId = activityId,
            fid = fid,
            currentUserId = user.id,
            activitiesRepository = activitiesRepository,
            commentsRepository = commentsRepository,
            pollsRepository = pollsRepository,
            subscriptionManager = feedsEventsSubscriptionManager,
            commentList =
                ActivityCommentListImpl(
                    query =
                        ActivityCommentsQuery(
                            objectId = activityId,
                            objectType = "activity",
                            depth = 3,
                        ),
                    currentUserId = user.id,
                    commentsRepository = commentsRepository,
                    subscriptionManager = feedsEventsSubscriptionManager,
                ),
        )

    override fun activityList(query: ActivitiesQuery): ActivityList =
        ActivityListImpl(
            query = query,
            currentUserId = user.id,
            activitiesRepository = activitiesRepository,
            subscriptionManager = feedsEventsSubscriptionManager,
        )

    override fun activityReactionList(query: ActivityReactionsQuery): ActivityReactionList =
        ActivityReactionListImpl(
            query = query,
            activitiesRepository = activitiesRepository,
            subscriptionManager = feedsEventsSubscriptionManager,
        )

    override suspend fun addActivity(request: AddActivityRequest): Result<ActivityData> {
        return activitiesRepository.addActivity(request)
    }

    override suspend fun upsertActivities(
        activities: List<ActivityRequest>
    ): Result<List<ActivityData>> {
        return activitiesRepository.upsertActivities(activities)
    }

    override suspend fun deleteActivities(
        request: DeleteActivitiesRequest
    ): Result<DeleteActivitiesResponse> {
        return activitiesRepository.deleteActivities(request)
    }

    override fun bookmarkList(query: BookmarksQuery): BookmarkList =
        BookmarkListImpl(
            query = query,
            bookmarksRepository = bookmarksRepository,
            subscriptionManager = feedsEventsSubscriptionManager,
        )

    override fun bookmarkFolderList(query: BookmarkFoldersQuery): BookmarkFolderList =
        BookmarkFolderListImpl(
            query = query,
            bookmarksRepository = bookmarksRepository,
            subscriptionManager = feedsEventsSubscriptionManager,
        )

    override fun commentList(query: CommentsQuery): CommentList =
        CommentListImpl(
            query = query,
            commentsRepository = commentsRepository,
            subscriptionManager = feedsEventsSubscriptionManager,
        )

    override fun activityCommentList(query: ActivityCommentsQuery): ActivityCommentList =
        ActivityCommentListImpl(
            query = query,
            currentUserId = user.id,
            commentsRepository = commentsRepository,
            subscriptionManager = feedsEventsSubscriptionManager,
        )

    override fun commentReplyList(query: CommentRepliesQuery): CommentReplyList =
        CommentReplyListImpl(
            query = query,
            currentUserId = user.id,
            commentsRepository = commentsRepository,
            subscriptionManager = feedsEventsSubscriptionManager,
        )

    override fun commentReactionList(query: CommentReactionsQuery): CommentReactionList =
        CommentReactionListImpl(
            query = query,
            commentsRepository = commentsRepository,
            subscriptionManager = feedsEventsSubscriptionManager,
        )

    override fun memberList(query: MembersQuery): MemberList =
        MemberListImpl(
            query = query,
            feedsRepository = feedsRepository,
            subscriptionManager = feedsEventsSubscriptionManager,
        )

    override fun pollVoteList(query: PollVotesQuery): PollVoteList =
        PollVoteListImpl(
            query = query,
            repository = pollsRepository,
            subscriptionManager = feedsEventsSubscriptionManager,
        )

    override fun pollList(query: PollsQuery): PollList =
        PollListImpl(
            query = query,
            pollsRepository = pollsRepository,
            subscriptionManager = feedsEventsSubscriptionManager,
        )

    override fun moderationConfigList(query: ModerationConfigsQuery): ModerationConfigList =
        ModerationConfigListImpl(query = query, moderationRepository = moderationRepository)

    override suspend fun getApp(): Result<AppData> {
        return appRepository.getApp()
    }

    override suspend fun queryDevices(): Result<ListDevicesResponse> {
        return devicesRepository.queryDevices()
    }

    override suspend fun createDevice(
        id: String,
        pushProvider: PushNotificationsProvider,
        pushProviderName: String,
    ): Result<Unit> {
        return devicesRepository.createDevice(id, pushProvider, pushProviderName)
    }

    override suspend fun deleteDevice(id: String): Result<Unit> {
        return devicesRepository.deleteDevice(id)
    }

    override suspend fun deleteFile(url: String): Result<Unit> {
        return filesRepository.deleteFile(url)
    }

    override suspend fun deleteImage(url: String): Result<Unit> {
        return filesRepository.deleteImage(url)
    }
}
