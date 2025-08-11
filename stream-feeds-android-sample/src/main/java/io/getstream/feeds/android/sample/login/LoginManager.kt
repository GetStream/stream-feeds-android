package io.getstream.feeds.android.sample.login

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.getstream.android.core.user.ApiKey
import io.getstream.android.core.user.User
import io.getstream.android.core.user.UserTokenProvider
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.sample.DemoAppConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    var state: UserState? = null
        private set

    suspend fun login(credentials: UserCredentials): Result<UserState> {
        val currentClient = state?.client
        state = null

        val newClient = FeedsClient(
            context = context,
            apiKey = ApiKey(DemoAppConfig.Current.apiKey),
            user = credentials.user,
            tokenProvider = object : UserTokenProvider {
                override fun loadToken() = credentials.userToken
            }
        )

        currentClient?.disconnect()

        return newClient.connect().map {
            UserState(user = credentials.user, client = newClient)
                .also(::state::set)
        }
    }

    data class UserState(
        val user: User,
        val client: FeedsClient,
    )
}
