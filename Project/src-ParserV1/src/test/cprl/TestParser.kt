package test.cprl

import edu.citadel.common.ErrorHandler
import edu.citadel.common.FatalException

import edu.citadel.cprl.Scanner
import edu.citadel.cprl.IdTable
import edu.citadel.cprl.Parser

import java.io.File
import kotlin.system.exitProcess

private const val SUFFIX = ".cprl"

fun main(args : Array<String>)
  {
    if (args.isEmpty())
        printUsageAndExit()

    for (fileName in args)
      {
        val errorHandler = ErrorHandler()

        try
          {
            var sourceFile = File(fileName)

            if (!sourceFile.isFile)
              {
                // see if we can find the file by appending the suffix
                val index = fileName.lastIndexOf('.')

                if (index < 0 || fileName.substring(index) != SUFFIX)
                  {
                    val fileName2 : String = fileName + SUFFIX
                    sourceFile = File(fileName2)

                    if (!sourceFile.isFile)
                        throw FatalException("File \"$fileName2\" not found")
                  }
                else
                  {
                    // don't try to append the suffix
                    throw FatalException("File \"$fileName\" not found")
                  }
              }

            printProgressMessage("Parsing $fileName...")

            val scanner = Scanner(sourceFile, 4, errorHandler)   // 4 lookahead tokens
            val idTable = IdTable()
            val parser  = Parser(scanner, idTable, errorHandler)

            parser.parseProgram()

            if (errorHandler.errorsExist())
                errorHandler.printMessage("Errors detected in $fileName -- parsing terminated.")
            else
                printProgressMessage("Parsing complete.")
          }
        catch (e : FatalException)
          {
            // report error and continue testing parser
            errorHandler.reportFatalError(e)
          }

        println()
      }
  }

private fun printProgressMessage(message : String) = println(message)

private fun printUsageAndExit()
  {
    println("Usage: testParser file1 file2 ...")
    println()
    exitProcess(0)
  }
