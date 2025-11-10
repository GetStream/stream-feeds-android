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

package io.getstream.feeds.android.sample.feed

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import io.getstream.feeds.android.sample.R

enum class Reaction(
    val value: String,
    val description: String,
    val icon: Int,
    val filledIcon: Int,
    val color: Color,
) {
    Heart("heart", "Like", R.drawable.favorite, R.drawable.favorite_filled, Color(0xFFE91E63)),
    Fire("fire", "Fire", R.drawable.fire, R.drawable.fire_filled, Color(0xFFFF5722)),
}

@Composable
fun Reaction.painter(hasOwnReaction: Boolean) =
    painterResource(if (hasOwnReaction) filledIcon else icon)
