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

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun LinkText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
    lineHeight: TextUnit = 20.sp,
) {
    val context = LocalContext.current

    // Regex to match markdown links: [text](url)
    val markdownLinkPattern = Regex("""\[([^]]+)]\(([^)]+)\)""")

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
            withLink(
                LinkAnnotation.Url(
                    url = url,
                    styles =
                        TextLinkStyles(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.Medium,
                            )
                        ),
                )
            ) {
                append(linkText)
            }

            lastIndex = end
        }

        // Add remaining text after the last link
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }

    Text(text = annotatedString, modifier = modifier, fontSize = fontSize, lineHeight = lineHeight)
}
