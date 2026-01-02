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

package io.getstream.feeds.android.sample.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.getstream.feeds.android.sample.R

data class FloatingActionButtonItem(val icon: Painter, val label: String, val onClick: () -> Unit)

@Composable
fun ExpandableFloatingActionButton(
    items: List<FloatingActionButtonItem>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // FAB Items - Each item animated individually with staggered delays
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items.forEachIndexed { index, item ->
                AnimatedVisibility(
                    visible = expanded,
                    enter = itemEnterAnimation(index),
                    exit = itemExitAnimation(items.size, index),
                ) {
                    FABItemRow(
                        item = item,
                        onClick = {
                            item.onClick()
                            expanded = false
                        },
                    )
                }
            }
        }

        // Main FAB
        FloatingActionButton(onClick = { expanded = !expanded }, shape = CircleShape) {
            Icon(
                painter = painterResource(R.drawable.add_24),
                contentDescription = if (expanded) "Close menu" else "Open menu",
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun FABItemRow(item: FloatingActionButtonItem, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Label
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            shape = MaterialTheme.shapes.small,
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }

        // Small FAB
        SmallFloatingActionButton(onClick = onClick, shape = CircleShape) {
            Icon(
                painter = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun itemEnterAnimation(index: Int): EnterTransition =
    slideInVertically(
        animationSpec =
            tween(
                durationMillis = 250,
                delayMillis = index * 50, // Staggered delay for bottom-to-top effect
            ),
        initialOffsetY = { it }, // Start from below (full height offset)
    ) + fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = index * 50))

@Composable
private fun itemExitAnimation(itemsSize: Int, index: Int): ExitTransition =
    slideOutVertically(
        animationSpec =
            tween(
                durationMillis = 200,
                delayMillis = (itemsSize - 1 - index) * 30, // Reverse order for exit
            ),
        targetOffsetY = { it },
    ) +
        fadeOut(
            animationSpec = tween(durationMillis = 200, delayMillis = (itemsSize - 1 - index) * 30)
        )
