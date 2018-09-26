package com.minnu.kotlinetuts.listeners

import com.minnu.kotlinetuts.model.Tasks

interface Listener {
    fun onComplete(tasks: Tasks?, index: Int)
}

interface Completion {
    fun onComplete(index: Int)
}