package io.getstream.feeds.android.sample

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.getstream.android.core.user.ApiKey
import io.getstream.android.core.user.User
import io.getstream.android.core.user.UserToken
import io.getstream.android.core.user.UserTokenProvider
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.sample.login.UserCredentials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.LoggedOut)
    val viewState: StateFlow<ViewState>
        get() = _viewState.asStateFlow()

    lateinit var client: FeedsClient

    fun connect(credentials: UserCredentials) {
        client = FeedsClient(
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
    data object Connecting : ViewState
    data class LoggedIn(val client: FeedsClient, val user: User) : ViewState
    data object LoggedOut : ViewState
}
