package edu.citadel.common.util

/*
 * Utilities for recognizing several character classes.
 */
object CharUtil
  {
    /**
     * Returns true only if the specified character is a letter.<br></br>
     * `'A'..'Z' + 'a'..'z' (r.e. char class: [A-Za-z])`
     */
    fun isLetter(ch: Char): Boolean = ch in 'a'..'z' || ch in 'A'..'Z'

    /**
     * Returns true only if the specified character is a digit.<br></br>
     * `'0'..'9' (r.e. char class: [0-9])`
     */
    fun isDigit(ch: Char): Boolean = ch in '0'..'9'

    /**
     * Returns true only if the specified character is a letter or a digit.<br></br>
     * `'A'..'Z' + 'a'..'z + '0'..'9' (r.e. char class: [A-Za-z0-9])`
     */
    fun isLetterOrDigit(ch: Char): Boolean = isLetter(ch) || isDigit(ch)

    /**
     * Returns true only if the specified character is a binary digit ('0' or '1').
     */
    fun isBinaryDigit(ch : Char) : Boolean = ch == '0' || ch == '1'

    /**
     * Returns true only if the specified character is a hex digit
     * `'0'..'9' + 'A'..'F' + 'a'..'f'`
     */
    fun isHexDigit(ch : Char) : Boolean
        = (ch in '0'..'9') || (ch in 'a'..'f') || (ch in 'A'..'F')

    /*
     * Returns true if c is an escaped character.
     */
    fun isEscapeChar(ch : Char) : Boolean
      {
        return ch == '\t' || ch == '\n' || ch == '\r'
            || ch == '\"' || ch == '\'' || ch == '\\'
      }

    /**
     * Returns the uppercase equivalent of the character, if any;
     * otherwise, returns the character itself.
     */
    fun toUpperCase(ch : Char): Char
      {
        return if (ch in 'a'..'z') (ch - 32) else ch
      }

    /**
     * Unescapes characters.  For example, if the parameter c is a tab,
     * this method will return "\t"
     *
     * @return The string for an escaped character.
     */
    fun unescapedChar(c : Char) : String
      {
        return when (c)
          {
            '\t' -> "\\t"    // tab
            '\n' -> "\\n"    // newline
            '\r' -> "\\r"    // carriage return
            '\"' -> "\\\""   // double quote
            '\'' -> "\\\'"   // single quote
            '\\' -> "\\\\"   // backslash
            else -> c.toString()
          }
      }
  }
