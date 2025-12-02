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

import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.state.ActivityList
import io.getstream.feeds.android.client.api.state.ActivityListState
import io.getstream.feeds.android.client.api.state.query.ActivitiesQuery
import io.getstream.feeds.android.client.internal.model.QueryConfiguration
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.repository.FeedOwnValuesRepository
import io.getstream.feeds.android.client.internal.repository.cache
import io.getstream.feeds.android.client.internal.state.event.handler.ActivityListEventHandler
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

/**
 * A paginated list of activities that supports real-time updates and filtering.
 *
 * `ActivityList` provides a convenient way to fetch, paginate, and observe activities with
 * automatic real-time updates via WebSocket events. It manages the state of activities and provides
 * methods for loading more activities as needed.
 *
 * @property query The query configuration used for fetching activities.
 * @property currentUserId The ID of the current user.
 * @property activitiesRepository The repository responsible for retrieving activities data.
 * @property subscriptionManager The manager for WebSocket subscriptions to receive real-time
 *   updates.
 */
internal class ActivityListImpl(
    override val query: ActivitiesQuery,
    private val currentUserId: String,
    private val activitiesRepository: ActivitiesRepository,
    private val feedOwnValuesRepository: FeedOwnValuesRepository,
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener>,
) : ActivityList {

    private val _state: ActivityListStateImpl = ActivityListStateImpl(query, currentUserId)

    private val eventHandler =
        ActivityListEventHandler(
            filter = query.filter,
            currentUserId = currentUserId,
            state = _state,
        )

    init {
        subscriptionManager.subscribe(eventHandler)
    }

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
        val nextQuery =
            ActivitiesQuery(
                filter = _state.queryConfig?.filter,
                sort = _state.queryConfig?.sort,
                limit = limit ?: query.limit,
                next = next,
                previous = null,
            )
        return queryActivities(nextQuery)
    }

    private suspend fun queryActivities(query: ActivitiesQuery): Result<List<ActivityData>> {
        return activitiesRepository
            .queryActivities(query)
            .onSuccess {
                _state.onQueryMoreActivities(it, QueryConfiguration(query.filter, query.sort))
                feedOwnValuesRepository.cache(it.models.mapNotNull(ActivityData::currentFeed))
            }
            .map { it.models }
    }
}
