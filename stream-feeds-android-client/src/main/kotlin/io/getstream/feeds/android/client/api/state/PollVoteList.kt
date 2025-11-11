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

import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery

/**
 * A list of poll votes that can be queried and paginated.
 *
 * This class provides a way to fetch and manage a collection of poll votes with support for
 * filtering, sorting, and pagination. It maintains an observable state that can be used in UI
 * components.
 *
 * ## Example Usage:
 * ```kotlin
 * // Create a poll vote list with a specific query
 * val query = PollVotesQuery(pollId = "poll-123")
 * val pollVoteList = feedsClient.pollVoteList(query)
 *
 * // Fetch initial poll votes matching the query
 * val pollVotes = pollVoteList.get()
 *
 * // Load more poll votes if available
 * if (pollVoteList.state.canLoadMore) {
 *    val morePollVotes = pollVoteList.queryMorePollVotes()
 * }
 *
 * // Observe state changes
 * pollVoteList.state.votes.collect { votes ->
 *    println("Updated poll votes: ${votes.size}")
 * }
 * ```
 */
public interface PollVoteList {

    /** The query used to fetch the poll votes. */
    public val query: PollVotesQuery

    /** An observable object representing the current state of the poll vote list. */
    public val state: PollVoteListState

    /**
     * Fetches the initial list of poll votes based on the current query.
     *
     * This method retrieves the first page of poll votes matching the query criteria. The results
     * are automatically stored in the state and can be accessed via the [state.votes] property.
     *
     * @return A [Result] containing a list of [PollVoteData] if successful, or an error if the
     *   request fails.
     */
    public suspend fun get(): Result<List<PollVoteData>>

    /**
     * Loads more poll votes using the next page token from the previous query.
     *
     * This method fetches additional poll votes if there are more available based on the current
     * query. The new poll votes are automatically merged with the existing ones in the state.
     *
     * @param limit The maximum number of poll votes to fetch. If null, the default limit from the
     *   API will be used.
     * @return A [Result] containing a list of [PollVoteData] if successful, or an error if the
     *   request fails. Returns an empty list if there are no more poll votes to fetch.
     */
    public suspend fun queryMorePollVotes(limit: Int? = null): Result<List<PollVoteData>>
}
