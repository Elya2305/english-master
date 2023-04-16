package english.master.action.list_cards.internal

import english.master.action.Action
import english.master.domain.UpdateWrapper

class AreYouSureAction : Action() {
    override fun process(update: UpdateWrapper): Any {
        return sendMessage(update, "Are you sure you want to delete a card?") // TODO keyboard
    }
}