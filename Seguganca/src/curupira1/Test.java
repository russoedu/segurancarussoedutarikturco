package curupira1;

public class Test {
	public static void main(String[] args) {
//		byte[] cipherKey = { 	(byte) 0x01, (byte) 0x02, (byte) 0x03,
//								(byte) 0x04, (byte) 0x05, (byte) 0x06,
//								(byte) 0x07, (byte) 0x08, (byte) 0x09,
//								(byte) 0x0A, (byte) 0x0B, (byte) 0x0C};
//		byte[] plainText = { 	(byte) 0x01, (byte) 0x02, (byte) 0x03,
//								(byte) 0x04, (byte) 0x05, (byte) 0x06,
//								(byte) 0x07, (byte) 0x08, (byte) 0x09,
//								(byte) 0x0A, (byte) 0x0B, (byte) 0x0C};

		byte[] cipherKey = { 	(byte) 0x00, (byte) 0x00, (byte) 0x00,
								(byte) 0x00, (byte) 0x00, (byte) 0x00,
								(byte) 0x00, (byte) 0x00, (byte) 0x00,
								(byte) 0x00, (byte) 0x00, (byte) 0x00};
				
		byte[] plainText = { 	(byte) 0x00, (byte) 0x00, (byte) 0x00,
								(byte) 0x00, (byte) 0x00, (byte) 0x00,
								(byte) 0x00, (byte) 0x00, (byte) 0x00,
								(byte) 0x00, (byte) 0x00, (byte) 0x00};

		byte[] cipherText = new byte[plainText.length];
		Curupira1 c = new Curupira1();
		
//		byte[][] teste = new byte[3][4];
//		c.blockToMatrix(cipherKey, teste, false);
//		c.permutationLayerPi(teste);
//		Curupira1.printMatrix("teste", teste);
		
		c.makeKey(cipherKey, cipherKey.length * 8);
		c.encrypt(plainText, cipherText);
	}
}
