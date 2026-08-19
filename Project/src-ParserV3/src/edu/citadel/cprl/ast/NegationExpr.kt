package edu.citadel.cprl.ast

import edu.citadel.common.ConstraintException
import edu.citadel.common.InternalCompilerException

import edu.citadel.cprl.Symbol
import edu.citadel.cprl.Token
import edu.citadel.cprl.Type


/**
 * The abstract syntax tree node for a negation expression.  A negation
 * expression is a unary expression where the operand has type Integer
 * and the operator is "-".  A simple example would be "-x".
 *
 * @constructor Construct a negation expression with the specified
 *              operator and operand.
 */
class NegationExpr(operator : Token, operand : Expression)
    : UnaryExpr(operator, operand)
  {
    init
      {
        type = Type.Integer
        assert(operator.symbol == Symbol.minus)
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
