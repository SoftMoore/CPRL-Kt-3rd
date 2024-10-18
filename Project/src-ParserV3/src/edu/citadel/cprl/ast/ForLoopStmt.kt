package edu.citadel.cprl.ast

import edu.citadel.common.ConstraintException

import edu.citadel.cprl.Type

/**
 * The abstract syntax tree node for a for-loop statement.
 *
 * @constructor Construct a for-loop statement with the specified
 *              loop variable and range expressions.
 */
class ForLoopStmt(private val loopVar    : Variable,
                  private val rangeStart : Expression,
                  private val rangeEnd   : Expression)
      : LoopStmt()
  {
    override fun checkConstraints()
      {
        assert(loopVar.type == Type.Integer)

        try
          {
            loopVar.checkConstraints()
            rangeStart.checkConstraints()
            rangeEnd.checkConstraints()
            statement.checkConstraints()

            if (rangeStart.type !== Type.Integer)
              {
                val errorMsg = "The first expression of a range should have type Integer."
                throw error(rangeStart.position, errorMsg)
              }

            if (rangeEnd.type !== Type.Integer)
              {
                val errorMsg = "The second expression of a range should have type Integer."
                throw error(rangeEnd.position, errorMsg)
              }

            if (rangeStart is ConstValue && rangeEnd is ConstValue)
              {
                if (rangeStart.intValue > rangeEnd.intValue)
                  {
                    var errorMsg = "Invalid range for loop variable.";
                    throw error(rangeEnd.position, errorMsg);
                  }
              }
          }
        catch (e: ConstraintException)
          {
            errorHandler.reportError(e)
          }
      }

    override fun emit()
      {
        // initialize loop variable
        loopVar.emit()
        rangeStart.emit()
        emitStoreInst(Type.Integer)

        emitLabel(L1)

        // check that value of loop variable is <= range end
        val loopVarExpr = VariableExpr(loopVar)
        loopVarExpr.emit()
        rangeEnd.emit()
        emit("BG $L2")

        statement.emit()

        // increment loop variable
        loopVar.emit()
        loopVar.emit()
        emit("LOADW")
        emit("LDCINT 1")
        emit("ADD")
        emit("STOREW")

        emit("BR $L1")
        emitLabel(L2)
      }
  }