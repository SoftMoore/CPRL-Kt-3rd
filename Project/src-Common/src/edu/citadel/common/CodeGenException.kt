package edu.citadel.common

/**
 * Class for exceptions encountered during code generation.
 *
 * @constructor Construct a code generation exception with the
 *              specified position and error message.
 *
 * @param position The position in the source file where the error was detected.
 * @param errorMsg A brief message about the nature of the error.
 */
class CodeGenException(position : Position, errorMsg : String)
    : CompilerException("Code Generation", position, errorMsg)
