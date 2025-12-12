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

import io.getstream.android.core.api.model.StreamRetryPolicy
import io.getstream.android.core.api.processing.StreamBatcher
import io.getstream.android.core.api.processing.StreamRetryProcessor
import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.internal.model.FeedOwnValues
import io.getstream.feeds.android.client.internal.model.toModel
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FeedOwnValuesUpdated
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.subscribe.onEvent
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.OwnBatchRequest
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope

internal class FeedOwnValuesRepositoryImpl(
    private val batcher: StreamBatcher<FeedId>,
    private val retryProcessor: StreamRetryProcessor,
    private val api: FeedsApi,
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener>,
) : FeedOwnValuesRepository {
    private val cache = ConcurrentHashMap<FeedId, FeedOwnValues>()

    init {
        batcher.onBatch { ids, _, _ -> processBatch(ids) }
    }

    override fun cache(ownValues: Map<FeedId, FeedOwnValues>) {
        val before = cache.toMap()
        cache.putAll(ownValues)
        val after = cache.toMap()

        if (after != before) {
            subscriptionManager.onEvent(FeedOwnValuesUpdated(cache.toMap()))
        }
    }

    override fun getOrRequest(id: FeedId): FeedOwnValues? {
        val cached = cache[id]

        if (cached == null) {
            batcher.offer(id)
        }

        return cached
    }

    private suspend fun processBatch(ids: List<FeedId>) {
        ids.filterNotTo(mutableSetOf(), cache::containsKey)
            .takeIf(Set<FeedId>::isNotEmpty)
            ?.let { uniqueIds -> retryProcessor.retry(retryPolicy) { fetch(uniqueIds) } }
            ?.onSuccess(::cache)
    }

    private suspend fun fetch(ids: Set<FeedId>): Map<FeedId, FeedOwnValues> {
        val request = OwnBatchRequest(ids.map(FeedId::rawValue))

        return api.ownBatch(ownBatchRequest = request)
            .data
            .entries
            .associateBy(keySelector = { FeedId(it.key) }, valueTransform = { it.value.toModel() })
            .toMap()
    }

    companion object {
        private val retryPolicy = StreamRetryPolicy.exponential(maxRetries = 3)

        fun createBatcher(scope: CoroutineScope) =
            StreamBatcher<FeedId>(
                scope = scope,
                batchSize = 100,
                initialDelayMs = 2000,
                maxDelayMs = 10_000,
            )
    }
}
