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

package io.getstream.feeds.android.client.internal.client.reconnect

import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.utils.refetchPages

/**
 * A [Refetchable] that holds a paginated list. It only describes its list: [refetch] loads the
 * pages again, from the first one, until they cover the loaded items.
 */
internal interface PaginatedRefetchable<T> : Refetchable {
    /** The number of loaded items, or null if nothing is loaded. */
    fun loadedCount(): Int?

    /** The id that tells [model] apart from the other items of the list. */
    fun idOf(model: T): Any

    /** Loads the page at [next], or the first page if it is null. Throws if the request fails. */
    suspend fun fetchPage(next: String?): PaginationResult<T>

    /** Replaces the loaded items with the refetched ones. */
    fun onRefetched(result: PaginationResult<T>)

    override suspend fun refetch() {
        val loadedCount = loadedCount() ?: return
        onRefetched(refetchPages(loadedCount, ::idOf) { next -> fetchPage(next) })
    }
}
