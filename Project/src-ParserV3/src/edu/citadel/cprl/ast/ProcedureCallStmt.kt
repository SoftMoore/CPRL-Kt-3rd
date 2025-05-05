package edu.citadel.cprl.ast

import edu.citadel.common.ConstraintException

import edu.citadel.cprl.Token
import edu.citadel.cprl.ArrayType
import edu.citadel.cprl.StringType

/**
 * The abstract syntax tree node for a procedure call statement.
 *
 * @constructor Construct a procedure call statement with the procedure
 *              name (an identifier token) and the list of actual parameters
 *              being passed as part of the call.
 */
class ProcedureCallStmt(private val procId : Token, actualParams : List<Expression>)
    : Statement()
  {
    // We need a mutable list since, for var parameters,
    // we have to replace variable expressions by variables
    private val actualParams : MutableList<Expression> = actualParams.toMutableList()

    // declaration of the procedure being called
    private lateinit var procDecl : ProcedureDecl   // nonstructural reference

    override fun checkConstraints()
      {
// ...
      }

    /**
     * Add "synthetic" padding parameter for string literals if needed.
     */
    private fun addPadding()
      {
        val paramDecls = procDecl.parameterDecls

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
// ...   call addPadding before emitting any code
  }