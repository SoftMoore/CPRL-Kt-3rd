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
        try
          {
            whileExpr?.let {   // can't move left brace to next line!
                it.checkConstraints()

                if (it.type != Type.Boolean)
                  {
                    val errorMsg = "The \"while\" expression should have type Boolean."
                    throw error(it.position, errorMsg)
                  }
              }

            statement.checkConstraints()
          }
        catch (e : ConstraintException)
          {
            errorHandler.reportError(e)
          }
      }

    override fun emit()
      {
        emitLabel(L1)

        whileExpr?.emitBranch(false, L2)

        statement.emit()
        emit("BR $L1")

        emitLabel(L2)
      }
  }
