package english.master.service

import english.master.client.ImageClient

class ImageService {
    private val imageClient = ImageClient()

    fun findWorkingImage(word: String): ByteArray? {
        val images = imageClient.getImages(word).results.shuffled()
        val shuffled = images.shuffled()
        for (img in shuffled) {
            val image = imageClient.downloadImage(img.urls.raw)
            if (image != null) {
                return image
            }
        }
        return null
    }
}