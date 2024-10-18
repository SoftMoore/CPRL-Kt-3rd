package edu.citadel.cprl.ast

import edu.citadel.common.Position

import edu.citadel.cprl.Type

/**
 * This class is used to pad composite actual parameters and initializations
 * with additional bytes in order to force alignment of string constants with
 * their corresponding types.
 *
 * @constructor Construct a padding with the number of bytes required.
 */
class Padding(val numBytes : Int)
    : Expression(Type.none, Position()), Initializer
  {
    override val size: Int
        get() = numBytes

    override fun checkConstraints()
      {
        assert(numBytes > 0) { "Number of bytes for padding should be positive." }
      }

    override fun emit() = emit("ALLOC $numBytes")

    override fun toString() : String = "Padding[numBytes]"
  }