package edu.citadel.common

/**
 * Superclass for all compiler exceptions.
 */
abstract class CompilerException : Exception
  {
    /**
     * Construct a compiler exception with information about the
     * compilation phase, position, and error message.
     *
     * @param errorType The name of compilation phase in which the error was detected.
     * @param position  The position in the source file where the error was detected.
     * @param errorMsg  A brief message about the nature of the error.
     */
    constructor(errorType : String, position : Position, errorMsg : String)
        : super("*** $errorType error detected near $position:\n    $errorMsg")

    /**
     * Construct a compiler exception with information about the
     * compilation phase and error message, but not its position.
     *
     * @param errorType The name of compilation phase in which the error was detected.
     * @param errorMsg  A brief message about the nature of the error.
     */
    constructor(errorType : String, errorMsg : String)
        : super("*** $errorType error detected:\n    $errorMsg")
 }
