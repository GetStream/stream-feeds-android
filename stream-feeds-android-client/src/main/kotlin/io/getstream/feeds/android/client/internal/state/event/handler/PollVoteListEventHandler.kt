package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.PollVoteListStateUpdates
import io.getstream.feeds.android.core.generated.models.PollVoteChangedFeedEvent
import io.getstream.feeds.android.core.generated.models.PollVoteRemovedFeedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

/**
 * Handles events related to poll vote lists in the feed state.
 *
 * @property pollId The unique identifier for the poll this handler is associated with.
 * @property state The instance that manages updates to the poll vote list state.
 */
internal class PollVoteListEventHandler(
    private val pollId: String,
    private val state: PollVoteListStateUpdates,
): StateEventHandler {

    override fun handleEvent(event: WSEvent) {
        when (event) {
            is PollVoteChangedFeedEvent -> {
                if (event.poll.id != pollId) return
                state.pollVoteUpdated(event.pollVote.toModel())
            }
            is PollVoteRemovedFeedEvent -> {
                if (event.poll.id != pollId) return
                state.pollVoteRemoved(event.pollVote.id)
            }
        }
    }
}