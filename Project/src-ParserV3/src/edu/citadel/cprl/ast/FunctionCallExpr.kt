package edu.citadel.cprl.ast

import edu.citadel.common.ConstraintException

import edu.citadel.cprl.ArrayType
import edu.citadel.cprl.StringType
import edu.citadel.cprl.Token

/**
 * The abstract syntax tree node for a function call expression.
 *
 * @constructor Construct a function call expression with the function name
 *              (an identifier token) and the list of actual parameters being
 *              passed as part of the call.
 */
class FunctionCallExpr(private val funId : Token,
                       actualParams : List<Expression>)
    : Expression(funId.position)
  {
    // We need a mutable list since, for var parameters,
    // we have to replace variable expressions by variables
    private val actualParams : MutableList<Expression> = actualParams.toMutableList()

    // declaration of the function being called
    private lateinit var funDecl : FunctionDecl   // nonstructural reference

    override fun checkConstraints()
      {
        try
          {
            // get the declaration for this function call from the identifier table
            val decl = idTable[funId.text]

            if (decl == null)
              {
                val errorMsg = "Function \"$funId\" has not been declared."
                throw error(funId.position, errorMsg)
              }
            else if (decl !is FunctionDecl)
              {
                val errorMsg = "Identifier \"$funId\" was not declared as a function."
                throw error(funId.position, errorMsg)
              }
            else
                funDecl = decl

            // at this point funDecl should not be null
            type = funDecl.type

            val paramDecls = funDecl.parameterDecls

            // check that numbers of parameters match
            if (actualParams.size != paramDecls.size)
              {
                val errorMsg = "Incorrect number of actual parameters."
                throw error(funId.position, errorMsg)
              }

            // check constraints for each actual parameter
            for (expr in actualParams)
                expr.checkConstraints()

            for (i in actualParams.indices)
              {
                var expr  : Expression = actualParams[i]
                val paramDecl : ParameterDecl = paramDecls[i]

                // check that parameter types match
                if (!matchTypes(paramDecl.type, expr))
                    throw error(expr.position, "Parameter type mismatch.")

                // check that arrays are passed as var parameters
                if (paramDecl.type is ArrayType)
                  {
                    if (expr is VariableExpr)
                      {
                        // replace a variable expression by a variable
                        expr = Variable(expr)
                        actualParams.set(i, expr)
                      }
                    else
                      {
                        val errorMsg = "Expression for an array parameter must be a variable."
                        throw error(expr.position, errorMsg)
                      }
                  }
              }
          }
        catch (e : ConstraintException)
          {
            errorHandler.reportError(e)
          }
      }

    /**
     * Add "synthetic" padding parameter for string literals if needed.
     */
    private fun addPadding()
      {
        val paramDecls = funDecl.parameterDecls

        // can't use a for-loop here since the number of actual parameters
        // can change with the insertion of padding for string types
        var i = 0
        var j = 0

        while (i < paramDecls.size)
          {
            val paramDecl = paramDecls[i]
            val expr = actualParams[j]

            if ((paramDecl.type is StringType && expr is ConstValue)
                    && paramDecl.type.size > expr.size)
              {
                val padding = Padding(paramDecl.type.size - expr.size)
                actualParams.add(++j, padding)
              }

            ++i
            ++j
          }
      }

    override fun emit()
      {
        addPadding()

        // allocate space on the stack for the return value
        emit("ALLOC ${funDecl.type.size}")

        // emit code for actual parameters
        for (expr in actualParams)
            expr.emit()

        emit("CALL ${funDecl.subprogramLabel}")
      }
  }