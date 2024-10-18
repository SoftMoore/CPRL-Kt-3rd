package edu.citadel.assembler.ast

import edu.citadel.cvm.Opcode

import edu.citadel.assembler.Symbol
import edu.citadel.assembler.Token

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction BYTE2INT.
 */
class InstructionBYTE2INT(labels: MutableList<Token>, opcode: Token)
    : InstructionNoArgs(labels, opcode)
  {
    override fun assertOpcode() = assertOpcode(Symbol.BYTE2INT)

    override fun emit() = emit(Opcode.BYTE2INT)
  }
