package edu.citadel.cprl

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
     * Default constructor.  Constructs a new token with symbol = Symbol.unknown.
     * Position and text are initialized to default values.
     */
    constructor() : this(Symbol.unknown, Position(), "")

    override fun toString() = text
  }
