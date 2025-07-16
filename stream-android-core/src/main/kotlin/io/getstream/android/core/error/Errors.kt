package io.getstream.android.core.error


/**
 * List of error codes that indicate an invalid token error.
 *
 * - 40: Authentication Token Expired
 * - 41: Authentication Token Not Valid Yet
 * - 42: Authentication Token Before Issued At
 */
internal val TokenInvalidErrorCodes = listOf(40, 41, 42)

/**
 * Determines if this [APIError] represents a token invalid error.
 * 
 * @return `true` if the error code is one of the token invalid error codes (40, 41, 42), `false` otherwise.
 */
public val APIError.isTokenInvalidErrorCode: Boolean
    get() = code in TokenInvalidErrorCodes

/**
 * Determines if this [APIError] represents a client error.
 * 
 * Client errors are HTTP status codes in the 400-499 range, indicating that the request 
 * contains bad syntax or cannot be fulfilled by the server due to client-side issues.
 * 
 * @return `true` if the error code is in the 400-499 range, `false` otherwise.
 */
public val APIError.isClientError: Boolean
    get() = code in 400..499