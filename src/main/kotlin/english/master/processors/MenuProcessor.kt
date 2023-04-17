package english.master.processors

import english.master.action.Action
import english.master.action.MenuAction

class MenuProcessor(
    action1: Action = MenuAction()
) : FlowProcessor(action1)
