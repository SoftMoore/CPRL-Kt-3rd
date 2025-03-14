package edu.citadel.cprl.ast

import edu.citadel.common.ConstraintException

import edu.citadel.cprl.Symbol
import edu.citadel.cprl.Token
import edu.citadel.cprl.Type

/**
 * The abstract syntax tree node for a logical expression.  A logical expression
 * is a binary expression where the operator is either "and" or "or".  A simple
 * example would be "(x &gt; 5) and (y &lt; 0)".
 *
 * @constructor Construct a logical expression with the operator ("and" or "or")
 *              and the two operands.
 */
class LogicalExpr(leftOperand : Expression, operator : Token, rightOperand : Expression)
    : BinaryExpr(leftOperand, operator, rightOperand)
  {
    // labels used during code generation for short-circuit version
    private val L1 : String = newLabel()   // label at start of right operand
    private val L2 : String = newLabel()   // label at end of logical expression

    /**
     * Initialize the type of the expression to Boolean.
     */
    init
      {
        type = Type.Boolean
        assert(operator.symbol.isLogicalOperator())
      }

    override fun checkConstraints()
      {
        try
          {
            leftOperand.checkConstraints()
            rightOperand.checkConstraints()

            if (leftOperand.type != Type.Boolean)
              {
                val errorMsg = "Left operand for a logical expression " +
                               "should have type Boolean."
                throw error(leftOperand.position, errorMsg)
              }

            if (rightOperand.type != Type.Boolean)
              {
                val errorMsg = "Right operand for a logical expression " +
                               "should have type Boolean."
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
        // Uses short-circuit evaluation for logical expressions.

        if (operator.symbol == Symbol.andRW)
          {
            // if left operand evaluates to true, branch
            // to code that will evaluate right operand
            leftOperand.emitBranch(true, L1)

            // otherwise, place "false" back on top of stack as value
            // for the compound "and" expression
            emit("LDCB ${FALSE}")
          }
        else   // operator.symbol must be Symbol.orRW
          {
            // if left operand evaluates to false, branch
            // to code that will evaluate right operand
            leftOperand.emitBranch(false, L1)

            // otherwise, place "true" back on top of stack as value
            // for the compound "or" expression
            emit("LDCB ${TRUE}")
          }

        // branch to code following the expression
        emit("BR $L2")

        emitLabel(L1)

        // evaluate the right operand and leave result on
        // top of stack as value for compound expression
        rightOperand.emit()
        emitLabel(L2)
      }
  }
