package test.common

import edu.citadel.common.Source

import java.io.FileReader
import java.io.PrintStream
import kotlin.system.exitProcess

fun main(args : Array<String>)
  {
    if (args.size != 1)
        printUsageAndExit()

    try
      {
        val fileName = args[0]
        val reader   = FileReader(fileName, Charsets.UTF_8)
        val source   = Source(reader)
        val out      = PrintStream(System.out, true, Charsets.UTF_8)

        while (source.currentChar != source.EOF)
          {
            val c = source.currentChar.toChar()

            if (c == '\n')
                out.println("\\n\t${source.charPosition}")
            else if (c != '\r')
                out.println("$c\t${source.charPosition}")

            source.advance()
          }
      }
    catch (e : Exception)
      {
        e.printStackTrace()
      }
  }

private fun printUsageAndExit()
  {
    System.err.println("Usage: testSource filename")
    System.err.println()
    exitProcess(0)
  }
