package english.master.action.list_cards.internal

import english.master.processors.FlowProcessor

class DeleteFlowProcessor: FlowProcessor() {
    init {
        val action1 = AreYouSureAction()
        val action2 = DeleteAction()

        active = action1
        action1.next = action2
    }
}
