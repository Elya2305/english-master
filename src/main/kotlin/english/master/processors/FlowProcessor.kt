package english.master.processors

import english.master.action.Action
import english.master.action.Active
import english.master.domain.UpdateWrapper
import kotlin.reflect.KClass

abstract class FlowProcessor(private val initialActive: Action) {
    var active: Action? = initialActive
    var waitForResponse = true

    fun process(update: UpdateWrapper): Any {
        if (hasActive()) {
            val result = active!!.process(update)
            if (result is JumpTo<*>) {
                jumpTo(result.clazz)
                return process(update)
            }
            waitForResponse = active!!.waitForResponse

            active = when (active!!.nextToProcess) {
                Active.NEXT -> active!!.next
                Active.CURRENT -> active
                Active.PREVIOUS -> active!!.previous
            }
            return result
        }
        throw RuntimeException("The flow is over") // todo send menu ?
    }

    fun <T : Action> jumpTo(clazz: KClass<T>) {
        var newActive = initialActive
        while (newActive::class != clazz) {
            newActive = newActive.next ?: throw RuntimeException("Can't jump to $clazz as it's not in the flow")
        }
        active = newActive
    }

    fun hasActive(): Boolean {
        return active != null
    }

    fun isOver(): Boolean {
        return !hasActive()
    }
}

data class JumpTo<T : Action>(
    val clazz: KClass<T>,
)
