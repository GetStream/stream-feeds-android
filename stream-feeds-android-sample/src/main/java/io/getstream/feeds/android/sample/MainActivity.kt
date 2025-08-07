package io.getstream.feeds.android.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.sample.feed.FeedsScreen
import io.getstream.feeds.android.sample.login.ConnectingScreen
import io.getstream.feeds.android.sample.login.LoginScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewState by viewModel.viewState.collectAsStateWithLifecycle()
            when (viewState) {
                is ViewState.Connecting -> {
                    ConnectingScreen()
                }

                is ViewState.LoggedOut -> {
                    LoginScreen(
                        onCredentialsSelected = { viewModel.connect(applicationContext, it) }
                    )
                }

                is ViewState.LoggedIn -> {
                    FeedsScreen(
                        fid = FeedId("user", (viewState as ViewState.LoggedIn).user.id),
                        feedsClient = (viewState as ViewState.LoggedIn).client,
                        avatarUrl = (viewState as ViewState.LoggedIn).user.imageURL,
                        currentUserId = (viewState as ViewState.LoggedIn).user.id,
                    )
                }
            }
        }
    }
}
