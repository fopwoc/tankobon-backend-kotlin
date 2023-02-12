package com.tankobon.domain.providers

import com.tankobon.domain.library.TaskQueue

class TaskQueueProvider private constructor() {
    companion object {
        private val instance: TaskQueue by lazy {
            TaskQueue()
        }

        fun get(): TaskQueue {
            return instance
        }
    }
}
