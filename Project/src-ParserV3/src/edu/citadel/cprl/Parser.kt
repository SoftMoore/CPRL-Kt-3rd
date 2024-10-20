package edu.citadel.cprl

import edu.citadel.common.Position
import edu.citadel.common.ErrorHandler
import edu.citadel.common.ParserException
import edu.citadel.common.InternalCompilerException

import edu.citadel.cprl.ast.*

import java.util.*

/**
 * This class uses recursive descent to perform syntax analysis of
 * the CPRL source language and to generate an abstract syntax tree.
 *
 * @constructor Construct a parser with the specified scanner,
 *              identifier table, and error handler.
 */
class Parser(private val scanner : Scanner,
             private val idTable : IdTable,
             private val errorHandler : ErrorHandler)
  {
    private val loopContext = LoopContext()
    private val subprogramContext = SubprogramContext()

    /**
     * Symbols that can follow a statement.
     */
    private val stmtFollowers = EnumSet.of(
// ...
      )

    /**
     * Symbols that can follow a subprogram declaration.
     */
    private val subprogDeclFollowers = EnumSet.of(
// ...
      )

    /**
     * Symbols that can follow a factor.
     */
    private val factorFollowers = EnumSet.of(
        Symbol.semicolon,   Symbol.loopRW,      Symbol.thenRW,
        Symbol.rightParen,  Symbol.andRW,       Symbol.orRW,
        Symbol.equals,      Symbol.notEqual,    Symbol.lessThan,
        Symbol.lessOrEqual, Symbol.greaterThan, Symbol.greaterOrEqual,
        Symbol.plus,        Symbol.minus,       Symbol.times,
        Symbol.divide,      Symbol.modRW,       Symbol.rightBracket,
        Symbol.comma,       Symbol.bitwiseAnd,  Symbol.bitwiseOr,
        Symbol.bitwiseXor,  Symbol.leftShift,   Symbol.rightShift,
        Symbol.dotdot
      )

    /**
     * Symbols that can follow an initial declaration (computed property).
     * Set is computed dynamically based on the scope level.
     */
    private val initialDeclFollowers : Set<Symbol>
        get()
          {
            // An initial declaration can always be followed by another
            // initial declaration, regardless of the scope level.
            val followers = EnumSet.of(
              Symbol.constRW, Symbol.varRW, Symbol.typeRW
            )

            if (idTable.scopeLevel == ScopeLevel.GLOBAL)
                followers.addAll(EnumSet.of(Symbol.procRW, Symbol.funRW))
            else
              {
                followers.addAll(stmtFollowers)
                followers.remove(Symbol.elseRW)
              }

            return followers
          }

    /**
     * Parse the following grammar rule:
     * `program = initialDecls subprogramDecls .`
     *
     * @return he parsed program.  Returns a program with an empty list
     *         of initial declarations and an empty list of subprogram
     *         declarations if parsing fails.
     */
    fun parseProgram() : Program
      {
        try
          {
            val initialDecls = parseInitialDecls()
            val subprogDecls = parseSubprogramDecls()

            // match(Symbol.EOF)
            // Let's generate a better error message than "Expecting "End-of-File" but ..."
            if (scanner.symbol != Symbol.EOF)
              {
                val errorMsg = "Expecting \"proc\" or \"fun\" " +
                               "but found \"${scanner.token}\" instead."
                throw error(errorMsg)
              }

            return Program(initialDecls, subprogDecls)
          }
        catch (e : ParserException)
          {
            errorHandler.reportError(e)
            recover(EnumSet.of(Symbol.EOF))
            return Program()
          }
      }

    /**
     * Parse the following grammar rule:
     * `initialDecls = { initialDecl } .`
     *
     * @return The list of initial declarations.
     */
    private fun parseInitialDecls() : MutableList<InitialDecl>
      {
        val initialDecls = ArrayList<InitialDecl>(10)

        while (scanner.symbol.isInitialDeclStarter())
            initialDecls.add(parseInitialDecl())

        return initialDecls
      }

    /**
     * Parse the following grammar rule:
     * `initialDecl = constDecl |  varDecl | typeDecl.`
     *
     * @return The parsed initial declaration.  Returns an
     *         empty initial declaration if parsing fails.
     */
    private fun parseInitialDecl() : InitialDecl
      {
// ...   throw an internal error if the symbol is not one of constRW, varRW, or typeRW
      }

    /**
     * Parse the following grammar rule:
     * `constDecl = "const" constId ":=" [ "-" ] literal ";" .`
     *
     * @return The parsed constant declaration.  Returns an
     *         empty initial declaration if parsing fails.
     */
    private fun parseConstDecl() : InitialDecl
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `literal = intLiteral | charLiteral | stringLiteral | "true" | "false" .`
     *
     * @return The parsed literal token.  Returns
     *         a default token if parsing fails.
     */
    private fun parseLiteral() : Token
      {
        try
          {
            if (scanner.symbol.isLiteral())
              {
                val literal = scanner.token
                matchCurrentSymbol()
                return literal
              }
            else
                throw error("Invalid literal expression.")
          }
        catch (e : ParserException)
          {
            errorHandler.reportError(e)
            recover(factorFollowers)
            return Token()
          }
      }

    /**
     * Parse the following grammar rule:
     * `varDecl = "var" identifiers ":" ( typeName | arrayTypeConstr | stringTypeConstr)
     *      *               [ ":=" initializer] ";" .`
     *
     * @return The parsed variable declaration.  Returns an
     *         empty initial declaration if parsing fails.
     */
    private fun parseVarDecl() : InitialDecl
      {
        try
          {
            match(Symbol.varRW)
            val identifiers = parseIdentifiers()
            match(Symbol.colon)

            val varType : Type
            val symbol = scanner.symbol
            if (symbol.isPredefinedType() || symbol == Symbol.identifier)
                varType = parseTypeName()
            else if (symbol == Symbol.arrayRW)
                varType = parseArrayTypeConstr()
            else if (symbol == Symbol.stringRW)
                varType = parseStringTypeConstr()
            else
              {
                val errorMsg = "Expecting a type name, reserved word " +
                               "\"array\", or reserved word \"string\"."
                throw error(errorMsg)
              }

            var initializer : Initializer = EmptyInitializer
            if (scanner.symbol == Symbol.assign)
              {
                matchCurrentSymbol()
                initializer = parseInitializer()
              }

            match(Symbol.semicolon)
            val varDecl = VarDecl(identifiers, varType, initializer, idTable.scopeLevel)

            for (decl in varDecl.singleVarDecls)
                idTable.add(decl)

            return varDecl
          }
        catch (e : ParserException)
          {
            errorHandler.reportError(e)
            recover(initialDeclFollowers)
            return EmptyInitialDecl
          }
      }

    /**
     * Parse the following grammar rule:
     * `identifiers = identifier { "," identifier } .`
     *
     * @return The list of identifier tokens.  Returns
     *         an empty list if parsing fails.
     */
    private fun parseIdentifiers() : List<Token>
      {
        try
          {
            val identifiers = ArrayList<Token>(10)
            var idToken = scanner.token
            match(Symbol.identifier)
            identifiers.add(idToken)

            while (scanner.symbol == Symbol.comma)
              {
                matchCurrentSymbol()
                idToken = scanner.token
                match(Symbol.identifier)
                identifiers.add(idToken)
              }

            return identifiers
          }
        catch (e : ParserException)
          {
            errorHandler.reportError(e)
            recover(setOf(Symbol.colon))
            return emptyList()
          }
      }

    /**
     * Parse the following grammar rule:</br>
     * `initializer = constValue | compositeInitializer .`
     *
     * @return The parsed initializer.  Returns an
     *         empty initializer if parsing fails.
     */
    private fun parseInitializer() : Initializer
      {
        try
          {
            val symbol = scanner.symbol
            if (symbol == Symbol.identifier || symbol.isLiteral() || symbol == Symbol.minus)
              {
                val expr = parseConstValue()
                return if (expr is ConstValue) expr else EmptyInitializer
              }
            else if (symbol == Symbol.leftBrace)
                return parseCompositeInitializer()
            else
              {
                val errorMsg = "Expecting literal, identifier, or left brace."
                throw error(errorMsg)
              }
          }
        catch (e: ParserException)
          {
            errorHandler.reportError(e)
            recover(initialDeclFollowers)
            return EmptyInitializer
          }
      }

    /**
     * Parse the following grammar rule:</br>
     * `compositeInitializer = "{" initializer { "," initializer } "}" .`
     *
     * @return The parsed composite initializer.  Returns an
     *         empty composite initializer if parsing fails.
     */
    private fun parseCompositeInitializer() : CompositeInitializer
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `typeDecl = arrayTypeDecl | recordTypeDecl | stringTypeDecl .`
     *
     * @return The parsed type declaration.  Returns an
     *         empty initial declaration if parsing fails.
     */
    private fun parseTypeDecl() : InitialDecl
      {
        assert(scanner.symbol == Symbol.typeRW)

        try
          {
            return when (scanner.lookahead(4).symbol)
              {
                Symbol.arrayRW -> parseArrayTypeDecl()
                Symbol.recordRW -> parseRecordTypeDecl()
                Symbol.stringRW -> parseStringTypeDecl()
                else            ->
                  {
                    val errorPos = scanner.lookahead(4).position
                    throw error(errorPos, "Invalid type declaration.")
                  }
              }
          }
        catch (e : ParserException)
          {
            errorHandler.reportError(e)
            matchCurrentSymbol()   // force scanner past "type"
            recover(initialDeclFollowers)
            return EmptyInitialDecl
          }
      }

    /**
     * Parse the following grammar rule:
     * `arrayTypeDecl = "type" typeId "=" "array" "[" intConstValue "]"
     *                  "of" typeName ";" .`
     *
     * @return The parsed array type declaration.  Returns an
     *         empty initial declaration if parsing fails.
     */
    private fun parseArrayTypeDecl() : InitialDecl
      {
// ...
      }

    /**
     * Parse the following grammar rule:</br>
     * `arrayTypeConstr = "array" "[" intConstValue "]" "of" typeName .`
     *
     * @return The array type defined by this array type constructor.
     *         Returns an empty array type if parsing fails.
     */
    private fun parseArrayTypeConstr() : ArrayType
      {
        try
          {
            match(Symbol.arrayRW)
            match(Symbol.leftBracket)
            val numElements = parseIntConstValue()
            match(Symbol.rightBracket)
            match(Symbol.ofRW)
            val elemType = parseTypeName()
            val nElements = numElements.intValue
            val typeName  = "array[$nElements] of $elemType"
            return ArrayType(typeName, nElements, elemType)
          }
        catch (e: ParserException)
          {
            errorHandler.reportError(e)
            recover(EnumSet.of(Symbol.semicolon))
            return ArrayType("_", 0, Type.UNKNOWN)
          }
      }

    /**
     * Parse the following grammar rule:
     * `recordTypeDecl = "type" typeId "=" "record" "{" fieldDecls "}" ";" .`
     *
     * @return The parsed record type declaration.  Returns
     *         an empty initial declaration if parsing fails.
     */
    private fun parseRecordTypeDecl() : InitialDecl
      {
        try
          {
            match(Symbol.typeRW)
            val typeId = scanner.token
            match(Symbol.identifier)
            match(Symbol.equals)
            match(Symbol.recordRW)
            match(Symbol.leftBrace)

            val fieldDecls : List<FieldDecl>
            try
              {
                idTable.openScope(ScopeLevel.RECORD)
                fieldDecls = parseFieldDecls()
              }
            finally
              {
                idTable.closeScope()
              }

            match(Symbol.rightBrace)
            match(Symbol.semicolon)

            val typeDecl = RecordTypeDecl(typeId, fieldDecls)
            idTable.add(typeDecl)
            return typeDecl
          }
        catch (e : ParserException)
          {
            errorHandler.reportError(e)
            recover(initialDeclFollowers)
            return EmptyInitialDecl
          }
      }

    /**
     * Parse the following grammar rule:
     * `fieldDecls = { fieldDecl } .`
     *
     * @return A (possibly empty) list of field declarations.
     */
    private fun parseFieldDecls() : List<FieldDecl>
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `fieldDecl = fieldId ":" typeName ";" .`
     *
     * @return The parsed field declaration.  Returns null if parsing fails.
     */
    private fun parseFieldDecl() : FieldDecl?
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `stringTypeDecl = "type" typeId "=" "string" "[" intConstValue "]" ";" .`
     *
     * @return The parsed string type declaration.  Returns an
     *         empty initial declaration if parsing fails.
     */
    private fun parseStringTypeDecl() : InitialDecl
      {
// ...
      }

    /**
     * Parse the following grammar rule:</br>
     * `stringTypeConstr = "string" "[" intConstValue "]" .`
     *
     * @return The string type defined by this string type constructor.
     *         Returns an empty string type if parsing fails.
     */
    private fun parseStringTypeConstr() : StringType
      {
        try
          {
            match(Symbol.stringRW)
            match(Symbol.leftBracket)
            val numElements = parseIntConstValue()
            match(Symbol.rightBracket)
            val nElements = numElements.intValue
            val typeName  = "string[$nElements]"
            return StringType(typeName, nElements)
          }
        catch (e: ParserException)
          {
            errorHandler.reportError(e)
            recover(EnumSet.of(Symbol.semicolon))
            return StringType("_", 0)
          }
      }

    /**
     * Parse the following grammar rule:
     * `typeName = "Integer" | "Boolean" | "Char" | typeId .`
     *
     * @return The parsed named type.  Returns Type.UNKNOWN if parsing fails.
     */
    private fun parseTypeName() : Type
      {
        try
          {
            when (scanner.symbol)
              {
                Symbol.IntegerRW ->
                  {
                    matchCurrentSymbol()
                    return Type.Integer
                  }
                Symbol.BooleanRW ->
                  {
                    matchCurrentSymbol()
                    return Type.Boolean
                  }
                Symbol.CharRW ->
                  {
                    matchCurrentSymbol()
                    return Type.Char
                  }
                Symbol.identifier ->
                  {
                    val typeId = scanner.token
                    matchCurrentSymbol()
                    val decl = idTable[typeId.text]

                    if (decl != null)
                      {
                        if (decl is ArrayTypeDecl || decl is RecordTypeDecl || decl is StringTypeDecl)
                            return decl.type
                        else
                          {
                            val errorMsg = "Identifier \"$typeId\" is not a valid type name."
                            throw error(typeId.position, errorMsg)
                          }
                      }
                    else
                      {
                        val errorMsg = "Identifier \"$typeId\" has not been declared."
                        throw error(typeId.position, errorMsg)
                      }
                  }
                else -> throw error("Invalid type name.")
              }
          }
        catch (e : ParserException)
          {
            errorHandler.reportError(e)
            recover(EnumSet.of(Symbol.semicolon, Symbol.comma,
                               Symbol.rightParen, Symbol.leftBrace))
            return Type.UNKNOWN
          }
      }

    /**
     * Parse the following grammar rule:
     * `subprogramDecls = { subprogramDecl } .`
     *
     * @return The list of subprogram declarations.
     */
    private fun parseSubprogramDecls() : List<SubprogramDecl>
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `subprogramDecl = procedureDecl | functionDecl .`
     *
     * @return The parsed subprogram declaration.  Returns an
     *         empty subprogram declaration if parsing fails.
     */
    private fun parseSubprogramDecl() : SubprogramDecl
      {
// ...   throw an internal error if the symbol is not one of procRW or funRW
      }

    /**
     * Parse the following grammar rule:
     * `procedureDecl = "proc" procId "(" [ parameterDecls } ")"
     *                  "{" initialDecls statements "}" .`
     *
     * @return The parsed procedure declaration.  Returns an
     *         empty subprogram declaration if parsing fails.
     */
    private fun parseProcedureDecl() : SubprogramDecl
      {
        try
          {
            match(Symbol.procRW)
            val procId = scanner.token
            match(Symbol.identifier)

            val procDecl = ProcedureDecl(procId)
            idTable.add(procDecl)
            match(Symbol.leftParen)

            try
              {
                idTable.openScope(ScopeLevel.LOCAL)

                if (scanner.symbol.isParameterDeclStarter())
                    procDecl.parameterDecls = parseParameterDecls()

                match(Symbol.rightParen)
                match(Symbol.leftBrace)
                procDecl.initialDecls = parseInitialDecls()

                subprogramContext.beginSubprogramDecl(procDecl)
                procDecl.statements = parseStatements()
                subprogramContext.endSubprogramDecl()
              }
            finally
              {
                idTable.closeScope()
              }

            match(Symbol.rightBrace)
            return procDecl
          }
        catch (e : ParserException)
          {
            errorHandler.reportError(e)
            recover(subprogDeclFollowers)
            return EmptySubprogramDecl
          }
      }

    /**
     * Parse the following grammar rule:
     * `functionDecl = "fun" funId "(" [ parameterDecls ] ")" ":" typeName
     *                 "{" initialDecls statements "}" .`
     *
     * @return The parsed function declaration.  Returns an
     *         empty subprogram declaration if parsing fails.
     */
    private fun parseFunctionDecl() : SubprogramDecl
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `parameterDecls = parameterDecl { "," parameterDecl } .`
     *
     * @return A list of parameter declarations.
     */
    private fun parseParameterDecls() : List<ParameterDecl>
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `parameterDecl = [ "var" ] paramId ":" typeName .`
     *
     * @return The parsed parameter declaration.  Returns null if parsing fails.
     */
    private fun parseParameterDecl() : ParameterDecl?
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `statements = { statement } .`
     *
     * @return A list of statements.
     */
    private fun parseStatements() : List<Statement>
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `statement = assignmentStmt | procedureCallStmt | compoundStmt | ifStmt
     *            | loopStmt       | forLoopStmt       | exitStmt     | readStmt
     *            | writeStmt      | writelnStmt       | returnStmt .`
     *
     * @return The parsed statement.  Returns an empty statement if parsing fails.
     */
    private fun parseStatement() : Statement
      {
        try
          {
            if (scanner.symbol == Symbol.identifier)
              {
                // Handle identifiers based on how they are declared,
                // or use the lookahead symbol if not declared.
                val idStr = scanner.text
                val decl  = idTable[idStr]

                if (decl != null)
                  {
                    if (decl is VariableDecl)
                        return parseAssignmentStmt()
                    else if (decl is ProcedureDecl)
                        return parseProcedureCallStmt()
                    else
                        throw error("Identifier \"$idStr\" cannot start a statement.")
                  }
                else
                  {
// ...   Big Hint: Read the book!
                  }
              }
            else
              {
                return when (scanner.symbol)
                  {
                    Symbol.leftBrace -> parseCompoundStmt()
                    Symbol.ifRW      -> parseIfStmt()
// ...
                    else -> throw internalError("${scanner.token} cannot start a statement.")
                  }
              }
          }
        catch (e: ParserException)
          {
            errorHandler.reportError(e)

            // Error recovery here is complicated for identifiers since they can both
            // start a statement and appear elsewhere in the statement.  (Consider,
            // for example, an assignment statement or a procedure call statement.)
            // Since the most common error is to declare or reference an identifier
            // incorrectly, we will assume that this is the case and advance to the
            // end of the current statement before performing error recovery.
            scanner.advanceTo(EnumSet.of(Symbol.semicolon, Symbol.rightBrace))
            recover(stmtFollowers)
            return EmptyStatement
          }
      }

    /**
     * Parse the following grammar rule:
     * `assignmentStmt = variable ":=" expression ";" .`
     *
     * @return The parsed assignment statement.  Returns
     *         an empty statement if parsing fails.
     */
    private fun parseAssignmentStmt() : Statement
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `compoundStmt = "{" statements "}" .`
     *
     * @return The parsed compound statement.  Returns
     *         an empty statement if parsing fails.
     */
    private fun parseCompoundStmt() : Statement
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `ifStmt = "if" booleanExpr "then" statement  [ "else" statement ] .`
     *
     * @return The parsed if statement.  Returns an
     *         empty statement if parsing fails.
     */
    private fun parseIfStmt() : Statement
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `loopStmt = [ "while" booleanExpr ] "loop" statement .`
     *
     * @return The parsed loop statement.  Returns
     *         an empty statement if parsing fails.
     */
    private fun parseLoopStmt() : Statement
      {
// ...
      }

    /**
     * Parse the following grammar rule:</br>
     * `forLoopStmt = "for" varId "in" intExpr ".." intExpr "loop" statement .`
     *
     * @return The parsed for-loop statement.  Returns
     *         an empty statement if parsing fails.
     */
    private fun parseForLoopStmt() : Statement
      {
        try
          {
            // create a new scope for the loop variable
            idTable.openScope(ScopeLevel.LOCAL)

            match(Symbol.forRW)
            val loopId = scanner.token
            match(Symbol.identifier)
            match(Symbol.inRW)
            val rangeStart = parseExpression()
            match(Symbol.dotdot)
            val rangeEnd = parseExpression()

            // Create an implicit variable declaration for the loop variable and add
            // it to the list of initial declarations for the subprogram declaration.
            val varDecl = VarDecl(listOf(loopId), Type.Integer,
                                  EmptyInitializer, ScopeLevel.LOCAL)
            val subprogDecl = subprogramContext.subprogramDecl
                              ?: kotlin.error("null reference to subprogram declaration")
            subprogDecl.initialDecls.add(varDecl)

            // Add the corresponding single variable declaration to the identifier tables.
            val loopSvDecl = varDecl.singleVarDecls.last()
            idTable.add(loopSvDecl)

            // Create loop variable to add to AST class ForLoopStmt
            val loopVariable = Variable(loopSvDecl, loopId.position, emptyList())
            match(Symbol.loopRW)
            val forLoopStmt = ForLoopStmt(loopVariable, rangeStart, rangeEnd)
            loopContext.beginLoop(forLoopStmt)
            forLoopStmt.statement = parseStatement()
            loopContext.endLoop()

            return forLoopStmt
          }
        catch (e: ParserException)
          {
            errorHandler.reportError(e)
            recover(stmtFollowers)
            return EmptyStatement
          }
        finally
          {
            idTable.closeScope()
          }
      }

    /**
     * Parse the following grammar rule:
     * `exitStmt = "exit" [ "when" booleanExpr ] ";" .`
     *
     * @return The parsed exit statement.  Returns
     *         an empty statement if parsing fails.
     */
    private fun parseExitStmt() : Statement
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `readStmt = "read" variable ";" .`
     *
     * @return The parsed read statement.  Returns
     *         an empty statement if parsing fails.
     */
    private fun parseReadStmt() : Statement
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `writeStmt = "write" expressions ";" .`
     *
     * @return The parsed write statement.  Returns
     *         an empty statement if parsing fails.
     */
    private fun parseWriteStmt() : Statement
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `expressions = expression { "," expression } .`
     *
     * @return A list of expressions.
     */
    private fun parseExpressions() : List<Expression>
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `writelnStmt = "writeln" [ expressions ] ";" .`
     *
     * @return The parsed writeln statement.  Returns
     *         an empty statement if parsing fails.
     */
    private fun parseWritelnStmt() : Statement
      {
        try
          {
            match(Symbol.writelnRW)

            val expressions : List<Expression>
            if (scanner.symbol.isExprStarter())
                expressions = parseExpressions()
            else
                expressions = emptyList()

            match(Symbol.semicolon)
            return OutputStmt(expressions, isWriteln = true)
          }
        catch (e : ParserException)
          {
            errorHandler.reportError(e)
            recover(stmtFollowers)
            return EmptyStatement
          }
      }

    /**
     * Parse the following grammar rules:
     * `procedureCallStmt = procId "(" [ actualParameters ] ")" ";" .
     *  actualParameters = expressions .`
     *
     * @return The parsed procedure call statement.  Returns
     *         an empty statement if parsing fails.
     */
    private fun parseProcedureCallStmt() : Statement
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `returnStmt = "return" [ expression ] ";" .`
     *
     * @return The parsed return statement.  Returns
     *         an empty statement if parsing fails.
     */
    private fun parseReturnStmt() : Statement
      {
// ...
      }

    /**
     * Parse the following grammar rules:
     * `variable = ( varId | paramId ) { indexExpr | fieldExpr } .
     *  indexExpr = "[" expression "]" .
     *  fieldExpr = "." fieldId .</code>`
     *
     * This method provides common logic for methods `parseVariable()` and
     * `parseVariableExpr()`.  The method does not handle any parser exceptions but
     * throws them back to the calling method where they can be handled appropriately.
     *
     * @return The parsed variable.
     * @throws ParserException if parsing fails.
     * @see .parseVariable
     * @see .parseVariableExpr
     */
    private fun parseVariableCommon() : Variable
      {
        val idToken = scanner.token
        match(Symbol.identifier)
        val decl = idTable[idToken.text]

        if (decl == null)
          {
            val errorMsg = "Identifier \"$idToken\" has not been declared."
            throw error(idToken.position, errorMsg)
          }
        else if (decl !is VariableDecl)
          {
            val errorMsg = "Identifier \"$idToken\" is not a variable."
            throw error(idToken.position, errorMsg)
          }

        val variableDecl = decl as VariableDecl

        val selectorExprs = ArrayList<Expression>(5)
        while (scanner.symbol.isSelectorStarter())
          {
            if (scanner.symbol == Symbol.leftBracket)
              {
                // parse index expression
                match(Symbol.leftBracket)
                selectorExprs.add(parseExpression())
                match(Symbol.rightBracket)
              }
            else if (scanner.symbol == Symbol.dot)
              {
                // parse field expression
                match(Symbol.dot)
                val fieldId = scanner.token
                match(Symbol.identifier)
                selectorExprs.add(FieldExpr(fieldId))
              }
          }

        return Variable(variableDecl, idToken.position, selectorExprs)
      }

    /**
     * Parse the following grammar rule:
     * `variable = ( varId | paramId ) { indexExpr | fieldExpr } .
     *
     * @return The parsed variable.  Returns null if parsing fails.
     */
    private fun parseVariable() : Variable?
      {
        try
          {
            return parseVariableCommon()
          }
        catch (e : ParserException)
          {
            errorHandler.reportError(e)
            recover(setOf(Symbol.assign, Symbol.semicolon))
            return null
          }
      }

    /**
     * Parse the following grammar rules:
     * `expression = relation { logicalOp relation } .
     *  logicalOp = "and" | "or" . `
     *
     * @return The parsed expression.
     */
    private fun parseExpression() : Expression
      {
        var expr = parseRelation()

        while (scanner.symbol.isLogicalOperator())
          {
            val operator = scanner.token
            matchCurrentSymbol()
            expr = LogicalExpr(expr, operator, parseRelation())
          }

        return expr
      }

    /**
     * Parse the following grammar rules:
     * `relation = simpleExpr [ relationalOp simpleExpr ] .
     *  relationalOp = "=" | "!=" | "<" | "<=" | ">" | ">=" .`
     *
     * @return The parsed relational expression.
     */
    private fun parseRelation() : Expression
      {
// ...
      }

    /**
     * Parse the following grammar rules:
     * `simpleExpr = [ signOp ] term { addingOp term } .
     *  signOp = "+" | "-" .
     *  addingOp  = "+" | "-" | "|" | "^" .`
     *
     * @return The parsed simple expression.
     */
    private fun parseSimpleExpr() : Expression
      {
// ...
      }

    /**
     * Parse the following grammar rules:
     * `term = factor { multiplyingOp factor } .
     *  multiplyingOp = "*" | "/" | "mod" | "&" | "<<" | ">>" .`
     *
     * @return The parsed term expression.
     */
    private fun parseTerm() : Expression
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `factor = ("not" | "~") factor | literal | constId | variableExpr
     *         | functionCallExpr | "(" expression ")" .`
     *
     * @return The parsed factor expression. Returns
     *         an empty expression if parsing fails.
     */
    private fun parseFactor() : Expression
      {
        try
          {
            if (scanner.symbol == Symbol.notRW || scanner.symbol == Symbol.bitwiseNot)
              {
                val operator = scanner.token
                matchCurrentSymbol()
                return NotExpr(operator, parseFactor())
              }
            else if (scanner.symbol.isLiteral())
              {
                // Handle constant literals separately from constant identifiers.
                return parseConstValue()
              }
            else if (scanner.symbol == Symbol.identifier)
              {
                // Three possible cases: a declared constant, a variable
                // expression, or a function call expression.  Use lookahead
                // tokens and declaration to determine correct parsing action.

                val idStr = scanner.text
                val decl  = idTable[idStr]

                if (decl != null)
                  {
                    if (decl is ConstDecl)
                        return parseConstValue()
                    else if (decl is VariableDecl)
                        return parseVariableExpr()
                    else if (decl is FunctionDecl)
                        return parseFunctionCallExpr()
                    else
                      {
                        val errorPos = scanner.position
                        val errorMsg = "Identifier \"$idStr\" is not valid as an expression."

                        // special recovery when procedure call is used as a function call
                        if (decl is ProcedureDecl)
                          {
                            scanner.advance()
                            if (scanner.symbol == Symbol.leftParen)
                              {
                                scanner.advanceTo(Symbol.rightParen)
                                scanner.advance()   // advance past the right paren
                              }
                          }

                        throw error(errorPos, errorMsg)
                      }
                  }
                else
                  {
                    // Make parsing decision using an additional lookahead symbol.
                    if (scanner.lookahead(2).symbol == Symbol.leftParen)
                        return parseFunctionCallExpr()
                    else
                        throw error("Identifier \"${scanner.token}\" has not been declared.")
                  }
              }
            else if (scanner.symbol == Symbol.leftParen)
              {
                matchCurrentSymbol()
                val expr = parseExpression()   // save expression
                match(Symbol.rightParen)
                return expr
              }
            else
                throw error("Invalid expression.")
          }
        catch (e : ParserException)
          {
            errorHandler.reportError(e)
            recover(factorFollowers)
            return EmptyExpression
          }
      }

    /**
     * Parse the following grammar rule:
     * `constValue = ( [ "-"] literal ) | constId .`
     *
     * @return The parsed constant value.  Returns
     *         an empty expression if parsing fails.
     */
    private fun parseConstValue() : Expression
      {
// ...
      }

    /**
     * Parse the following grammar rule:
     * `variableExpr = variable .`
     *
     * @return The parsed variable expression.  Returns
     *         an empty expression if parsing fails.
     */
    private fun parseVariableExpr() : Expression
      {
        try
          {
            val variable = parseVariableCommon()
            return VariableExpr(variable)
          }
        catch (e : ParserException)
          {
            errorHandler.reportError(e)
            recover(factorFollowers)
            return EmptyExpression
          }
      }

    /**
     * Parse the following grammar rules:
     * `functionCallExpr = funId "(" [ actualParameters ] ")" .
     *  actualParameters = expressions .`
     *
     * @return The parsed function call expression.  Returns
     *         an empty expression if parsing fails.
     */
    private fun parseFunctionCallExpr() : Expression
      {
// ...
      }

    // Utility parsing methods

    /**
     * Wrapper around method parseConstValue() that always
     * returns a valid integer constant value.
     */
    private fun parseIntConstValue() : ConstValue
      {
        val token = Token(Symbol.intLiteral, Position(), "1")
        val defaultConstValue = ConstValue(token)

        var intConstValue = parseConstValue()

        if (intConstValue is EmptyExpression)
            intConstValue = defaultConstValue // Error has already been reported.
        else if (intConstValue.type !== Type.Integer)
          {
            val errorMsg = "Constant value should have type Integer."
            // no error recovery required here
            errorHandler.reportError(error(intConstValue.position, errorMsg))
            intConstValue = defaultConstValue
          }

        return (intConstValue as ConstValue)
      }

    /**
     * Check that the current lookahead symbol is the expected symbol.  If it
     * is, then advance the scanner.  Otherwise, throw a ParserException.
     */
    private fun match(expectedSymbol : Symbol)
      {
        if (scanner.symbol == expectedSymbol)
            scanner.advance()
        else
          {
            val errorMsg = "Expecting \"$expectedSymbol\" but " +
                           "found \"${scanner.token}\" instead."
            throw error(errorMsg)
          }
      }

    /**
     * Advance the scanner.  This method represents an unconditional
     * match with the current scanner symbol.
     */
    private fun matchCurrentSymbol() = scanner.advance()

    /**
     * Advance the scanner until the current symbol is one of the
     * symbols in the specified set of follows.
     */
    private fun recover(followers : Set<Symbol>) = scanner.advanceTo(followers)

    /**
     * Create a parser exception with the specified error message and
     * the current scanner position.
     */
    private fun error(errorMsg : String) : ParserException
        = error(scanner.position, errorMsg)

    /**
     * Create a parser exception with the specified error position
     * and error message.
     */
    private fun error(errorPos : Position, errorMsg : String)
        = ParserException(errorPos, errorMsg)

    /**
     * Create an internal compiler exception with the specified error
     * message and the current scanner position.
     */
    private fun internalError(errorMsg : String)
        = InternalCompilerException(scanner.position, errorMsg)
  }