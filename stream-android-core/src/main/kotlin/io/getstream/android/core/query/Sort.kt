package io.getstream.android.core.query

import io.getstream.kotlin.base.annotation.marker.StreamInternalApi

/**
 * The direction of a sort operation.
 * This enum defines whether a sort should be performed in ascending (forward) or
 * descending (reverse) order. The raw values correspond to the values expected by the remote API.
 */
public enum class SortDirection(public val value: Int) {
    /**
     * Sort in ascending order (A to Z, 1 to 9, etc.).
     */
    FORWARD(1),

    /**
     * Sort in descending order (Z to A, 9 to 1, etc.).
     */
    REVERSE(-1);
}

/**
 * A sort configuration that combines a sort field with a direction.
 *
 * This class represents a complete sort specification that can be applied to collections
 * of the associated model type. It provides both local sorting capabilities and the ability
 * to generate remote API request parameters.
 */
public open class Sort<T>(
    public val field: SortField<T>,
    public val direction: SortDirection,
): Comparator<T> {

    /**
     * Converts this sort configuration to a DTO map for API requests.
     */
    public fun toDto(): Map<String, Any> = mapOf(
        "field" to field.remote,
        "direction" to direction.value
    )

    override fun compare(o1: T?, o2: T?): Int {
        return field.comparator.compare(o1, o2, direction)
    }
}

/**
 * A protocol that defines a sortable field for a specific model type.
 *
 * This interface provides the foundation for creating sortable fields that can be used
 * both for local sorting and remote API requests. It includes a comparator for local
 * sorting operations and a remote string identifier for API communication.
 */
public interface SortField<T> {
    /**
     * A comparator that can be used for local sorting operations.
     */
    public val comparator: AnySortComparator<T>

    /**
     * The string identifier used when sending sort parameters to the remote API.
     */
    public val remote: String

    public companion object {
        /**
         * Creates a new sort field with the specified remote identifier and local value extractor.
         *
         * @param remote The string identifier used for remote API requests
         * @param localValue A function that extracts the comparable value from a model instance
         */
        public fun <T, V : Comparable<V>> create(
            remote: String,
            localValue: (T) -> V
        ): SortField<T> = SortFieldImpl(remote, localValue)
    }
}

/**
 * Private implementation of SortField
 */
internal class SortFieldImpl<T, V : Comparable<V>>(
    override val remote: String,
    private val localValue: (T) -> V
) : SortField<T> {

    override val comparator: AnySortComparator<T> =
        SortComparator(localValue).toAny()
}

/**
 * A comparator that can sort model instances by extracting comparable values.
 *
 * This class provides the foundation for local sorting operations by wrapping a lambda that
 * extracts comparable values from model instances. It handles the comparison logic and direction
 * handling internally.
 *
 * @param T The type of the model instances to be compared.
 * @param V The type of the comparable value extracted from the model instances.
 * @property value A lambda that extracts a comparable value from a model instance.
 */
public class SortComparator<T, V : Comparable<V>>(
    public val value: (T) -> V
) {

    /**
     * Compares two model instances using the extracted values and sort direction.
     *
     * @param lhs The first model instance to compare
     * @param rhs The second model instance to compare
     * @param direction The direction of the sort
     * @return A comparison result indicating the relative ordering
     */
    public fun compare(lhs: T?, rhs: T?, direction: SortDirection): Int {
        val value1 = lhs?.let(value)
        val value2 = rhs?.let(value)

        return when {
            value1 == null && value2 == null -> 0
            value1 == null -> -direction.value
            value2 == null -> direction.value
            else -> value1.compareTo(value2) * direction.value
        }
    }

    /**
     * Converts this comparator to a type-erased version.
     *
     * @return An AnySortComparator that wraps this comparator
     */
    public fun toAny(): AnySortComparator<T> {
        return AnySortComparator(this)
    }
}

/**
 * A type-erased wrapper for sort comparators that can work with any model type.
 *
 * This class provides a way to store and use sort comparators without knowing their
 * specific generic type parameters. It's useful for creating collections of different
 * sort configurations that can all work with the same model type.
 *
 * Type erased type avoids making SortField generic while keeping the underlying
 * value type intact (no runtime type checks while sorting).
 */
public class AnySortComparator<T>(
    private val compare: (T?, T?, SortDirection) -> Int
) {

    /**
     * Creates a type-erased comparator from a specific comparator instance.
     *
     * @param sort The specific comparator to wrap
     */
    public constructor(sort: SortComparator<T, *>) : this(sort::compare)

    /**
     * Compares two model instances using the wrapped comparator.
     *
     * @param lhs The left-hand side model instance
     * @param rhs The right-hand side model instance
     * @param direction The direction of the sort
     * @return A comparison result indicating the relative ordering
     */
    public fun compare(lhs: T?, rhs: T?, direction: SortDirection): Int {
        return this.compare.invoke(lhs, rhs, direction)
    }
}

/**
 * Extension function to sort a list of models using a list of sort configurations.
 *
 * @param T The type of elements in the list.
 * @param sort A list of sort configurations to apply to the list.
 */
@StreamInternalApi
public fun <T> List<T>.sortedWith(sort: List<Sort<T>>): List<T> =
    sortedWith(CompositeComparator(sort))

/**
 * A composite comparator that combines multiple sort comparators.
 * This class allows for sorting based on multiple criteria, where each comparator is applied in
 * sequence.
 *
 * This implementation mirrors the Swift Array.sorted(using:) extension behavior:
 * - Iterates through each sort comparator in order
 * - Returns the first non-equal comparison result
 * - If all comparators return equal (0), returns 0 to maintain stable sort order
 *
 * @param T The type of elements to be compared.
 * @param comparators The list of comparators to be combined.
 */
@StreamInternalApi
public class CompositeComparator<T>(private val comparators: List<Comparator<T>>) :
    Comparator<T> {

    override fun compare(o1: T, o2: T): Int {
        for (comparator in comparators) {
            val result = comparator.compare(o1, o2)
            when (result) {
                0 -> continue // Equal, move to the next comparator
                else -> return result // Return the first non-equal comparison result
            }
        }
        return 0 // All comparators returned equal, maintain original order (Swift returns false)
    }
}
