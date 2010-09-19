package curupira1;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Set 3, vector# 252
		// key = fcfcfcfcfcfcfcfcfcfcfcfc
		// plain = 00fcfcfc00fcfcfc00fcfcfc
		// cipher = 2efe828244b459535d3d28ad
		// byte[] cipherKey = { (byte)0xfc, (byte)0xfc, (byte)0xfc, (byte)0xfc,
		// (byte)0xfc, (byte)0xfc, (byte)0xfc, (byte)0xfc, (byte)0xfc,
		// (byte)0xfc, (byte)0xfc, (byte)0xfc };
		// byte[] plaintext = { (byte)0x00, (byte)0xfc, (byte)0xfc, (byte)0xfc,
		// (byte)0x00, (byte)0xfc, (byte)0xfc, (byte)0xfc, (byte)0x00,
		// (byte)0xfc, (byte)0xfc, (byte)0xfc};

		// Set 1, vector# 0:
		// key = 800000000000000000000000
		// plain = 000000000000000000000000
		// cipher = 126b5eae509b2e929b1b08ff
		// byte[] cipherKey = { (byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00,
		// (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
		// (byte)0x00, (byte)0x00, (byte)0x00 };
		// byte[] plaintext = { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
		// (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
		// (byte)0x00, (byte)0x00, (byte)0x00 };

		// New Vector with round values
		// key = 	00 00 00 00 00 00 00 00 00 00 00 00
		// plain = 	00 00 00 00 00 00 00 00 00 00 00 00
		// cipher = 12 6b 5e ae 50 9b 2e 92 9b 1b 08 ff
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
		c.makeKey(cipherKey, cipherKey.length * 8);
		c.encrypt(plainText, cipherText);

		/*
		 * // S-Box // comparação com a tabela completa
		 * System.out.printf("-- S-Box\n"); byte b = (byte)0x8e;
		 * System.out.printf("S[%s] = %s\n\n", byteToHex(b),
		 * byteToHex(c.SBox(b)));
		 * 
		 * // CAMADA NÃO-LINEAR // teste de involução
		 * System.out.printf("-- Não-Linear\n"); printMatrix(A);
		 * c.nonlinearGama(A); printMatrix(A); c.nonlinearGama(A);
		 * printMatrix(A);
		 * 
		 * // CAMADA DE PERMUTAÇÃO // teste de involução
		 * System.out.printf("-- Permutação\n"); printMatrix(A);
		 * c.permutationPi(A); printMatrix(A); c.permutationPi(A);
		 * printMatrix(A);
		 * 
		 * // CAMADA DE DIFUSÃO LINEAR // teste de involução
		 * System.out.printf("-- Difusão Linear\n"); printMatrix(A);
		 * c.lineardiffusionTheta(A); printMatrix(A); c.lineardiffusionTheta(A);
		 * printMatrix(A);
		 */
	}
}
