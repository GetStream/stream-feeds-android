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

package io.getstream.feeds.android.client.internal.state.query

import io.getstream.android.core.api.filter.toRequest
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.client.api.state.query.CommentsSort
import io.getstream.feeds.android.client.api.state.query.CommentsSortDataFields
import io.getstream.feeds.android.network.models.QueryCommentsRequest

/**
 * Converts a [io.getstream.feeds.android.client.api.state.query.CommentsQuery] to the corresponding
 * [io.getstream.feeds.android.network.models.QueryCommentsRequest].
 */
internal fun CommentsQuery.toRequest(): QueryCommentsRequest =
    QueryCommentsRequest(
        filter = filter?.toRequest().orEmpty(),
        limit = limit,
        next = next,
        prev = previous,
        sort = sort?.toRequest(),
    )

/**
 * Converts a [io.getstream.feeds.android.client.api.state.query.CommentsSort] to the corresponding
 * [io.getstream.feeds.android.network.models.QueryCommentsRequest.Sort].
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
