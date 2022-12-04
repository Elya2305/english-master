package english.master.util

import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.imageio.ImageIO

object PngGenerator {
    fun generateWordCard(word: String): ByteArray {
        try {
            val width = 200
            val height = 200

            val bi = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            val ig2 = bi.createGraphics()
            ig2.font = Font("TimesRoman", Font.BOLD, 20)
            val fontMetrics = ig2.fontMetrics
            val stringWidth = fontMetrics.stringWidth(word)
            val stringHeight = fontMetrics.ascent
            ig2.paint = Color.black
            ig2.drawString(word, (width - stringWidth) / 2, height / 2 + stringHeight / 4)

            val baos = ByteArrayOutputStream()
            ImageIO.write(bi, "png", baos)
            return baos.toByteArray()
        } catch (ie: IOException) {
            ie.printStackTrace()
            throw ie
        }
    }
}