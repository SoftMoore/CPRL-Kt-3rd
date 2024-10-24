package edu.citadel.cprl.ast

import edu.citadel.common.CodeGenException
import edu.citadel.common.ConstraintException
import edu.citadel.common.util.IntUtil

import edu.citadel.cprl.Symbol
import edu.citadel.cprl.Token
import edu.citadel.cprl.Type
import edu.citadel.cprl.StringType

/**
 * The abstract syntax tree node for a constant value expression, which is
 * either a literal or a declared constant identifier representing a literal.
 */
class ConstValue : Expression, Initializer
  {
    /**
     * The literal token for the constant.  If the constant value is is created from a
     * constant declaration, then the literal is the one contained in the declaration.
     */
    private var literal : Token

    /**
     * The constant declaration containing the constant value,  The declaration
     * is null for literals.
     */
    private val decl : ConstDecl?   // nonstructural reference

    override val size : Int
        get() = type.size

    /**
     * Constructs a constant value from a literal token.  The type
     * for the constant value is determined by the literal's type
     */
    constructor(literal : Token) : super(Type.typeOf(literal), literal.position)
      {
        this.literal = literal
        this.decl    = null
      }

    /**
     * Construct a constant value from a constant identifier
     * token and its corresponding constant declaration.
     */
    constructor(identifier : Token, decl : ConstDecl)
        : super(decl.type, identifier.position)
      {
        this.literal = decl.literal
        this.decl    = decl
      }

    /**
     * An integer value for the declaration literal.  For an integer literal,
     * this property simply returns its integer value.  For a char literal,
     * this property returns the underlying integer value for the character.
     * For a boolean literal, this property returns 0 for false and 1 for true.
     * For any other literal, the property returns 0.
     */
    val intValue : Int
        get()
          {
            when (literal.symbol)
              {
                Symbol.intLiteral  ->
                  {
                    try
                      {
                        return IntUtil.toInt(literal.text)
                      }
                    catch (e : NumberFormatException)
                      {
                        // error will be reported in checkConstraints()
                        return 1
                      }
                  }
                Symbol.trueRW      -> return 1
                Symbol.charLiteral ->
                  {
                    val ch = literal.text[1]
                    return ch.code
                  }
                else -> return 0
              }
          }

    override fun checkConstraints()
      {
        try
          {
            // Check that an integer literal can be converted to an integer.
            // Check is not required for literal in constant declaration.
            if (literal.symbol == Symbol.intLiteral && decl == null)
              {
                try
                  {
                    IntUtil.toInt(literal.text)
                  }
                catch (e : NumberFormatException)
                  {
                    val errorMsg = "The number \"${literal.text}\" cannot " +
                                   "be converted to an integer in CPRL."
                    throw error(literal.position, errorMsg)
                  }
              }
          }
        catch (e : ConstraintException)
          {
            errorHandler.reportError(e)
          }
      }

    override fun emit()
      {
        when (type)
          {
            Type.Integer  -> emit("LDCINT ${intValue}")
            Type.Boolean  -> emit("LDCB ${intValue}")
            Type.Char     -> emit("LDCCH ${literal.text}")
            is StringType -> emit("LDCSTR ${literal.text}")
            else ->
              {
                val errorMsg = "Invalid type for constant value."
                throw CodeGenException(literal.position, errorMsg)
              }
          }
      }
  }