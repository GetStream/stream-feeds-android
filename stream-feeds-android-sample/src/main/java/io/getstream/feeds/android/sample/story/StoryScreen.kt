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

package io.getstream.feeds.android.sample.story

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
fun StoryScreen(
    activities: List<ActivityData>,
    onWatched: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        var currentIndex by remember { mutableIntStateOf(0) }
        val activity = activities[currentIndex]
        val firstImage = activity.attachments.firstOrNull()?.assetUrl

        LaunchedEffect(currentIndex) { onWatched(activity.id) }

        Box(
            modifier =
                Modifier.background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
                    .systemBarsPadding()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val isRightSide = offset.x > size.width / 2

                            if (isRightSide) {
                                if (currentIndex == activities.lastIndex) {
                                    onDismiss()
                                } else {
                                    currentIndex++
                                }
                            } else if (currentIndex > 0) {
                                currentIndex--
                            }
                        }
                    }
        ) {
            if (firstImage != null) {
                ImageStoryContent(activity = activity, imageUrl = firstImage)
            } else {
                TextOnlyStoryContent(activity = activity)
            }

            StoryIndicator(
                total = activities.size,
                currentIndex = currentIndex,
                modifier = Modifier.fillMaxWidth().padding(8.dp).align(Alignment.TopCenter),
            )
        }
    }
}

@Composable
private fun StoryIndicator(currentIndex: Int, total: Int, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        repeat(total) { index ->
            val color =
                if (index <= currentIndex) MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.onBackground.copy(alpha = .3f)
            Box(
                modifier =
                    Modifier.weight(1f)
                        .height(4.dp)
                        .background(color = color, shape = MaterialTheme.shapes.small)
            )
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

    Column(modifier = Modifier.fillMaxSize()) {

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
