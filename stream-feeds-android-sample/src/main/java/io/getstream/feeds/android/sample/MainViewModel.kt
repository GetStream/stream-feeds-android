/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.getstream.feeds.android.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.android.core.user.User
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.sample.login.LoginManager
import io.getstream.feeds.android.sample.login.UserCredentials
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(private val loginManager: LoginManager) : ViewModel() {

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Loading)
    val viewState: StateFlow<ViewState>
        get() = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            _viewState.value =
                when (val current = loginManager.currentState()) {
                    null -> ViewState.LoggedOut
                    else -> ViewState.LoggedIn(current.client, current.user)
                }
        }
    }

    fun connect(credentials: UserCredentials) {
        viewModelScope.launch {
            _viewState.value = ViewState.Loading
            _viewState.value =
                loginManager
                    .login(credentials)
                    .fold(
                        onSuccess = { ViewState.LoggedIn(it.client, it.user) },
                        onFailure = { ViewState.LoggedOut },
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
