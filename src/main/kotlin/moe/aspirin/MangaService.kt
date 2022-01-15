package moe.aspirin

import com.github.junrar.Junrar
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.name.Rename
import net.lingala.zip4j.ZipFile
import java.io.File
import java.nio.file.*

class MangaService {

    fun watchFolder() {
        val mangaPath = File("manga")
        mangaPath.mkdir()

        val thread = Thread {

            println("libraryPrepare")
            libraryPrepare()

            try {
                println("Watching directory for changes")
                val watchKey = Path.of(mangaPath.absolutePath)
                    .register(FileSystems.getDefault().newWatchService(),
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE
                )
                while (true) {
                    for (event in watchKey.pollEvents()) {
                        libraryPrepare(event.kind().name())
                    }

                    Thread.sleep(10000)
                    val valid = watchKey.reset()
                    if (!valid) {
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun libraryPrepare(trigger: String?) {
        println("libraryPrepare. Trigger: $trigger")
        libraryPrepare()
    }

    private fun libraryPrepare() {
        File("manga").listFiles()?.filter { e -> e.isDirectory }
            //?.also { e -> MangaDB().cleanMangaList(e) }
            ?.forEach { e -> MangaDB().createMangaList(e.name, titlePrepare(e)) }
        println("library prepare complete")
    }

    private fun titlePrepare(titleDir: File): String {
        println("titlePrepare ${titleDir.absolutePath}")

        titleDir.listFiles()?.filter { e -> e.isFile }?.forEach { e ->
            if (e.path.contains(Regex("zip|cbz"))) titleUnzip(e)
            if (e.path.contains(Regex("rar|cbr"))) titleUnrar(e)
        }
        File(titleDir.absolutePath).listFiles()?.filter { e -> e.isDirectory }?.forEach { e ->
            volumePrepare(e)
        }

        return Json.encodeToString(titleDir.listFiles()
            ?.filter { e -> e.isDirectory }
            ?.sortedBy { it.name.toString() }
            ?.map { e -> e.listFiles()?.count { i -> i.isFile && !e.name.contains(".DS_Store") }?.minus(1) }?.toList())
    }

    private fun volumePrepare(volumePath: File) {
        println("volumePrepare ${volumePath.absolutePath}, thumb gen")

        volumePath.listFiles()?.filter { e -> e.isDirectory }
            ?.forEach { e ->
                fileLevelRecursion(e, volumePath.absolutePath)
                e.delete()
            }

        val mangaThumbPath = File("data/thumb")
        mangaThumbPath.mkdir()

        val volumeThumb = File("${mangaThumbPath.path}/${volumePath.parentFile.name}/${volumePath.name}")
        volumeThumb.parentFile.mkdir()
        volumeThumb.mkdir()

        if (volumeThumb.listFiles().isNullOrEmpty()) volumePath.listFiles()
            ?.filter { e -> e.isFile && !e.name.contains(".DS_Store") }
            ?.map { e -> Thumbnails.of("${volumePath.absolutePath}/${e.name}")
                .size(245,340)
                .outputFormat("jpg")
                .toFiles(volumeThumb, Rename.PREFIX_DOT_THUMBNAIL) }
    }

    private fun fileLevelRecursion(file: File, origin: String) {
        file.listFiles()?.forEach { e ->
            if (e.isDirectory) {
                fileLevelRecursion(e, origin)
                e.delete()
            }
            if (e.isFile) {
                e.copyTo(File("$origin/${e.name}"), true)
                e.delete()
            }
        }
    }

    private fun titleUnzip(path: File) {
        println("unzip $path")
        ZipFile(path.path).extractAll("${path.parent}/${path.nameWithoutExtension}")
        path.delete()
    }

    private fun titleUnrar(path: File) {
        println("unrar $path")
        Junrar.extract(path.path, path.parent)
        path.delete()
    }

}


