package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 
 * Conversor class
 * 
 */
public class Util {
	/**
	 * Convert a plain text String to an array of bytes
	 */
	public static byte[] convertStringToVector(String plainText) {
		int size = plainText.length();
		byte[] vectorBlock = new byte[size / 2];
		for (int i = 0; i < size; i += 2) {
			String sByte1 = plainText.substring(i, i + 1);
			String sByte2 = plainText.substring(i + 1, i + 2);

			int byte1 = (stringToByte(sByte1) << (byte) 0x04);
			;
			int byte2 = stringToByte(sByte2);

			vectorBlock[i / 2] = (byte) (byte1 ^ byte2);
		}
		return vectorBlock;
	}

	/**
	 * Convert a single character to a byte
	 */
	public static byte stringToByte(String sByte) {
		if (sByte.equals("0"))
			return (byte) 0x00;
		else if (sByte.equals("1"))
			return (byte) 0x01;
		else if (sByte.equals("2"))
			return (byte) 0x02;
		else if (sByte.equals("3"))
			return (byte) 0x03;
		else if (sByte.equals("4"))
			return (byte) 0x04;
		else if (sByte.equals("5"))
			return (byte) 0x05;
		else if (sByte.equals("6"))
			return (byte) 0x06;
		else if (sByte.equals("7"))
			return (byte) 0x07;
		else if (sByte.equals("8"))
			return (byte) 0x08;
		else if (sByte.equals("9"))
			return (byte) 0x09;
		else if (sByte.toLowerCase().equals("a"))
			return (byte) 0x0A;
		else if (sByte.toLowerCase().equals("b"))
			return (byte) 0x0B;
		else if (sByte.toLowerCase().equals("c"))
			return (byte) 0x0C;
		else if (sByte.toLowerCase().equals("d"))
			return (byte) 0x0D;
		else if (sByte.toLowerCase().equals("e"))
			return (byte) 0x0E;
		else
			return (byte) 0x0F;
	}

	/**
	 * Convert a single byte to it's hexadecimal value
	 */
	public static String byteToHex(byte b) {
		return Integer.toString((b & 0xFF) + 0x100, 16).substring(1);
	}

	/**
	 * Convert an array of bytes to a string
	 */
	public static String byteToHex(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++)
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		return result;
	}

	/**
	 * Convert a block (array of bytes) to a matrix representation
	 */
	public static void blockToMatrix(byte[] block, byte[][] matrix,
			boolean columnMapping) {
		int size = block.length / 3;
		if (columnMapping) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < size; j++) {
					matrix[i][j] = block[i + 3 * j];
				}
			}
		} else {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < size; j++) {
					matrix[i][j] = block[size * i + j];
				}
			}
		}
	}

	/**
	 * Convert a matrix to a block representation
	 */
	public static void matrixToBlock(byte[] block, byte[][] matrix,
			boolean columnMapping) {
		int size = matrix[0].length;
		if (columnMapping) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < size; j++) {
					block[i + 3 * j] = matrix[i][j];
				}
			}
		} else {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < size; j++) {
					block[size * i + j] = matrix[i][j];
				}
			}
		}
	}

	/**
	 * Copy a matrix
	 */
	public static void copyMatrix(byte[][] in, byte[][] out) {
		for (int i = 0; i < in.length; i++)
			for (int j = 0; j < in[i].length; j++)
				out[i][j] = in[i][j];
	}

	public static byte[] readFile(String filePath) throws java.io.IOException {
		File file = new File(filePath);
		FileInputStream fis = new FileInputStream(file);
		byte[] bytes = new byte[(int) file.length()];
		fis.read(bytes, 0, (int) file.length());
		return bytes;
	}

	public static boolean saveFile(String filePath, byte[] data)
			throws IOException {
		File file = new File(filePath);
		FileOutputStream fis = new FileOutputStream(file);
		fis.write(data);
		fis.close();
		return true;
	}

	/**
	 * Computes a XOR operation between thwo byte arrays
	 */
	public static byte[] xor(byte[] a, byte[] b) {
		byte[] output = new byte[a.length];

		for (int i = 0; i < a.length; i++)
			output[i] = (byte) (a[i] ^ b[i]);

		return output;
	}
	
	public static byte[] not (byte[] in) {
		byte[] output = new byte[in.length];
		
		for (int i = 0; i < in.length; i++)
			output[i] = (byte)(in[i] ^ 0xFF);
		
		return output;
	}
	
	public static byte[] and (byte[] a, byte[] b) {
		byte[] output = new byte[a.length];
		
		for (int i = 0; i < a.length; i++)
			output[i] = (byte)(a[i] & b[i]);
		
		return output;
	}

	public static byte[] multiplyByPx(byte[] input) {
		byte[] output = new byte[input.length];

		for (int i = 0; i < 9; i++) {
			output[i] = input[i + 1];
		}

		output[9] = (byte) (input[10] ^ T1(input[0]));
		output[10] = (byte) (input[11] ^ T0(input[0]));
		output[11] = input[0];

		return output;
	}

	static byte T1(byte U) {
		return (byte) (U ^ ((U & 0xFF) >>> 3) ^ ((U & 0xFF) >>> 5));
	}

	static byte T0(byte U) {
		return (byte) ((U << 5) ^ (U << 3));
	}

	public static byte[] lpad(byte[] message, int n) {
		byte[] leftPaddedMessage = new byte[n];

		for (int i = 0; i < n - message.length; i++)
			leftPaddedMessage[i] = 0;
		for (int i = n - message.length; i < n; i++) {
			leftPaddedMessage[i] = message[i + message.length - n];
		}

		return leftPaddedMessage;
	}

	public static byte[] rpad(byte[] message, int n) {
		byte[] rightPaddedMessage = new byte[n];

		for (int i = 0; i < message.length; i++)
			rightPaddedMessage[i] = message[i];
		for (int i = message.length; i < n; i++)
			rightPaddedMessage[i] = 0;

		return rightPaddedMessage;
	}
}
