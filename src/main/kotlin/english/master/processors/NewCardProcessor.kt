package english.master.processors

import english.master.action.add_word_with_choice.RequestWordAction
import english.master.action.add_word_with_choice.SendCardMenuAction
import english.master.action.add_word_with_choice.SendPhotoWithEmptyPlaceholderAction
import english.master.action.add_word_with_choice.ValidateWordAction
import english.master.action.common.EditCardDescriptionIfNeededAction


class NewCardProcessor(
    action1: RequestWordAction = RequestWordAction(),
    action2: ValidateWordAction = ValidateWordAction(),
    action3: SendPhotoWithEmptyPlaceholderAction = SendPhotoWithEmptyPlaceholderAction(),
    action4: SendCardMenuAction = SendCardMenuAction(),
    action5: EditCardDescriptionIfNeededAction = EditCardDescriptionIfNeededAction(),
) : FlowProcessor(action1) {

    init {
        active = action1
        action1.next = action2
        action2.next = action3
        action3.next = action4
        action4.next = action5
    }
}
