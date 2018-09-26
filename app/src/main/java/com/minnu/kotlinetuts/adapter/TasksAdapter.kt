package com.minnu.kotlinetuts.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.minnu.kotlinetuts.R
import com.minnu.kotlinetuts.activities.MainActivity
import com.minnu.kotlinetuts.database.DatabaseHandler
import com.minnu.kotlinetuts.listeners.Completion
import com.minnu.kotlinetuts.log.L.Companion.toast
import com.minnu.kotlinetuts.manager.TimeManager.Companion.add
import com.minnu.kotlinetuts.manager.TimeManager.Companion.formatMillisecondToTime
import com.minnu.kotlinetuts.manager.TimeManager.Companion.leftTime
import com.minnu.kotlinetuts.model.Tasks
import kotlinx.android.synthetic.main.adapter_item.view.*
import java.util.*

class TasksAdapter(val context: Context, private var arrayList: ArrayList<Tasks>) : RecyclerView.Adapter<TasksAdapter.ViewHolder>() {

    private var db = DatabaseHandler(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_item, parent, false))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var t = arrayList[position]

        holder.taskTextView.text = t.task

        var _1 = formatMillisecondToTime(t.taskStartTime)!!.subSequence(12, 17).toString()
        var _2 = formatMillisecondToTime(t.taskCurrentTimeToEndTime)!!.subSequence(12, 17).toString()
        var _new = formatMillisecondToTime(t.taskStartTime)!!.subSequence(0, 11).toString()

        if (_1.substring(0, 1) == "0")
            _1 = _1.replaceFirst("0", "")

        if (_2.substring(0, 1) == "0")
            _2 = _2.replaceFirst("0", "")

        if (formatMillisecondToTime(t.taskStartTime)!!.subSequence(0, 12) == formatMillisecondToTime(t.taskCurrentTimeToEndTime)!!.subSequence(0, 12))
            holder.startTimeTextView.text = "$_new, $_1 - $_2"
        else
            holder.startTimeTextView.text = "${formatMillisecondToTime(t.taskStartTime)} - ${formatMillisecondToTime(t.taskCurrentTimeToEndTime)}"

        holder.remainingTimeTextView.text = "${leftTime(t.taskRemainingTime)} remaining"

        if (t.taskStatus)
            settingColorToViews(holder, R.color.lightGray)
        else
            settingColorToViews(holder, R.color.colorPrimaryDark)

        holder.itemView.setOnLongClickListener {
            deleteOrUpdate(t, position)
            true
        }
    }

    private fun settingColorToViews(holder: ViewHolder, colorId: Int) {
        holder.taskImageView.setBackgroundColor(ContextCompat.getColor(context, colorId))
        holder.alarmImageView.setColorFilter(ContextCompat.getColor(context, colorId))
        holder.taskTextView.setTextColor(ContextCompat.getColor(context, colorId))
        holder.remainingTimeTextView.setTextColor(ContextCompat.getColor(context, colorId))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // Holds the TextView that will add each animal to
        val taskImageView = view.taskImageView!!
        val alarmImageView = view.alarmImageView!!
        val taskTextView = view.taskTextView!!
        val startTimeTextView = view.startTimeTextView!!
        val remainingTimeTextView = view.remainingTimeTextView!!
    }

    private fun deleteOrUpdate(tasks: Tasks, position: Int) {
        // Initialize a new instance of
        val builder = AlertDialog.Builder(context)

        // Set the alert dialog title
        builder.setTitle("Delete Or Update")

        // Display a message on alert dialog
        builder.setMessage("Do you  want to delete or update?")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Update") { dialog, _ ->
            // Do something when user press the positive button
            //update(tasks, position)
            add(context, "Update", tasks, db, object : Completion {
                override fun onComplete(index: Int) {
                    notifyDataSetChanged()
                }
            })
            dialog.dismiss()
        }

        // Display a negative button on alert dialog
        builder.setNegativeButton("Delete") { _, _ ->
            db.deleteTaskFromSQLiteDb(tasks, object : Completion {
                override fun onComplete(pos: Int) {
                    if (pos > 0) {
//                        cancelAlarm(context, tasks.id)
                        toast(context, "Deleted")
                        arrayList.removeAt(position)
                        notifyDataSetChanged()
                        if (arrayList.size == 0) MainActivity.makeVisible()
                    }
                }
            })
        }

        // Display a neutral button on alert dialog
        builder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }
}