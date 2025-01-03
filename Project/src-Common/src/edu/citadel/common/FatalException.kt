package edu.citadel.common

/**
 * Class for fatal exceptions encountered during compilation.
 * A fatal exception is one for which compilation of the source
 * file should be abandoned.
 *
 * @constructor Construct a fatal exception with the specified error message.
 * @param       errorMsg A brief message about the nature of the error.
 */
class FatalException(errorMsg : String)
    : RuntimeException("*** Fatal exception: $errorMsg")
