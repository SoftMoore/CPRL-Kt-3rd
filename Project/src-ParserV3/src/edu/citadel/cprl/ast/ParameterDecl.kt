package edu.citadel.cprl.ast

import edu.citadel.cprl.ScopeLevel
import edu.citadel.cprl.Token
import edu.citadel.cprl.Type

/**
 * The abstract syntax tree node for a parameter declaration.
 *
 * @constructor Construct a parameter declaration with its identifier,
 *              type, and a boolean value that indicates if it is a
 *              variable parameter declaration.
 */
class ParameterDecl(paramId : Token, type : Type, var isVarParam : Boolean)
    : Declaration(paramId, type), VariableDecl
  {
    override var relAddr = 0      // relative address for this declaration

    override val scopeLevel = ScopeLevel.LOCAL   // always LOCAL for a parameter

    /**
     * The size (number of bytes) associated with this parameter declaration.
     * The size of a value parameter declaration is the number of bytes associated
     * with its type.  For variable parameters, the size is the number of bytes
     * needed for a memory address.
     */
    override val size : Int
        get() = if (isVarParam) Type.Address.size else type.size

    override fun checkConstraints()
      {
        assert(type != Type.UNKNOWN && type != Type.none)
          { "Invalid CPRL type in parameter declaration." }
      }

    override fun emit()
      {
        // nothing to emit for parameter declarations
      }

    override fun toString() : String
      {
        val builder = StringBuilder()
        builder.append(if (isVarParam) "var " else "")
               .append(idToken)
               .append(" : ")
               .append(type);
        return builder.toString();
      }
  }
