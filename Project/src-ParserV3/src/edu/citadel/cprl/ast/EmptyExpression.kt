package edu.citadel.cprl.ast

import edu.citadel.common.Position

/**
 * An empty expression passes constraint checks and emits no code.
 * It is returned from parsing statements as an alternative to
 * returning null when parsing errors are encountered.
 */
object EmptyExpression : Expression(Position())
  {
    override fun checkConstraints()
      {
        // nothing to check
      }

    override fun emit()
      {
        // nothing to emit
      }
  }
