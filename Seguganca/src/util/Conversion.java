package util;

import java.math.BigInteger;

public class Conversion {
        
        /**
         * Transforms a byte value into a hex string.
         * @param b the byte to be transformed into a hex string.
         * @return
         * The byte b as a hex string.
         */
        public static String byteToHex(byte b)
        {
                return Integer.toString( ( b & 0xff ) + 0x100, 16).substring( 1 ).toUpperCase();
        }
        
        /**
         * Transforms a byte array into a hex string.
         * @param b the byte array to be transformed into a hex string.
         * @return
         * The byte array b as a hex string.
         */
        public static String byteToHex(byte[] b)
        {
                  String result = "";
                  for (int i = 0; i < b.length; i++) 
                          result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
                  return result.toUpperCase();
        }
        
        /**
         * Converts a hex string into a byte array.
         * @param s the hex string to be converted into a byte array.
         * @return
         * The hex string as a byte array.
         */
        public static byte[] hexStringToByteArray(String s) 
        {
                int len = s.length();
                byte[] data = new byte[len / 2];
                for (int i = 0; i < len; i += 2) {
                        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                                        .digit(s.charAt(i + 1), 16));
                }
                return data;
        }
        
        public static BigInteger byteArrayToBigInteger (byte[] M) {
                byte[] M1 = new byte[M.length + 1];
                M1[0] = 0;
                
                for (int i = 1; i < M1.length; i++)
                        M1[i] = M[i-1];
                
                return new BigInteger(M1);
        }
        
        public static byte[] bigIntegerToByteArray (BigInteger bi) {
                byte[] M1 = bi.toByteArray();
                
                if (M1[0] == 0 && M1.length > 1) {
                        byte[] M = new byte[M1.length - 1];
                        for (int i = 0; i < M.length; i++)
                                M[i] = M1[i+1];
                        
                        return M;
                }

                return M1;
        }

}