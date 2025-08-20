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
package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.FollowsQuery
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable state object that manages the current state of a follow list.
 *
 * This class maintains maintains the current list of follows, pagination information, and provides
 * real-time updates when follows are modified. It automatically handles WebSocket events to keep
 * the follow list synchronized.
 */
public interface FollowListState {

    /** The original query configuration used to fetch follows. */
    public val query: FollowsQuery

    /**
     * All the paginated follows currently loaded.
     *
     * This array contains all follows that have been fetched across multiple pagination requests.
     * The follows are automatically sorted according to the current sorting configuration.
     */
    public val follows: StateFlow<List<FollowData>>

    /**
     * Last pagination information from the most recent request.
     *
     * Contains the `next` and `previous` cursor values that can be used to fetch additional pages
     * of follows.
     */
    public val pagination: PaginationData?

    /**
     * Indicates whether there are more follows to load.
     *
     * This is true if the `next` cursor in the pagination data is not null, meaning there are
     * additional pages of follows available to fetch.
     */
    public val canLoadMore: Boolean
        get() = pagination?.next != null
}
