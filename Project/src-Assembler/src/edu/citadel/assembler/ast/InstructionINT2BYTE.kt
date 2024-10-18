package edu.citadel.assembler.ast

import edu.citadel.cvm.Constants
import edu.citadel.cvm.Opcode

import edu.citadel.assembler.Symbol
import edu.citadel.assembler.Token

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction INT2BYTE.
 */
class InstructionINT2BYTE(labels: MutableList<Token>, opcode: Token)
    : InstructionNoArgs(labels, opcode)
  {
    override fun assertOpcode() = assertOpcode(Symbol.INT2BYTE)

    override fun emit() = emit(Opcode.INT2BYTE)
  }
