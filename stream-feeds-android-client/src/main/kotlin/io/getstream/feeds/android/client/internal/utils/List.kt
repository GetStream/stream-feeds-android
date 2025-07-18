package io.getstream.feeds.android.client.internal.utils

import io.getstream.android.core.query.CompositeComparator
import io.getstream.android.core.query.Sort
import okhttp3.internal.toImmutableList

/**
 * Updates an existing element in the list or inserts a new one if not found.
 *
 * This function performs an upsert operation by finding an element with the same ID
 * (as determined by the [idSelector]) and replacing it with the new [element].
 * If no matching element is found, the new [element] is appended to the end of the list.
 *
 * The operation returns a new immutable list, leaving the original list unchanged.
 *
 * @param T The type of elements in the list.
 * @param element The element to insert or use for updating an existing element.
 * @param idSelector A function that extracts a unique identifier from an element.
 *                   This is used to determine if an element already exists in the list.
 * @return A new list containing the upserted element. If an existing element was found,
 *         it will be replaced; otherwise, the new element will be added to the end.
 */
internal fun <T> List<T>.upsert(element: T, idSelector: (T) -> String): List<T> {
    val existingIndex = this.indexOfFirst { idSelector(it) == idSelector(element) }
    return if (existingIndex >= 0) {
        this.toMutableList().apply { this[existingIndex] = element }
    } else {
        this + element
    }
}

/**
 * Inserts an element into a mutable list while maintaining sorted order.
 *
 * This function uses binary search to find the correct insertion point for the new element,
 * ensuring that the list remains sorted according to the provided [comparator].
 * The element is inserted even if an equal element already exists in the list.
 *
 * **Note:** This function assumes that the list is already sorted according to the
 * provided comparator. If the list is not sorted, the behavior is undefined.
 *
 * @param T The type of elements in the list.
 * @param element The element to insert into the list.
 * @param comparator The comparator used to determine the sort order and insertion point.
 * @return A new immutable list containing all original elements plus the inserted element
 *         in the correct sorted position.
 *
 * @see List.binarySearch
 * @see MutableList.add
 */
internal fun <T> MutableList<T>.insertSorted(element: T, comparator: Comparator<T>): List<T> {
    val insertionPoint = this.binarySearch(element, comparator)
    val index = if (insertionPoint >= 0) {
        insertionPoint + 1
    } else {
        -(insertionPoint + 1)
    }
    this.add(index, element)
    return this.toImmutableList()
}

/**
 * Inserts an element into a mutable list while maintaining sorted order using Sort configurations.
 *
 * This function uses binary search to find the correct insertion point for the new element,
 * ensuring that the list remains sorted according to the provided [sort] configurations.
 * Multiple sort criteria can be applied in sequence, with each subsequent sort acting as a
 * tiebreaker for the previous one. The element is inserted even if an equal element already
 * exists in the list.
 *
 * **Note:** This function assumes that the list is already sorted according to the
 * provided sort configurations. If the list is not sorted, the behavior is undefined.
 *
 * @param T The type of elements in the list.
 * @param element The element to insert into the list.
 * @param sort A list of [Sort] configurations that define the sort order and insertion point.
 *             The sorts are applied in sequence, with earlier sorts taking precedence.
 * @return A new immutable list containing all original elements plus the inserted element
 *         in the correct sorted position.
 *
 * @see Sort
 * @see CompositeComparator
 * @see List.binarySearch
 * @see MutableList.add
 */
internal fun <T> MutableList<T>.insertSorted(element: T, sort: List<Sort<T>>): List<T> =
    insertSorted(element, CompositeComparator(sort))

/**
 * Updates an existing element in a sorted list or inserts a new one while maintaining sort order.
 *
 * This function performs an upsert operation by finding an element with the same ID
 * (as determined by the [idSelector]) and replacing it with the new [element].
 * If no matching element is found, the new [element] is inserted in the correct sorted position.
 * This ensures that the list remains sorted according to the provided [comparator].
 *
 * The operation returns a new immutable list, leaving the original list unchanged.
 *
 * **Note:** This function assumes that the list is already sorted according to the
 * provided comparator. If the list is not sorted, the behavior is undefined.
 *
 * @param T The type of elements in the list.
 * @param element The element to insert or use for updating an existing element.
 * @param idSelector A function that extracts a unique identifier from an element.
 *                   This is used to determine if an element already exists in the list.
 * @param comparator The comparator used to determine the sort order and insertion point.
 * @return A new sorted list containing the upserted element. If an existing element was found,
 *         it will be replaced and repositioned; otherwise, the new element will be inserted
 *         in the correct sorted position.
 *
 * @see List.binarySearch
 * @see MutableList.add
 * @see upsert
 * @see insertSorted
 */
internal fun <T> List<T>.upsertSorted(
    element: T,
    idSelector: (T) -> String,
    comparator: Comparator<T>
): List<T> {
    val elementId = idSelector(element)
    val listWithoutExisting = this.filterNot { idSelector(it) == elementId }
    return listWithoutExisting.toMutableList().insertSorted(element, comparator)
}

/**
 * Updates an existing element in a sorted list or inserts a new one while maintaining sort order using Sort configurations.
 *
 * This function performs an upsert operation by finding an element with the same ID
 * (as determined by the [idSelector]) and replacing it with the new [element].
 * If no matching element is found, the new [element] is inserted in the correct sorted position.
 * Multiple sort criteria can be applied in sequence, with each subsequent sort acting as a
 * tiebreaker for the previous one. This ensures that the list remains sorted according to the
 * provided [sort] configurations.
 *
 * The operation returns a new immutable list, leaving the original list unchanged.
 *
 * **Note:** This function assumes that the list is already sorted according to the
 * provided sort configurations. If the list is not sorted, the behavior is undefined.
 *
 * @param T The type of elements in the list.
 * @param element The element to insert or use for updating an existing element.
 * @param idSelector A function that extracts a unique identifier from an element.
 *                   This is used to determine if an element already exists in the list.
 * @param sort A list of [Sort] configurations that define the sort order and insertion point.
 *             The sorts are applied in sequence, with earlier sorts taking precedence.
 * @return A new sorted list containing the upserted element. If an existing element was found,
 *         it will be replaced and repositioned; otherwise, the new element will be inserted
 *         in the correct sorted position.
 *
 * @see Sort
 * @see CompositeComparator
 * @see List.binarySearch
 * @see MutableList.add
 * @see upsert
 * @see insertSorted
 */
internal fun <T> List<T>.upsertSorted(
    element: T,
    idSelector: (T) -> String,
    sort: List<Sort<T>>
): List<T> = upsertSorted(element, idSelector, CompositeComparator(sort))


/**
 * Merges two sorted arrays while maintaining the sort order and handling duplicates.
 *
 * This function combines two pre-sorted lists into a single sorted list while resolving
 * duplicate elements. When duplicate elements are detected (elements with the same ID as
 * determined by [idSelector]), the element from the [other] list takes precedence.
 *
 * **Note:** Both lists must be pre-sorted according to the provided [comparator].
 * If either list is not sorted, the behavior is undefined.
 *
 * @param T The type of elements in the lists.
 * @param other The second sorted list to merge with this list.
 * @param idSelector A function that extracts a unique identifier from an element.
 *                   This is used to detect duplicate elements across the two lists.
 * @param comparator The comparator that both lists are sorted by and used for merging.
 * @return A new sorted list containing all elements from both lists, with duplicates
 *         resolved by taking elements from the [other] list.
 *
 * @see List.binarySearch
 * @see Comparator
 */
internal fun <T> List<T>.mergeSorted(
    other: List<T>,
    idSelector: (T) -> String,
    comparator: Comparator<T>,
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
 * This function combines two pre-sorted lists into a single sorted list while resolving
 * duplicate elements. When duplicate elements are detected (elements with the same ID as
 * determined by [idSelector]), the element from the [other] list takes precedence.
 * Multiple sort criteria can be applied in sequence, with each subsequent sort acting as a
 * tiebreaker for the previous one.
 *
 * **Note:** Both lists must be pre-sorted according to the provided [sort] configurations.
 * If either list is not sorted according to these configurations, the behavior is undefined.
 *
 * @param T The type of elements in the lists.
 * @param other The second sorted list to merge with this list.
 * @param idSelector A function that extracts a unique identifier from an element.
 *                   This is used to detect duplicate elements across the two lists.
 * @param sort A list of [Sort] configurations that both lists are sorted by and used for merging.
 *             The sorts are applied in sequence, with earlier sorts taking precedence.
 * @return A new sorted list containing all elements from both lists, with duplicates
 *         resolved by taking elements from the [other] list.
 *
 * @see Sort
 * @see CompositeComparator
 * @see mergeSorted
 */
internal fun <T> List<T>.mergeSorted(
    other: List<T>,
    idSelector: (T) -> String,
    sort: List<Sort<T>>,
): List<T> = mergeSorted(other, idSelector, CompositeComparator(sort))
