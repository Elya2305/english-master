package english.master.util

import english.master.client.WordDefinition
import english.master.db.CardRecord
import english.master.domain.Definitions


// todo replace with normal abstract cache (this is just for testing purposes)
// todo (2) maybe we don't need it at all
object CacheService {
    private const val MESSAGE_ID = "MESSAGE_ID"
    private const val CARD = "CARD_ID"
    private const val CARDS = "CARDS"
    private const val DEFINITIONS = "DEFINITIONS_ID"
    private const val CHOSEN_DEFINITIONS = "CHOSEN_DEFINITIONS"
    private val cache: HashMap<String, Any> = HashMap()

    fun putMessageId(userId: Long, messageId: Int, identifier: String = "") {
        cache["$MESSAGE_ID#$identifier#$userId"] = messageId
    }

    fun getMessageId(userId: Long, identifier: String = ""): Int? {
        return cache["$MESSAGE_ID#$identifier#$userId"] as Int?
    }

    fun putCard(userId: Long, card: CardRecord) {
        cache["$CARD#$userId"] = card
    }

    fun getCard(userId: Long): CardRecord {
        return cache["$CARD#$userId"] as CardRecord
    }

    fun putDefinitions(userId: Long, definitions: Definitions) {
        cache["$DEFINITIONS#$userId"] = definitions
    }

    fun getDefinitions(userId: Long): Definitions? {
        return cache["$DEFINITIONS#$userId"] as Definitions?
    }

    fun putChosenDefinitions(userId: Long, definitions: ArrayList<WordDefinition>) {
        cache["$CHOSEN_DEFINITIONS#$userId"] = definitions
    }

    fun getChosenDefinitions(userId: Long): ArrayList<WordDefinition> {
        return cache["$CHOSEN_DEFINITIONS#$userId"] as ArrayList<WordDefinition>
    }

    fun putCards(userId: Long, cards: List<CardRecord>) {
        cache["$CARDS#$userId"] = cards
    }

    fun getCards(userId: Long): List<CardRecord>? {
        return cache["$CARDS#$userId"] as List<CardRecord>?
    }

    fun cleanCache(userId: Long) {
        cache.keys.filter { it.contains(userId.toString()) }
            .forEach { cache.remove(it) }
    }
}