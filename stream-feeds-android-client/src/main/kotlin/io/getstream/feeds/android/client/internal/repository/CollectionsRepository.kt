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

import io.getstream.feeds.android.network.models.CreateCollectionsRequest
import io.getstream.feeds.android.network.models.CreateCollectionsResponse
import io.getstream.feeds.android.network.models.DeleteCollectionsResponse
import io.getstream.feeds.android.network.models.ReadCollectionsResponse
import io.getstream.feeds.android.network.models.UpdateCollectionsRequest
import io.getstream.feeds.android.network.models.UpdateCollectionsResponse

/** A repository for managing collections. */
internal interface CollectionsRepository {

    /**
     * Read collections with optional filtering by user ID and collection name. By default, users
     * can only read their own collections.
     *
     * @param refs List of collection references to read in the format "<name>:<id>".
     * @return A [Result] containing the [ReadCollectionsResponse] if successful.
     */
    suspend fun readCollections(refs: List<String>): Result<ReadCollectionsResponse>

    /**
     * Create new collections in a batch operation. Collections are data objects that can be
     * attached to activities for managing shared data across multiple activities.
     *
     * @param request The [CreateCollectionsRequest] containing the collections to be created.
     * @return A [Result] containing the [CreateCollectionsResponse] if successful.
     */
    suspend fun createCollections(
        request: CreateCollectionsRequest
    ): Result<CreateCollectionsResponse>

    /**
     * Delete collections in a batch operation. Users can only delete their own collections.
     *
     * @param refs List of collection references to delete in the format "<name>:<id>".
     * @return A [Result] containing the [DeleteCollectionsResponse] if successful.
     */
    suspend fun deleteCollections(refs: List<String>): Result<DeleteCollectionsResponse>

    /**
     * Update existing collections in a batch operation. Only the custom data field is updatable.
     * Users can only update their own collections.
     *
     * @param request The [UpdateCollectionsRequest] containing the collections to be updated.
     * @return A [Result] containing the [UpdateCollectionsResponse] if successful.
     */
    suspend fun updateCollections(
        request: UpdateCollectionsRequest
    ): Result<UpdateCollectionsResponse>
}
