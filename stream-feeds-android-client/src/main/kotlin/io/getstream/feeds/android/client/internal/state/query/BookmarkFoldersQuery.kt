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

package io.getstream.feeds.android.client.internal.state.query

import io.getstream.android.core.api.filter.toRequest
import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersFilterField
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQuery
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersSort
import io.getstream.feeds.android.client.internal.model.QueryConfiguration
import io.getstream.feeds.android.client.internal.model.mapping.toRequest
import io.getstream.feeds.android.network.models.QueryBookmarkFoldersRequest

internal typealias BookmarkFoldersQueryConfig =
    QueryConfiguration<BookmarkFolderData, BookmarkFoldersFilterField, BookmarkFoldersSort>

/**
 * Converts the [io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQuery] to a
 * [io.getstream.feeds.android.network.models.QueryBookmarkFoldersRequest].
 */
internal fun BookmarkFoldersQuery.toRequest(): QueryBookmarkFoldersRequest {
    return QueryBookmarkFoldersRequest(
        filter = filter?.toRequest(),
        sort = sort?.map { it.toRequest() },
        limit = limit,
        next = next,
        prev = previous,
    )
}
