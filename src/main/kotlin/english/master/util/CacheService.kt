package english.master.util

import english.master.client.WordDefinition
import english.master.db.CardRecord


// todo replace with normal abstract cache
object CacheService {
    private val MESSAGE_ID = "MESSAGE_ID"
    private val CARD = "CARD_ID"
    private val CARDS = "CARDS"
    private val DEFINITIONS = "DEFINITIONS_ID"
    private val CHOSEN_DEFINITIONS = "CHOSEN_DEFINITIONS"
    private val cache: HashMap<String, Any> = HashMap()

    fun putMessageId(userId: Long, messageId: Int, identifier: String = "") {
        cache["$MESSAGE_ID#$identifier#$userId"] = messageId
    }

    fun getMessageId(userId: Long, identifier: String = ""): Int {
        return cache["$MESSAGE_ID#$identifier#$userId"] as Int
    }

    fun putCard(userId: Long, card: CardRecord) {
        cache["$CARD#$userId"] = card
    }

    fun getCard(userId: Long): CardRecord {
        return cache["$CARD#$userId"] as CardRecord
    }

    fun putDefinitions(userId: Long, definitions: List<WordDefinition>) {
        cache["$DEFINITIONS#$userId"] = definitions
    }

    fun getDefinitions(userId: Long): List<WordDefinition>? {
        return cache["$DEFINITIONS#$userId"] as List<WordDefinition>?
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