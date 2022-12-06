package english.master.processors

import english.master.action.Action
import english.master.action.Active
import english.master.domain.UpdateWrapper

abstract class FlowProcessor {
    var active: Action? = null
    var waitForResponse = true

    fun process(update: UpdateWrapper): Any {
        if (hasActive()) {
            val result = active!!.process(update)
            waitForResponse = active!!.waitForResponse

            active = when(active!!.nextToProcess) {
                Active.NEXT -> active!!.next
                Active.CURRENT -> active
                Active.PREVIOUS -> active!!.previous
            }
            return result
        }

        throw RuntimeException("The flow is over")
    }

    fun hasActive(): Boolean {
        return active != null
    }
}
