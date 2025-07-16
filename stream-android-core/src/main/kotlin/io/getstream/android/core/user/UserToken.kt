package io.getstream.android.core.user

/**
 * Represents a user JWT token.
 *
 * @param rawValue The raw string value of the user token.
 */
public data class UserToken(public val rawValue: String) {

    public companion object {

        /**
         * Represents an empty user token.
         */
        public val EMPTY: UserToken = UserToken(rawValue = "")
    }
}

