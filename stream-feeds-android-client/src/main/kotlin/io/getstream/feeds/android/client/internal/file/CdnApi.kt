package io.getstream.feeds.android.client.internal.file

import io.getstream.feeds.android.core.generated.models.FileUploadResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

internal interface CdnApi {
    @Multipart
    @POST("/api/v2/uploads/file")
    suspend fun sendFile(@Part file: MultipartBody.Part): FileUploadResponse

    @Multipart
    @POST("/api/v2/uploads/image")
    suspend fun sendImage(@Part file: MultipartBody.Part): FileUploadResponse
}
