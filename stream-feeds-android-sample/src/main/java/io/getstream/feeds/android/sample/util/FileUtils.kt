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
package io.getstream.feeds.android.sample.util

import android.content.Context
import android.net.Uri
import android.util.Log
import io.getstream.android.core.result.runSafely
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun Context.copyToCache(uris: List<Uri>): Result<List<File>> {
    val files = mutableListOf<File>()
    for (uri in uris) {
        try {
            files.add(copyToCache(uri))
        } catch (e: Exception) {
            Log.e("copyToFiles", "Error copying from URI: $uri", e)
            deleteFiles(files)
            return Result.failure(e)
        }
    }

    return Result.success(files)
}

suspend fun Context.copyToCache(uri: Uri) =
    withContext(Dispatchers.IO) {
        val outputFile = File(cacheDir, "attachment_${System.currentTimeMillis()}.tmp")

        contentResolver.openInputStream(uri).use { inputStream ->
            checkNotNull(inputStream) { "Error opening input stream for URI: $uri" }

            FileOutputStream(outputFile).use(inputStream::copyTo)
        }
        outputFile
    }

suspend fun deleteFiles(files: List<File>) = runSafely {
    withContext(Dispatchers.IO) { files.forEach(File::delete) }
}
