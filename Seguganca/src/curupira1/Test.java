package curupira1;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Set 3, vector# 252
		// key		= 		fcfcfcfcfcfcfcfcfcfcfcfc
		// plain 	= 		00fcfcfc00fcfcfc00fcfcfc
		// cipher 	= 		2efe828244b459535d3d28ad		
		//byte[] cipherKey  = {	(byte)0xfc, (byte)0xfc, (byte)0xfc, (byte)0xfc, (byte)0xfc, (byte)0xfc, (byte)0xfc, (byte)0xfc, (byte)0xfc, (byte)0xfc, (byte)0xfc, (byte)0xfc };
		//byte[] plaintext  = {	(byte)0x00, (byte)0xfc, (byte)0xfc, (byte)0xfc, (byte)0x00, (byte)0xfc, (byte)0xfc, (byte)0xfc, (byte)0x00, (byte)0xfc, (byte)0xfc, (byte)0xfc};
		
		// Set 1, vector# 0:
		// key 		= 		800000000000000000000000
		// plain 	= 		000000000000000000000000
		// cipher 	= 		126b5eae509b2e929b1b08ff		
		//byte[] cipherKey  = {	(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00 };
		//byte[] plaintext  = {	(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00 };		
		
		// New Vector with round values
		// key 		= 		000000000000000000000000
		// plain 	= 		000000000000000000000000
		// cipher 	= 		126b5eae509b2e929b1b08ff		
		byte[] cipherKey  = {	(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00 };
		byte[] plaintext  = {	(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00 };
		
		
		byte[] ciphertext = new byte[plaintext.length]; 		
		
		// TODO: O RESULTADO NÃO ESTÁ BATENDO!!! =(
		System.out.printf("plaintext  = %s\n", byteToHex(plaintext));
		Curupira1 c = new Curupira1();
		c.makeKey(cipherKey, cipherKey.length*8);
		c.encrypt(plaintext, ciphertext);
		System.out.printf("ciphertext = %s\n\n", byteToHex(ciphertext));
		
		/*		
		// S-Box
		// comparação com a tabela completa
		System.out.printf("-- S-Box\n");
		byte b = (byte)0x8e;
		System.out.printf("S[%s] = %s\n\n", byteToHex(b), byteToHex(c.SBox(b)));
		
		// CAMADA NÃO-LINEAR
		// teste de involução
		System.out.printf("-- Não-Linear\n");
		printMatrix(A);
		c.nonlinearGama(A);
		printMatrix(A);
		c.nonlinearGama(A);
		printMatrix(A);
		
		// CAMADA DE PERMUTAÇÃO
		// teste de involução
		System.out.printf("-- Permutação\n");
		printMatrix(A);
		c.permutationPi(A);
		printMatrix(A);
		c.permutationPi(A);
		printMatrix(A);
		
		// CAMADA DE DIFUSÃO LINEAR
		// teste de involução
		System.out.printf("-- Difusão Linear\n");
		printMatrix(A);
		c.lineardiffusionTheta(A);
		printMatrix(A);
		c.lineardiffusionTheta(A);
		printMatrix(A);
		*/
	}
	
	public static void printVector(byte[] A)
	{
		for (int i = 0; i < A.length; i++) 
            System.out.printf("%3s ", byteToHex(A[i]));		
		System.out.println();
	}
	
	public static void printMatrix(byte[][] A)
	{
		for (int i = 0; i < A.length; i++) 
			printVector(A[i]);
		System.out.println();
	}
	
	public static String byteToHex(byte b)
	{
		return Integer.toString( ( b & 0xff ) + 0x100, 16).substring( 1 );
	}
	
	public static String byteToHex(byte[] b)
	{
		  String result = "";
		  for (int i = 0; i < b.length; i++) 
			  result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		  return result;
		}
}
