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

import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.CommentReactionListState
import io.getstream.feeds.android.client.api.state.query.CommentReactionsQuery
import io.getstream.feeds.android.client.api.state.query.CommentReactionsSort
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.state.query.CommentReactionsQueryConfig
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import io.getstream.feeds.android.client.internal.utils.upsert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An observable state object that manages the current state of a comment reaction list.
 *
 * This class provides reactive state management for a collection of comment reactions. It
 * automatically handles real-time updates when reactions are added or removed from the comment, and
 * maintains pagination state for loading additional reactions.
 */
internal class CommentReactionListStateImpl(override val query: CommentReactionsQuery) :
    CommentReactionListMutableState {

    private val _reactions: MutableStateFlow<List<FeedsReactionData>> =
        MutableStateFlow(emptyList())

    internal var queryConfig: CommentReactionsQueryConfig? = null
        private set

    private var _pagination: PaginationData? = null

    private val reactionsSorting: List<CommentReactionsSort>
        get() = query.sort ?: CommentReactionsSort.Default

    override val reactions: StateFlow<List<FeedsReactionData>>
        get() = _reactions.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onCommentRemoved() {
        _reactions.update { emptyList() }
    }

    override fun onQueryMoreReactions(
        result: PaginationResult<FeedsReactionData>,
        queryConfig: CommentReactionsQueryConfig,
    ) {
        _pagination = result.pagination
        // Update the query configuration for future queries
        this.queryConfig = queryConfig
        // Merge the new reactions with the existing ones (keeping the sort order)
        _reactions.update { current ->
            current.mergeSorted(result.models, FeedsReactionData::id, reactionsSorting)
        }
    }

    override fun onReactionRemoved(reaction: FeedsReactionData) {
        _reactions.update { current -> current.filter { it.id != reaction.id } }
    }

    override fun onReactionUpserted(reaction: FeedsReactionData) {
        _reactions.update { current -> current.upsert(reaction, FeedsReactionData::id) }
    }
}

internal interface CommentReactionListMutableState :
    CommentReactionListState, CommentReactionListStateUpdates

internal interface CommentReactionListStateUpdates {

    /** Handles the deletion of the parent comment. */
    fun onCommentRemoved()

    /**
     * Handles the successful loading of reactions.
     *
     * @param result The result containing the loaded reactions and pagination data.
     * @param queryConfig The query configuration used for the request.
     */
    fun onQueryMoreReactions(
        result: PaginationResult<FeedsReactionData>,
        queryConfig: CommentReactionsQueryConfig,
    )

    /**
     * Handles the removal of a reaction from the comment.
     *
     * @param reaction The reaction that was removed.
     */
    fun onReactionRemoved(reaction: FeedsReactionData)

    /**
     * Handles the addition or update of a reaction to the comment.
     *
     * @param reaction The reaction that was added or updated.
     */
    fun onReactionUpserted(reaction: FeedsReactionData)
}
