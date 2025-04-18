package edu.citadel.cprl.ast

import edu.citadel.common.Position
import edu.citadel.common.ErrorHandler
import edu.citadel.common.CodeGenException
import edu.citadel.common.ConstraintException

import edu.citadel.cprl.IdTable
import edu.citadel.cprl.StringType
import edu.citadel.cprl.Type

import java.io.PrintWriter

/**
 * Base class for all abstract syntax tree classes.
 */
abstract class AST
  {
    /**
     * Returns a new value for a label number.  This method should
     * be called once for each label before code generation.
     */
    protected fun newLabel() : String
      {
        ++currentLabelNum
        return "L${currentLabelNum}"
      }

    /**
     * Returns a new constraint exception with the specified position and message.
     */
    protected fun error(errorPos : Position, errorMsg : String) : ConstraintException
        = ConstraintException(errorPos, errorMsg)

    /**
     * Returns a new constraint exception with the specified message.
     */
    protected fun error(errorMsg : String) : ConstraintException
        = ConstraintException(errorMsg)

    /**
     * Check semantic/contextual constraints.
     */
    abstract fun checkConstraints()

    /**
     * Emit object code.
     *
     * @throws CodeGenException if the method is unable to generate object code.
     */
    abstract fun emit()

    /**
     * Returns true if the expression type is assignment compatible with
     * the specified type.  This method is used to compare types for
     * assignment statements, subprogram parameters, and return values.
     */
    protected fun matchTypes(type : Type, expr : Expression) : Boolean
      {
        val exprType = expr.type
        if (type == expr.type)
            return true
        else if (type is StringType && exprType is StringType)
            return (exprType.capacity <= type.capacity) && (expr is ConstValue)
        else
            return false
      }

    /**
     * Emits the appropriate LOAD instruction based on the type.
     */
    protected fun emitLoadInst(t : Type)
      {
        when (t.size)
          {
            4 -> emit("LOADW")
            2 -> emit("LOAD2B")
            1 -> emit("LOADB")
            else -> emit("LOAD ${t.size}")
          }
      }

    /**
     * Emits the appropriate STORE instruction based on the type.
     */
    protected fun emitStoreInst(t : Type)
      {
        when (t.size)
          {
            4 -> emit("STOREW")
            2 -> emit("STORE2B")
            1 -> emit("STOREB")
            else -> emit("STORE ${t.size}")
          }
      }

    /**
     * Emits a STORE instruction based on the number of bytes.
     */
    protected fun emitStoreInst(numBytes : Int) = emit("STORE $numBytes")

    /**
     * Emit label for assembly instruction.  This instruction appends a colon
     * to the end of the label and writes out the result on a single line.
     */
    protected fun emitLabel(label : String) =  out.println("$label:")

    /**
     * Emit string representation for an assembly instruction.
     */
    protected fun emit(instruction : String) = out.println("   $instruction")

    companion object
      {
        // current label number for control flow
        private var currentLabelNum = -1

        var out = PrintWriter(System.out)

        var idTable = IdTable()
        var errorHandler = ErrorHandler()

        /**
         * Initializes companion members that are shared with all AST subclasses.
         * The members must be re-initialized each time that the compiler is
         * run on a different file; e.g., via a command like cprlc *.cprl.
         */
        fun reset(idTable: IdTable, errorHandler : ErrorHandler)
          {
            this.idTable = idTable;
            this.errorHandler = errorHandler;
            currentLabelNum = -1
          }
      }
  }