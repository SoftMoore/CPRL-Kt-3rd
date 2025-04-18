package edu.citadel.assembler.ast

import edu.citadel.assembler.Symbol
import edu.citadel.assembler.Token
import edu.citadel.common.ConstraintException
import edu.citadel.common.util.IntUtil


/**
 * This class serves as a base class for the abstract syntax tree for an
 * assembly language instruction with one argument.  Note that property
 * arg can't be null for subclasses of this class.
 *
 * @constructor Construct a one-argument instruction with a list of labels,
 *              an opcode, and an argument.
 */
abstract class InstructionOneArg(labels : MutableList<Token>, opcode : Token, val arg : Token)
     : Instruction(labels, opcode)
  {
    /**
     * check semantic/contextual constraints
     */
    override fun checkConstraints()
      {
        try
          {
            assertOpcode()
            checkLabels()
            checkArgType()
          }
        catch (e : ConstraintException)
          {
            errorHandler.reportError(e)
          }
      }

    /**
     * This method is called by instructions that have an argument that
     * references a label.  It verifies that the referenced label exists.
     */
    protected fun checkLabelArgDefined()
      {
        if (arg.symbol != Symbol.identifier)
          {
            val errorMessage = "Expecting a label identifier but found ${arg.symbol}."
            throw ConstraintException(arg.position, errorMessage)
          }

        val label = arg.text + ":"
        if (!labelMap.containsKey(label))
          {
            val errorMessage = "Label \"${arg.text}\" has not been defined."
            throw ConstraintException(arg.position, errorMessage)
          }
      }

    /**
     * This method is called by instructions to verify the type of its argument.
     */
    protected fun checkArgType(argType : Symbol)
      {
        if (arg.symbol != argType)
          {
            val errorMessage = "Invalid type for argument -- should be $argType"
            throw ConstraintException(arg.position, errorMessage)
          }

        if (arg.symbol === Symbol.intLiteral)
          {
            try
              {
                IntUtil.toInt(arg.text)
              }
            catch (e: NumberFormatException)
              {
                val errorMsg = "The number \"${arg.text}\" cannot" +
                    " be converted to an integer in CPRL."
                arg.text = "0" // to prevent additional error messages
                throw error(arg.position, errorMsg)
              }
          }
      }

    /**
     * Returns the argument as converted to an integer.  Valid
     * only for instructions with arguments of type intLiteral.
     */
    fun argToInt() : Int
      {
        assert(arg.symbol == Symbol.intLiteral)
          { "Can't convert argument $arg to an integer." }
        return IntUtil.toInt(arg.text)
      }

    /**
     * Returns the argument as converted to a byte.  Valid
     * only for instructions with arguments of type intLiteral.
     */
    fun argToByte() : Byte
      {
        assert(arg.symbol == Symbol.intLiteral) { "Can't convert argument $arg to a byte." }
        return java.lang.Byte.parseByte(arg.text)
      }

    override fun toString() = "${super.toString()} ${arg.text}"

    /**
     * Checks that the argument of the instruction has
     * the correct type.  Implemented in each instruction
     * by calling the method checkArgType(Symbol).
     */
    protected abstract fun checkArgType()
  }
