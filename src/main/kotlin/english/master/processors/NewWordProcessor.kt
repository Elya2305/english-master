package english.master.processors

import english.master.action.add_word_with_choice.EditDescriptionAction
import english.master.action.add_word_with_choice.GenerateMenuAction
import english.master.action.add_word_with_choice.RequestWordAction
import english.master.action.add_word_with_choice.SendPhotoWithPlaceholderAction

class NewWordProcessor : FlowProcessor() {

    init {
        val action1 = RequestWordAction()
        val action2 = SendPhotoWithPlaceholderAction()
        val action3 = GenerateMenuAction()
        val action4 = EditDescriptionAction()

        active = action1
        action1.next = action2
        action2.next = action3
        action3.next = action4
    }
}
