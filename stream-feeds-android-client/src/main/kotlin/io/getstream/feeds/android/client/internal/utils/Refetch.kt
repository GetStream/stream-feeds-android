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

package io.getstream.feeds.android.client.internal.utils

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.internal.model.PaginationResult
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

/**
 * Loads pages from the start until they cover [loadedCount] items, so that loaded data can be
 * replaced with a fresh copy of the same size. At least the first page is loaded.
 *
 * @param loadedCount The number of items that are loaded now.
 * @param idSelector Returns the identifier of an item.
 * @param fetchPage Loads the page for the given next page token, or the first page for `null`.
 * @return The loaded items in page order, with the pagination data of the last page.
 */
internal suspend fun <T> refetchPages(
    loadedCount: Int,
    idSelector: (T) -> Any,
    fetchPage: suspend (next: String?) -> PaginationResult<T>,
): PaginationResult<T> {
    val models = LinkedHashMap<Any, T>()
    var pagination = PaginationData.EMPTY
    do {
        val page = fetchPage(pagination.next)
        currentCoroutineContext().ensureActive()
        // A page that repeats loaded items means the tokens are not moving forward
        val addsNewModels = page.models.any { idSelector(it) !in models }
        page.models.forEach { model ->
            // Items can move to a later page while refetching
            val id = idSelector(model)
            models.remove(id)
            models[id] = model
        }
        pagination = page.pagination
    } while (addsNewModels && models.size < loadedCount && pagination.next != null)
    return PaginationResult(models.values.toList(), pagination)
}
