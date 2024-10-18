package edu.citadel.cprl.ast

import edu.citadel.common.ConstraintException
import edu.citadel.common.InternalCompilerException

import edu.citadel.cprl.Symbol
import edu.citadel.cprl.Token
import edu.citadel.cprl.Type

/**
 * The abstract syntax tree node for an adding expression.  An adding expression
 * is a binary expression where the operator is an adding operator ("+" or "-") or
 * an adding bitwise operator ("|" or "^").  A simple example would be "x + 5".
 *
 * @constructor Construct an adding expression with the operator and the two operands.
 */
class AddingExpr(leftOperand : Expression, operator : Token, rightOperand : Expression)
    : BinaryExpr(leftOperand, operator, rightOperand)
  {
    init
      {
        type = Type.Integer   // initialize type of the expression to Integer
        assert(operator.symbol.isAddingOperator())
      }

    override fun checkConstraints()
      {
        try
          {
            leftOperand.checkConstraints()
            rightOperand.checkConstraints()

            // adding expression valid only for integers
            if (leftOperand.type != Type.Integer)
              {
                val errorMsg = "Left operand should have type Integer."
                throw error(leftOperand.position, errorMsg)
              }

            if (rightOperand.type != Type.Integer)
              {
                val errorMsg = "Right operand should have type Integer."
                throw error(rightOperand.position, errorMsg)
              }
          }
        catch (e : ConstraintException)
          {
            errorHandler.reportError(e)
          }
      }

    override fun emit()
      {
        leftOperand.emit()
        rightOperand.emit()

        when (operator.symbol)
          {
            Symbol.plus       -> emit("ADD")
            Symbol.minus      -> emit("SUB")
            Symbol.bitwiseOr  -> emit("BITOR")
            Symbol.bitwiseXor -> emit("BITXOR")
            else ->
              {
                val errorMsg = "Invalid adding operator."
                throw InternalCompilerException(operator.position, errorMsg)
              }
          }
      }
  }
