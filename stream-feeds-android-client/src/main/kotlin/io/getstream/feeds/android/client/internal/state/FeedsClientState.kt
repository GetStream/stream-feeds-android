package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.websocket.WebSocketConnectionState

internal class FeedsClientStateImpl : FeedsClientMutableState {

    private var _connectionState: WebSocketConnectionState = WebSocketConnectionState.Initialized

    override val connectionState: WebSocketConnectionState
        get() = _connectionState

    override fun setConnectionState(state: WebSocketConnectionState) {
        _connectionState = state
    }
}

internal interface FeedsClientMutableState : FeedsClientState, FeedsClientStateUpdates

internal interface FeedsClientState {
    val connectionState: WebSocketConnectionState
}

internal interface FeedsClientStateUpdates {

    fun setConnectionState(state: WebSocketConnectionState)
}