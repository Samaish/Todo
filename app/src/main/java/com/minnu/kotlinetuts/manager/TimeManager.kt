package com.minnu.kotlinetuts.manager

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.minnu.kotlinetuts.R
import com.minnu.kotlinetuts.activities.MainActivity
import com.minnu.kotlinetuts.constant.Constants
import com.minnu.kotlinetuts.constant.Constants.Companion.CANT_INSERT_EMPTY_DATA
import com.minnu.kotlinetuts.constant.Constants.Companion.EMPTY
import com.minnu.kotlinetuts.constant.Constants.Companion.SELECT_DATA
import com.minnu.kotlinetuts.constant.Constants.Companion.SELECT_TIME
import com.minnu.kotlinetuts.database.DatabaseHandler
import com.minnu.kotlinetuts.listeners.Completion
import com.minnu.kotlinetuts.log.L.Companion.toast
import com.minnu.kotlinetuts.manager.AlarmManager.Companion.scheduleAlarm
import com.minnu.kotlinetuts.model.Tasks
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TimeManager {
    companion object {

        private var dateTv: TextView? = null
        private var timeTv: TextView? = null

        fun leftTime(milliseconds: Long): String {

            val days = TimeUnit.MILLISECONDS.toDays(milliseconds).toInt()
            val hours = (TimeUnit.MILLISECONDS.toHours(milliseconds) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds))).toInt()
            val minutes = (TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds))).toInt()
            val seconds = (TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))).toInt()

            return if (days > 0)
                "$days day, $hours hour, $minutes min, $seconds sec"
            else if (days == 0 && hours > 0)
                "$hours hour, $minutes min, $seconds sec"
            else if (days == 0 && hours == 0 && minutes > 0)
                "$minutes min, $seconds sec"
            else if (days == 0 && hours == 0 && minutes == 0 && seconds > 0)
                "$seconds sec"
            else if (milliseconds > 0)
                "$milliseconds milliseconds"
            else
                "time is less"
        }

        private fun selectedTimeToMilliseconds(dateTime: String): Long {

            var sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val oldDate = sdf.parse(getDateTime1())
            var newDate = sdf.parse(dateTime)
            return newDate.time - oldDate.time
        }

        private fun getDateTime1(): String {
/*
            val c = Calendar.getInstance()

//            val year = c.get(Calendar.YEAR)
//            val month = c.get(Calendar.MONTH)
//            val day = c.get(Calendar.DAY_OF_MONTH)
//            val hourOfDay = c.get(Calendar.HOUR_OF_DAY) // 24 hour clock
//            val minute = c.get(Calendar.MINUTE)
//            val second = c.get(Calendar.SECOND)
*/
            return "${Calendar.getInstance().get(Calendar.YEAR)}/" +
                    "${Calendar.getInstance().get(Calendar.MONTH)}/" +
                    "${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)} " +
                    "${Calendar.getInstance().get(Calendar.HOUR_OF_DAY)}:" +
                    "${Calendar.getInstance().get(Calendar.MINUTE)}" + ":" +
                    "${Calendar.getInstance().get(Calendar.SECOND)}"
        }

/*
//        fun amPm(time: String): String {
//            var amPm = ""
//            //Displaying given time in 12 hour format with AM/PM
//            //old format
//            val sdf = SimpleDateFormat("HH:mm")
//            try {
//                val date3 = sdf.parse(time)
//                //new format
//                val sdf2 = SimpleDateFormat("hh:mm aa")
//                //formatting the given time to new format with AM/PM
//                //System.out.println("Given time in AM/PM: " + sdf2.format(date3))
//                Log.d("ADAPTER", "TIME IN AM/PM = " + sdf2.format(date3))
//
//                amPm = sdf2.format(date3)
//
//            } catch (e: ParseException) {
//                e.printStackTrace()
//                Log.d("ADAPTER", "TIME IN AM/PM = " + e.message)
//
//            }
//            return amPm
//        }
*/

        fun formatMillisecondToTime(millisecond: Long): String? {
            val sdf = SimpleDateFormat("MMM dd,yyyy HH:mm")
            val date = Date(millisecond)
            return sdf.format(date)
        }

        @SuppressLint("InflateParams")
        fun add(context: Context, addOrUpdate: String, tasks: Tasks?, db: DatabaseHandler, completion: Completion) {
            // Inflates the dialog with custom view
            val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_add_task, null)
            val etTask = dialogView.findViewById(R.id.et_task) as EditText
            val dateIv = dialogView.findViewById(R.id.dateImageView) as ImageView
            val timeIv = dialogView.findViewById(R.id.timeImageView) as ImageView
            dateTv = dialogView.findViewById(R.id.dateTextView) as TextView
            timeTv = dialogView.findViewById(R.id.timeTextView) as TextView

            val cancelBtn = dialogView.findViewById(R.id.cancelBtn) as AppCompatButton
            val scheduleBtn = dialogView.findViewById(R.id.scheduleBtn) as AppCompatButton

            scheduleBtn.text = addOrUpdate

            if (tasks!!.task.isNotEmpty()) {
                etTask.setText(tasks.task)
                dateTv?.text = tasks.taskDate
                timeTv?.text = tasks.taskTime

                if (dateTv?.text!!.isNotEmpty())
                    dateTv?.visibility = View.VISIBLE
                if (timeTv?.text!!.isNotEmpty())
                    timeTv?.visibility = View.VISIBLE
            }

            dateIv.setOnClickListener {
                showDate(context, dateTv!!)
            }
            timeIv.setOnClickListener {
                showTime(context, timeTv!!)
            }

            val builder = AlertDialog.Builder(context)
                    .setView(dialogView)
                    .setTitle(Constants.ADD_YOUR_TASK)
                    .setMessage(Constants.PLEASE_SCHEDULE_YOUR_TASK)
                    .setIcon(R.drawable.icon)

            val dialog = builder.show()

            scheduleBtn.setOnClickListener {

                if (etTask.text != null && etTask.text.isEmpty()) {
                    etTask.error = EMPTY
                    toast(context, CANT_INSERT_EMPTY_DATA)
                    return@setOnClickListener
                }
                if (dateTv!!.text.isEmpty()) {
                    toast(context, SELECT_DATA)
                    return@setOnClickListener
                }
                if (timeTv!!.text.isEmpty()) {
                    toast(context, SELECT_TIME)
                    return@setOnClickListener
                }

                tasks.task = etTask.text.toString()
                tasks.taskDate = dateTv!!.text.toString()
                tasks.taskTime = timeTv!!.text.toString()
                tasks.taskStartTime = System.currentTimeMillis()
                tasks.taskRemainingTime = selectedTimeToMilliseconds(selectedTimeToMilliseconds())
                tasks.taskCurrentTimeToEndTime = tasks.taskStartTime + tasks.taskRemainingTime

                if (tasks.taskRemainingTime <= 0) {
                    toast(context, Constants.TIME_MUST_BE_GREATER_THAN_CURRENT_TIME)
                    return@setOnClickListener
                }

                if (addOrUpdate == "Schedule") {
                    db.insertTaskToSQLiteDb(tasks, object : Completion {
                        override fun onComplete(index: Int) {
                            tasks.id = index
                            scheduleAlarm(MainActivity.context!!, tasks, object : Completion {
                                override fun onComplete(index: Int) {
                                    completion.onComplete(index)
                                }
                            })
                        }
                    })
                } else if (addOrUpdate == "Update") {

                    db.updateData(tasks, object : Completion {
                        override fun onComplete(index: Int) {
                            completion.onComplete(index)
                            if (index > 0) {
                                toast(context, "Updated")
                                scheduleAlarm(context!!, tasks, object : Completion {
                                    override fun onComplete(index: Int) {
                                        completion.onComplete(index)
                                    }
                                })
                            } else
                                toast(context, "Can't Update, error occurred")
                            dialog.dismiss()
                        }
                    })
                }
                dialog.dismiss()
            }

            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
        }

        private fun selectedTimeToMilliseconds(): String {
            return dateTv?.text.toString() + " " + timeTv?.text.toString()
        }

        private fun showDate(context: Context, dateTv: TextView) {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                /*         //            var monthParse = SimpleDateFormat("MM")
             //            var monthDisplay = SimpleDateFormat("MMM")
             //            dateTv.text = "$dayOfMonth-${monthDisplay.format(monthParse.parse((monthOfYear + 1).toString()))}-$year"
             //        var taskTime = "2018-09-8 13:50:10";
             */
                dateTv.text = "$year/$monthOfYear/$dayOfMonth"

                if (dateTv.text.isNotEmpty())
                    dateTv.visibility = View.VISIBLE
            }, year, month, day)
            dpd.show()
        }

        private fun showTime(context: Context, timeTv: TextView) {

            val cal = Calendar.getInstance()

            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.hour = hour
                    timePicker.minute = minute
                } else {
                    timePicker.currentHour = hour
                    timePicker.currentMinute = minute
                }
                timeTv.text = "$hour:$minute:0"
                if (timeTv.text.isNotEmpty())
                    timeTv.visibility = View.VISIBLE
            }
            TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }
    }
}