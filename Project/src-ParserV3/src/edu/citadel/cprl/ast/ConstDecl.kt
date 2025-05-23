package edu.citadel.cprl.ast

import edu.citadel.common.ConstraintException
import edu.citadel.common.util.IntUtil

import edu.citadel.cprl.Symbol
import edu.citadel.cprl.Token
import edu.citadel.cprl.Type

/**
 * The abstract syntax tree node for a constant declaration.
 *
 * @constructor Construct a constant declaration with its identifier, type, and literal.
 */
class ConstDecl(identifier : Token, constType : Type, val literal : Token)
    : InitialDecl(identifier, constType)
  {
    override fun checkConstraints()
      {
// ...
      }
  }
