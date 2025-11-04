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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.ramcosta.composedestinations.generated.destinations.MainScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.model.PushNotificationsProvider
import io.getstream.feeds.android.sample.login.LoginManager
import io.getstream.feeds.android.sample.login.UserCredentials
import io.getstream.feeds.android.sample.utils.logResult
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val loginManager: LoginManager,
    private val firebaseMessaging: FirebaseMessaging,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = MainScreenDestination.argsFrom(savedStateHandle)

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Loading)
    val viewState: StateFlow<ViewState>
        get() = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            // Unregister the device for push notifications if logging out
            if (args.logout) {
                loginManager.currentClient()?.let(::deleteDevice)
            }
            if (args.logout) {
                loginManager.logout()
            }

            _viewState.value =
                when (val current = loginManager.currentClient()) {
                    null -> ViewState.LoggedOut
                    else -> ViewState.LoggedIn(current)
                }
        }
    }

    fun connect(credentials: UserCredentials) {
        viewModelScope.launch {
            _viewState.value = ViewState.Loading
            val viewState =
                loginManager
                    .login(credentials)
                    .fold(onSuccess = ViewState::LoggedIn, onFailure = { ViewState.LoggedOut })
            // If the user is logged in, register the device for push notifications
            if (viewState is ViewState.LoggedIn) {
                registerDevice(viewState.client)
            }
            _viewState.value = viewState
        }
    }

    private suspend fun registerDevice(client: FeedsClient) {
        getFirebaseToken().logResult(TAG, "[registerDevice] getFirebaseToken").onSuccess { token ->
            client
                .createDevice(
                    id = token,
                    pushProvider = PushNotificationsProvider.FIREBASE,
                    pushProviderName = "feeds-android-firebase",
                )
                .logResult(TAG, "createDevice")
        }
    }

    private fun deleteDevice(client: FeedsClient) {
        viewModelScope.launch {
            getFirebaseToken().logResult(TAG, "[deleteDevice] getFirebaseToken").onSuccess { token
                ->
                client.deleteDevice(token).logResult(TAG, "deleteDevice")
            }
        }
    }

    private suspend fun getFirebaseToken(): Result<String> = runSafely {
        firebaseMessaging.token.await()
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}

sealed interface ViewState {
    data object Loading : ViewState

    data class LoggedIn(val client: FeedsClient) : ViewState

    data object LoggedOut : ViewState
}
