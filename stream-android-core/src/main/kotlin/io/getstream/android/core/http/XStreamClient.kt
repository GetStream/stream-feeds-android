package io.getstream.android.core.http

import android.content.Context
import android.os.Build
import io.getstream.android.core.result.runSafely
import io.getstream.kotlin.base.annotation.marker.StreamInternalApi

/**
 * Utility class for creating the `X-Stream-Client` header value.
 *
 * This class generates a standardized client identification string that includes
 * information about the product, operating system, device, and application.
 * The generated string is used in HTTP headers to identify the client making
 * requests to Stream services.
 *
 * The format of the generated string is:
 * ```
 * {product}-{version}|os={os}|api_version={api}|device_model={device}|app={app}|app_version={version}
 * ```
 *
 * Example output:
 * ```
 * stream-feeds-android-1.0.0|os=Android 14|api_version=34|device_model=Google Pixel 7|app=MyApp|app_version=2.1.0
 * ```
 */
@StreamInternalApi
public class XStreamClient private constructor(public val value: String) {

    public companion object {

        /**
         * Generates a standardized client identification string for the X-Stream-Client header.
         *
         * This method collects various pieces of information about the client environment
         * and formats them into a pipe-separated string that can be used to identify
         * the client in HTTP requests to Stream services.
         *
         * @param context The Android context used to retrieve application information
         * @param product The name of the Stream product (e.g., "stream-feeds-android")
         * @param productVersion The version of the Stream product
         * @param app Optional custom application name. If null, the app name will be
         *            automatically retrieved from the application manifest
         * @param appVersion Optional custom application version. If null, the app version
         *                   will be automatically retrieved from the package information
         *
         * @return A formatted string containing client identification information in the format:
         *         `{product}-{version}|os={os}|api_version={api}|device_model={device}|app={app}|app_version={version}`
         *
         * @see getAppName For automatic app name retrieval
         * @see getAppVersion For automatic app version retrieval
         */
        @StreamInternalApi
        public fun create(
            context: Context,
            product: String,
            productVersion: String,
            app: String? = null,
            appVersion: String? = null,
        ): XStreamClient {
            val productIdentifier = "${product}-${productVersion}"
            val os = "Android ${Build.VERSION.RELEASE}"
            val apiVersion = Build.VERSION.SDK_INT
            val deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
            val value = buildString {
                append(productIdentifier)
                append("|os=$os")
                append("|api_version=$apiVersion")
                append("|device_model=$deviceModel")
                append("|app=${app ?: getAppName(context)}")
                append("|app_version=${appVersion ?: getAppVersion(context)}")
            }
            return XStreamClient(value)
        }

        /**
         * Retrieves the application's name as displayed in the launcher.
         *
         * - If the application label is not available, it falls back to `nonLocalizedLabel`.
         * - If both are unavailable, it returns `"UnknownApp"`.
         *
         * @return The application name or `"UnknownApp"` if retrieval fails.
         */
        private fun getAppName(context: Context): String {
            val applicationInfo = context.applicationInfo
            val stringId = applicationInfo.labelRes

            return if (stringId != 0) {
                context.getString(stringId)
            } else {
                applicationInfo.nonLocalizedLabel?.toString() ?: "UnknownApp"
            }
        }

        /**
         * Retrieves the version name of the application.
         *
         * @return The version name (e.g., "1.2.3") or `"nameNotFound"` if retrieval fails.
         */
        private fun getAppVersion(context: Context): String {
            return runSafely {
                context.packageManager
                    ?.getPackageInfo(context.packageName ?: return@runSafely null, 0)
                    ?.versionName
            }.getOrNull() ?: "nameNotFound"
        }
    }
}
