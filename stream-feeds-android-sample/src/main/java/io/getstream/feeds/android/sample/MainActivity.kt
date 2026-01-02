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

package io.getstream.feeds.android.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.FeedsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.feeds.android.sample.components.LoadingScreen
import io.getstream.feeds.android.sample.login.LoginScreen
import io.getstream.feeds.android.sample.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val bottomSheetNavigator = rememberBottomSheetNavigator()
            val navController = rememberNavController(bottomSheetNavigator)

            AppTheme {
                ModalBottomSheetLayout(
                    bottomSheetNavigator = bottomSheetNavigator,
                    sheetShape = MaterialTheme.shapes.large,
                ) {
                    DestinationsNavHost(NavGraphs.root, navController = navController)
                }
            }
        }
    }
}

@Destination<RootGraph>(start = true, navArgs = MainScreenArgs::class)
@Composable
fun MainScreen(navigator: DestinationsNavigator) {
    val viewModel = hiltViewModel<MainViewModel>()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    Surface(Modifier.fillMaxSize()) {
        when (val viewState = viewState) {
            is ViewState.Loading -> {
                LoadingScreen()
            }

            is ViewState.LoggedOut -> {
                LoginScreen(onCredentialsSelected = viewModel::connect)
            }

            is ViewState.LoggedIn -> {
                LaunchedEffect(Unit) {
                    navigator.navigate(FeedsScreenDestination) {
                        popUpTo(NavGraphs.root) { inclusive = true }
                    }
                }
            }
        }
    }
}

data class MainScreenArgs(val logout: Boolean = false)
