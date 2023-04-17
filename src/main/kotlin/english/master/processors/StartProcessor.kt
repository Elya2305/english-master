package english.master.processors

import english.master.action.Action
import english.master.action.StartAction

class StartProcessor(
    action1: Action = StartAction()
) : FlowProcessor(action1)
