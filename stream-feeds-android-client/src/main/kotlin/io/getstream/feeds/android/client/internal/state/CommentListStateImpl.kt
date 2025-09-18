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
package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.state.CommentListState
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.client.api.state.query.toComparator
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import io.getstream.feeds.android.client.internal.utils.treeRemoveFirst
import io.getstream.feeds.android.client.internal.utils.treeUpdateFirst
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An observable state object that manages the current state of a comment list.
 *
 * This interface maintains the current list of comments, pagination information, and provides
 * real-time updates. It automatically handles WebSocket events to keep the comment list
 * synchronized.
 *
 * ## Features
 * - **Observable State**: Uses flow properties for reactive UI updates
 * - **Real-time Updates**: Automatically receives WebSocket events for comment changes
 * - **Pagination Support**: Tracks pagination state for loading more comments
 * - **Change Handlers**: Internal handlers for processing WebSocket events
 *
 * @property query The query used to fetch the comments.
 */
internal class CommentListStateImpl(override val query: CommentsQuery) : CommentListMutableState {

    private val _comments: MutableStateFlow<List<CommentData>> = MutableStateFlow(emptyList())

    private var _pagination: PaginationData? = null

    override val comments: StateFlow<List<CommentData>>
        get() = _comments.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    private val comparator = query.sort.toComparator()

    override fun onQueryMoreComments(result: PaginationResult<CommentData>) {
        _pagination = result.pagination
        // Merge the new comments with the existing ones (keeping the sort order)
        _comments.update { current ->
            current.mergeSorted(result.models, CommentData::id, comparator)
        }
    }

    override fun onCommentUpdated(comment: CommentData) {
        _comments.update { current ->
            current.treeUpdateFirst(
                matcher = { it.id == comment.id },
                childrenSelector = { it.replies.orEmpty() },
                updateElement = { comment },
                updateChildren = { parent, children -> parent.copy(replies = children) },
                comparator = comparator,
            )
        }
    }

    override fun onCommentRemoved(commentId: String) {
        _comments.update { current ->
            current.treeRemoveFirst(
                matcher = { it.id == commentId },
                childrenSelector = { it.replies.orEmpty() },
                updateChildren = { parent, children ->
                    parent.copy(replies = children, replyCount = parent.replyCount - 1)
                },
            )
        }
    }
}

internal interface CommentListMutableState : CommentListState, CommentListStateUpdates

/** Interface defining the methods for updating the comment list state. */
internal interface CommentListStateUpdates {

    /**
     * Handles the result of querying more comments.
     *
     * @param result The pagination result containing the new comments.
     * @param filter The filter used for the query, if any.
     * @param sort The sorting configuration used for the query, if any.
     */
    fun onQueryMoreComments(result: PaginationResult<CommentData>)

    /**
     * Handles the update of a comment in the list.
     *
     * @param comment The updated comment data.
     */
    fun onCommentUpdated(comment: CommentData)

    /**
     * Handles the removal of a comment from the list.
     *
     * @param commentId The ID of the removed comment.
     */
    fun onCommentRemoved(commentId: String)
}
