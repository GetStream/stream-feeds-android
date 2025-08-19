package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.api.file.FeedUploader

/**
 * Configuration class for the Stream Feeds Android client.
 * This class contains all the configuration options needed to customize the behavior
 * of the Stream Feeds client, such as the option for customizing the CDN.
 *
 * @param customUploader Optional [FeedUploader] implementation for overriding the default CDN.
 */
public class FeedsConfig(
    public val customUploader: FeedUploader? = null,
)
