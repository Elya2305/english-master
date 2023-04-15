package english.master.processors

import english.master.action.common.EditCardDescriptionIfNeededAction
import english.master.action.common.RequestWordAction
import english.master.action.look_up.SendCardMenuAction
import english.master.action.look_up.SendDefinitionsAction
import english.master.action.look_up.SendPhotoWithChosenDefinitionAction
import english.master.action.look_up.ValidateAndLookUpWordAction


class LookUpWordProcessor : FlowProcessor() {

    init {
        val action1 = RequestWordAction()
        val action2 = ValidateAndLookUpWordAction()
        val action3 = SendDefinitionsAction()
        val action4 = SendPhotoWithChosenDefinitionAction()
        val action5 = SendCardMenuAction()
        val action6 = EditCardDescriptionIfNeededAction()

        active = action1
        action1.next = action2
        action2.next = action3

        action3.previous = action2
        action3.next = action4
        action4.next = action5
        action5.next = action6
    }
}
