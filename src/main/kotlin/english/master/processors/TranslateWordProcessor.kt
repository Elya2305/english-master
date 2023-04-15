package english.master.processors

import english.master.action.common.RequestWordAction
import english.master.action.translate.TranslateAction

class TranslateWordProcessor : FlowProcessor() {
    init {
        val action1 = RequestWordAction()
        val action2 = TranslateAction()

        active = action1
        action1.next = action2
    }
}