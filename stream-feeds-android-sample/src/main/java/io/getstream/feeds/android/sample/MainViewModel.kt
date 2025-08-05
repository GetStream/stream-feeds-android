package io.getstream.feeds.android.sample

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.android.core.user.ApiKey
import io.getstream.android.core.user.User
import io.getstream.android.core.user.UserToken
import io.getstream.android.core.user.UserTokenProvider
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.sample.login.UserCredentials
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.LoggedOut)
    val viewState: StateFlow<ViewState>
        get() = _viewState

    private val _errorEvent: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 1)
    val errorEvent: SharedFlow<String>
        get() = _errorEvent

    fun connect(context: Context, credentials: UserCredentials) {
        val client = FeedsClient(
            context = context,
            apiKey = ApiKey(DemoAppConfig.Current.apiKey),
            user = credentials.user,
            tokenProvider = object : UserTokenProvider {
                override fun loadToken(): UserToken {
                    return credentials.userToken
                }
            }
        )
        viewModelScope.launch {
            _viewState.value = ViewState.Connecting
            client.connect()
                .onSuccess {
                    _viewState.value = ViewState.LoggedIn(client, credentials.user)
                }
                .onFailure {
                    _viewState.value = ViewState.LoggedOut
                }
        }
    }
}

sealed interface ViewState {
    data object Connecting: ViewState
    data class LoggedIn(val client: FeedsClient, val user: User): ViewState
    data object LoggedOut: ViewState
}