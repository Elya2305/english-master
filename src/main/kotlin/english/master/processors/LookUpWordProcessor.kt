package english.master.processors

import english.master.action.look_up.SendDefinitionsAction
import english.master.action.look_up.RequestWordAction
import english.master.action.look_up.ValidateAndLookUpWordAction


class LookUpWordProcessor : FlowProcessor() {

    init {
        val action1 = RequestWordAction()
        val action2 = ValidateAndLookUpWordAction()
        val action3 = SendDefinitionsAction()

        active = action1
        action1.next = action2
        action2.next = action3

        action3.previous = action2
    }
}
