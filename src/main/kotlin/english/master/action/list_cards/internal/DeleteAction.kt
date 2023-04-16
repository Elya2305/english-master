package english.master.action.list_cards.internal

import english.master.action.Action
import english.master.domain.UpdateWrapper
import english.master.util.equalsIgnoreCase

class DeleteAction : Action() {
    override fun process(update: UpdateWrapper): Any {
        val txt = update.text
        if ("Yes".equalsIgnoreCase(txt)) {
            return sendMessage(update, "Deleted")
        }
        return sendMessage(update, "Not deleted")
    }
}
