package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.MemberListStateUpdates
import io.getstream.feeds.android.core.generated.models.FeedMemberRemovedEvent
import io.getstream.feeds.android.core.generated.models.FeedMemberUpdatedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent

internal class MemberListEventHandler(
    private val state: MemberListStateUpdates,
) : StateEventHandler {
    override fun handleEvent(event: WSEvent) {
        when (event) {
            is FeedMemberRemovedEvent -> {
                state.onMemberRemoved(event.memberId)
            }

            is FeedMemberUpdatedEvent -> {
                state.onMemberUpdated(event.member.toModel())
            }
        }
    }
}