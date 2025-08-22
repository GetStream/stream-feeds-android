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
package io.getstream.feeds.android.sample.login

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import io.getstream.android.core.user.ApiKey
import io.getstream.android.core.user.User
import io.getstream.android.core.user.UserTokenProvider
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.sample.DemoAppConfig
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class LoginManager
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
) {
    private val mutex = Mutex()
    private var state: UserState? = null

    suspend fun currentState(): UserState? =
        mutex.withLock {
            if (state != null) state
            else {
                loadCredentials()?.let { connect(it) }?.getOrNull()?.also { state = it }
            }
        }

    suspend fun login(credentials: UserCredentials): Result<UserState> =
        mutex.withLock {
            connect(credentials).onSuccess {
                state = it
                storeCredentials(credentials)
            }
        }

    suspend fun logout() {
        mutex.withLock {
            state?.client?.disconnect()
            state = null
            clearStoredCredentials()
        }
    }

    private suspend fun loadCredentials(): UserCredentials? =
        dataStore.data.first()[loggedUserIdKey]?.let { userId ->
            UserCredentials.BuiltIn.find { it -> it.user.id == userId }
        }

    private suspend fun storeCredentials(credentials: UserCredentials) {
        dataStore.edit { it[loggedUserIdKey] = credentials.user.id }
    }

    private suspend fun clearStoredCredentials() {
        dataStore.edit { it.remove(loggedUserIdKey) }
    }

    private suspend fun connect(credentials: UserCredentials): Result<UserState> {
        val client =
            FeedsClient(
                context = context,
                apiKey = ApiKey(DemoAppConfig.Current.apiKey),
                user = credentials.user,
                tokenProvider =
                    object : UserTokenProvider {
                        override suspend fun loadToken() = credentials.userToken
                    },
            )

        return client.connect().map { UserState(user = credentials.user, client = client) }
    }

    data class UserState(val user: User, val client: FeedsClient)

    companion object {
        private val loggedUserIdKey = stringPreferencesKey("logged_user_id")
    }
}
