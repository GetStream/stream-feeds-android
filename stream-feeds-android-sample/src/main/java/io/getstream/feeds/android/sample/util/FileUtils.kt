package io.getstream.feeds.android.sample.util

import android.content.Context
import android.net.Uri
import android.util.Log
import io.getstream.android.core.result.runSafely
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

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

suspend fun Context.copyToCache(uri: Uri) = withContext(Dispatchers.IO) {
    val outputFile = File(cacheDir, "attachment_${System.currentTimeMillis()}.tmp")

    contentResolver.openInputStream(uri).use { inputStream ->
        checkNotNull(inputStream) { "Error opening input stream for URI: $uri" }

        FileOutputStream(outputFile).use(inputStream::copyTo)
    }
    outputFile
}

suspend fun deleteFiles(files: List<File>) = runSafely {
    withContext(Dispatchers.IO) {
        files.forEach(File::delete)
    }
}
