package edu.citadel.common.util

object IntUtil
  {
    /**
     * Returns the integer value for a string.  Accepts prefixes 0b/0B for
     * binary and 0x/0X for hexadecimal.
     *
     * @throws NumberFormatException if the string does not contain a parsable
     *         integer that is valid for the CVM.
     */
    fun toInt(text: String): Int
      {
        if (text.startsWith("0B") || text.startsWith("0b"))
            return Integer.parseUnsignedInt(text.substring(2), 2)
        else if (text.startsWith("0X") || text.startsWith("0x`"))
            return Integer.parseUnsignedInt(text.substring(2), 16)
        else
            return text.toInt()
      }
  }