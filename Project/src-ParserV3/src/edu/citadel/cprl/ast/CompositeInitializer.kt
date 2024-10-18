package edu.citadel.cprl.ast

import edu.citadel.common.Position

import java.util.LinkedList

class CompositeInitializer(override val position : Position) : AST(), Initializer
  {
    // Use linked list since we could be inserting padding in the middle.
    val initializers = LinkedList<Initializer>()

    /**
     * Add the specified initializer to this composite.
     */
    fun add(initializer : Initializer) = initializers.add(initializer)

    override val size : Int
        get()
          {
            return initializers.sumOf { it.size }
          }

    override fun checkConstraints()
      {
        for (initializer in initializers)
            initializer.checkConstraints()
      }

    override fun emit()
      {
        for (initializer in initializers)
            initializer.emit()
      }

    override fun toString() : String = "CompositeInitializer[$initializers]"
  }