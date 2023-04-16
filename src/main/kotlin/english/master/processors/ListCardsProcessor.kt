package english.master.processors

import english.master.action.list_cards.HandleActionAction
import english.master.action.list_cards.PickACardMessageAction
import english.master.action.list_cards.SendChosenCardWithAvailableActionsAction
import english.master.action.list_cards.SendListOfCards

class ListCardsProcessor : FlowProcessor() {
    init {
        val action1 = SendListOfCards()
        val action2 = PickACardMessageAction()
        val action3 = SendChosenCardWithAvailableActionsAction()
        val action4 = HandleActionAction()

        active = action1
        action1.next = action2
        action2.next = action3
        action3.next = action4
    }
}