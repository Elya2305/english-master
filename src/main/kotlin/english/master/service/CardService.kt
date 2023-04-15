package english.master.service

import english.master.db.CardRecord
import english.master.db.repo.CardRepo
import english.master.util.CacheService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CardService {
    private val cardRepo = CardRepo()

    fun saveCardAsync(cardRecord: CardRecord) {
        GlobalScope.launch {
            val card = cardRepo.insert(cardRecord)
            CacheService.putCard(cardRecord.userId, card)
        }
    }
}