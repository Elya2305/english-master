package english.master.client

import java.lang.System.getenv

class BotClient : AbstractClient() {
    private val token = getenv("BOT_TOKEN")
    private val url = "https://api.telegram.org/bot$token/"

    fun sendMessage(text: String, chatId: Long) {
        post(
            url + "sendMessage",
            mapOf("chat_id" to chatId, "text" to text),
            Any::class.java
        )
    }
}
