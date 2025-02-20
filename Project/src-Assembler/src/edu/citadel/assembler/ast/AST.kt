package edu.citadel.assembler.ast

import edu.citadel.common.Position
import edu.citadel.common.ConstraintException
import edu.citadel.common.ErrorHandler
import edu.citadel.common.util.ByteUtil
import edu.citadel.cvm.Opcode

import java.io.OutputStream

/**
 * Base class for all abstract syntax trees.
 */
abstract class AST
  {
    /**
     * Create a constraint exception with the specified position and message.
     */
    protected fun error(errorPos : Position, errorMsg : String)
        = ConstraintException(errorPos, errorMsg)

    /**
     * Emit the instruction opcode.
     */
    protected open fun emit(opcode: Opcode) = out.write(opcode.toInt())

    /**
     * Emit a byte argument for the instruction.
     */
    protected fun emit(arg : Byte) = out.write(arg.toInt())

    /**
     * Emit an integer argument for the instruction.
     */
    protected fun emit(arg : Int) = out.write(ByteUtil.intToBytes(arg))

    /**
     * Emit a character argument for the instruction.
     */
    protected fun emit(arg : Char) = out.write(ByteUtil.charToBytes(arg))

    /**
     * Check semantic/contextual constraints.
     */
    abstract fun checkConstraints()

    /**
     * Emit the object code for the AST.
     */
    abstract fun emit()

    companion object
      {
        /**
         * The output stream to be used for code generation.
         */
        lateinit var out : OutputStream

        /**
         * The error handler to be used for code generation.
         */
        lateinit var errorHandler : ErrorHandler

        /**
         * Initializes static members that are shared with all instructions.
         * The members must be re-initialized each time that the assembler is
         * run on a different file; e.g., via a command like assemble *.asm.
         */
        fun reset(errorHandler : ErrorHandler)
          {
            this.errorHandler = errorHandler
            Instruction.resetMaps()
          }
      }
  }
