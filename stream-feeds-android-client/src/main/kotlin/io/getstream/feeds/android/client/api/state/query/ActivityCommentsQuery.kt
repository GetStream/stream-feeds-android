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

package io.getstream.feeds.android.client.api.state.query

/**
 * A query configuration for fetching comments for a specific activity.
 *
 * This class defines the parameters used to fetch comments for an activity, including pagination
 * settings, sorting options, and depth configuration for threaded comments.
 *
 * ## Features
 * - **Pagination**: Supports `next` and `previous` cursors for efficient pagination
 * - **Sorting**: Configurable sorting options for comment ordering
 * - **Depth Control**: Limits the depth of threaded comment replies
 * - **Reply Limits**: Controls the number of replies to fetch per comment
 *
 * @property objectId The unique identifier of the activity to fetch comments for.
 * @property objectType The type of object (typically "activity" for activity comments).
 * @property depth The maximum depth of threaded comments to fetch. This parameter controls how many
 *   levels of nested replies to include. For example, a depth of 2 will include comments and their
 *   direct replies, but not replies to replies.
 * - `null`: No depth limit (fetch all levels)
 * - `1`: Only top-level comments
 * - `2`: Comments and their direct replies
 * - `3`: Comments, replies, and replies to replies
 *
 * @property limit The maximum number of comments to fetch per request. This parameter controls the
 *   page size for pagination. Larger values reduce the number of API calls needed but may increase
 *   response time.
 * - `null`: Use server default (typically 25)
 * - `10-50`: Recommended range for most use cases
 * - `>50`: May impact performance
 *
 * @property next The pagination cursor for fetching the next page of comments. This cursor is
 *   provided by the server in the pagination response and should be used to fetch the next page of
 *   results.
 * @property previous The pagination cursor for fetching the previous page of comments. This cursor
 *   is provided by the server in the pagination response and should be used to fetch the previous
 *   page of results.
 * @property repliesLimit The maximum number of replies to fetch per comment. This parameter
 *   controls how many replies are included for each comment in the response. It's useful for
 *   limiting the size of threaded comments.
 * - `null`: Fetch all replies (subject to depth limit)
 * - `5-10`: Recommended for most use cases
 * - `>20`: May impact performance
 *
 * @property sort The sorting criteria for comments.
 */
public data class ActivityCommentsQuery(
    public val objectId: String,
    public val objectType: String,
    public val depth: Int? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
    public val repliesLimit: Int? = null,
    public val sort: CommentsSort? = null,
)
