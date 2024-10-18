package edu.citadel.cprl.ast

import edu.citadel.common.ConstraintException

import edu.citadel.cprl.ArrayType
import edu.citadel.cprl.Token
import edu.citadel.cprl.Type

/**
 * The abstract syntax tree node for an array type declaration.
 *
 * @constructor Construct an array type declaration with its identifier, element type, and
 *              number of elements.  Note that the index type is always Integer in CPRL.
 *
 * @param typeId      The token containing the identifier for the array.
 * @param elementType The type of elements in the array.
 * @property numElements The number of elements in the array.
 */
class ArrayTypeDecl(typeId : Token, elementType : Type, private val numElements : ConstValue)
    : InitialDecl(typeId, ArrayType(typeId.text, numElements.intValue, elementType))
  {
    override fun checkConstraints()
      {
// ...
      }
  }
