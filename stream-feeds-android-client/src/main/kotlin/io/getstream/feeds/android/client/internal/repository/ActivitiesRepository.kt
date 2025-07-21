package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.core.generated.models.AddActivityRequest

/**
 * Represents the repository for managing activities.
 * Performs requests and transforms API models to domain models.
 */
internal interface ActivitiesRepository {

    suspend fun addActivity(request: AddActivityRequest): Result<ActivityData>
}