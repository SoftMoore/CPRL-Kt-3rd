package test.cprl

import edu.citadel.common.ErrorHandler
import edu.citadel.cprl.Scanner
import edu.citadel.cprl.Symbol
import edu.citadel.cprl.Token

import java.io.PrintStream
import java.io.File

import kotlin.system.exitProcess

private val out = PrintStream(java.lang.System.out, true, Charsets.UTF_8)

fun main(args : Array<String>)
  {
    if (args.size != 1)
        printUsageAndExit()

    try
      {
        println("initializing...")

        val fileName     = args[0]
        val sourceFile   = File(fileName)
        val errorHandler = ErrorHandler()
        val scanner      = Scanner(sourceFile, 4, errorHandler)   // 4 lookahead tokens
        var token : Token

        println("starting main loop...")
        println()

        do
          {
            token = scanner.token
            printToken(token)
            scanner.advance()
          }
        while (token.symbol != Symbol.EOF)

        println()
        println("...done")
      }
    catch (e : Exception)
      {
        e.printStackTrace()
      }
  }

private fun printToken(token : Token)
  {
    out.printf("line: %2d   char: %2d   token: ",
        token.position.lineNumber,
        token.position.charNumber)

    val symbol = token.symbol
    if (symbol.isReservedWord())
        out.print("Reserved Word -> ")
    else if (symbol == Symbol.identifier    || symbol == Symbol.intLiteral
          || symbol == Symbol.stringLiteral || symbol == Symbol.charLiteral)
        out.print(token.symbol.toString() + " -> ")

    out.println(token.text)
  }

private fun printUsageAndExit()
  {
    System.err.println("Usage: testScanner filename")
    System.err.println()
    exitProcess(0)
  }
