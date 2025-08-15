package io.getstream.feeds.android.client.api.model

/**
 * Represents the supported push notifications providers.
 */
public enum class PushNotificationsProvider(public val value: String) {
    /** Firebase push notification provider */
    FIREBASE("firebase"),

    /** Huawei push notification provider */
    HUAWEI("huawei"),

    /** Xiaomi push notification provider */
    XIAOMI("xiaomi");
}
