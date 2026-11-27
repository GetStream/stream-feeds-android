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

/**
 * Loads the page around an anchor item, then the pages before and after it until they cover what is
 * loaded on each side of the anchor, so that loaded data can be replaced with a fresh copy that
 * keeps its position. A direction stops when it is covered, when it has no more pages, or when a
 * page adds nothing new.
 *
 * If the first page does not hold the anchor, it is taken as the first page of the list: the pages
 * after it are loaded until they cover all the loaded items, as [refetchPages] does.
 *
 * @param anchorId The identifier of the anchor item.
 * @param loadedBefore The number of items that are loaded before the anchor now.
 * @param loadedAfter The number of items that are loaded after the anchor now.
 * @param idSelector Returns the identifier of an item.
 * @param fetchPage Loads the page for the given previous or next page token, or the page around the
 *   anchor when both are `null`. At most one of the tokens is set.
 * @return The loaded items in list order, with the previous page token of the first page and the
 *   next page token of the last one.
 */
internal suspend fun <T> refetchPagesAround(
    anchorId: Any,
    loadedBefore: Int,
    loadedAfter: Int,
    idSelector: (T) -> Any,
    fetchPage: suspend (previous: String?, next: String?) -> PaginationResult<T>,
): PaginationResult<T> {
    val around = fetchPage(null, null)
    currentCoroutineContext().ensureActive()
    val anchorIndex = around.models.indexOfFirst { idSelector(it) == anchorId }
    val anchorFound = anchorIndex >= 0
    val ids = around.models.mapTo(HashSet(), idSelector)

    /** Follows the tokens of one direction until [missing] new items are loaded. */
    suspend fun loadPages(
        missing: Int,
        firstToken: String?,
        tokenOf: (PaginationData) -> String?,
        fetch: suspend (token: String) -> PaginationResult<T>,
    ): Pair<List<List<T>>, String?> {
        val pages = mutableListOf<List<T>>()
        var remaining = missing
        var token = firstToken
        while (remaining > 0) {
            val page = fetch(token ?: break)
            currentCoroutineContext().ensureActive()
            // Items can move between pages while refetching: only the new ones are kept
            val newModels = page.models.filter { ids.add(idSelector(it)) }
            token = tokenOf(page.pagination)
            // A page that repeats loaded items means the tokens are not moving
            if (newModels.isEmpty()) break
            pages += newModels
            remaining -= newModels.size
        }
        return pages to token
    }

    val (before, previous) =
        if (anchorFound) {
            loadPages(
                missing = loadedBefore - anchorIndex,
                firstToken = around.pagination.previous,
                tokenOf = PaginationData::previous,
            ) { token ->
                fetchPage(token, null)
            }
        } else {
            emptyList<List<T>>() to around.pagination.previous
        }
    val coveredAfter = if (anchorFound) around.models.size - anchorIndex - 1 else around.models.size
    val neededAfter = if (anchorFound) loadedAfter else loadedBefore + 1 + loadedAfter
    val (after, next) =
        loadPages(
            missing = neededAfter - coveredAfter,
            firstToken = around.pagination.next,
            tokenOf = PaginationData::next,
        ) { token ->
            fetchPage(null, token)
        }
    // Each previous page goes before the ones loaded so far
    val models = before.asReversed().flatten() + around.models + after.flatten()
    return PaginationResult(models, PaginationData(next = next, previous = previous))
}
