package io.getstream.feeds.android.client.internal.subscribe

import io.getstream.feeds.android.network.models.WSEvent

/** Listener interface for Feeds socket events. */
internal interface FeedsEventListener {

    /**
     * Called when a new event is received from the socket.
     *
     * @param event The event received from the WebSocket.
     */
    fun onEvent(event: WSEvent)
}