package io.getstream.feeds.android.client.api.model

//import io.getstream.android.push.PushDeviceGenerator

/**
 * Configuration class for push notifications in the Stream Feeds Android client.
 *
 * This class holds the configuration needed to set up push notifications for the Stream Feeds
 * client, including the list of push device generators that will be used to generate push tokens
 * for different push notification providers (FCM, HMS, etc.).
 *
 * @param pushDeviceGenerators List of [PushDeviceGenerator] instances that will be used to generate
 * push tokens for different push notification providers. Defaults to an empty list if not specified.
 */
public class PushNotificationsConfig(
    public val pushDeviceGenerators: List<String> = emptyList() // TODO: Rework this
)
