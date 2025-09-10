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
package io.getstream.feeds.android.sample.story

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.UserData
import io.getstream.feeds.android.sample.components.UserAvatar

@Composable
fun StoryScreen(activity: ActivityData, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        val firstImage = activity.attachments.firstOrNull()?.assetUrl

        Box(
            modifier =
                Modifier.background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
                    .clickable(onClick = onDismiss)
        ) {
            if (firstImage != null) {
                ImageStoryContent(activity = activity, imageUrl = firstImage)
            } else {
                TextOnlyStoryContent(activity = activity)
            }
        }
    }
}

@Composable
private fun BoxScope.ImageStoryContent(activity: ActivityData, imageUrl: String) {
    // Background image
    AsyncImage(
        model = imageUrl,
        contentDescription = "Story image",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
    )

    // Bottom gradient overlay
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.7f),
                                )
                        )
                )
    )

    // Content at the bottom
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .systemBarsPadding()
    ) {
        StoryUserInfo(user = activity.user, textColor = Color.White)

        // Story text
        activity.text?.let { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun TextOnlyStoryContent(activity: ActivityData) {
    // Background color
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant))

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {

        // Large centered text
        Text(
            text = activity.text.orEmpty(),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier =
                Modifier.fillMaxWidth()
                    .weight(1f)
                    .wrapContentHeight(Alignment.CenterVertically)
                    .padding(32.dp),
            textAlign = TextAlign.Center,
        )

        // User info at bottom
        StoryUserInfo(
            user = activity.user,
            textColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun StoryUserInfo(user: UserData, textColor: Color, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        UserAvatar(user.image, modifier = Modifier.size(40.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = user.name ?: user.id,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
        )
    }
}
