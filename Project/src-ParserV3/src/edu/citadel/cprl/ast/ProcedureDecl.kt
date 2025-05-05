package edu.citadel.cprl.ast

import edu.citadel.cprl.ArrayType
import edu.citadel.cprl.Token

/**
 * The abstract syntax tree node for a procedure declaration.
 *
 * @constructor Construct a procedure declaration with its name (an identifier).
 */
class ProcedureDecl(procId : Token) : SubprogramDecl(procId)
  {
    override fun checkConstraints()
      {
        for (paramDecl in parameterDecls)
          {
            paramDecl.checkConstraints()

            // arrays are always passed as var params.
            if (paramDecl.type is ArrayType)
                paramDecl.isVarParam = true
          }

        for (decl in initialDecls)
            decl.checkConstraints()

        for (statement in statements)
            statement.checkConstraints()
      }

    override fun emit()
      {
        setRelativeAddresses()

        emitLabel(subprogramLabel)

        // no need to emit PROC instruction if varLength == 0
        if (varLength > 0)
            emit("PROC $varLength")

        for (decl in initialDecls)
            decl.emit()

        for (statement in statements)
            statement.emit()

        emit("RET $paramLength")   // required for procedures
      }
  }
