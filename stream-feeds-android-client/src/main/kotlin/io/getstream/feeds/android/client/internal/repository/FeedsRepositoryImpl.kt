package io.getstream.feeds.android.client.internal.repository

import io.getstream.android.core.query.sortedWith
import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.api.state.query.ActivitiesSort
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.client.api.state.query.toRequest
import io.getstream.feeds.android.core.generated.apis.ApiService
import io.getstream.feeds.android.core.generated.models.AcceptFollowRequest
import io.getstream.feeds.android.core.generated.models.QueryFeedMembersRequest
import io.getstream.feeds.android.core.generated.models.QueryFollowsRequest
import io.getstream.feeds.android.core.generated.models.RejectFollowRequest
import io.getstream.feeds.android.core.generated.models.SingleFollowRequest
import io.getstream.feeds.android.core.generated.models.UpdateFeedMembersRequest
import io.getstream.feeds.android.core.generated.models.UpdateFeedRequest

/**
 * Default implementation of the [FeedsRepository] interface.
 *
 * Uses the provided [ApiService] to perform network requests related to feeds.
 *
 * @property api The API service used to perform network requests.
 */
internal class FeedsRepositoryImpl(private val api: ApiService) : FeedsRepository {

    override suspend fun getOrCreateFeed(query: FeedQuery): Result<GetOrCreateInfo> = runSafely {
        val fid = query.fid
        val request = query.toRequest()
        val response = api.getOrCreateFeed(
            feedGroupId = fid.group,
            feedId = fid.id,
            getOrCreateFeedRequest = request,
        )
        val rawFollowers = response.followers.map { it.toModel() }
        GetOrCreateInfo(
            activities = PaginationResult(
                models = response.activities
                    .map { it.toModel() }
                    .sortedWith(ActivitiesSort.Default),
                pagination = PaginationData(
                    next = response.next,
                    previous = response.prev
                )
            ),
            activitiesQueryConfig = QueryConfiguration(
                filter = query.activityFilter,
                sort = ActivitiesSort.Default,
            ),
            feed = response.feed.toModel(),
            followers = rawFollowers.filter { it.isFollowerOf(fid) },
            following = response.following.map { it.toModel() }.filter { it.isFollowing(fid) },
            followRequests = rawFollowers.filter { it.isFollowRequest },
            members = PaginationResult(
                models = response.members.map { it.toModel() },
                pagination = response.memberPagination?.toModel() ?: PaginationData.EMPTY,
            ),
            ownCapabilities = response.ownCapabilities,
            pinnedActivities = response.pinnedActivities.map { it.toModel() },
            aggregatedActivities = response.aggregatedActivities.map { it.toModel() },
            notificationStatus = response.notificationStatus,
        )
    }

    override suspend fun stopWatching(groupId: String, feedId: String): Result<Unit> = runSafely {
        api.stopWatchingFeed(feedGroupId = groupId, feedId = feedId)
    }

    override suspend fun deleteFeed(
        feedGroupId: String,
        feedId: String,
        hardDelete: Boolean
    ): Result<Unit> = runSafely {
        api.deleteFeed(feedGroupId = feedGroupId, feedId = feedId, hardDelete = hardDelete)
    }

    override suspend fun updateFeed(
        feedGroupId: String,
        feedId: String,
        request: UpdateFeedRequest
    ): Result<FeedData> = runSafely {
        api.updateFeed(
            feedGroupId = feedGroupId,
            feedId = feedId,
            updateFeedRequest = request
        ).feed.toModel()
    }

    override suspend fun queryFeeds(query: FeedsQuery): Result<PaginationResult<FeedData>> =
        runSafely {
            val request = query.toRequest()
            val response = api.feedsQueryFeeds(queryFeedsRequest = request)
            val feeds = response.feeds.map { it.toModel() }
            val pagination = PaginationData(next = response.next, previous = response.prev)
            PaginationResult(feeds, pagination)
        }

    override suspend fun queryFollowSuggestions(
        feedGroupId: String,
        limit: Int?
    ): Result<List<FeedData>> = runSafely {
        api.getFollowSuggestions(feedGroupId = feedGroupId, limit = limit)
            .suggestions
            .map { it.toModel() }
    }

    override suspend fun queryFollows(
        request: QueryFollowsRequest
    ): Result<PaginationResult<FollowData>> =
        runSafely {
            val response = api.queryFollows(request)
            val follows = response.follows.map { it.toModel() }
            val pagination = PaginationData(next = response.next, previous = response.prev)
            PaginationResult(follows, pagination)
        }

    override suspend fun follow(request: SingleFollowRequest): Result<FollowData> = runSafely {
        api.follow(request).follow.toModel()
    }

    override suspend fun unfollow(
        source: FeedId,
        target: FeedId
    ): Result<Unit> = runSafely {
        api.unfollow(source = source.rawValue, target = target.rawValue)
    }

    override suspend fun acceptFollow(request: AcceptFollowRequest): Result<FollowData> =
        runSafely {
            api.acceptFollow(request).follow.toModel()
        }

    override suspend fun rejectFollow(request: RejectFollowRequest): Result<FollowData> =
        runSafely {
            api.rejectFollow(request).follow.toModel()
        }

    override suspend fun updateFeedMembers(
        feedGroupId: String,
        feedId: String,
        request: UpdateFeedMembersRequest
    ): Result<ModelUpdates<FeedMemberData>> = runSafely {
        val response = api.updateFeedMembers(
            feedGroupId = feedGroupId,
            feedId = feedId,
            updateFeedMembersRequest = request
        )
        val added = response.added.map { it.toModel() }
        val removedIds = response.removedIds
        val updated = response.updated.map { it.toModel() }
        ModelUpdates(added, removedIds, updated)
    }

    override suspend fun acceptFeedMember(
        feedGroupId: String,
        feedId: String
    ): Result<FeedMemberData> = runSafely {
        api.acceptFeedMemberInvite(feedGroupId = feedGroupId, feedId = feedId).member.toModel()
    }

    override suspend fun rejectFeedMember(
        feedGroupId: String,
        feedId: String
    ): Result<FeedMemberData> = runSafely {
        api.rejectFeedMemberInvite(feedGroupId = feedGroupId, feedId = feedId).member.toModel()
    }

    override suspend fun queryFeedMembers(
        feedGroupId: String,
        feedId: String,
        request: QueryFeedMembersRequest
    ): Result<PaginationResult<FeedMemberData>> = runSafely {
        val response = api.queryFeedMembers(
            feedGroupId = feedGroupId,
            feedId = feedId,
            queryFeedMembersRequest = request
        )
        val members = response.members.map { it.toModel() }
        val pagination = PaginationData(next = response.next, previous = response.prev)
        PaginationResult(members, pagination)
    }
}
