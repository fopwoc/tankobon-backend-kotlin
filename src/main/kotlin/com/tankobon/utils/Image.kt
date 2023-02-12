package com.tankobon.utils

import com.sksamuel.scrimage.ImageParseException
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.JpegWriter
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

private const val THUMBNAIL_HEIGHT = 340
private const val JPEG_COMPRESSION_FACTOR = 50

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
    val log = logger("fs-thumbnail")
    log.trace("${originalPath.path} ${thumbnailPath.path}")

    try {
        ImmutableImage.loader()
            .fromFile(originalPath)
            .scaleToHeight(THUMBNAIL_HEIGHT)
            .output(
                JpegWriter().withCompression(JPEG_COMPRESSION_FACTOR),
                File("${thumbnailPath.path}/${originalPath.nameWithoutExtension}.jpg")
            )
    } catch (e: ImageParseException) {
        log.debug("cant parse ${originalPath.path}")
        originalPath.delete()
    }
}
