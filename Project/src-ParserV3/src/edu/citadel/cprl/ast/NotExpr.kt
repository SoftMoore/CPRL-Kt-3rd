package edu.citadel.cprl.ast

import edu.citadel.common.ConstraintException

import edu.citadel.cprl.Symbol
import edu.citadel.cprl.Token
import edu.citadel.cprl.Type

/**
 * The abstract syntax tree node for a not expression.  A not expression is a unary
 * expression of the form "not expr" (logical negation) or "~x" (bitwise negation
 * or one's complement).  A simple example would be "not isEmpty()".
 *
 * @constructor Construct a not expression with the specified operator and operand.
 */
class NotExpr(operator : Token, operand : Expression) : UnaryExpr(operator, operand)
  {
    init
      {
        val symbol = operator.symbol
        assert(symbol == Symbol.notRW || symbol == Symbol.bitwiseNot)

        type = if (symbol == Symbol.notRW) Type.Boolean else Type.Integer
      }

    override fun checkConstraints()
      {
// ...
      }

    override fun emit()
      {
// ...
      }
  }