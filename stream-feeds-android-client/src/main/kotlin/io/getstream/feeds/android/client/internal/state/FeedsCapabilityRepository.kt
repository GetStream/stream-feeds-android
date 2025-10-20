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

import io.getstream.android.core.api.model.StreamRetryPolicy
import io.getstream.android.core.api.processing.StreamBatcher
import io.getstream.android.core.api.processing.StreamRetryProcessor
import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.FeedOwnCapability
import io.getstream.feeds.android.network.models.OwnCapabilitiesBatchRequest
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CompletableDeferred

internal class FeedsCapabilityRepository(
    private val batcher: StreamBatcher<FeedId>,
    private val retryProcessor: StreamRetryProcessor,
    private val api: FeedsApi,
) {
    private val cache = ConcurrentHashMap<FeedId, List<FeedOwnCapability>>()
    private val pending = ConcurrentHashMap<FeedId, CompletableDeferred<List<FeedOwnCapability>>>()

    init {
        batcher.onBatch { ids, _, _ -> processBatch(ids) }
    }

    /** Caches the provided [capabilities], replacing existing entries for the same feed IDs. */
    fun cache(capabilities: Map<FeedId, List<FeedOwnCapability>>) {
        cache.putAll(capabilities)
        notifyPending()
    }

    /** Requests to fetch the capabilities for the provided [id] if they are not already cached. */
    suspend fun fetch(id: FeedId): Result<List<FeedOwnCapability>> {
        cache[id]?.let {
            return Result.success(it)
        }

        val deferred =
            pending.getOrPut(id) {
                batcher.offer(id)
                CompletableDeferred()
            }

        // Re-check cache in case batch completed during getOrPut
        cache[id]?.let {
            pending.remove(id)?.complete(it)
            return Result.success(it)
        }

        return runSafely { deferred.await() }
    }

    private suspend fun processBatch(ids: List<FeedId>) {
        val uncachedIds = ids.filterNotTo(mutableSetOf(), cache::containsKey)

        if (uncachedIds.isEmpty()) {
            notifyPending()
            return
        }

        retryProcessor
            .retry(retryPolicy) { fetchAll(uncachedIds) }
            .fold(
                onSuccess = ::cache,
                onFailure = { error ->
                    uncachedIds.forEach { id -> pending.remove(id)?.completeExceptionally(error) }
                },
            )
    }

    private suspend fun fetchAll(ids: Set<FeedId>) =
        api.ownCapabilitiesBatch(
                ownCapabilitiesBatchRequest =
                    OwnCapabilitiesBatchRequest(ids.toList().map(FeedId::rawValue))
            )
            .capabilities
            .mapKeys { FeedId(it.key) }

    private fun notifyPending() {
        pending.keys.toSet().forEach { id ->
            cache[id]?.let { caps -> pending.remove(id)?.complete(caps) }
        }
    }

    companion object {
        private val retryPolicy = StreamRetryPolicy.exponential(maxRetries = 3)
    }
}
