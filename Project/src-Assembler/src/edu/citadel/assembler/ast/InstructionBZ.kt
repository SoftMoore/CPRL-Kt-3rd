package edu.citadel.assembler.ast

import edu.citadel.cvm.Constants
import edu.citadel.cvm.Opcode

import edu.citadel.assembler.Symbol
import edu.citadel.assembler.Token

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction BZ.
 */
class InstructionBZ(labels : MutableList<Token>, opcode : Token, arg : Token)
    : InstructionOneArg(labels, opcode, arg)
  {
    override val argSize: Int
        get() = Constants.BYTES_PER_INTEGER

    override fun assertOpcode() = assertOpcode(Symbol.BZ)

    override fun checkArgType()
      {
        checkArgType(Symbol.identifier)
        checkLabelArgDefined()
      }

    override fun emit()
      {
        emit(Opcode.BZ)
        emit(getDisplacement(arg))
      }
  }
