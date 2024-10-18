package edu.citadel.common

/**
 * Class for exceptions encountered during constraint analysis.
 */
class ConstraintException : CompilerException
  {
    /**
     * Construct a constraint exception with the specified position and error message.
     *
     * @param position The position in the source file where the error was detected.
     * @param errorMsg A brief message about the nature of the error.
     */
    constructor(position : Position, errorMsg : String)
        : super("Constraint", position, errorMsg)

    /**
     * Construct a constraint exception with the specified error message.
     *
     * @param errorMsg A brief message about the nature of the error.
     */
    constructor(errorMsg : String) : super("Constraint", errorMsg)
  }
