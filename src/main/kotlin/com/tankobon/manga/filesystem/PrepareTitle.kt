package com.tankobon.manga.filesystem

//import com.tankobon.logger
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

fun prepareTitle(titleDir: File): List<Int> {
    //logger.info("Title preparation: ${titleDir.path}")

    runBlocking {
        coroutineScope {
            titleDir.listFiles()?.filter { it.isFile && !it.name.contains(".DS_Store") }
                ?.forEach {
                    print("prepareTitle archiveNavigator ${Thread.currentThread().name}")
                    withContext(Default) {
                        //fileNavigator(it)
                    }
                }
        }

        titleDir.listFiles()?.filter { it.isDirectory }
            ?.sortedBy { it.name.toString() }
            ?.forEachIndexed { i, e ->
                prepareVolume(
                    if (!Regex("^\\d*\$").matches(e.name)) {
                        val path = File("${e.parentFile.path}/$i")
                        e.renameTo(path)
                        path
                    } else e
                )
            }
    }

    return titleDir.listFiles()
        ?.filter { it.isDirectory }
        ?.sortedBy { it.name.toString() }
        ?.map { e ->
            e.listFiles()
                ?.count { i -> i.isFile && !e.name.contains(".DS_Store") }
                ?.minus(1) ?: -1
        }?.toList().orEmpty()
}
