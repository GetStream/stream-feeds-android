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

package io.getstream.feeds.android.client.internal.model

private const val LANGUAGE_KEY = "language"
private const val TRANSLATION_SUFFIX = "_text"

/** The language code of the original text, read from the backend's `i18n` map. */
internal fun Map<String, String>?.originalLanguage(): String? = this?.get(LANGUAGE_KEY)

/** The translations in the backend's `i18n` map, keyed by language code. */
internal fun Map<String, String>?.translations(): Map<String, String> =
    this.orEmpty()
        .filterKeys { it.endsWith(TRANSLATION_SUFFIX) }
        .mapKeys { it.key.removeSuffix(TRANSLATION_SUFFIX) }

/**
 * The translation to [language], or `null` when there is none, when [language] is the original
 * language, or when the translation equals [text].
 */
internal fun translationOf(
    text: String?,
    translations: Map<String, String>,
    originalLanguage: String?,
    language: String,
): String? = translations[language]?.takeIf { language != originalLanguage && it != text }

/**
 * Whether translations held locally should be kept when [updatedText] arrives without any.
 * WebSocket events never carry translations, and the backend clears them when the text changes.
 */
internal fun keepsTranslations(
    text: String?,
    updatedText: String?,
    updatedTranslations: Map<String, String>,
    updatedOriginalLanguage: String?,
): Boolean = updatedTranslations.isEmpty() && updatedOriginalLanguage == null && updatedText == text
