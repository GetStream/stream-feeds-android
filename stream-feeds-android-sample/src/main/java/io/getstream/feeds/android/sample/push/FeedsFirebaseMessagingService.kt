/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.getstream.feeds.android.sample.push

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.getstream.feeds.android.sample.MainActivity
import io.getstream.feeds.android.sample.R

/**
 * A Firebase Messaging Service to handle incoming push notifications for Stream Feeds.
 */
class FeedsFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "feeds_notifications"
        private const val CHANNEL_NAME = "Feeds Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for Stream Feeds"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Handle the new token
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Extract title and body from the remote message
        val title = message.data["title"] ?: "New Notification"
        val body = message.data["body"] ?: "You have a new message"

        // Show the notification
        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        // Only proceed if notification permission is granted or not required
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Create notification channel for Android 8.0 and higher
        createNotificationChannel()

        // Create an intent to open the main activity when notification is tapped
        val intent =
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        // Build the notification
        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .build()

        // Show the notification
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel
        val importance = NotificationManagerCompat.IMPORTANCE_HIGH
        val channel =
            NotificationChannelCompat.Builder(CHANNEL_ID, importance)
                .setName(CHANNEL_NAME)
                .setDescription(CHANNEL_DESCRIPTION)
                .setLightsEnabled(true)
                .setVibrationEnabled(true)
                .build()

        // Register the channel with the system
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
    }
}
