package english.master.processors

import english.master.action.common.RequestWordAction
import english.master.action.translate.TranslateAction

class TranslateWordProcessor(
    action1: RequestWordAction = RequestWordAction(),
    action2: TranslateAction = TranslateAction(),
) : FlowProcessor(action1) {

    init {
        active = action1
        action1.next = action2
    }
}