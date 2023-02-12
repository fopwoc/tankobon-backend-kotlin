package com.tankobon.domain.library

import com.tankobon.domain.providers.MangaServiceProvider
import com.tankobon.utils.injectLogger
import com.tankobon.utils.msToMinutes
import com.tankobon.utils.msToRemainderMs
import com.tankobon.utils.msToRemainderSeconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.UUID
import kotlin.system.measureTimeMillis

private const val TASK_DELAY = 1000L
private const val TASK_DEBOUNCE = 1000L * 5

enum class TaskState { WAITING, ONGOING, DONE, }
data class Task(
    val file: File,
    val uuid: UUID,
    val state: TaskState,
    val lastUpdate: Long,
)

class TaskQueue {
    companion object {
        val log by injectLogger()
    }

    private val queue = mutableListOf<Task>()
    private var keepWorking = false

    fun getCount(): Int {
        return queue.count()
    }

    fun submit(newTask: Task) {
        log.trace("submit new task $newTask")

        val currentQueue = queue

        val oldTask = currentQueue.firstOrNull {
            it.uuid == newTask.uuid || it.file.nameWithoutExtension == newTask.file.nameWithoutExtension
        }

        val updateTask = oldTask?.let { newTask.copy(uuid = it.uuid) }

        log.trace("previous task was $oldTask")
        log.trace("update task is $updateTask")

        if (oldTask != null && updateTask != null) {
            when (updateTask.state) {
                TaskState.WAITING -> {
                    if (oldTask.state == TaskState.WAITING) {
                        queue[queue.indexOf(oldTask)] = updateTask
                    }
                }

                TaskState.ONGOING -> {
                    if (oldTask.state == TaskState.WAITING) {
                        queue[queue.indexOf(oldTask)] = updateTask
                    }
                }

                TaskState.DONE -> {
                    if (oldTask.state != TaskState.DONE) {
                        queue[queue.indexOf(oldTask)] = updateTask
                    }
                }
            }
        } else {
            log.debug("NEW EVENT $newTask")
            queue.add(newTask)
        }
    }

    private fun runTask(task: Task) {
        submit(task.copy(state = TaskState.ONGOING, lastUpdate = System.currentTimeMillis()))
        val time = measureTimeMillis {
            val result = titleCalculate(task)
            log.debug("result for ${task.uuid} is ${result.volume.map { it.content.size }}")
            log.trace("trace result for ${task.uuid} is $result")
            runBlocking { MangaServiceProvider.get().updateMangaLibrary(result) }
        }

        log.debug(
            "task ${task.uuid} done. Time estimated " +
                "${msToMinutes(time)}:${msToRemainderSeconds(time)}:${msToRemainderMs(time)}"
        )
        submit(task.copy(state = TaskState.DONE, lastUpdate = System.currentTimeMillis()))
    }

    private fun getTask(): Task? {
        val currentQueue = queue
        val newTask = currentQueue
            .filter { it.state == TaskState.WAITING }
            .sortedWith(compareBy { it.lastUpdate })
            .firstOrNull()

        if (newTask != null && newTask.lastUpdate < System.currentTimeMillis()) runTask(newTask)
        return null
    }

    suspend fun runQueue() {
        keepWorking = true
        while (keepWorking) {
            delay(TASK_DELAY)
            log.trace("queue $queue")
            if (queue.isNotEmpty()) {
                queue.filter {
                    it.state == TaskState.DONE && it.lastUpdate + (TASK_DEBOUNCE) < System.currentTimeMillis()
                }.forEach { queue.remove(it) }

                getTask()
            }
        }
    }

    // TODO SIGKILL support
    fun stopQueue() {
        keepWorking = false
    }
}
