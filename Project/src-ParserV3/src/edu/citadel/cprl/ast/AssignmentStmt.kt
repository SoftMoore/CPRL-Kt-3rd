package edu.citadel.cprl.ast

import edu.citadel.common.ConstraintException
import edu.citadel.common.Position

/**
 * The abstract syntax tree node for an assignment statement.
 *
 * @property variable The variable on the left side of the assignment symbol.
 * @property expr     The expression on the right side of the assignment symbol.
 * @property assignPosition Position of the assignment symbol (for error reporting).
 */
class AssignmentStmt(private val variable : Variable,
                     private val expr : Expression,
                     private val assignPosition : Position)
    : Statement()
  {
    override fun checkConstraints()
      {
// ...
      }

    override fun emit()
      {
// ...
      }
  }
