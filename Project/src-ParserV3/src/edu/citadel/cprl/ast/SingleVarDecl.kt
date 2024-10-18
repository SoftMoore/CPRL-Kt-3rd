package edu.citadel.cprl.ast

import edu.citadel.common.ConstraintException
import edu.citadel.common.InternalCompilerException

import edu.citadel.cprl.*

/**
 * The abstract syntax tree node for a single variable declaration.
 * A single variable declaration has the form `var x : Integer;`
 *
 * Note: A variable declaration where more than one variable is declared
 * is simply a container for multiple single variable declarations.
 *
 * @constructor Construct a single variable declaration with its identifier,
 *              type, initial value, and scope level.
 */
class SingleVarDecl(identifier : Token, varType : Type,
                    private  val initializer : Initializer,
                    override val scopeLevel  : ScopeLevel)
    : InitialDecl(identifier, varType), VariableDecl
  {
    override var relAddr = 0   // relative address for the variable
                               // introduced by this declaration

    /**
     * The size (number of bytes) associated with this single variable declaration,
     * which is simply the number of bytes associated with its type.
     */
    override val size : Int
        get() = type.size

    override fun checkConstraints()
      {
        try
          {
            // check constraints only if initializer is not empty
            if (!initializer.isEmpty)
                checkInitializer(type, initializer)
          }
        catch (e: ConstraintException)
          {
            errorHandler.reportError(e)
          }
      }

    private fun checkInitializer(type : Type, initializer : Initializer)
      {
        if (type.isScalar || type is StringType)
          {
            // initializer must be a ConstValue of the appropriate type
            if (initializer is ConstValue)
              {
                // check that the initializer has the correct type
                if (!matchTypes(type, initializer))
                  {
                    val errorMsg = "Type mismatch for variable initialization."
                    throw error(initializer.position, errorMsg)
                  }
              }
            else
              {
                val errorMsg = "Initializer must be a constant value."
                throw error(initializer.position, errorMsg)
              }
          }
        else if (type is ArrayType)
          {
            // must be a composite initializer with correct number of values
            if (initializer is CompositeInitializer)
              {
                val initializers = initializer.initializers
                if (initializers.size != type.numElements)
                  {
                    val errorMsg = ("Incorrect number of initializers for array type $type.")
                    throw error(initializer.position, errorMsg)
                  }

                for (i in initializers)
                    checkInitializer(type.elementType, i)
              }
            else
              {
                val errorMsg = "Initializer for an array must be composite."
                throw error(initializer.position, errorMsg)
              }
          }
        else if (type is RecordType)
          {
            // initializer must be composite with correct number of values and types
            if (initializer is CompositeInitializer)
              {
                val initializers = initializer.initializers
                val fieldDecls = type.fieldDecls
                if (initializers.size != fieldDecls.size)
                  {
                    val errorMsg = ("Incorrect number of initializers for record type $type.")
                    throw error(initializer.position, errorMsg)
                  }

                for (i in initializers.indices)
                    checkInitializer(fieldDecls[i].type, initializers[i])
              }
            else
              {
                val errorMsg = "Initializer for a record must be composite."
                throw error(initializer.position, errorMsg)
              }
          }
      }

    /**
     * Adds padding to a composite initializer if needed.
     */
    private fun addPadding(type : Type, initializer : Initializer)
      {
        if (type is ArrayType)
          {
            // initializer must be a composite with correct number of values and types
            val compositeInitializer = initializer as CompositeInitializer
            val initializers = compositeInitializer.initializers
            val elementType  = type.elementType

            if (elementType is ArrayType || elementType is RecordType)
              {
                // each initializer must also be composite
                for (i in initializers)
                    addPadding(elementType, i)
              }
            else if (elementType is StringType)
              {
                // need to add padding only for strings
                // i is index into the array, j is index into the composite initializer
                var i = 0
                var j = 0
                while (i < type.numElements)
                  {
                    // initializer must be a constant value with string type
                    val constValue = initializers[j] as ConstValue
                    assert(matchTypes(elementType, constValue))
                    if (elementType.size > constValue.size)
                      {
                        val numBytes = elementType.size - constValue.size
                        initializers.add(++j, Padding(numBytes))
                      }

                    ++i
                    ++j
                  }
              }
          }
        else if (type is RecordType)
          {
            // initializer must be a composite with correct number of values and types
            val compositeInitializer = initializer as CompositeInitializer
            val initializers = compositeInitializer.initializers

            val fieldDecls = type.fieldDecls
            var i = 0
            var j = 0
            while (i < fieldDecls.size)
              {
                val fieldDecl = fieldDecls[i]
                val fieldInitializer = initializers[j]

                if (fieldDecl.type is ArrayType || fieldDecl.type is RecordType)
                  {
                    // fieldInitializer must be composite
                    addPadding(fieldDecl.type, fieldInitializer)
                  }
                else if (fieldDecl.type is StringType)
                  {
                    // need to add padding only for strings
                    // initializer must be a constant value with string type
                    val constValue = initializers[j] as ConstValue
                    assert(matchTypes(fieldDecl.type, constValue))
                    if (fieldDecl.type.size > constValue.size)
                      {
                        val numBytes = fieldDecl.type.size - constValue.size
                        initializers.add(++j, Padding(numBytes))
                      }
                  }

                ++i
                ++j
              }
          }
      }

    override fun emit()
      {
        // emit code only if the initializer is not empty
        if (!initializer.isEmpty)
          {
            if (scopeLevel == ScopeLevel.GLOBAL)
                emit("LDGADDR $relAddr")
            else
                emit("LDLADDR $relAddr")

            if (initializer is ConstValue)
              {
                initializer.emit()
                emitStoreInst(initializer.type)
              }
            else if (initializer is CompositeInitializer)
              {
                addPadding(type, initializer)
                initializer.emit()
                emitStoreInst(initializer.size)
              }
            else
              {
                val errorMsg = "Unexpected initializer type."
                throw InternalCompilerException(position, errorMsg)
              }
          }
      }
  }