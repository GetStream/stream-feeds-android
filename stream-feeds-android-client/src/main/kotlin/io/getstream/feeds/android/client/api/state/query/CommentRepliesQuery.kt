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

/**
 * A query configuration for fetching replies to a specific comment.
 *
 * This class defines the parameters used to fetch replies to a comment, including pagination
 * settings, sorting options, and depth configuration for nested reply structures.
 *
 * ## Features
 * - **Pagination**: Supports `next` and `previous` cursors for efficient pagination
 * - **Sorting**: Configurable sorting options for reply ordering
 * - **Depth Control**: Limits the depth of nested reply structures
 * - **Reply Limits**: Controls the number of nested replies to fetch per reply
 *
 * @property commentId The unique identifier of the comment to fetch replies for.
 * @property depth The maximum depth of nested replies to fetch. This parameter controls how many
 *   levels of nested replies to include. For example, a depth of 2 will include replies and their
 *   direct replies, but not replies to replies to replies.
 * - `null`: No depth limit (fetch all levels)
 * - `1`: Only top-level comments
 * - `2`: Comments and their direct replies
 * - `3`: Comments, replies, and replies to replies
 *
 * @property limit The maximum number of replies to fetch per request. This parameter controls the
 *   page size for pagination. Larger values reduce the number of API calls needed but may increase
 *   response time.
 * - `null`: Use server default
 *
 * @property next The pagination cursor for fetching the next page of replies. This cursor is
 *   provided by the server in the pagination response and should be used to fetch the next page of
 *   results.
 * @property previous The pagination cursor for fetching the previous page of replies. This cursor
 *   is provided by the server in the pagination response and should be used to fetch the previous
 *   page of results.
 * @property repliesLimit The maximum number of nested replies to fetch per reply. This parameter
 *   controls how many nested replies are included for each reply in the response. It's useful for
 *   limiting the size of deeply threaded reply structures.
 * @property sort The sorting criteria for replies.
 */
public data class CommentRepliesQuery(
    public val commentId: String,
    public val depth: Int? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
    public val repliesLimit: Int? = null,
    public val sort: CommentsSort? = null,
)
