package edu.citadel.cprl

import edu.citadel.common.CodeGenException
import edu.citadel.common.ErrorHandler
import edu.citadel.common.FatalException

import edu.citadel.cprl.ast.AST
import edu.citadel.cprl.ast.Program

import java.io.*
import kotlin.system.exitProcess

private const val SUFFIX = ".cprl"

/**
 * This function drives the compilation process.
 *
 * @param args Must include the name of the CPRL source file, either the complete
 *             file name or the base file name with suffix ".cprl" omitted.
 */
fun main(args : Array<String>)
  {
    if (args.isEmpty())
        printUsageAndExit()

    for (fileName in args)
      {
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

            val compiler = Compiler()
            compiler.compile(sourceFile)
          }
        catch (e : FatalException)
          {
            // report error and continue compiling
            val errorHandler = ErrorHandler()
            errorHandler.reportFatalError(e)
          }

        println()
      }
  }

private fun printProgressMessage(message : String) = println(message)

private fun printUsageAndExit()
  {
    System.err.println("Usage: cprlc file1 file2 ...")
    System.err.println()
    exitProcess(0)
  }

/**
 * Compiler for the CPRL programming language.
 */
class Compiler
  {
    /**
     * Compile the source file.  If there are no errors in the source file,
     * the object code is placed in a file with the same base file name as
     * the source file but with a ".asm" suffix.
     *
     * @throws IOException Thrown if there are problems reading the source
     *                     file or writing to the target file.
     */
    fun compile(sourceFile : File)
      {
        val errorHandler = ErrorHandler()
        val scanner = Scanner(sourceFile, 4, errorHandler)   // 4 lookahead tokens
        val idTable = IdTable()
        val parser  = Parser(scanner, idTable, errorHandler)
        AST.reset(idTable, errorHandler)

        printProgressMessage("Starting compilation for ${sourceFile.name}")
        printProgressMessage("...parsing")

        // parse source file
        val program : Program = parser.parseProgram()

        // check constraints
        if (!errorHandler.errorsExist())
          {
            AST.idTable = idTable
            AST.errorHandler = errorHandler
            printProgressMessage("...checking constraints")
            program.checkConstraints()
          }

        // generate code
        if (!errorHandler.errorsExist())
          {
            printProgressMessage("...generating code")

            // no error recovery from errors detected during code generation
            try
              {
                AST.out = targetPrintWriter(sourceFile)
                program.emit()
              }
            catch (e : CodeGenException)
              {
                errorHandler.reportError(e)
              }
          }

        if (errorHandler.errorsExist())
            errorHandler.printMessage("Errors detected in ${sourceFile.name} -- compilation terminated.")
        else
          printProgressMessage("Compilation complete.")
      }

    /**
     * Returns a print writer used for writing the assembly code.  The target
     * print writer writes to a file with the same base file name as the source
     * file but with a ".asm" suffix.
     */
    private fun targetPrintWriter(sourceFile : File) : PrintWriter
      {
        // get source file name minus the suffix
        var baseName = sourceFile.name
        val suffixIndex = baseName.lastIndexOf(SUFFIX)
        if (suffixIndex > 0)
            baseName = baseName.substring(0, suffixIndex)

        val targetFileName = "$baseName.asm"

        try
          {
            val targetFile = File(sourceFile.parent, targetFileName)
            val fileWriter = FileWriter(targetFile, Charsets.UTF_8)
            return PrintWriter(fileWriter, true)
          }
        catch (e : IOException)
          {
            e.printStackTrace()
            throw FatalException("Failed to create file $targetFileName")
          }
      }
  }
