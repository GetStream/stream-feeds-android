package io.getstream.feeds.android.client.internal.file

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FeedUploader
import io.getstream.feeds.android.client.api.file.UploadedFile
import io.getstream.feeds.android.client.api.file.isImage
import io.getstream.feeds.android.client.internal.http.ProgressRequestBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody

internal class StreamFeedUploader(
    private val cdnApi: CdnApi
) : FeedUploader {
    override suspend fun upload(
        payload: FeedUploadPayload,
        progress: ((Double) -> Unit)?
    ): Result<UploadedFile> = runSafely {
        val requestBody = payload.file
            .asRequestBody(payload.file.getMediaType())
            .withProgressListener(progress)
        val part = MultipartBody.Part.createFormData(
            name = "file",
            filename = payload.file.name,
            body = requestBody
        )

        val response = if (payload.type.isImage) cdnApi.sendImage(part) else cdnApi.sendFile(part)

        UploadedFile(
            fileUrl = response.file ?: throw IllegalStateException("Uploaded file URL is null"),
            thumbnailUrl = response.thumbUrl
        )
    }

    private fun RequestBody.withProgressListener(progress: ((Double) -> Unit)?): RequestBody =
        if (progress == null) {
            this
        } else {
            ProgressRequestBody(this) { uploaded, total ->
                progress(uploaded.toDouble() / total)
            }
        }
}
