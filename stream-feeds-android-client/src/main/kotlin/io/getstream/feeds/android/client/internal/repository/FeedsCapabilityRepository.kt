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
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FeedCapabilitiesUpdated
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.subscribe.onEvent
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.FeedOwnCapability
import io.getstream.feeds.android.network.models.OwnCapabilitiesBatchRequest
import java.util.Collections.singletonMap
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope

internal class FeedsCapabilityRepository(
    private val batcher: StreamBatcher<FeedId>,
    private val retryProcessor: StreamRetryProcessor,
    private val api: FeedsApi,
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener>,
) {
    private val cache = ConcurrentHashMap<FeedId, Set<FeedOwnCapability>>()

    init {
        batcher.onBatch { ids, _, _ -> processBatch(ids) }
    }

    /** Caches the provided [capabilities] and notify listeners in case of changes. */
    fun cache(capabilities: Map<FeedId, Set<FeedOwnCapability>>) {
        val before = cache.toMap()
        cache.putAll(capabilities)
        val after = cache.toMap()

        if (after != before) {
            subscriptionManager.onEvent(FeedCapabilitiesUpdated(cache.toMap()))
        }
    }

    fun getOrRequest(id: FeedId): Set<FeedOwnCapability>? {
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

    private suspend fun fetch(ids: Set<FeedId>): Map<FeedId, Set<FeedOwnCapability>> {
        val request = OwnCapabilitiesBatchRequest(ids.map(FeedId::rawValue))

        return api.ownCapabilitiesBatch(ownCapabilitiesBatchRequest = request)
            .capabilities
            .entries
            .associateBy(keySelector = { FeedId(it.key) }, valueTransform = { it.value.toSet() })
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

internal fun FeedsCapabilityRepository.cache(feed: FeedData) {
    cache(singletonMap(feed.fid, feed.ownCapabilities))
}

internal fun FeedsCapabilityRepository.cache(feeds: Iterable<FeedData>) {
    cache(feeds.associateBy(FeedData::fid, FeedData::ownCapabilities))
}
