package jp.co.geniee.samples.gnad.common

import java.util.ArrayList

class GNQueue(private var maxSize: Int) {

    private var queue: ArrayList<Any> = ArrayList()

    fun dequeue(): Any? {
        val obj: Any?
        if (queue.size == 0) return null
        obj = queue[0]
        queue.removeAt(0)

        return obj
    }

    fun enqueue(obj: Any?) {
        if (obj == null) return
        if (queue.size >= maxSize) {
            queue.removeAt(0)
        }
        queue.add(obj)
    }

    fun size(): Int {
        return queue.size
    }

}
