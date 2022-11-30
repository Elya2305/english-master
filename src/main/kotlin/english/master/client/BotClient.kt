package english.master.client

class BotClient : AbstractClient() {
    private val token = "5747700946:AAG77HAGuhEl7UDQ-G4RfvMthAOrXY3okvs"
    private val url = "https://api.telegram.org/bot$token/"

    fun sendMessage(text: String, chatId: Long) {
        post(
            url + "sendMessage",
            mapOf("chat_id" to chatId, "text" to text),
            Any::class.java
        )
    }
}
