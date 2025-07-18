package io.getstream.feeds.android.client.api.model

/**
 * Configuration class for the Stream Feeds Android client.
 *
 * This class contains all the configuration options needed to customize the behavior
 * of the Stream Feeds client, including push notifications settings.
 *
 * @param pushNotificationsConfig Configuration for push notifications behavior.
 * Defaults to [PushNotificationsConfig] with default settings.
 */
public class FeedsConfig(
    public val pushNotificationsConfig: PushNotificationsConfig = PushNotificationsConfig(),
)