package edu.citadel.common

/**
 * Bounded circular buffer.
 *
 * @constructor Construct buffer with the specified capacity.
 */
class BoundedBuffer<E>(val capacity : Int)
  {
    private val buffer: Array<Any?> = arrayOfNulls(capacity)
    private var index = 0   // circular index

    /**
     * Return the element at index i.  Does not remove the element from the buffer.
     */
    @Suppress("UNCHECKED_CAST")
    operator fun get(i: Int) : E?
      {
        return buffer[(index + i) % capacity] as E?
      }

    /**
     * Add an element to the buffer.  Overwrites if the buffer is full.
     */
    fun add(e : E)
      {
        buffer[index] = e
        index = (index + 1) % capacity
      }
  }