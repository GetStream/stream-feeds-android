package io.getstream.android.core.user

/**
 * Represents a user in the Stream system.
 *
 * @property id The unique identifier for the user.
 * @property name The name of the user (optional).
 * @property imageURL The URL of the user's image (optional).
 * @property role The role of the user (default "user").
 * @property type The type of authentication used by the user (default [UserAuthType.REGULAR]).
 * @property customData Custom data associated with the user, represented as a map (default empty
 * map).
 */
public data class User(
    public val id: String,
    public val name: String? = null,
    public val imageURL: String? = null,
    public val role: String = "user",
    public val type: UserAuthType = UserAuthType.REGULAR,
    public val customData: Map<String, Any> = emptyMap(),
) {

    public companion object {

        /**
         * Creates a guest user with the provided ID.
         *
         * @param userId The ID of the user.
         * @return a guest [User].
         */
        public fun guest(userId: String): User =
            User(id = userId, name = userId, type = UserAuthType.GUEST)

        /**
         * Creates an anonymous user.
         *
         * @return an anonymous user.
         */
        public fun anonymous(): User =
            User(id = "!anon", type = UserAuthType.ANONYMOUS)
    }
}

/**
 * Represents the type of user authentication.
 */
public enum class UserAuthType {
    REGULAR,
    ANONYMOUS,
    GUEST;
}
