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
package io.getstream.feeds.android.sample.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun LinkText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    color: Color = Color.Black,
    lineHeight: androidx.compose.ui.unit.TextUnit = 20.sp,
) {
    val context = LocalContext.current

    // Regex to match markdown links: [text](url)
    val markdownLinkPattern = Regex("""\[([^\]]+)\]\(([^)]+)\)""")

    val annotatedString = buildAnnotatedString {
        var lastIndex = 0

        markdownLinkPattern.findAll(text).forEach { matchResult ->
            val (linkText, url) = matchResult.destructured
            val start = matchResult.range.first
            val end = matchResult.range.last + 1

            // Add text before the link
            if (start > lastIndex) {
                append(text.substring(lastIndex, start))
            }

            // Add the clickable link
            pushStringAnnotation(tag = "URL", annotation = url)
            withStyle(
                style =
                    SpanStyle(
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Medium,
                    )
            ) {
                append(linkText)
            }
            pop()

            lastIndex = end
        }

        // Add remaining text after the last link
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }

    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style =
            androidx.compose.ui.text.TextStyle(
                fontSize = fontSize,
                color = color,
                lineHeight = lineHeight,
            ),
        onClick = { offset ->
            annotatedString
                .getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()
                ?.let { annotation ->
                    try {
                        // Ensure URL has a proper protocol scheme
                        val url = annotation.item
                        val completeUrl =
                            when {
                                url.startsWith("http://") || url.startsWith("https://") -> url
                                url.startsWith("www.") -> "https://$url"
                                url.contains(".") && !url.contains("://") -> "https://$url"
                                else -> url
                            }

                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(completeUrl))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Handle error opening URL (e.g., invalid URL format)
                        e.printStackTrace()
                    }
                }
        },
    )
}
