package english.master.processors

import english.master.action.add_word_with_choice.RequestWordAction
import english.master.action.add_word_with_choice.SendCardMenuAction
import english.master.action.add_word_with_choice.SendPhotoWithEmptyPlaceholderAction
import english.master.action.add_word_with_choice.ValidateWordAction
import english.master.action.common.EditCardDescriptionIfNeededAction


class NewCardProcessor : FlowProcessor() {

    init {
        val action1 = RequestWordAction()
        val action2 = ValidateWordAction()
        val action3 = SendPhotoWithEmptyPlaceholderAction()
        val action4 = SendCardMenuAction()
        val action5 = EditCardDescriptionIfNeededAction()

        active = action1
        action1.next = action2
        action2.next = action3
        action3.next = action4
        action4.next = action5
    }
}
