package util;

/**
 * 
 * Conversor class
 *
 */
public class Conversor {
	/**
	 * Convert a plain text String to an array of bytes
	 * @param plainText
	 * @return array of bytes
	 */
	public static byte[] convertStringToVector(String plainText){
		if(plainText.length() != 24 && plainText.length() != 36 && plainText.length() != 48){
			return null;
		}
		else{
			int size = plainText.length();
			byte[] vectorBlock = new byte[size / 2];
			for(int i = 0; i < size; i+=2){
				String sByte1 = plainText.substring(i, i + 1);
				String sByte2 = plainText.substring(i + 1, i + 2);
				
				int byte1 = (stringToByte(sByte1) << (byte)0x04);;
				int byte2 = stringToByte(sByte2);
				
				vectorBlock[i/2] = (byte)(byte1 ^ byte2);
			}
			return vectorBlock;
		}
	}
	
	public static byte stringToByte(String sByte){
		if(sByte.equals("0"))
			return (byte)0x00;
		else if(sByte.equals("1"))
			return (byte)0x01;
		else if(sByte.equals("2"))
			return (byte)0x02;
		else if(sByte.equals("3"))
			return (byte)0x03;
		else if(sByte.equals("4"))
			return (byte)0x04;
		else if(sByte.equals("5"))
			return (byte)0x05;
		else if(sByte.equals("6"))
			return (byte)0x06;
		else if(sByte.equals("7"))
			return (byte)0x07;
		else if(sByte.equals("8"))
			return (byte)0x08;
		else if(sByte.equals("9"))
			return (byte)0x09;
		else if(sByte.equals("a"))
			return (byte)0x0A;
		else if(sByte.equals("b"))
			return (byte)0x0B;
		else if(sByte.equals("c"))
			return (byte)0x0C;
		else if(sByte.equals("d"))
			return (byte)0x0D;
		else if(sByte.equals("e"))
			return (byte)0x0E;
		else
			return (byte)0x0F;
	}
	
	public static String byteToHex(byte b){
		return Integer.toString((b & 0xFF) + 0x100, 16).substring(1);
	}

	public static String byteToHex(byte[] b){
		String result = "";
		for (int i = 0; i < b.length; i++)
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		return result;
	}
	
	/**
	 * Convert a vector representing a block to a matrix representing a M2t matrix
	 * @param block Block of byte
	 * @param matrix Matrix of byte
	 * @param columnMapping Boolean
	 */
	public static void blockToMatrix(byte[] block, byte[][] matrix, boolean columnMapping){
		int size = block.length / 3;
		if(columnMapping){
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < size; j++) {
					matrix[i][j] = block [i + 3 * j];
				}
			}
		}
		else{
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < size; j++) {
					matrix[i][j] = block [size * i + j];
				}
			}
		}
			
	}
	/**
	 * Convert a matrix representing a M2t matrix a to vector representing a block
	 * @param block Block of byte
	 * @param matrix Matrix of byte
	 */
	public static void matrixToBlock(byte[] block, byte[][] matrix, boolean columnMapping){
		int size = matrix[0].length;
		if(columnMapping){
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < size; j++) {
					block [i + 3 * j] = matrix[i][j];
				}
			}
		}
		else{
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < size; j++) {
					block [size * i + j] = matrix[i][j];
				}
			}
		}
	}
}
