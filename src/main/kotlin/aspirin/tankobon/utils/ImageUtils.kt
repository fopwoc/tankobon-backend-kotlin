package aspirin.tankobon.utils

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.JpegWriter
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun imageConverter(file: File) {
    val originalBuffer: BufferedImage = ImageIO.read(file)
    val thumbnail = BufferedImage(originalBuffer.width, originalBuffer.height, BufferedImage.TYPE_INT_RGB)

    val g: Graphics2D = thumbnail.createGraphics()
    g.drawImage(originalBuffer, 0, 0, originalBuffer.width, originalBuffer.height, null)
    ImageIO.write(thumbnail, "JPG", File("${file.parentFile.path}/${file.nameWithoutExtension}.jpg"))
    g.dispose()

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
