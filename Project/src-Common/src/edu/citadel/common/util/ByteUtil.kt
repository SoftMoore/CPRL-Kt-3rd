package edu.citadel.common.util

/**
 * Methods to convert integers and characters to byte representations, and vice versa.
 */
object ByteUtil
  {
    private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()

    /**
     * Convert a single byte to a string of 2 hexadecimal digits.
     */
    fun byteToHexString(b : Byte) : String
      {
        val builder = StringBuilder(2)
        val n : Int = if (b > 0) b.toInt() else (b.toInt() + 256) % 256
        builder.append(HEX_ARRAY[n ushr 4])
               .append(HEX_ARRAY[n and 0x0F])
        return builder.toString()
      }

    /**
     * Convert a 2-byte char to a string of 4 hexadecimal digits.
     */
    fun charToHexString(c : Char) : String
      {
        val builder = StringBuilder(2)
        builder.append(HEX_ARRAY[c.code ushr 12])
               .append(HEX_ARRAY[c.code and 0x0F00 shr 8])
               .append(HEX_ARRAY[c.code and 0x00F0 shr 4])
               .append(HEX_ARRAY[c.code and 0x000F])
        return builder.toString()
      }

    /**
     * Convert an array of bytes to a string of hexadecimal digits separated by spaces.
     */
    fun bytesToHexString(bytes : ByteArray) : String
      {
        val builder = StringBuilder(bytes.size*3)
        for (b in bytes)
            builder.append(byteToHexString(b))

        return builder.toString()
      }

    /**
     * Converts 2 bytes to a Char.  The bytes passed as arguments are
     * ordered with b1 as the high-order byte and b0 as the low-order byte.
     */
    fun bytesToChar(b1 : Byte, b0 : Byte) : Char
        = ((b1.toInt() shl 8 and 0x0000FF00) or
           (b0.toInt() and 0x000000FF)).toChar()

    /**
     * Converts 4 bytes to an Int.  The bytes passed as arguments are
     * ordered with b3 as the high-order byte and b0 as the low-order byte.
     */
    fun bytesToInt(b3 : Byte, b2 : Byte, b1 : Byte, b0 : Byte) : Int
        =    (b3.toInt() shl 24 and -0x1000000
          or (b2.toInt() shl 16 and 0x00FF0000)
          or (b1.toInt() shl 8  and 0x0000FF00)
          or (b0.toInt()        and 0x000000FF))

    /**
     * Converts a byte to an int.  The specified byte is the low-order
     * (least significant) byte for the int and the three high-order
     * bytes are all zero.
     */
    fun byteToInt(b: Byte): Int
      {
        val zero = 0.toByte()
        return bytesToInt(zero, zero, zero, b)
      }

    /**
     * Converts a char to an int.  The specified char is in the two
     * low-order (least significant) bytes for the int.  The two
     * high-order bytes are both zero.
     */
    fun charToInt(c: Char): Int
      {
        val zero = 0.toByte()
        val charBytes = charToBytes(c)
        return bytesToInt(zero, zero, charBytes[0], charBytes[1])
      }

    /**
     * Converts a Char to an array of 2 bytes.  The bytes in the return
     * array are ordered with the byte at index 0 as the high-order byte
     * and the byte at index 1 as the low-order byte.
     */
    fun charToBytes(c : Char) : ByteArray
      {
        val result = ByteArray(2)
        result[0] = (c.code.ushr(8) and 0xFF).toByte()
        result[1] = (c.code.ushr(0) and 0xFF).toByte()
        return result
      }

    /**
     * Converts a Short to an array of 2 bytes.  The bytes in the return
     * array are ordered with the byre at index 0 as the high-order byte
     * and the byte at index 1 as the low-order byte.
     */
    fun shortToBytes(n : Short) : ByteArray
      {
        val result = ByteArray(2)
        result[0] = (n.toInt().ushr(8) and 0xFF).toByte()
        result[1] = (n.toInt().ushr(0) and 0xFF).toByte()
        return result
      }

    /**
     * Converts an Int to an array of 4 bytes.  The bytes in the return
     * array are ordered with the byte at index 0 as the high-order byte
     * and the byte at index 3 as the low-order byte.
     */
    fun intToBytes(n : Int) : ByteArray
      {
        val result = ByteArray(4)
        result[0] = (n.ushr(24) and 0xFF).toByte()
        result[1] = (n.ushr(16) and 0xFF).toByte()
        result[2] = (n.ushr(8)  and 0xFF).toByte()
        result[3] = (n.ushr(0)  and 0xFF).toByte()
        return result
      }

    /**
     * Returns the low-order (least significant) byte of the specified integer.
     */
    fun intToByte(n: Int): Byte = intToBytes(n)[3]

    /**
     * Returns a char formed from the two low-order (least significant)
     * bytes of the specified integer.
     */
    fun intToChar(n: Int): Char
      {
        val intBytes = intToBytes(n)
        return bytesToChar(intBytes[2], intBytes[3])
      }
  }
