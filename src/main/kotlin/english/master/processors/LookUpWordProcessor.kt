package english.master.processors

import english.master.action.common.EditCardDescriptionIfNeededAction
import english.master.action.common.RequestWordAction
import english.master.action.look_up.SendCardMenuAction
import english.master.action.look_up.SendDefinitionsAction
import english.master.action.look_up.SendPhotoWithChosenDefinitionAction
import english.master.action.look_up.ValidateAndLookUpWordAction

class LookUpWordProcessor(
    action1: RequestWordAction = RequestWordAction(),
    action2: ValidateAndLookUpWordAction = ValidateAndLookUpWordAction(),
    action3: SendDefinitionsAction = SendDefinitionsAction(),
    action4: SendPhotoWithChosenDefinitionAction = SendPhotoWithChosenDefinitionAction(),
    action5: SendCardMenuAction = SendCardMenuAction(),
    action6: EditCardDescriptionIfNeededAction = EditCardDescriptionIfNeededAction(),
) : FlowProcessor(action1) {

    init {
        active = action1
        action1.next = action2
        action2.next = action3

        action3.previous = action2
        action3.next = action4
        action4.next = action5
        action5.next = action6
    }
}
