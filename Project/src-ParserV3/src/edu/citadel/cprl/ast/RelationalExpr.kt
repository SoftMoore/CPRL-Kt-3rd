package edu.citadel.cprl.ast

import edu.citadel.common.CodeGenException
import edu.citadel.common.ConstraintException

import edu.citadel.cprl.Symbol
import edu.citadel.cprl.Token
import edu.citadel.cprl.Type

/**
 * The abstract syntax tree node for a relational expression.  A relational
 * expression is a binary expression where the operator is a relational
 * operator such as "<=" or ">".  A simple example would be "x < 5".
 *
 * @constructor Construct a relational expression with the operator
 *              ("=", "<=", etc.) and the two operands.
 */
class RelationalExpr(leftOperand : Expression, operator : Token, rightOperand : Expression)
    : BinaryExpr(leftOperand, operator, rightOperand)
  {
    // labels used during code generation
    private val L1 : String = newLabel()   // label at start of right operand
    private val L2 : String = newLabel()   // label at end of the relational expression

    /**
     * Initialize the type of the expression to Boolean.
     */
    init
      {
        type = Type.Boolean
        assert(operator.symbol.isRelationalOperator())
      }

    override fun checkConstraints()
      {
// ...
      }

    override fun emit()
      {
        emitBranch(false, L1)
        emit("LDCB $TRUE")    // push true back on the stack
        emit("BR $L2")        // jump over code to emit false
        emitLabel(L1)
        emit("LDCB $FALSE")   // push false onto the stack
        emitLabel(L2)
      }

    override fun emitBranch(condition : Boolean, label : String)
      {
        emitOperands()

        when (operator.symbol)
          {
            Symbol.equals         -> emit(if (condition) "BE $label"  else "BNE $label")
            Symbol.notEqual       -> emit(if (condition) "BNE $label" else "BE $label")
            Symbol.lessThan       -> emit(if (condition) "BL $label"  else "BGE $label")
            Symbol.lessOrEqual    -> emit(if (condition) "BLE $label" else "BG $label")
            Symbol.greaterThan    -> emit(if (condition) "BG $label"  else "BLE $label")
            Symbol.greaterOrEqual -> emit(if (condition) "BGE $label" else "BL $label")
            else ->
              {
                val errorMsg = "Invalid relational operator."
                throw CodeGenException(operator.position, errorMsg)
              }
          }
      }

    private fun emitOperands()
      {
        // Relational operators compare integers only, so we need to make sure
        // that we have enough bytes on the stack.  Pad with zero bytes.
        for (n in 1..Type.Integer.size - leftOperand.type.size)
            emit("LDCB 0")

        leftOperand.emit()

        for (n in 1..Type.Integer.size - rightOperand.type.size)
            emit("LDCB 0")

        rightOperand.emit()
      }
  }