package com.tankobon.manga.library

import com.tankobon.manga.library.filesystem.title
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File

enum class TaskState { WAITING, ONGOING, DONE, }
data class Task(val file: File, val state: TaskState, val startTime: Long)

class TaskQueue {
    companion object {
        val LOG = LoggerFactory.getLogger(TaskQueue::class.java) //дима сказал
    }

    private val taskQueue = mutableListOf<Task>()

    fun submit(task: Task) {

        val currentQueue = taskQueue
        val oldTask = currentQueue.firstOrNull { it.file == task.file }

        if (oldTask != null) {
            when (task.state) {
                TaskState.WAITING -> {
                    taskQueue[taskQueue.indexOf(oldTask)] = task
                }
                TaskState.ONGOING -> {
                    if (oldTask.state == TaskState.WAITING)
                        taskQueue[taskQueue.indexOf(oldTask)] = task
                }
                TaskState.DONE -> {
                    taskQueue.remove(oldTask)
                }
            }
        } else {
            taskQueue.add(task)
        }
    }

    private fun runTask(task: Task) {
        submit(task.copy(state = TaskState.ONGOING))
        title(task.file)
        submit(task.copy(state = TaskState.DONE))
    }

    private fun getTask():Task? {
        val currentQueue = taskQueue
        val newTask = currentQueue.filter { it.state == TaskState.WAITING }.sortedWith(compareBy { it.startTime }).firstOrNull()

        if ( newTask != null && newTask.startTime < System.currentTimeMillis()) runTask(newTask)
        return null
    }

     fun runQueue() {
         GlobalScope.launch {
            while (true) {
                delay(1000L)
                LOG.trace("queue $taskQueue")
                val newTask = getTask()

                if (newTask != null) runTask(newTask)
            }
        }
    }
}
