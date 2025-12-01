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

import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.network.models.FeedOwnCapability
import java.util.Collections.singletonMap

/**
 * A repository for managing feed capabilities. Caches feed capabilities and requests capabilities
 * that are not yet cached.
 */
internal interface FeedOwnDataRepository {
    /**
     * Caches the provided feed capabilities.
     *
     * @param capabilities A map of feed IDs to their corresponding sets of capabilities.
     */
    fun cache(capabilities: Map<FeedId, Set<FeedOwnCapability>>)

    /**
     * Retrieves cached capabilities for the specified feed. If the capabilities are not cached,
     * queue them for fetching and return null.
     *
     * @param id The feed ID to retrieve capabilities for.
     * @return The cached capabilities for the feed, or null if not cached.
     */
    fun getOrRequest(id: FeedId): Set<FeedOwnCapability>?
}

internal fun FeedOwnDataRepository.cache(feed: FeedData) {
    cache(singletonMap(feed.fid, feed.ownCapabilities))
}

internal fun FeedOwnDataRepository.cache(feeds: Iterable<FeedData>) {
    cache(feeds.associateBy(FeedData::fid, FeedData::ownCapabilities))
}
