package com.lemonlab.all_in_one

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        //  NotificationSender().createNotificationChannel(this)

        val title = p0.data["title"].toString()
        val message = p0.data["body"].toString()

        val context = applicationContext

        val type = if (title == context.getString(R.string.newLikesMessage))
            NotificationType.Likes
        else
            NotificationType.Comment

        NotificationSender().createNotification(
            context = context, notificationType = type,
            content = message, title = title
        )

        super.onMessageReceived(p0)

    }

}