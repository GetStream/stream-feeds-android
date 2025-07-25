package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.FeedStateUpdates
import io.getstream.feeds.android.core.generated.models.ActivityAddedEvent
import io.getstream.feeds.android.core.generated.models.ActivityDeletedEvent
import io.getstream.feeds.android.core.generated.models.ActivityPinnedEvent
import io.getstream.feeds.android.core.generated.models.ActivityReactionAddedEvent
import io.getstream.feeds.android.core.generated.models.ActivityReactionDeletedEvent
import io.getstream.feeds.android.core.generated.models.ActivityUnpinnedEvent
import io.getstream.feeds.android.core.generated.models.ActivityUpdatedEvent
import io.getstream.feeds.android.core.generated.models.BookmarkAddedEvent
import io.getstream.feeds.android.core.generated.models.BookmarkDeletedEvent
import io.getstream.feeds.android.core.generated.models.CommentAddedEvent
import io.getstream.feeds.android.core.generated.models.CommentDeletedEvent
import io.getstream.feeds.android.core.generated.models.FeedDeletedEvent
import io.getstream.feeds.android.core.generated.models.FeedUpdatedEvent
import io.getstream.feeds.android.core.generated.models.FollowCreatedEvent
import io.getstream.feeds.android.core.generated.models.FollowDeletedEvent
import io.getstream.feeds.android.core.generated.models.FollowUpdatedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

/**
 * This class handles feed events and updates the feed state accordingly.
 * It is responsible for processing incoming events related to feeds,
 * such as activity updates, reactions, comments, and other feed-related actions.
 *
 * @param fid The unique identifier for the feed this handler is associated with.
 * @property state The instance that manages updates to the feed state.
 */
internal class FeedEventHandler(
    private val fid: FeedId,
    private val state: FeedStateUpdates,
): StateEventHandler {

    /**
     * Processes a WebSocket event and updates the feed state.
     *
     * @param event The WebSocket event to process.
     */
    override fun handleEvent(event: WSEvent) {
        when (event) {
            is ActivityAddedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityAdded(event.activity.toModel())
                }
            }

            is ActivityDeletedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityRemoved(event.activity.toModel())
                }
            }

            is ActivityReactionAddedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onReactionAdded(event.reaction.toModel())
                }
            }

            is ActivityReactionDeletedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onReactionRemoved(event.reaction.toModel())
                }
            }

            is ActivityUpdatedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityUpdated(event.activity.toModel())
                }
            }

            is ActivityPinnedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityPinned(event.pinnedActivity.toModel())
                }
            }

            is ActivityUnpinnedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityUnpinned(event.pinnedActivity.activity.id)
                }
            }

            is BookmarkAddedEvent -> {
                if (event.bookmark.activity.feeds.contains(fid.rawValue)) {
                    state.onBookmarkAdded(event.bookmark.toModel())
                }
            }

            is BookmarkDeletedEvent -> {
                if (event.bookmark.activity.feeds.contains(fid.rawValue)) {
                    state.onBookmarkRemoved(event.bookmark.toModel())
                }
            }

            is CommentAddedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onCommentAdded(event.comment.toModel())
                }
            }

            is CommentDeletedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onCommentRemoved(event.comment.toModel())
                }
            }

            is FeedDeletedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onFeedDeleted()
                }
            }

            is FeedUpdatedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onFeedUpdated(event.feed.toModel())
                }
            }

            is FollowCreatedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onFollowAdded(event.follow.toModel())
                }
            }

            is FollowDeletedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onFollowRemoved(event.follow.toModel())
                }
            }

            is FollowUpdatedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onFollowUpdated(event.follow.toModel())
                }
            }

            else -> {
                // Handle other events if necessary
            }
        }
    }
}
