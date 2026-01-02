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

package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.internal.model.FeedOwnValues
import io.getstream.feeds.android.client.internal.model.ownValues
import java.util.Collections.singletonMap

/**
 * A repository for managing feed own values, e.g. [FeedData.ownCapabilities],
 * [FeedData.ownFollows], [FeedData.ownMembership]. Caches feed own values and requests those that
 * are not yet cached.
 */
internal interface FeedOwnValuesRepository {
    /**
     * Caches the provided feed own values.
     *
     * @param ownValues A map of feed IDs to their corresponding sets of own values.
     */
    fun cache(ownValues: Map<FeedId, FeedOwnValues>)

    /**
     * Retrieves cached own values for the specified feed. If the values are not cached, queues them
     * for fetching and returns null.
     *
     * @param id The feed ID to retrieve own values for.
     * @return The cached own values for the feed, or null if not cached.
     */
    fun getOrRequest(id: FeedId): FeedOwnValues?
}

internal fun FeedOwnValuesRepository.cache(feed: FeedData) {
    cache(singletonMap(feed.fid, feed.ownValues()))
}

internal fun FeedOwnValuesRepository.cache(feeds: Iterable<FeedData>) {
    cache(feeds.associateBy(FeedData::fid, FeedData::ownValues))
}
