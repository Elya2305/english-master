package english.master.processors

import english.master.action.view_cards.GenerateCardAction
import english.master.action.view_cards.RequestCardNumberAction

class ShowCardsProcessor : FlowProcessor() {

    init {
        val action1 = RequestCardNumberAction()
        val action2 = GenerateCardAction()

        active = action1
        action1.next = action2
    }
}
