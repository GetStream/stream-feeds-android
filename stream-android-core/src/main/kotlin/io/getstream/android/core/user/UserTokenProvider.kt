package io.getstream.android.core.user

import androidx.annotation.WorkerThread
import io.getstream.kotlin.base.annotation.marker.StreamInternalApi

/**
 * Provides a token used to authenticate the user with Stream Chat API.
 * The SDK doesn't refresh the token internally and will call [loadToken] function once the previous
 * one has expired.
 */
public interface UserTokenProvider {

    /**
     * Loads the token for the current user.
     * The token will be loaded only if the token was not loaded yet or existing one has expired.
     * If the token cannot be loaded, returns an empty string and never throws an exception.
     *
     * @return The valid JWT token.
     */
    @WorkerThread
    public fun loadToken(): UserToken
}

/**
 * An implementation of [UserTokenProvider] that keeps previous values of the loaded token.
 * This implementation delegate the process to obtain a new token to another tokenProvider.
 *
 * @property provider The [UserTokenProvider] used to obtain new tokens.
 */
@StreamInternalApi
public class CacheableUserTokenProvider(
    private val provider: UserTokenProvider,
) : UserTokenProvider {

    @Volatile
    private var cachedToken: UserToken = UserToken.EMPTY

    override fun loadToken(): UserToken {
        val currentToken = cachedToken
        return synchronized(this) {
            cachedToken
                .takeIf { it != currentToken }
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