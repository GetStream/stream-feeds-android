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

package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.CollectionData
import io.getstream.feeds.android.client.api.state.query.CollectionsQuery

/**
 * A paginated list of collections matching a query.
 *
 * The list updates when this client creates, updates or deletes collections.
 *
 * ## Example:
 * ```kotlin
 * val query = CollectionsQuery()
 * val collectionList = feedsClient.collectionList(query)
 *
 * // Fetch the first page of collections matching the query
 * val collections = collectionList.get()
 *
 * // Load more collections if available
 * if (collectionList.state.canLoadMore) {
 *     val moreCollections = collectionList.queryMoreCollections()
 * }
 *
 * // Observe state changes
 * collectionList.state.collections.collect { collections ->
 *     println("Updated collections: ${collections.size}")
 * }
 * ```
 */
public interface CollectionList {

    /** The query used to fetch the collections. */
    public val query: CollectionsQuery

    /** An observable object representing the current state of the collection list. */
    public val state: CollectionListState

    /**
     * Retrieves the first page of collections.
     *
     * @return A [Result] containing a list of [CollectionData] or an error if the operation fails.
     */
    public suspend fun get(): Result<List<CollectionData>>

    /**
     * Retrieves the next page of collections based on the current pagination state. If there are no
     * more pages available, it returns an empty list.
     *
     * @param limit The maximum number of collections to retrieve. If null, uses the limit of the
     *   query.
     * @return A [Result] containing a list of [CollectionData] or an error if the operation fails.
     *   Returns an empty list if there are no more pages available.
     */
    public suspend fun queryMoreCollections(limit: Int? = null): Result<List<CollectionData>>
}
