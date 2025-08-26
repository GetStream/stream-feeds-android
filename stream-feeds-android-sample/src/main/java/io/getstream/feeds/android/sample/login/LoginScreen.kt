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

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import io.getstream.feeds.android.client.BuildConfig
import io.getstream.feeds.android.sample.R
import io.getstream.feeds.android.sample.components.UserAvatar

@Composable
fun LoginScreen(
    credentials: List<UserCredentials> = UserCredentials.BuiltIn,
    onCredentialsSelected: (UserCredentials) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(32.dp))
        Image(
            modifier = Modifier.size(width = 80.dp, height = 40.dp),
            painter = painterResource(R.drawable.ic_stream),
            contentDescription = null,
        )

        Spacer(Modifier.height(12.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.user_login_screen_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )

        Spacer(Modifier.height(12.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.user_login_screen_subtitle),
            fontSize = 14.sp,
            color = Color.DarkGray,
        )

        Spacer(Modifier.height(20.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(credentials) { item ->
                UserLoginItem(item, onCredentialsSelected)
                DividerItem()
            }
        }

        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(R.string.sdk_version_template, BuildConfig.PRODUCT_VERSION),
            fontSize = 14.sp,
            color = Color.Gray,
        )
    }

    // Request notification permission on Android 13+
    val context = LocalContext.current

    // Permission launcher for notification permission
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = {
                /* No need to handle.
                In your app, don't call FeedsClient#createDevice() if the permission is not granted.
                */
            },
        )

    // Request notification permission on launch (Android 13+)
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PermissionChecker.PERMISSION_GRANTED

            if (!hasPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
private fun UserLoginItem(
    credentials: UserCredentials,
    onClick: (UserCredentials) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable(
                    onClick = { onClick(credentials) },
                    indication = ripple(),
                    interactionSource = remember { MutableInteractionSource() },
                )
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UserAvatar(credentials.user.imageURL, Modifier.size(40.dp))

        Text(
            modifier = Modifier.weight(1f).padding(start = 16.dp),
            text = credentials.user.name ?: credentials.user.id,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )

        Image(
            modifier = Modifier.wrapContentSize(),
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
        )
    }
}

@Composable
private fun DividerItem() {
    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen {}
}
