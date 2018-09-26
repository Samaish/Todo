package com.minnu.kotlinetuts.manager

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.minnu.kotlinetuts.R
import com.minnu.kotlinetuts.activities.MainActivity

class NotificationManager {

    companion object {
        private lateinit var builder: Notification.Builder
        var mManager: NotificationManager? = null
        fun notifyMe(context: Context, id: Int, task: String) {

            mManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val intent1 = Intent(context, MainActivity::class.java)
            intent1.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            val pendingNotificationIntent = PendingIntent.getActivity(context, id, intent1, PendingIntent.FLAG_ONE_SHOT)

            builder = Notification.Builder(context)
            builder.setAutoCancel(true)
            builder.setSmallIcon(R.drawable.icon)
            builder.setContentTitle("To Do Remainder")
            builder.setContentText(task)
            builder.setContentIntent(pendingNotificationIntent)
            builder.build()
            val myNotication = builder.notification
            mManager!!.notify(id, myNotication)
        }
    }
}
