package aspirin.tankobon.manga.archive

import com.github.junrar.Junrar
import java.io.File

fun unRAR(path: File) {
    val newPath = File("${path.parent}/${path.nameWithoutExtension}/0")
    newPath.mkdirs()
    Junrar.extract(path.path, newPath.path)
    path.delete()
}
