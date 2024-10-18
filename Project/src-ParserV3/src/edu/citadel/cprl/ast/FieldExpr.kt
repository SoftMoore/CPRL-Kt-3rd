package edu.citadel.cprl.ast

import edu.citadel.cprl.Token
import edu.citadel.cprl.Type

/**
 * The abstract syntax tree node for a field expression, which has the form
 * ".id".  A field expression is used as a selector for a record field and
 * for a string length.  The value of a field expression has type integer.
 * For a record field, the value is the field offset.  For a string, the
 * values is always 0 since string length is always at offset 0.
 *
 * @constructor Construct a field expression with its field name.
 */
class FieldExpr(val fieldId : Token) : Expression(Type.Integer, fieldId.position)
  {
    // Note: value for fieldDecl is assigned in Variable.checkConstraints()
    lateinit var fieldDecl : FieldDecl   // nonstructural reference

    override fun checkConstraints()
      {
        // nothing to do for now
      }

    override fun emit()
      {
        assert(fieldDecl.offset >= 0) { "Invalid value for field offset." }
// ...
      }
  }
