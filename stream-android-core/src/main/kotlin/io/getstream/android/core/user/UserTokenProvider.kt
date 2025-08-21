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
package io.getstream.android.core.user

import io.getstream.kotlin.base.annotation.marker.StreamInternalApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Provides a token used to authenticate the user with Stream Chat API. The SDK doesn't refresh the
 * token internally and will call [loadToken] function once the previous one has expired.
 */
public interface UserTokenProvider {

    /**
     * Loads the token for the current user. The token will be loaded only if the token was not
     * loaded yet or existing one has expired. If the token cannot be loaded, returns an empty
     * string and never throws an exception.
     *
     * @return The valid JWT token.
     */
    public suspend fun loadToken(): UserToken
}

/**
 * An implementation of [UserTokenProvider] that keeps previous values of the loaded token. This
 * implementation delegate the process to obtain a new token to another tokenProvider.
 *
 * @property provider The [UserTokenProvider] used to obtain new tokens.
 */
@StreamInternalApi
public class CacheableUserTokenProvider(private val provider: UserTokenProvider) :
    UserTokenProvider {

    @Volatile
    private var cachedToken: UserToken = UserToken.EMPTY

    private val mutex = Mutex()

    override suspend fun loadToken(): UserToken {
        val currentToken = cachedToken
        return mutex.withLock {
            cachedToken.takeIf { it != currentToken }
                ?: provider.loadToken().also { cachedToken = it }
        }
    }

    /**
     * Obtain the cached token.
     *
     * @return The cached token.
     */
    public fun getCachedToken(): UserToken {
        return cachedToken
    }
}
