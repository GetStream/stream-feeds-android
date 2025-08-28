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
package io.getstream.feeds.android.client.api.state.query

import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.query.Filter
import io.getstream.feeds.android.client.api.query.Sort
import io.getstream.feeds.android.client.api.query.SortDirection
import io.getstream.feeds.android.client.api.query.SortField
import io.getstream.feeds.android.client.api.query.toRequest
import io.getstream.feeds.android.client.internal.model.mapping.toRequest
import io.getstream.feeds.android.network.models.QueryPollVotesRequest

/**
 * A query for retrieving poll votes with filtering, sorting, and pagination options.
 *
 * Use this struct to configure how poll votes should be fetched from the Stream Feeds API. You can
 * specify filters to narrow down results, sorting options, and pagination parameters.
 *
 * @property pollId The unique identifier of the poll to fetch votes for.
 * @param userId Optional user ID used for authentication.
 * @param filter Optional filter to apply to the poll votes query. Use this to narrow down results
 *   based on specific criteria. Supported filters:
 * - field: `created_at`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `id`, operators: `equal`, `in`
 * - field: `is_answer`, operators: `equal`
 * - field: `option_id`, operators: `equal`, `in`, `exists`
 * - field: `user_id`, operators: `equal`, `in`
 * - field: `poll_id`, operators: `equal`, `in`
 * - field: `updated_at`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 *
 * @property limit Maximum number of poll votes to return in a single request. If not specified, the
 *   API will use its default limit.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 *   provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 *   provided in the response of a previous request.
 * @property sort Array of sorting criteria to apply to the poll votes. If not specified, the API
 *   will use its default sorting.
 */
public data class PollVotesQuery(
    public val pollId: String,
    public val userId: String? = null,
    public val filter: Filter? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
    public val sort: List<PollVotesSort>? = null,
)

/**
 * Represents a sort specification for poll votes.
 *
 * This class allows you to specify how poll votes should be sorted when querying them from the
 * Stream Feeds API.
 *
 * @param field The field by which to sort the poll votes.
 * @param direction The direction of the sort operation (ascending or descending).
 */
public class PollVotesSort(field: PollVotesSortField, direction: SortDirection) :
    Sort<PollVoteData>(field, direction) {

    public companion object {

        /**
         * Default sorting configuration for poll votes.
         *
         * This uses the `CreatedAt` field in reverse order, meaning the most recently created poll
         * votes will appear first.
         */
        public val Default: List<PollVotesSort> =
            listOf(PollVotesSort(PollVotesSortField.CreatedAt, SortDirection.REVERSE))
    }
}

/**
 * Represents a field that can be used for sorting poll votes.
 *
 * This type provides a type-safe way to specify which field should be used when sorting poll votes
 * results.
 */
public sealed interface PollVotesSortField : SortField<PollVoteData> {

    /**
     * Sort by the answer text of the poll option. This field allows sorting poll votes by the text
     * content of the selected option.
     */
    public data object AnswerText :
        PollVotesSortField,
        SortField<PollVoteData> by SortField.create("answer_text", { it.answerText.orEmpty() })

    /**
     * Sort by the unique identifier of the poll vote. This field allows sorting poll votes by their
     * unique ID.
     */
    public data object Id :
        PollVotesSortField, SortField<PollVoteData> by SortField.create("id", PollVoteData::id)

    /**
     * Sort by the creation timestamp of the poll vote. This field allows sorting poll votes by when
     * they were created (newest/oldest first).
     */
    public data object CreatedAt :
        PollVotesSortField,
        SortField<PollVoteData> by SortField.create("created_at", PollVoteData::createdAt)

    /**
     * Sort by the last update timestamp of the poll vote. This field allows sorting poll votes by
     * when they were last updated (newest/oldest first).
     */
    public data object UpdatedAt :
        PollVotesSortField,
        SortField<PollVoteData> by SortField.create("updated_at", PollVoteData::updatedAt)
}

/** Converts this [PollVotesQuery] to a [QueryPollVotesRequest]. */
internal fun PollVotesQuery.toRequest(): QueryPollVotesRequest =
    QueryPollVotesRequest(
        filter = filter?.toRequest(),
        limit = limit,
        next = next,
        prev = previous,
        sort = sort?.map { it.toRequest() },
    )
