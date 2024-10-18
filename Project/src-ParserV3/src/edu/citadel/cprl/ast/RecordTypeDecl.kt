package edu.citadel.cprl.ast

import edu.citadel.cprl.RecordType
import edu.citadel.cprl.Token

/**
 * The abstract syntax tree node for a record type declaration.
 *
 * @constructor Construct a record type declaration with its type
 *              name (identifier) and list of field declarations.
 *
 * @param typeId     The token containing the identifier for the record type name.
 * @param fieldDecls The list of field declarations for the record.
 */
class RecordTypeDecl(typeId : Token, val fieldDecls : List<FieldDecl>)
    : InitialDecl(typeId, RecordType(typeId.text, fieldDecls))
  {
    override fun checkConstraints()
      {
// ...
      }
  }
