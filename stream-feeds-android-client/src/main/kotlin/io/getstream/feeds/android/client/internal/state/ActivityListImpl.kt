package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.ActivitiesQuery
import io.getstream.feeds.android.client.api.state.ActivityList
import io.getstream.feeds.android.client.api.state.ActivityListState
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository

/**
 * A paginated list of activities that supports real-time updates and filtering.
 *
 * `ActivityList` provides a convenient way to fetch, paginate, and observe activities
 * with automatic real-time updates via WebSocket events. It manages the state of activities
 * and provides methods for loading more activities as needed.
 *
 * @property query The query configuration used for fetching activities.
 * @property currentUserId The ID of the current user.
 * @property activitiesRepository The repository responsible for retrieving activities data.
 */
internal class ActivityListImpl(
    override val query: ActivitiesQuery,
    private val currentUserId: String,
    private val activitiesRepository: ActivitiesRepository,
    // TODO: Observe events
) : ActivityList {

    private val _state: ActivityListStateImpl = ActivityListStateImpl(query, currentUserId)

    override val state: ActivityListState
        get() = _state

    override suspend fun get(): Result<List<ActivityData>> {
        return queryActivities(query)
    }

    override suspend fun queryMoreActivities(limit: Int?): Result<List<ActivityData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery = ActivitiesQuery(
            filter = _state.queryConfig?.filter,
            sort = _state.queryConfig?.sort,
            limit = limit ?: query.limit,
            next = next,
            previous = null,
        )
        return queryActivities(nextQuery)
    }

    private suspend fun queryActivities(query: ActivitiesQuery): Result<List<ActivityData>> {
        return activitiesRepository.queryActivities(query)
            .onSuccess {
                _state.onQueryMoreActivities(it, QueryConfiguration(query.filter, query.sort))
            }
            .map {
                it.models
            }
    }
}
