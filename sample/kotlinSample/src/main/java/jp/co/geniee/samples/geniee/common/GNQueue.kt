package jp.co.geniee.samples.geniee.common

class GNQueue(private val maxSize: Int) {
    private val queue = ArrayList<Any>()

    fun dequeue(): Any? = if (queue.isEmpty()) null else queue.removeAt(0)

    fun enqueue(obj: Any?) {
        if (obj == null) return
        if (queue.size >= maxSize) {
            queue.removeAt(0)
        }
        queue.add(obj)
    }

    fun size(): Int = queue.size
}
