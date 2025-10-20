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
import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FeedCapabilitiesUpdated
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.subscribe.onEvent
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.FeedOwnCapability
import io.getstream.feeds.android.network.models.OwnCapabilitiesBatchRequest
import java.util.concurrent.ConcurrentHashMap

internal class FeedsCapabilityRepository(
    private val batcher: StreamBatcher<FeedId>,
    private val retryProcessor: StreamRetryProcessor,
    private val api: FeedsApi,
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener>,
) {
    private val cache = ConcurrentHashMap<FeedId, List<FeedOwnCapability>>()

    init {
        batcher.onBatch { ids, _, _ -> processBatch(ids) }
    }

    /** Caches the provided [capabilities], replacing existing entries for the same feed IDs. */
    fun cache(capabilities: Map<FeedId, List<FeedOwnCapability>>) {
        cache.putAll(capabilities)
        subscriptionManager.onEvent(FeedCapabilitiesUpdated(cache.toMap()))
    }

    /** Requests to fetch the capabilities for the provided [id] if they are not already cached. */
    fun request(id: FeedId) {
        if (!cache.containsKey(id)) {
            batcher.offer(id)
        }
    }

    private suspend fun processBatch(ids: List<FeedId>) {
        ids.filterNotTo(mutableSetOf(), cache::containsKey)
            .takeIf(Set<FeedId>::isNotEmpty)
            ?.let { uniqueIds -> retryProcessor.retry(retryPolicy) { fetch(uniqueIds) } }
            ?.onSuccess(::cache)
    }

    private suspend fun fetch(ids: Set<FeedId>) =
        api.ownCapabilitiesBatch(
                ownCapabilitiesBatchRequest =
                    OwnCapabilitiesBatchRequest(ids.toList().map(FeedId::rawValue))
            )
            .capabilities
            .mapKeys { FeedId(it.key) }

    companion object {
        private val retryPolicy = StreamRetryPolicy.exponential(maxRetries = 3)
    }
}
