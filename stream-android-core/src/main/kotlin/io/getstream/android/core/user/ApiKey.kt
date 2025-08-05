package io.getstream.android.core.user

/**
 * Represents an API key used for authentication in the Stream system.
 * An API key can be obtained by registering on [our website](https://getstream.io/chat/trial/).
 *
 * @property value The string value of the API key.
 */
public data class ApiKey(public val value: String)
