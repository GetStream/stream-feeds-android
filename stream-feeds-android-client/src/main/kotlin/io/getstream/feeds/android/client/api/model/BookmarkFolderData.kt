package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.internal.model.mapping.toDate
import io.getstream.feeds.android.core.generated.models.BookmarkFolderResponse
import java.util.Date

/**
 * Data class representing a bookmark folder.
 *
 * @property createdAt The date the folder was created.
 * @property custom Optional custom data as a map.
 * @property id Unique identifier for the folder.
 * @property name Name of the folder.
 * @property updatedAt The date the folder was last updated.
 */
public data class BookmarkFolderData(
    val createdAt: Date,
    val custom: Map<String, Any?>?,
    val id: String,
    val name: String,
    val updatedAt: Date
)

/**
 * Converts a [BookmarkFolderResponse] to a [BookmarkFolderData] model.
 */
internal fun BookmarkFolderResponse.toModel(): BookmarkFolderData = BookmarkFolderData(
    createdAt = createdAt.toDate(),
    custom = custom,
    id = id,
    name = name,
    updatedAt = updatedAt.toDate(),
)

