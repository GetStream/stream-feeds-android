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

package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import kotlinx.coroutines.flow.StateFlow

public interface PollVoteListState {

    /** The query used to fetch the poll votes. */
    public val query: PollVotesQuery

    /** All the paginated poll votes. */
    public val votes: StateFlow<List<PollVoteData>>

    /** Last pagination information. */
    public val pagination: PaginationData?

    /** Indicates whether there are more poll votes available to load. */
    public val canLoadMore: Boolean
        get() = pagination?.next != null
}
