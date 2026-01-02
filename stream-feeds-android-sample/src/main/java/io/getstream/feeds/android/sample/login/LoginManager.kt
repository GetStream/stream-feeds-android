/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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
import io.getstream.android.core.api.authentication.StreamTokenProvider
import io.getstream.android.core.api.model.value.StreamApiKey
import io.getstream.android.core.api.model.value.StreamToken
import io.getstream.android.core.api.model.value.StreamUserId
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.logging.HttpLoggingLevel
import io.getstream.feeds.android.client.api.logging.LoggingConfig
import io.getstream.feeds.android.client.api.model.FeedsConfig
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
    @param:ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
) {
    private val mutex = Mutex()
    private var client: FeedsClient? = null

    suspend fun currentClient(): FeedsClient? =
        mutex.withLock {
            if (client != null) client
            else {
                loadCredentials()?.let { connect(it) }?.getOrNull()?.also { client = it }
            }
        }

    suspend fun login(credentials: UserCredentials): Result<FeedsClient> =
        mutex.withLock {
            connect(credentials).onSuccess {
                client = it
                storeCredentials(credentials)
            }
        }

    suspend fun logout() {
        mutex.withLock {
            client?.disconnect()
            client = null
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

    private suspend fun connect(credentials: UserCredentials): Result<FeedsClient> {
        val client =
            FeedsClient(
                context = context,
                apiKey = StreamApiKey.fromString(DemoAppConfig.Current.apiKey),
                user = credentials.user,
                tokenProvider =
                    object : StreamTokenProvider {
                        override suspend fun loadToken(userId: StreamUserId): StreamToken {
                            return credentials.userToken
                        }
                    },
                config =
                    FeedsConfig(
                        loggingConfig = LoggingConfig(httpLoggingLevel = HttpLoggingLevel.Body)
                    ),
            )

        return client.connect().map { client }
    }

    companion object {
        private val loggedUserIdKey = stringPreferencesKey("logged_user_id")
    }
}
