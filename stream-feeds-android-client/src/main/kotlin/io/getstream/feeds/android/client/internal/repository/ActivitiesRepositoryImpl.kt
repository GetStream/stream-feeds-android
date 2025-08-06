package io.getstream.feeds.android.client.internal.repository

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.api.state.query.ActivitiesQuery
import io.getstream.feeds.android.client.api.state.query.toRequest
import io.getstream.feeds.android.core.generated.apis.ApiService
import io.getstream.feeds.android.core.generated.models.ActivityRequest
import io.getstream.feeds.android.core.generated.models.AddActivityRequest
import io.getstream.feeds.android.core.generated.models.AddReactionRequest
import io.getstream.feeds.android.core.generated.models.MarkActivityRequest
import io.getstream.feeds.android.core.generated.models.QueryActivityReactionsRequest
import io.getstream.feeds.android.core.generated.models.UpdateActivityRequest
import io.getstream.feeds.android.core.generated.models.UpsertActivitiesRequest

/**
 * Default implementation of the [ActivitiesRepository] interface.
 *
 * Uses the provided [ApiService] to perform network requests related to activities.
 *
 * @property api The API service used to perform network requests.
 */
internal class ActivitiesRepositoryImpl(
    private val api: ApiService,
    // TODO: Handle attachment uploads logic
) : ActivitiesRepository {

    override suspend fun addActivity(request: AddActivityRequest): Result<ActivityData> =
        runSafely {
            api.addActivity(request).activity.toModel()
        }

    override suspend fun deleteActivity(
        activityId: String,
        hardDelete: Boolean
    ): Result<Unit> = runSafely {
        api.deleteActivity(activityId, hardDelete)
    }

    override suspend fun getActivity(activityId: String): Result<ActivityData> = runSafely {
        api.getActivity(activityId).activity.toModel()
    }

    override suspend fun updateActivity(
        activityId: String,
        request: UpdateActivityRequest
    ): Result<ActivityData> = runSafely {
        api.updateActivity(activityId, request).activity.toModel()
    }

    override suspend fun upsertActivities(activities: List<ActivityRequest>): Result<List<ActivityData>> =
        runSafely {
            val request = UpsertActivitiesRequest(activities)
            api.upsertActivities(request).activities.map { it.toModel() }
        }

    override suspend fun pin(activityId: String, fid: FeedId): Result<ActivityData> = runSafely {
        api.pinActivity(
            feedGroupId = fid.group,
            feedId = fid.id,
            activityId = activityId
        ).activity.toModel()
    }

    override suspend fun unpin(
        activityId: String,
        fid: FeedId,
    ): Result<ActivityData> = runSafely {
        api.unpinActivity(
            feedGroupId = fid.group,
            feedId = fid.id,
            activityId = activityId,
        ).activity.toModel()
    }

    override suspend fun markActivity(
        feedGroupId: String,
        feedId: String,
        request: MarkActivityRequest
    ): Result<Unit> = runSafely {
        api.markActivity(feedGroupId = feedGroupId, feedId = feedId, markActivityRequest = request)
    }

    override suspend fun queryActivities(
        query: ActivitiesQuery
    ): Result<PaginationResult<ActivityData>> = runSafely {
        val request = query.toRequest()
        val response = api.queryActivities(request)
        PaginationResult(
            models = response.activities.map { it.toModel() },
            pagination = PaginationData(next = response.next, previous = response.prev)
        )
    }

    override suspend fun addReaction(
        activityId: String,
        request: AddReactionRequest
    ): Result<FeedsReactionData> = runSafely {
        api.addReaction(activityId, request).reaction.toModel()
    }

    override suspend fun deleteReaction(
        activityId: String,
        type: String
    ): Result<FeedsReactionData> = runSafely {
        api.deleteActivityReaction(activityId = activityId, type = type).reaction.toModel()
    }

    override suspend fun queryActivityReactions(
        activityId: String,
        request: QueryActivityReactionsRequest
    ): Result<PaginationResult<FeedsReactionData>> = runSafely {
        val response = api.queryActivityReactions(activityId, request)
        PaginationResult(
            models = response.reactions.map { it.toModel() },
            pagination = PaginationData(next = response.next, previous = response.prev)
        )
    }
}
