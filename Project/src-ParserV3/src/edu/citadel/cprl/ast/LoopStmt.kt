package edu.citadel.cprl.ast

import edu.citadel.common.ConstraintException
import edu.citadel.cprl.Type

/**
 * The abstract syntax tree node for a loop statement.
 */
open class LoopStmt : Statement()
  {
    var whileExpr : Expression? = null
    var statement : Statement = EmptyStatement

    // labels used during code generation
    protected val L1 : String = newLabel()   // label for start of loop
    protected val L2 : String = newLabel()   // label for end of loop

    /**
     * The label for the end of the loop statement.
     */
    val exitLabel : String = L2

    override fun checkConstraints()
      {
//  ...
      }

    override fun emit()
      {
//  ...
      }
  }
