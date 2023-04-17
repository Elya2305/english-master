package english.master.processors

import english.master.action.view_cards.GenerateCardAction
import english.master.action.view_cards.RequestCardNumberAction

class ShowCardsProcessor(
    action1: RequestCardNumberAction = RequestCardNumberAction(),
    action2: GenerateCardAction = GenerateCardAction(),
) : FlowProcessor(action1) {

    init {
        active = action1
        action1.next = action2
    }
}
