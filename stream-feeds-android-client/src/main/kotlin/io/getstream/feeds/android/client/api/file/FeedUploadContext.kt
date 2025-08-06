package io.getstream.feeds.android.client.api.file

import io.getstream.feeds.android.client.api.model.FeedId

public open class FeedUploadContext(public val feedId: FeedId) : UploadContext

public typealias FeedUploadPayload = UploadPayload<FeedUploadContext>

public typealias FeedUploader = Uploader<FeedUploadContext>
