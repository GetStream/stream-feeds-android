package io.getstream.feeds.android.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.android.core.user.User
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.sample.login.LoginManager
import io.getstream.feeds.android.sample.login.UserCredentials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginManager: LoginManager,
) : ViewModel() {

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Loading)
    val viewState: StateFlow<ViewState>
        get() = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            _viewState.value = when (val current = loginManager.currentState()) {
                null -> ViewState.LoggedOut
                else -> ViewState.LoggedIn(current.client, current.user)
            }
        }
    }

    fun connect(credentials: UserCredentials) {
        viewModelScope.launch {
            _viewState.value = ViewState.Loading
            _viewState.value = loginManager.login(credentials)
                .fold(
                    onSuccess = { ViewState.LoggedIn(it.client, it.user) },
                    onFailure = { ViewState.LoggedOut }
                )
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            _viewState.value = ViewState.LoggedOut
            loginManager.logout()
        }
    }
}

sealed interface ViewState {
    data object Loading : ViewState
    data class LoggedIn(val client: FeedsClient, val user: User) : ViewState
    data object LoggedOut : ViewState
}
