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

/** Interface for managing user tokens in the Stream SDK. */
@StreamInternalApi
public interface TokenManager {

    /**
     * Ensures that the token is loaded. If the token is not loaded, it will load it synchronously.
     */
    public fun ensureTokenLoaded()

    /** Load a new token synchronously. */
    public fun loadSync(): UserToken

    /** Expire the current token. */
    public fun expireToken()

    /** Check if a [UserTokenProvider] has been set. */
    public fun hasTokenProvider(): Boolean

    /**
     * Set a new [CacheableUserTokenProvider] to provide the token.
     *
     * @param provider A [CacheableUserTokenProvider] that will be used to provide the token.
     */
    public fun setTokenProvider(provider: CacheableUserTokenProvider)

    /**
     * Get the last loaded token.
     *
     * @return The last loaded token. If the token was expired, an [UserToken.EMPTY] will be
     *   returned.
     */
    public fun getToken(): UserToken

    /**
     * Check if a token was loaded and is not expired.
     *
     * @return true if a token was loaded and it is not expired, false otherwise.
     */
    public fun hasToken(): Boolean
}

/** Default implementation of [TokenManager] that manages user tokens. */
@StreamInternalApi
public class TokenManagerImpl : TokenManager {

    @Volatile private var token: UserToken = UserToken.EMPTY

    private var provider: UserTokenProvider? = null

    override fun ensureTokenLoaded() {
        if (!hasToken()) {
            loadSync()
        }
    }

    override fun loadSync(): UserToken {
        val token = provider?.loadToken() ?: UserToken.EMPTY
        this.token = token
        return token
    }

    override fun expireToken() {
        token = UserToken.EMPTY
    }

    override fun hasTokenProvider(): Boolean {
        return provider != null
    }

    override fun setTokenProvider(provider: CacheableUserTokenProvider) {
        this.provider = provider
        this.token = provider.getCachedToken()
    }

    override fun getToken(): UserToken {
        return token
    }

    override fun hasToken(): Boolean {
        return token != UserToken.EMPTY
    }
}
