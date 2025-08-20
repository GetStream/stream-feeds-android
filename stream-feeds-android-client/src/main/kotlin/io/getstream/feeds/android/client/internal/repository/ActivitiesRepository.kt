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
package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedAddActivityRequest
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.state.query.ActivitiesQuery
import io.getstream.feeds.android.core.generated.models.ActivityRequest
import io.getstream.feeds.android.core.generated.models.AddActivityRequest
import io.getstream.feeds.android.core.generated.models.AddReactionRequest
import io.getstream.feeds.android.core.generated.models.DeleteActivitiesRequest
import io.getstream.feeds.android.core.generated.models.DeleteActivitiesResponse
import io.getstream.feeds.android.core.generated.models.MarkActivityRequest
import io.getstream.feeds.android.core.generated.models.QueryActivityReactionsRequest
import io.getstream.feeds.android.core.generated.models.UpdateActivityRequest

/**
 * Represents the repository for managing activities. Performs requests and transforms API models to
 * domain models.
 */
internal interface ActivitiesRepository {

    /**
     * Adds a new activity to the feed.
     *
     * @param request The request containing the activity data to be added.
     * @return A [Result] containing the added [ActivityData] or an error.
     */
    suspend fun addActivity(request: AddActivityRequest): Result<ActivityData>

    /**
     * Adds a new activity to the feed.
     *
     * @param request The request containing the activity data to be added.
     * @return A [Result] containing the added [ActivityData] or an error.
     */
    suspend fun addActivity(
        request: FeedAddActivityRequest,
        attachmentUploadProgress: ((FeedUploadPayload, Double) -> Unit)? = null,
    ): Result<ActivityData>

    /**
     * Deletes an activity from the feed.
     *
     * @param activityId The ID of the activity to be deleted.
     * @param hardDelete If true, the activity will be permanently deleted; otherwise, it will be
     *   soft-deleted.
     * @return A [Result] indicating success or failure.
     */
    suspend fun deleteActivity(activityId: String, hardDelete: Boolean): Result<Unit>

    /**
     * Deletes multiple activities based on the provided request.
     *
     * @param request The request containing the IDs of activities to be deleted.
     * @return A [Result] indicating success or failure.
     */
    suspend fun deleteActivities(request: DeleteActivitiesRequest): Result<DeleteActivitiesResponse>

    /**
     * Retrieves an activity by its ID.
     *
     * @param activityId The ID of the activity to retrieve.
     * @return A [Result] containing the [ActivityData] or an error.
     */
    suspend fun getActivity(activityId: String): Result<ActivityData>

    /**
     * Updates an existing activity.
     *
     * @param activityId The ID of the activity to update.
     * @param request The request containing the updated activity data.
     * @return A [Result] containing the updated [ActivityData] or an error.
     */
    suspend fun updateActivity(
        activityId: String,
        request: UpdateActivityRequest,
    ): Result<ActivityData>

    /**
     * Upserts a list of activities.
     *
     * @param activities The list of activities to upsert.
     * @return A [Result] containing the list of upserted [ActivityData] or an error.
     */
    suspend fun upsertActivities(activities: List<ActivityRequest>): Result<List<ActivityData>>

    /**
     * Pins an activity to a feed.
     *
     * @param activityId The ID of the activity to pin.
     * @param fid The ID of the feed where the activity should be pinned.
     * @return A [Result] containing the pinned [ActivityData] or an error.
     */
    suspend fun pin(activityId: String, fid: FeedId): Result<ActivityData>

    /**
     * Unpins an activity from a feed.
     *
     * @param activityId The ID of the activity to unpin.
     * @param fid The ID of the feed from which the activity should be unpinned.
     * @return A [Result] containing the unpinned [ActivityData] or an error.
     */
    suspend fun unpin(activityId: String, fid: FeedId): Result<ActivityData>

    /**
     * Marks activities in a feed as read or seen.
     *
     * @param feedGroupId The group ID of the feed.
     * @param feedId The ID of the feed.
     * @param request The request containing the marking criteria.
     * @return A [Result] indicating success or failure.
     */
    suspend fun markActivity(
        feedGroupId: String,
        feedId: String,
        request: MarkActivityRequest,
    ): Result<Unit>

    /**
     * Queries activities based on the provided query parameters.
     *
     * @param query The query parameters to filter and sort activities.
     * @return A [Result] containing a [PaginationResult] of [ActivityData] or an error.
     */
    suspend fun queryActivities(query: ActivitiesQuery): Result<PaginationResult<ActivityData>>

    /**
     * Adds a reaction to an activity.
     *
     * @param activityId The ID of the activity to react to.
     * @param request The request containing the reaction data.
     * @return A [Result] containing the [FeedsReactionData] or an error.
     */
    suspend fun addReaction(
        activityId: String,
        request: AddReactionRequest,
    ): Result<FeedsReactionData>

    /**
     * Deletes a reaction from an activity.
     *
     * @param activityId The ID of the activity from which to delete the reaction.
     * @param type The type of the reaction to delete.
     * @return A [Result] containing the deleted [FeedsReactionData] or an error.
     */
    suspend fun deleteReaction(activityId: String, type: String): Result<FeedsReactionData>

    /**
     * Queries reactions for a specific activity.
     *
     * @param activityId The ID of the activity for which to query reactions.
     * @param request The request containing pagination and filtering criteria.
     * @return A [Result] containing a [PaginationResult] of [FeedsReactionData].
     */
    suspend fun queryActivityReactions(
        activityId: String,
        request: QueryActivityReactionsRequest,
    ): Result<PaginationResult<FeedsReactionData>>
}
