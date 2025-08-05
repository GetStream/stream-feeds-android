package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.BookmarkFolderListStateUpdates
import io.getstream.feeds.android.core.generated.models.BookmarkFolderDeletedEvent
import io.getstream.feeds.android.core.generated.models.BookmarkFolderUpdatedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

internal class BookmarkFolderListEventHandler(
    private val state: BookmarkFolderListStateUpdates,
) : StateEventHandler {

    override fun handleEvent(event: WSEvent) {
        when (event) {
            is BookmarkFolderDeletedEvent -> state.onBookmarkFolderRemoved(event.bookmarkFolder.id)
            is BookmarkFolderUpdatedEvent -> state.onBookmarkFolderUpdated(event.bookmarkFolder.toModel())
        }
    }
}