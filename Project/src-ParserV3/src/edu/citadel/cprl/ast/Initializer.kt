package edu.citadel.cprl.ast

import edu.citadel.common.CodeGenException
import edu.citadel.common.Position

/**
 * Interface for a variable initializer, which is either a constant value,
 * padding, or a composite constant value.  The initializer classes implement
 * a variant of the Composite Pattern.
 */
sealed interface Initializer
  {
    /**
     * The number of bytes for this initializer.
     */
    val size : Int

    /**
     * The position of this initializer.
     */
    val position : Position

    /**
     * True only if the initializer contains no values.
     */
    val isEmpty : Boolean
        get() = size == 0

    /**
     * Check semantic/contextual constraints.
     */
    fun checkConstraints()

    /**
     * Emit object code.
     *
     * @throws CodeGenException if the method is unable to generate object code.
     */
    fun emit()
  }