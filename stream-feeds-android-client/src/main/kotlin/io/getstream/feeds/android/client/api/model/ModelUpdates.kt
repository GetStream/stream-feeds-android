package io.getstream.feeds.android.client.api.model

/**
 * Model representing updates to a collection of items.
 *
 * @param T The type of items in the collection.
 * @property added A list of items that have been added to the collection.
 * @property removedIds A list of IDs of items that have been removed from the collection.
 * @property updated A list of items that have been updated in the collection.
 */
public data class ModelUpdates<T>(
    public val added: List<T>,
    public val removedIds: List<String>,
    public val updated: List<T>,
)
