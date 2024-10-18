package edu.citadel.cprl.ast

import edu.citadel.common.Position

/**
 * An empty initializer passes constraint checks and emits no code.
 * It is returned from parsing initializers as an alternative to
 * returning null when parsing errors are encountered.
 */
object EmptyInitializer : Initializer
  {
    override val size = 0

    override val position = Position();

    override fun checkConstraints()
      {
        // nothing to check
      }

    override fun emit()
      {
        // nothing to emit
      }
  }