package edu.citadel.cprl.ast

import edu.citadel.common.ConstraintException
import edu.citadel.common.InternalCompilerException

import edu.citadel.cprl.Symbol
import edu.citadel.cprl.Token
import edu.citadel.cprl.Type


/**
 * The abstract syntax tree node for a multiplying expression.  A multiplying
 * expression is a binary expression where the operator is a multiplying
 * operator such a "*", "/", "mod", "<<",  etc.  A simple example would be "5*x".
 *
 * @constructor Construct a multiplying expression with the operator and the two operands.
 */
class MultiplyingExpr(leftOperand : Expression, operator : Token, rightOperand : Expression)
    : BinaryExpr(leftOperand, operator, rightOperand)
  {
    /**
     * Initialize the type of the expression to Integer.
     */
    init
      {
        type = Type.Integer
        assert(operator.symbol.isMultiplyingOperator())
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