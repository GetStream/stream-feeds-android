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
package io.getstream.feeds.android.sample.ui

import android.content.Context
import android.content.res.Configuration
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import coil3.Image
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.asImage
import coil3.request.ImageRequest
import coil3.request.crossfade

class CoilImageLoaderFactory : SingletonImageLoader.Factory {
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        val lightThemePlaceholder by context.lazyColorImage(0x15000000)
        val darkThemePlaceholder by context.lazyColorImage(0xFF1F1F1F)

        val placeholderFactory = { request: ImageRequest ->
            if (request.context.isNightMode()) {
                darkThemePlaceholder
            } else {
                lightThemePlaceholder
            }
        }

        return ImageLoader.Builder(context)
            .crossfade(true)
            .placeholder(placeholderFactory)
            .fallback(placeholderFactory)
            .error(placeholderFactory)
            .build()
    }

    // Using a BitmapDrawable instead of a ColorDrawable because crossfade doesn't play well with
    // the latter
    private fun Context.lazyColorImage(color: Long): Lazy<Image> =
        lazy(LazyThreadSafetyMode.NONE) {
            createBitmap(1, 1).apply { eraseColor(color.toInt()) }.toDrawable(resources).asImage()
        }

    private fun Context.isNightMode(): Boolean {
        val uiMode = resources.configuration.uiMode
        return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}
