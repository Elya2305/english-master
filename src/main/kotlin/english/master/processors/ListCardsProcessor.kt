package english.master.processors

import english.master.action.list_cards.*

class ListCardsProcessor(
    action1: SendListOfCards = SendListOfCards(),
    action2: PickACardMessageAction = PickACardMessageAction(),
    action3: ValidateIndexAndSaveCardAction = ValidateIndexAndSaveCardAction(),
    action4: SendChosenCardWithAvailableActionsAction = SendChosenCardWithAvailableActionsAction(),
    action5: HandleActionAction = HandleActionAction(),
) : FlowProcessor(action1) {

    init {
        active = action1
        action1.next = action2
        action2.next = action3
        action3.next = action4
        action4.next = action5
    }
}