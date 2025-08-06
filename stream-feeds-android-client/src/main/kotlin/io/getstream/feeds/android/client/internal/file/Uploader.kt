package io.getstream.feeds.android.client.internal.file

import io.getstream.feeds.android.client.api.file.UploadContext
import io.getstream.feeds.android.client.api.file.UploadPayload
import io.getstream.feeds.android.client.api.file.Uploader
import io.getstream.feeds.android.core.generated.models.Attachment
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal suspend fun <C : UploadContext> Uploader<C>.uploadAll(
    files: List<UploadPayload<C>>,
    attachmentUploadProgress: ((UploadPayload<C>, Double) -> Unit)?,
): List<Attachment> {
    if (files.isEmpty()) return emptyList()

    return coroutineScope {
        files.map { localFile ->
            async {
                val progressCallback: ((Double) -> Unit)? = attachmentUploadProgress?.let { progress ->
                    { progress(localFile, it) }
                }
                val uploadedFile = upload(payload = localFile, progress = progressCallback).getOrThrow()
                Attachment(
                    assetUrl = uploadedFile.fileUrl,
                    thumbUrl = uploadedFile.thumbnailUrl,
                )
            }
        }.awaitAll()
    }
}
