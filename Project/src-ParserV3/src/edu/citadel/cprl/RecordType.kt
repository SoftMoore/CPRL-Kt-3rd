package edu.citadel.cprl

import edu.citadel.cprl.ast.FieldDecl

/**
 * This class encapsulates the language concept of a record type
 * in the programming language CPRL.
 *
 * @constructor Construct a record type with the specified type name,
 *              list of field declarations, and size.
 */
class RecordType(typeName : String, val fieldDecls : List<FieldDecl>)
    : Type(typeName, 0)
// ... In call to superclass constructor, 0 is not correct as the size for the record type.
// ... What is the size for the record type?  Hint: Read the book.
  {
    // Use a hash map for efficient lookup of field names.
    private var fieldNameMap = HashMap<String, FieldDecl>()

    init
      {
        for (fieldDecl in fieldDecls)
            fieldNameMap[fieldDecl.idToken.text] = fieldDecl

        // compute fieldDecl offsets
        for (i in 1 until fieldDecls.size)
          {
            val prevDecl = fieldDecls[i - 1]
            fieldDecls[i].offset = prevDecl.offset + prevDecl.size
          }
      }

    /**
     * Returns true if this record type contains a field with the specified field name.
     */
    public fun containsField(fieldName : String) : Boolean = fieldNameMap.containsKey(fieldName);

    /**
     * Returns the field declaration associated with the identifier string.
     * Returns null if the identifier string is not found.
     */
    operator fun get(idStr : String) : FieldDecl? = fieldNameMap[idStr]
  }