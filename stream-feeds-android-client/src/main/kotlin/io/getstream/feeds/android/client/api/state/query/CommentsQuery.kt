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

import io.getstream.android.core.api.filter.Filter
import io.getstream.android.core.api.filter.FilterField
import io.getstream.android.core.api.filter.toRequest
import io.getstream.feeds.android.network.models.QueryCommentsRequest
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
 *   filter = CommentsFilterField.ObjectId.equal("activity-123"),
 *   sort = CommentsSort.Best,
 *   limit = 20,
 * )
 *
 * // Complex query with multiple filters
 * val complexQuery = CommentsQuery(
 *   filter = Filters.and(
 *     CommentsFilterField.ObjectId.equal("activity-123"),
 *     CommentsFilterField.Score.greater(5.0),
 *     CommentsFilterField.Status.equal("active")
 *   ),
 *   sort = CommentsSort.Top,
 *   limit = 50,
 * )
 * ```
 *
 * @property filter Filter criteria for the comments query. This filter can be a simple single
 *   filter or a complex combination of multiple filters using logical operators (`.and`, `.or`).
 *   The filter determines which comments are included in the query results based on field values
 *   and comparison operators. See [CommentsFilterField] for available filter fields and their
 *   supported operators.
 * @property limit Maximum number of comments to return in a single request. If not specified, the
 *   API will use its default limit.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 *   provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 *   provided in the response of a previous request.
 * @property sort Optional sorting criteria for the comments. See [CommentsSort].
 */
public data class CommentsQuery(
    public val filter: CommentsFilter? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
    public val sort: CommentsSort? = null,
)

public typealias CommentsFilter = Filter<CommentsFilterField>

public data class CommentsFilterField(override val remote: String) : FilterField {
    public companion object {
        /**
         * Filter by comment ID.
         *
         * Supported operators: `equal`, `in`
         */
        public val id: CommentsFilterField = CommentsFilterField("id")

        /**
         * Filter by user ID who created the comment.
         *
         * Supported operators: `equal`, `in`
         */
        public val userId: CommentsFilterField = CommentsFilterField("user_id")

        /**
         * Filter by object type.
         *
         * Supported operators: `equal`, `in`
         */
        public val objectType: CommentsFilterField = CommentsFilterField("object_type")

        /**
         * Filter by object ID.
         *
         * Supported operators: `equal`, `in`
         */
        public val objectId: CommentsFilterField = CommentsFilterField("object_id")

        /**
         * Filter by parent comment ID.
         *
         * Supported operators: `equal`, `in`
         */
        public val parentId: CommentsFilterField = CommentsFilterField("parent_id")

        /**
         * Filter by comment text content.
         *
         * Supported operators: `q`
         */
        public val commentText: CommentsFilterField = CommentsFilterField("comment_text")

        /**
         * Filter by comment status.
         *
         * Supported operators: `equal`, `in`
         */
        public val status: CommentsFilterField = CommentsFilterField("status")

        /**
         * Filter by upvote count.
         *
         * Supported operators: `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val upvoteCount: CommentsFilterField = CommentsFilterField("upvote_count")

        /**
         * Filter by downvote count.
         *
         * Supported operators: `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val downvoteCount: CommentsFilterField = CommentsFilterField("downvote_count")

        /**
         * Filter by reply count.
         *
         * Supported operators: `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val replyCount: CommentsFilterField = CommentsFilterField("reply_count")

        /**
         * Filter by comment score.
         *
         * Supported operators: `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val score: CommentsFilterField = CommentsFilterField("score")

        /**
         * Filter by confidence score.
         *
         * Supported operators: `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val confidenceScore: CommentsFilterField = CommentsFilterField("confidence_score")

        /**
         * Filter by controversy score.
         *
         * Supported operators: `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val controversyScore: CommentsFilterField = CommentsFilterField("controversy_score")

        /**
         * Filter by creation timestamp.
         *
         * Supported operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val createdAt: CommentsFilterField = CommentsFilterField("created_at")
    }
}

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
