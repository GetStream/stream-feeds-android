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

import io.getstream.android.core.query.Filter
import io.getstream.android.core.query.toRequest
import io.getstream.feeds.android.core.generated.models.QueryCommentsRequest
import java.util.Date

/**
 * A query for retrieving comments with filtering, sorting, and pagination options.
 *
 * Use this struct to configure how comments should be fetched from the Stream Feeds API. You can
 * specify filters to narrow down results, sorting options, and pagination parameters.
 *
 * ## Example Usage
 *
 * ```kotlin
 * // Simple query with basic filter
 * val query = CommentsQuery(
 *   filter = Filters.equal("object_id", "activity-123),
 *   sort = CommentsSort.Best,
 *   limit = 20,
 * )
 *
 * // Complex query with multiple filters
 * val complexQuery = CommentsQuery(
 *   filter = Filters.and(
 *     Filters.equal("object_id", "activity-123"),
 *     Filters.greater("score", 5.0),
 *     Filters.equal("status", "active")
 *   ),
 *   sort = CommentsSort.Top,
 *   limit = 50,
 * )
 * ```
 *
 * @property Filter criteria for the comments query. This filter can be a simple single filter or a
 *   complex combination of multiple filters using logical operators (`.and`, `.or`). The filter
 *   determines which comments are included in the query results based on field values and
 *   comparison operators. Supported filters:
 * - field: `id`, operators: `equal`, `in`
 * - field: `user_id`, operators: `equal`, `in`
 * - field: `object_type`, operators: `equal`, `in`
 * - field: `object_id`, operators: `equal`, `in`
 * - field: `parent_id`, operators: `equal`, `in`
 * - field: `comment_text`, operators: `q`
 * - field: `status`, operators: `equal`, `in`
 * - field: `upvote_count`, operators: `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `downvote_count`, operators: `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `reply_count`, operators: `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `score`, operators: `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `confidence_score`, operators: `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `controversy_score`, operators: `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `created_at`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 *
 * @property limit Maximum number of comments to return in a single request. If not specified, the
 *   API will use its default limit.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 *   provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 *   provided in the response of a previous request.
 * @property sort Optional sorting criteria for the comments. See [CommentsSort].
 */
public data class CommentsQuery(
    public val filter: Filter?,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
    public val sort: CommentsSort? = null,
)

/** Represents the sorting options available for comments. */
public sealed interface CommentsSort {
    /** By quality score (best quality first). */
    public data object Best : CommentsSort

    /** By controversy level (most controversial first) */
    public data object Controversial : CommentsSort

    /** Chronological order (oldest first). */
    public data object First : CommentsSort

    /** Reverse chronological order (newest first). */
    public data object Last : CommentsSort

    /** By popularity (most upvotes first). */
    public data object Top : CommentsSort
}

/** Converts a [CommentsQuery] to the corresponding [QueryCommentsRequest]. */
internal fun CommentsQuery.toRequest(): QueryCommentsRequest =
    QueryCommentsRequest(
        filter = filter?.toRequest().orEmpty(),
        limit = limit,
        next = next,
        prev = previous,
        sort = sort?.toRequest(),
    )

/**
 * Converts a [CommentsSort] to the corresponding [QueryCommentsRequest.Sort].
 *
 * @return The [QueryCommentsRequest.Sort] representation of this [CommentsSort].
 */
internal fun CommentsSort.toRequest(): QueryCommentsRequest.Sort =
    when (this) {
        CommentsSort.Best -> QueryCommentsRequest.Sort.Best
        CommentsSort.Controversial -> QueryCommentsRequest.Sort.Controversial
        CommentsSort.First -> QueryCommentsRequest.Sort.First
        CommentsSort.Last -> QueryCommentsRequest.Sort.Last
        CommentsSort.Top -> QueryCommentsRequest.Sort.Top
    }

internal interface CommentsSortDataFields {
    val createdAt: Date
    val confidenceScore: Float
    val controversyScore: Float?
    val score: Int
}

internal fun CommentsSort?.toComparator(): Comparator<CommentsSortDataFields> =
    when (this) {
        CommentsSort.Top ->
            compareByDescending(CommentsSortDataFields::score)
                .thenByDescending(CommentsSortDataFields::createdAt)

        CommentsSort.Best ->
            compareByDescending(CommentsSortDataFields::confidenceScore)
                .thenByDescending(CommentsSortDataFields::createdAt)

        CommentsSort.Controversial -> compareByDescending { it.controversyScore ?: -1f }
        CommentsSort.First -> compareBy(CommentsSortDataFields::createdAt)
        CommentsSort.Last,
        null -> compareByDescending(CommentsSortDataFields::createdAt)
    }
