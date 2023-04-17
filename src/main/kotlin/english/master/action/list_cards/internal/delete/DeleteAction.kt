package english.master.action.list_cards.internal.delete

import english.master.action.Action
import english.master.db.repo.CardRepo
import english.master.domain.UpdateWrapper
import english.master.util.CacheService
import english.master.util.equalsIgnoreCase

class DeleteAction : Action(waitForResponse = false) {
    private val cardRepo = CardRepo()

    override fun process(update: UpdateWrapper): Any {
        val txt = update.text
        if ("Yes".equalsIgnoreCase(txt)) {
            cardRepo.delete(CacheService.getCard(update.userId).id!!)
            return sendMessage(update, "Deleted")
        }
        return sendMessage(update, "Not deleted")
    }
}
