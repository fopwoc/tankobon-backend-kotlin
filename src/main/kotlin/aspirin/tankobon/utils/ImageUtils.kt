package aspirin.tankobon.utils

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.JpegWriter
import java.io.File

fun imageConverter(file: File) {
    ImmutableImage.loader()
        .fromFile(file)
        .output(
            JpegWriter().withCompression(100),
            File("${file.parentFile.path}/${file.nameWithoutExtension}.jpg")
        )
    file.delete()
}

fun thumbnailGenerator(originalPath: File, thumbnailPath: File) {
    ImmutableImage.loader()
        .fromFile(originalPath)
        .scaleToHeight(340)
        .output(
            JpegWriter().withCompression(50),
            File("${thumbnailPath.path}/${originalPath.nameWithoutExtension}.jpg")
        )
}

