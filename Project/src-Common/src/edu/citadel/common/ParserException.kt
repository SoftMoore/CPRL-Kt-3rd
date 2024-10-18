package edu.citadel.common

/**
 * Class for exceptions encountered during parsing.
 *
 * @constructor Construct a parser exception with the specified position
 *              and error message.
 *
 * @param position The position in the source file where the error was detected.
 * @param errorMsg A brief message about the nature of the error.
 */
class ParserException(position : Position, errorMsg : String)
    : CompilerException("Syntax", position, errorMsg)
