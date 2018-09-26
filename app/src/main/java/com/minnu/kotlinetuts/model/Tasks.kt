package com.minnu.kotlinetuts.model

import com.minnu.kotlinetuts.log.L.Companion.l

class Tasks {
    companion object {
        var TAG = "Tasks"
        var arrayList: ArrayList<Tasks> = ArrayList()
    }

    var id = 0
    var task = ""
    var taskDate = ""
    var taskTime = ""
    var taskStartTime: Long = 0
    var taskCurrentTimeToEndTime: Long = 0
    var taskRemainingTime: Long = 0
    var taskStatus: Boolean = false

    fun print() {
        l(TAG, "\ntaskId: $id, \n" +
                "task: $task, \n" +
                "taskDate: $taskDate, \n" +
                "taskTime: $taskTime, \n" +
                "taskStartTime: $taskStartTime, \n" +
                "taskRemainingTime: $taskRemainingTime, \n" +
                "taskCurrentTimeToEndTime: $taskCurrentTimeToEndTime, \n" +
//                "taskEndTime: $taskEndTime, \n" +
                "taskStatus: $taskStatus \n " +
                "________________________________________________________________________________________________________________________________"
        )
    }
}