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

package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.api.sort.CompositeComparator
import io.getstream.android.core.api.sort.SortDirection
import io.getstream.feeds.android.client.api.model.CollectionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.CollectionListState
import io.getstream.feeds.android.client.api.state.query.CollectionsQuery
import io.getstream.feeds.android.client.api.state.query.CollectionsSort
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.state.query.matches
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An observable state object that manages the current state of a collection list.
 *
 * @property query The query used to fetch the collections.
 */
internal class CollectionListStateImpl(override val query: CollectionsQuery) :
    CollectionListMutableState {

    private val _collections: MutableStateFlow<List<CollectionData>> = MutableStateFlow(emptyList())

    private var _pagination: PaginationData? = null

    private val collectionsSorting: List<CollectionsSort>
        get() = query.sort?.takeIf { it.isNotEmpty() } ?: CollectionsSort.Default

    // The backend breaks ties by id: ascending when every sort field is ascending, descending
    // otherwise
    private val collectionsComparator: Comparator<CollectionData> = run {
        val allForward = collectionsSorting.all { it.direction == SortDirection.FORWARD }
        val byId =
            if (allForward) compareBy(CollectionData::id)
            else compareByDescending(CollectionData::id)
        CompositeComparator(collectionsSorting).then(byId)
    }

    override val collections: StateFlow<List<CollectionData>>
        get() = _collections.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onQueryCollections(result: PaginationResult<CollectionData>, replace: Boolean) {
        _pagination = result.pagination
        if (replace) {
            _collections.update { result.models }
        } else {
            _collections.update { current -> current.merge(result.models) }
        }
    }

    override fun onCollectionsAdded(collections: List<CollectionData>) {
        // Unlike a page, these come in request order
        val sorted = collections.sortedWith(collectionsComparator)
        _collections.update { current -> current.merge(sorted) }
    }

    override fun onCollectionsUpdated(collections: List<CollectionData>) {
        val updates = collections.associateBy(CollectionData::reference)
        _collections.update { current ->
            val untouched = current.filter { it.reference !in updates }
            val updated =
                current.mapNotNull { existing ->
                    // Update responses are built from the request, so their creation date is not
                    // the stored one
                    updates[existing.reference]
                        ?.copy(createdAt = existing.createdAt)
                        // We drop elements that used to match the filter but no longer do
                        ?.takeIf { it matches query.filter }
                }
            untouched.merge(updated.sortedWith(collectionsComparator))
        }
    }

    override fun onCollectionsRemoved(refs: Collection<String>) {
        val removed = refs.toSet()
        _collections.update { current -> current.filter { it.reference !in removed } }
    }

    /**
     * Merges [incoming], which must already be sorted, replacing the ones with the same reference.
     */
    private fun List<CollectionData>.merge(incoming: List<CollectionData>): List<CollectionData> =
        mergeSorted(incoming, CollectionData::reference, collectionsComparator)
}

/** A mutable state interface for managing the collection list state. */
internal interface CollectionListMutableState : CollectionListState, CollectionListStateUpdates

/** An interface that defines the methods for updating the state of a collection list. */
internal interface CollectionListStateUpdates {

    /**
     * Called when collections are queried.
     *
     * @param result The result containing the collections and pagination data.
     * @param replace If true, replaces the current collections; otherwise, merges with them.
     */
    fun onQueryCollections(result: PaginationResult<CollectionData>, replace: Boolean = false)

    /**
     * Called when collections are created.
     *
     * @param collections The created collections that match the query.
     */
    fun onCollectionsAdded(collections: List<CollectionData>)

    /**
     * Called when collections are updated. The ones that no longer match the query are removed.
     *
     * @param collections The updated collections.
     */
    fun onCollectionsUpdated(collections: List<CollectionData>)

    /**
     * Called when collections are deleted.
     *
     * @param refs The references of the deleted collections in the `name:id` format.
     */
    fun onCollectionsRemoved(refs: Collection<String>)
}
