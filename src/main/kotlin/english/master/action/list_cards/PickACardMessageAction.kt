package english.master.action.list_cards

import english.master.action.Action
import english.master.domain.UpdateWrapper

class PickACardMessageAction : Action() {

    override fun process(update: UpdateWrapper): Any {
        return sendMessage(update, "Pick a card by providing a number to see an actions you can do with a card")
    }
}