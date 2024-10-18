package edu.citadel.cprl.ast

import edu.citadel.cprl.Token

import edu.citadel.cvm.Constants

/**
 * Base class for CPRL procedures and functions.
 *
 * @constructor Construct a subprogram declaration with the specified subprogram identifier.
 */
abstract class SubprogramDecl(subprogramId : Token) : Declaration(subprogramId)
  {
    /** The list of parameter declarations for this subprogram. */
    var parameterDecls : List<ParameterDecl> = emptyList()

    /** The list of initial declarations for this subprogram. */
    var initialDecls : MutableList<InitialDecl> = mutableListOf()

    /** The list of statements for this subprogram. */
    var statements : List<Statement> = emptyList()

    /**
     * The number of bytes required for all variables in the initial declarations.
     * Note: Value is set in method setRelativeAddresses().
     */
    protected var varLength = 0

    /** The label associated with the first statement of the subprogram. */
    val subprogramLabel : String = "_$subprogramId"

    /** The number of bytes for all parameters. */
    val paramLength : Int
        get()
          {
            var paramLength = 0

            for (paramDecl in parameterDecls)
                paramLength += paramDecl.size

            return paramLength
          }

    override fun checkConstraints()
      {
        for (paramDecl in parameterDecls)
            paramDecl.checkConstraints()

        for (decl in initialDecls)
            decl.checkConstraints()

        for (statement in statements)
            statement.checkConstraints()
      }

    /**
     * Set the relative address (offset) for each variable and
     * parameter, and compute the length of all variables.
     */
    protected fun setRelativeAddresses()
      {
        // initial relative address for a subprogram
        var currentAddr : Int = Constants.BYTES_PER_CONTEXT

        for (decl in initialDecls)
          {
            if (decl is VarDecl)
              {
                // set relative address for single variable declarations
                for (singleVarDecl in decl.singleVarDecls)
                  {
                    singleVarDecl.relAddr = currentAddr
                    currentAddr += singleVarDecl.size
                  }
              }
          }

        // compute length of all variables by subtracting initial relative address
        varLength = currentAddr - Constants.BYTES_PER_CONTEXT

        // set relative address for parameters
        if (parameterDecls.isNotEmpty())
          {
            // initial relative address for a subprogram parameter
            currentAddr = 0

            // we need to process the parameter declarations in reverse order
            val iter = parameterDecls.listIterator(parameterDecls.size)
            while (iter.hasPrevious())
              {
                val decl = iter.previous()
                currentAddr -= decl.size
                decl.relAddr = currentAddr
              }
          }
      }
  }