package com.lemonlab.all_in_one

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject


enum class NotificationType {
    Likes, Comment
}


class NotificationSender {
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                context.getString(R.string.app_name),
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = context.getString(R.string.channelDescription)
            val notificationManager =
                context.getSystemService(NotificationManager::class.java)!!
            notificationManager.createNotificationChannel(channel)
        }


    }

    fun createNotification(
        context: Context, notificationType: NotificationType,
        title: String, content: String
    ) {

        val intent = Intent(context, MainActivity::class.java)

        intent.putExtra("type", notificationType)

        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val icon =
            R.drawable.ic_info // TODO set app icon here.

        val builder = NotificationCompat.Builder(context, context.getString(R.string.app_name))
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(100, builder)

    }


    fun notifyUserLikes(context: Context, userID: String, likes: Int) {
        val title = context.getString(R.string.newLikesTitle)
        val messageBody = context.getString(R.string.newLikesMessage, likes)

        sendNotification(context, title, messageBody, userID)

    }


    fun notifyUserComment(context: Context, userID: String) {

        val title = context.getString(R.string.newCommentTitle)
        val sender = FirebaseAuth.getInstance().currentUser?.displayName
            ?: context.getString(R.string.username)

        val messageBody = context.getString(R.string.newCommentMessage, sender)

        sendNotification(context, title, messageBody, userID)

    }

    private fun sendNotification(
        context: Context,
        title: String,
        messageBody: String,
        userID: String
    ) {
        // sends notification to user with specified userID

        // json object that'll be sent to server
        val json = JSONObject()

        val requestQueue = Volley.newRequestQueue(context)
        try {
            val notificationObj = JSONObject()
            notificationObj.put("title", title)
            notificationObj.put("body", messageBody)
            json.put("to", "/topics/$userID")
            json.put("data", notificationObj)
            val request = object : JsonObjectRequest(
                Method.POST, "https://fcm.googleapis.com/fcm/send",
                json,
                Response.Listener<JSONObject> { }, Response.ErrorListener { }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val header = HashMap<String, String>()
                    header["content-type"] = "application/json"
                    header["authorization"] = "key=AIzaSyC6Wgbrit4wwFIx3vzwqG4AdWMZ6D9Ao4s"
                    return header
                }
            }
            requestQueue.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}