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

package io.getstream.feeds.android.client.internal.utils

import io.getstream.android.core.api.sort.CompositeComparator
import io.getstream.android.core.api.sort.Sort

/**
 * Updates elements in the list that match the given [filter] by applying the [update] function.
 * Elements that don't match the filter remain unchanged.
 *
 * @param filter A predicate function that determines whether an element should be updated.
 * @param update A function that takes an element and returns its updated version.
 * @return A new list containing the updated elements. Elements that did not match the filter are
 *   included unchanged.
 */
internal inline fun <T> List<T>.updateIf(filter: (T) -> Boolean, update: (T) -> T): List<T> = map {
    if (filter(it)) update(it) else it
}

/**
 * Updates an existing element in the list or inserts a new one if not found.
 *
 * This function performs an upsert operation by finding an element with the same ID (as determined
 * by the [idSelector]) and replacing it with the new [element]. If no matching element is found,
 * the new [element] is appended to the end of the list.
 *
 * The operation returns a new immutable list, leaving the original list unchanged.
 *
 * @param element The element to insert or use for updating an existing element.
 * @param idSelector A function that extracts a unique identifier from an element. This is used to
 *   determine if an element already exists in the list.
 * @param prepend Whether the new element should be added at the start instead of the end.
 * @return A new list containing the upserted element. If an existing element was found, it will be
 *   replaced; otherwise, the new element will be added to the start or end of the list depending on
 *   the [prepend] parameter.
 */
internal fun <T> List<T>.upsert(
    element: T,
    idSelector: (T) -> String,
    prepend: Boolean = false,
): List<T> {
    val existingIndex = this.indexOfFirst { idSelector(it) == idSelector(element) }
    return when {
        existingIndex >= 0 -> toMutableList().apply { this[existingIndex] = element }
        prepend -> toMutableList().apply { add(0, element) }
        else -> this + element
    }
}

/**
 * Inserts an element into the list, ensuring uniqueness based on a specified key.
 *
 * This function removes any existing elements in the list that have the same key as the new
 * [element] (as determined by the [keySelector]), and then appends [element]. The operation returns
 * a new list, leaving the original list unchanged.
 *
 * @param element The element to insert into the list.
 * @param keySelector A function that extracts a key from an element. Used to determine uniqueness.
 * @return A new list containing all original elements except those with the same key as [element],
 *   plus the new [element] at the end.
 */
internal fun <T, R> List<T>.insertUniqueBy(element: T, keySelector: (T) -> R): List<T> {
    val elementKey = keySelector(element)
    return toMutableList().apply {
        removeAll { keySelector(it) == elementKey }
        add(element)
    }
}

/**
 * Inserts an element into a sorted list, ensuring uniqueness based on a specified key.
 *
 * This function removes any existing elements in the list that have the same key as the new
 * [element] (as determined by the [keySelector]), and then inserts [element] in the correct
 * position to maintain sorted order according to the provided [comparator]. The operation returns a
 * new list, leaving the original list unchanged.
 *
 * **Note:** This function assumes that the list is already sorted according to the provided
 * comparator. If the list is not sorted, the behavior is undefined.
 *
 * @param element The element to insert into the list.
 * @param keySelector A function that extracts a key from an element. Used to determine uniqueness.
 * @param comparator The comparator used to determine the sort order and insertion point.
 * @return A new sorted list containing all original elements except those with the same key as
 *   [element], plus the new [element] in the correct sorted position.
 */
internal fun <T, R> List<T>.insertUniqueBySorted(
    element: T,
    keySelector: (T) -> R,
    comparator: Comparator<T>,
): List<T> {
    val elementKey = keySelector(element)
    return toMutableList().apply {
        removeAll { keySelector(it) == elementKey }
        insertSorted(element, comparator)
    }
}

/**
 * Inserts an element into a mutable list while maintaining sorted order.
 *
 * This function uses binary search to find the correct insertion point for the new element,
 * ensuring that the list remains sorted according to the provided [comparator]. The element is
 * inserted even if an equal element already exists in the list.
 *
 * **Note:** This function assumes that the list is already sorted according to the provided
 * comparator. If the list is not sorted, the behavior is undefined.
 *
 * @param T The type of elements in the list.
 * @param element The element to insert into the list.
 * @param comparator The comparator used to determine the sort order and insertion point.
 * @return A new immutable list containing all original elements plus the inserted element in the
 *   correct sorted position.
 */
internal fun <T> MutableList<T>.insertSorted(element: T, comparator: Comparator<in T>): List<T> {
    val insertionPoint = this.binarySearch(element, comparator)
    val index =
        if (insertionPoint >= 0) {
            insertionPoint + 1
        } else {
            -(insertionPoint + 1)
        }
    this.add(index, element)
    return this.toImmutableList()
}

private fun <T> MutableList<T>.toImmutableList(): List<T> {
    return this.toList()
}

/**
 * Inserts an element into a mutable list while maintaining sorted order using Sort configurations.
 *
 * This function uses binary search to find the correct insertion point for the new element,
 * ensuring that the list remains sorted according to the provided [sort] configurations. Multiple
 * sort criteria can be applied in sequence, with each subsequent sort acting as a tiebreaker for
 * the previous one. The element is inserted even if an equal element already exists in the list.
 *
 * **Note:** This function assumes that the list is already sorted according to the provided sort
 * configurations. If the list is not sorted, the behavior is undefined.
 *
 * @param T The type of elements in the list.
 * @param element The element to insert into the list.
 * @param sort A list of [Sort] configurations that define the sort order and insertion point. The
 *   sorts are applied in sequence, with earlier sorts taking precedence.
 * @return A new immutable list containing all original elements plus the inserted element in the
 *   correct sorted position.
 */
internal fun <T> MutableList<T>.insertSorted(element: T, sort: List<Sort<T>>): List<T> =
    insertSorted(element, CompositeComparator(sort))

/**
 * Updates an existing element in a sorted list or inserts a new one while maintaining sort order.
 *
 * This function performs an upsert operation by finding an element with the same ID (as determined
 * by the [idSelector]) and replacing it with the new [element]. If no matching element is found,
 * the new [element] is inserted in the correct sorted position. This ensures that the list remains
 * sorted according to the provided [comparator].
 *
 * The operation returns a new immutable list, leaving the original list unchanged.
 *
 * **Note:** This function assumes that the list is already sorted according to the provided
 * comparator. If the list is not sorted, the behavior is undefined.
 *
 * @param T The type of elements in the list.
 * @param element The element to insert or use for updating an existing element.
 * @param idSelector A function that extracts a unique identifier from an element. This is used to
 *   determine if an element already exists in the list.
 * @param comparator The comparator used to determine the sort order and insertion point.
 * @param update A function that takes the existing element and returns the updated element. Used
 *   only if an existing element is found.
 * @return A new sorted list containing the upserted element. If an existing element was found, it
 *   will be updated and repositioned; otherwise, the new element will be inserted in the correct
 *   sorted position as-is.
 */
internal fun <T, ID> List<T>.upsertSorted(
    element: T,
    idSelector: (T) -> ID,
    comparator: Comparator<in T>,
    update: (old: T) -> T = { element },
): List<T> {
    val elementId = idSelector(element)
    val existingIndex = this.indexOfFirst { idSelector(it) == elementId }

    return if (existingIndex >= 0) {
        // Element exists - check if sort order has changed
        val updatedElement = update(this[existingIndex])
        val sortComparison = comparator.compare(this[existingIndex], updatedElement)

        if (sortComparison == 0) {
            // Sort order hasn't changed - replace in place to preserve position
            this.toMutableList().apply { this[existingIndex] = updatedElement }
        } else {
            // Sort order has changed - remove and insert at correct position
            this.toMutableList()
                .apply { removeAt(existingIndex) }
                .insertSorted(updatedElement, comparator)
        }
    } else {
        // Element doesn't exist - insert at correct position
        this.toMutableList().insertSorted(element, comparator)
    }
}

/**
 * Updates an existing element in a sorted list or inserts a new one while maintaining sort order
 * using Sort configurations.
 *
 * This function performs an upsert operation by finding an element with the same ID (as determined
 * by the [idSelector]) and replacing it with the new [element]. If no matching element is found,
 * the new [element] is inserted in the correct sorted position. Multiple sort criteria can be
 * applied in sequence, with each subsequent sort acting as a tiebreaker for the previous one. This
 * ensures that the list remains sorted according to the provided [sort] configurations.
 *
 * The operation returns a new immutable list, leaving the original list unchanged.
 *
 * **Note:** This function assumes that the list is already sorted according to the provided sort
 * configurations. If the list is not sorted, the behavior is undefined.
 *
 * @param T The type of elements in the list.
 * @param element The element to insert or use for updating an existing element.
 * @param idSelector A function that extracts a unique identifier from an element. This is used to
 *   determine if an element already exists in the list.
 * @param sort A list of [Sort] configurations that define the sort order and insertion point. The
 *   sorts are applied in sequence, with earlier sorts taking precedence.
 * @param update A function that takes the existing element and returns the updated element. Used
 *   only if an existing element is found.
 * @return A new sorted list containing the upserted element. If an existing element was found, it
 *   will be updated and repositioned; otherwise, the new element will be inserted in the correct
 *   sorted position as-is.
 */
internal fun <T, ID> List<T>.upsertSorted(
    element: T,
    idSelector: (T) -> ID,
    sort: List<Sort<T>>,
    update: (old: T) -> T = { element },
): List<T> = upsertSorted(element, idSelector, CompositeComparator(sort), update)

/**
 * Upserts all elements from another list into this list based on a specified key.
 *
 * This function updates existing elements in the list that have the same key (as determined by
 * [idSelector]) as elements in [that] list. If an element from [that] does not exist in this list,
 * it is appended.
 *
 * @param that The list of elements to upsert into this list.
 * @param idSelector A function that extracts a key from an element. This is used to determine
 *   whether an element already exists in the list.
 * @return A new list containing the upserted elements. Existing elements are updated, and new
 *   elements are appended.
 */
internal fun <T, R> List<T>.upsertAll(that: List<T>, idSelector: (T) -> R): List<T> {
    // Using LinkedHashMap instead of e.g. HashMap to preserve order
    val toUpsert = that.associateByTo(LinkedHashMap(), idSelector)
    val result = ArrayList<T>(this.size + that.size)

    // Update what should be updated
    forEach { item ->
        val key = idSelector(item)
        result.add(toUpsert[key] ?: item)
        toUpsert.remove(key)
    }

    // Insert the rest
    result.addAll(toUpsert.values)

    return result
}

/**
 * Merges two sorted arrays while maintaining the sort order and handling duplicates.
 *
 * This function combines two pre-sorted lists into a single sorted list while resolving duplicate
 * elements. When duplicate elements are detected (elements with the same ID as determined by
 * [idSelector]), the element from the [other] list takes precedence.
 *
 * **Note:** Both lists must be pre-sorted according to the provided [comparator]. If either list is
 * not sorted, the behavior is undefined.
 *
 * @param T The type of elements in the lists.
 * @param other The second sorted list to merge with this list.
 * @param idSelector A function that extracts a unique identifier from an element. This is used to
 *   detect duplicate elements across the two lists.
 * @param comparator The comparator that both lists are sorted by and used for merging.
 * @return A new sorted list containing all elements from both lists, with duplicates resolved by
 *   taking elements from the [other] list.
 */
internal fun <T> List<T>.mergeSorted(
    other: List<T>,
    idSelector: (T) -> String,
    comparator: Comparator<in T>,
): List<T> {
    // Create a set of IDs from the other list for quick duplicate detection
    val otherIds = other.mapTo(mutableSetOf(), idSelector)

    // Filter this list to exclude elements that exist in other (by ID)
    val filteredThis = this.filterNot { idSelector(it) in otherIds }

    // Now merge the filtered list with other using standard merge algorithm
    val result = mutableListOf<T>()
    var i = 0
    var j = 0

    while (i < filteredThis.size && j < other.size) {
        val thisElement = filteredThis[i]
        val otherElement = other[j]
        val comparison = comparator.compare(thisElement, otherElement)

        when {
            comparison <= 0 -> {
                // thisElement comes before or is equal to otherElement
                result.add(thisElement)
                i++
            }

            else -> {
                // otherElement comes before thisElement
                result.add(otherElement)
                j++
            }
        }
    }

    // Add remaining elements from filteredThis
    while (i < filteredThis.size) {
        result.add(filteredThis[i])
        i++
    }

    // Add remaining elements from other
    while (j < other.size) {
        result.add(other[j])
        j++
    }

    return result.toImmutableList()
}

/**
 * Merges two sorted arrays while maintaining the sort order and handling duplicates using Sort
 * configurations.
 *
 * This function combines two pre-sorted lists into a single sorted list while resolving duplicate
 * elements. When duplicate elements are detected (elements with the same ID as determined by
 * [idSelector]), the element from the [other] list takes precedence. Multiple sort criteria can be
 * applied in sequence, with each subsequent sort acting as a tiebreaker for the previous one.
 *
 * **Note:** Both lists must be pre-sorted according to the provided [sort] configurations. If
 * either list is not sorted according to these configurations, the behavior is undefined.
 *
 * @param T The type of elements in the lists.
 * @param other The second sorted list to merge with this list.
 * @param idSelector A function that extracts a unique identifier from an element. This is used to
 *   detect duplicate elements across the two lists.
 * @param sort A list of [Sort] configurations that both lists are sorted by and used for merging.
 *   The sorts are applied in sequence, with earlier sorts taking precedence.
 * @return A new sorted list containing all elements from both lists, with duplicates resolved by
 *   taking elements from the [other] list.
 */
internal fun <T> List<T>.mergeSorted(
    other: List<T>,
    idSelector: (T) -> String,
    sort: List<Sort<T>>,
): List<T> = mergeSorted(other, idSelector, CompositeComparator(sort))
