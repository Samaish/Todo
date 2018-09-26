package com.minnu.kotlinetuts.manager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.minnu.kotlinetuts.listeners.Completion
import com.minnu.kotlinetuts.model.Tasks
import com.minnu.kotlinetuts.receiver.AlarmBroadcastReceiver

class AlarmManager {
    companion object {

        fun scheduleAlarm(context: Context, tasks: Tasks, completion: Completion) {
            var alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val myIntent = Intent(context, AlarmBroadcastReceiver::class.java)
            myIntent.putExtra("id", tasks.id)
            myIntent.putExtra("task", tasks.task)

            var pendingIntent = Intent(context, AlarmBroadcastReceiver::class.java).let {
                PendingIntent.getBroadcast(context, tasks.id, myIntent, PendingIntent.FLAG_ONE_SHOT)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmMgr!!.setExact(AlarmManager.RTC_WAKEUP, tasks.taskCurrentTimeToEndTime, pendingIntent)
            }
            completion.onComplete(tasks.id)
        }

        fun cancelAlarm(context: Context, id: Int) {
            var alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            var pendingIntent = Intent(context, AlarmBroadcastReceiver::class.java).let {
                PendingIntent.getBroadcast(context, id, Intent(context, AlarmBroadcastReceiver::class.java), PendingIntent.FLAG_ONE_SHOT)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                alarmMgr!!.setExact(AlarmManager.RTC_WAKEUP, (500 - System.currentTimeMillis()), pendingIntent)
        }
    }
}