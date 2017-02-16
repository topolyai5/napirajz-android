package hu.napirajz.android

import hu.napirajz.android.response.NapirajzData
import java.util.*

class HistoryService {

    private var stack = Stack<NapirajzData>()

    fun add(data: NapirajzData) {
        stack.push(data)
    }

    fun previous(): NapirajzData {
        stack.pop()
        return current()
    }

    fun current(): NapirajzData {
        return stack.peek()
    }

    fun forSaveInstance(): Stack<NapirajzData> {
        return stack
    }

    fun fromSaveInstance(stack: Stack<NapirajzData>) {
        this.stack = stack
    }

    fun isEmpty(): Boolean {
        return stack.isEmpty()
    }

    fun hasPrevious(): Boolean {
        return stack.size > 1
    }

}