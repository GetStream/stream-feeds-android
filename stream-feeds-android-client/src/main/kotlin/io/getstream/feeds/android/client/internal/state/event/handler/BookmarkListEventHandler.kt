package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.BookmarkListStateUpdates
import io.getstream.feeds.android.core.generated.models.BookmarkFolderDeletedEvent
import io.getstream.feeds.android.core.generated.models.BookmarkFolderUpdatedEvent
import io.getstream.feeds.android.core.generated.models.BookmarkUpdatedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

internal class BookmarkListEventHandler(
    private val state: BookmarkListStateUpdates,
): StateEventHandler {

    override fun handleEvent(event: WSEvent) {
        when (event) {
            is BookmarkFolderDeletedEvent -> state.onBookmarkFolderRemoved(event.bookmarkFolder.id)
            is BookmarkFolderUpdatedEvent -> state.onBookmarkFolderUpdated(event.bookmarkFolder.toModel())
            is BookmarkUpdatedEvent -> state.onBookmarkUpdated(event.bookmark.toModel())
        }
    }
}