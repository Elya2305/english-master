package english.master.action.list_cards

import english.master.action.Action
import english.master.db.repo.CardRepo
import english.master.domain.UpdateWrapper
import english.master.util.CacheService

class ValidateIndexAndSaveCardAction : Action() {
    private val cardRepo = CardRepo()

    override fun process(update: UpdateWrapper): Any {
        return try {
            val index = Integer.parseInt(update.message!!.text).toLong()
            val card = cardRepo.findIndexedByCreatedAt(index, update.userId)
            CacheService.putCard(update.userId, card)
            proceedToNext()
        } catch (e: Exception) {
            repeatCurrent()
            sendMessage(update, "Please provide a valid number")
        }
    }
}
