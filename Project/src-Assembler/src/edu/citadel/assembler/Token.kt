package edu.citadel.assembler

import edu.citadel.common.Position

/**
 * This class encapsulates the properties of a language token.  A token
 * consists of a symbol (a.k.a., the token type), a position, and a string
 * that contains the text of the token.
 *
 * @constructor Constructs a new token with the given symbol, position, and text.
 */
class Token(val symbol : Symbol, val position : Position, text : String)
  {
    var text : String = text.ifEmpty { symbol.toString() }

    /**
     * Constructs a new Token with the specified symbol and text.
     * Position is initialized to the default value.  This constructor
     * is useful when replacing instructions during optimization.
     */
    constructor(symbol : Symbol, text : String = "") : this(symbol, Position(), text)

    override fun toString() = text
  }
