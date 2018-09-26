package com.minnu.kotlinetuts.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.minnu.kotlinetuts.database.DatabaseHandler
import com.minnu.kotlinetuts.manager.NotificationManager.Companion.notifyMe

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val b = intent!!.extras
        val id = b.getInt("id")
        val task = b.getString("task")

        if (task != null) {
            Toast.makeText(context, "Reminder!!\n$task", Toast.LENGTH_LONG).show()
            notifyMe(context!!, id, task)
            var db = DatabaseHandler(context)
            db.updateStatus(true, id)
        }
    }
}