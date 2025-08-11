package io.getstream.feeds.android.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.sample.feed.FeedsScreen
import io.getstream.feeds.android.sample.login.ConnectingScreen
import io.getstream.feeds.android.sample.login.LoginScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DestinationsNavHost(NavGraphs.root)
        }
    }
}

@Destination<RootGraph>(start = true)
@Composable
fun MainScreen() {
    val viewModel = hiltViewModel<MainViewModel>()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    when (viewState) {
        is ViewState.Connecting -> {
            ConnectingScreen()
        }

        is ViewState.LoggedOut -> {
            LoginScreen(
                onCredentialsSelected = viewModel::connect
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
