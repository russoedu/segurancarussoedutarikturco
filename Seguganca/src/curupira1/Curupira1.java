package curupira1;

import pcs2055.BlockCipher;

public class Curupira1 implements BlockCipher {
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * Local vars
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	private int blockBits;
	private int keyBits;
	/**
	 * The cipher key
	 */
	private byte[] cipherKey;

	// Although it's not a good Java convention, we used uppercase names to
	// match the paper names
	private byte[] P = { 0x03, 0x0F, 0x0E, 0x00, 0x05, 0x04, 0x0B, 0x0C, 0x0D, 0x0A, 0x09, 0x06, 0x07, 0x08, 0x02, 0x01 };
	private byte[] Q = { 0x09, 0x0E, 0x05, 0x06, 0x0A, 0x02, 0x03, 0x0C, 0x0F, 0x00, 0x04, 0x0D, 0x07, 0x0B, 0x01, 0x08 };

	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * Inherited abstract methods
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	
	public int blockBits() {
		return this.blockBits;
	}

	public int keyBits() {
		return this.keyBits;
	}

	public void makeKey(byte[] cipherKey, int keyBits) {
		//Testado
		this.cipherKey = cipherKey;
		this.keyBits = keyBits;
	}

	public void encrypt(byte[] mBlock, byte[] cBlock) {
		// Cipher Key
		printVector("cipherKey", cipherKey);
		int t = this.keyBits/48;
		byte[][] key = new byte[3][2*t];
		blockToMatrix(this.cipherKey, key, true);
		printMatrix("Cipher Key Matrix", key);

		// Plain text
		printVector("plainText", mBlock);
		byte[][] plainTextMatrix = new byte[3][4];
		blockToMatrix(mBlock, plainTextMatrix, false);
		printMatrix("PlainText Matrix", plainTextMatrix);
		
		int nrRounds = 4 * t + 2; 
		
		// initial key addition (whitening)
		constantAdditionLayerSigma(key, 0);
		cyclicShiftLayerCsi(key);
		printMatrix("Initial key addition", key);
		linearDiffusionLayerMi(key, false);
		printMatrix("Linear diffusion", key);
		linearDiffusionLayerMi(key, false);
		printMatrix("Linear diffusion", key);
		
		keyAdditionLayerSigma(plainTextMatrix, keySelectionPhi(key));

		printMatrix("-- 1st add round key", plainTextMatrix);
		
		// round function
		for (int round = 1; round < nrRounds; round ++) 
		{
			// Key Evolution
			constantAdditionLayerSigma(key, round);
			cyclicShiftLayerCsi(key);
			linearDiffusionLayerMi(key, false);
			
			// round cipher
			nonLinearLayerGama(plainTextMatrix);
			permutationLayerPi(plainTextMatrix);
			linearDiffusionLayerTheta(plainTextMatrix);
			keyAdditionLayerSigma(plainTextMatrix, keySelectionPhi(key));
			
			printMatrix("Round " + round, plainTextMatrix);
		}
		
		// last round function
		constantAdditionLayerSigma(key, nrRounds - 1);
		cyclicShiftLayerCsi(key);
		linearDiffusionLayerMi(key, false);
		nonLinearLayerGama(plainTextMatrix);
		permutationLayerPi(plainTextMatrix);
		keyAdditionLayerSigma(plainTextMatrix, keySelectionPhi(key));
				
		// End
		matrixToBlock(cBlock, plainTextMatrix);
		printMatrix("End", plainTextMatrix);
		printVector(cBlock);
	}

	public void decrypt(byte[] cBlock, byte[] mBlock) {
		// TODO
	}
	
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * The CURUPIRA-1 round methods
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	
	/**
	 * The non-linear layer 'gama'
	 * gama(a) = b <=> b[i][j] = S[a[i][j]]
	 * @param Matrix of byte[][]
	 */
	public void nonLinearLayerGama (byte[][] matrix){
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[i].length; j++){
				matrix[i][j] = this.S(matrix[i][j]);
			}
		}
	}
	
	/**
	 * The permutation layer 'pi'
	 * pi(a) = b <=> b[i][j] = a[i][i ^ j]
	 * @param Matrix of byte[][]
	 */
	public void permutationLayerPi(byte[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			byte[] rowCopy = matrix[i];
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] = rowCopy[i ^ j];
			}
		}
	}

	/**
	 * The linear diffusion layer 'theta'
	 * theta(a) = b <=> b = D * a
	 * Calculated using Algorithm 2
	 * @param Matrix of [][]byte
	 */
	public void linearDiffusionLayerTheta(byte[][] matrix) {
		byte v;
		byte w;
		for(int j = 0; j < matrix[0].length; j++){
			v = xTimes((byte)(matrix[0][j] ^ matrix[1][j] ^ matrix[2][j]));
			w = xTimes(v);
			
			matrix[0][j] = (byte)(matrix[0][j] ^ v); 
			matrix[1][j] = (byte)(matrix[1][j] ^ w);
			matrix[2][j] = (byte)(matrix[2][j] ^ v ^ w);
		}
	}

	/**
	 * The key addition layer 'sigma'[k]
	 * sigma[k](a) = b <=> b[i][j] = a[i][j] ^ k[i][j]
	 * @param Matrix of byte[][] k
	 * @param Matrix of byte[][] a
	 * @return Matrix of byte[][] b
	 */
	public void keyAdditionLayerSigma(byte[][] matrix, byte[][] k) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] = (byte) (matrix[i][j] ^ k[i][j]);
			}
		}
	}
	
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * Schedule methods
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	/**
	 * The constant addition layer 'sigma'
	 * @param Matrix of byte[][]
	 * @param Byte round
	 */
	public void constantAdditionLayerSigma(byte[][] matrix, int round){
		byte q[][] = q(round);
		
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				matrix[i][j] = (byte)(matrix[i][j] ^ q[i][j]);
	}

	/**
	 * The cyclic shift layer 'csi'
	 * Csi(a) = b <=> 	b[0][j] = a[0][j]
	 * 					b[1][j] = a[1][(j + 1) mod 2t]
	 * 					b[2][j] = a[2][(j - 1) mod 2t] 
	 * @param Matrix of byte[][] a
	 * @return Matrix of byte[][] b
	 */
	public void cyclicShiftLayerCsi(byte[][] matrix){
		//i = 0
		matrix[0] = matrix[0];
		//i = 1
		for(int j = 0; j < (matrix.length + 1); j++){
			matrix[1][j] = matrix[1][(j + 1) % (matrix.length + 1)];
		}
		//i = 2
		for(int j = 0; j < (matrix.length + 1); j++){
			if (j == 0){
				matrix[2][j] = matrix[2][matrix.length];
			}
			else{
				matrix[2][j] = matrix[2][j - 1];
			}
		}
	}
	
	/**
	 * The linear diffusion layer 'mi'
	 * mi(a) = E * a, where E = I + c * C
	 * Calculated using Algorithm 3
	 * @param Matrix of byte[][]
	 * @param Boolean invert [select E (false) or E^-1 (true)]
	 * @return Matrix of byte[][] b = E * a 
	 */
	public void linearDiffusionLayerMi(byte[][] matrix, boolean invert){
		byte v;
		for(int j = 0; j < matrix[0].length; j++){
			v = (byte)(matrix[0][j] ^ matrix[1][j] ^ matrix[2][j]);
			
			if (invert) {
				v = (byte)(cTimes(v) ^ v);
			}
			else{
				v = cTimes(v);
			}
			
			matrix[0][j] = (byte)(matrix[0][j] ^ v); 
			matrix[1][j] = (byte)(matrix[1][j] ^ v);
			matrix[2][j] = (byte)(matrix[2][j] ^ v);
		}
	}
	
	/**
	 * The key selection 'phi'
	 * k(r) = phi[r](K) <=> k[0][j](r) = S[K[0][j](r)] and k[i][j](r) = K[i][j](r) for i > 0 and 0 <= j < 4.
	 * @param Matrix of byte[][] K
	 * @return the truncated cipher key matrix
	 */
	public byte[][] keySelectionPhi(byte[][] K){ 
		byte[][] k = new byte[3][4];
		for(int i = 0; i < K.length; i ++){
			for (int j = 0; j < K[i].length; j++){
				if(i == 0){
					k[i][j] = S(K[0][j]);
				}
				else{
					k[i][j] = K[i][j];
				}
			}
		}
		return k;
	}

	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * General
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	
	/**
	 * xTimes
	 * @param byte b
	 * @return xTimes(b)
	 */
	private byte xTimes(byte b){
		return (byte)((b << 1) ^ (((b >> 7) & 1) * 0x1B));
	}
	
	/**
	 * cTimes
	 * @param bytex
	 * @return cTimes(b)
	 */
	private byte cTimes(byte b){		
		return (byte)xTimes(xTimes((byte)(xTimes((byte)(xTimes(b) ^ b)) ^ b)));
	}

	
	/**
	 * The S computing
	 * Computes S[u] from the mini-boxes P and Q
	 * @param byte $u
	 * @return byte S[$u]
	 */
	public byte S(byte u) {
		byte uh1 = P[(u >> 4) & 0x0F];
		byte ul1 = Q[u & 0x0F];

		byte uh2 = Q[(uh1 & 0x0C) ^ ((ul1 >> 0x02) & 0x03)];
		byte ul2 = P[((uh1 << 0x02) & 0x0C) ^ (ul1 & 0x03)];

		uh1 = P[(uh2 & 0x0C) ^ ((ul2 >> 0x02) & 0x03)];
		ul1 = Q[((uh2 << 0x02) & 0x0C) ^ (ul2 & 0x03)];

		return (byte) ((uh1 << 0x04) ^ ul1);
	}
	
	/**
	 * The schedule constants q
	 * q(0) = 0
	 * q[i][j](s) = S[2t(s-1) + j]		if i=0
	 * q[i][j](s) = 0 					otherwise
	 * @param Matrix of byte[][] s
	 * @return Matrix of byte[][] q(s)
	 */
	public byte[][] q(int s){
		int t = keyBits / 48;
		byte[][] q = new byte[3][2*t];
		
		//q(0) = 0
		if(s == 0x00){
			for (int i = 0; i < 3; i++){
				for (int j = 0; j < (2 * t); j++){
					q[i][j] = 0x00;
				}
			}
		}
		else
		{
			for (int i = 0; i < 3; i++){
				for (int j = 0; j < (2 * t); j++){
					//i = 0
					if(i == 0){
						q[i][j] = S((byte)(0x02 * t * (s - 0x01) + j));
					}
					//otherwise
					else{
						q[i][j] = 0x00;
					}
				}
			}
		}
		return q;
	}
	
	/**
	 * Convert a vector representing a block to a matrix representing a M2t matrix
	 * @param Block of byte[]
	 * @param Matrix of byte[][]
	 * @param Boolean columnMapping
	 */
	public void blockToMatrix(byte[] block, byte[][] matrix, boolean columnMapping){
		if(columnMapping){
			int t = block.length / 3;
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < t; j++) {
					matrix[i][j] = block [i + 3 * j];
				}
			}
		}
		else{
			int size = block.length / 3;
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < size; j++) {
					matrix[i][j] = block [size * i + j];
				}
			}
		}
			
	}
	/**
	 * Convert a matrix representing a M2t matrix a to vector representing a block
	 * @param Block of byte[]
	 * @param Matrix of byte[][]
	 */
	private void matrixToBlock(byte[] block, byte[][] matrix){
		int size = matrix[0].length;
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < size; j++) {
				block [size * i + j] = matrix[i][j];
			}
		}
	}
	
	
	public static void printVector(String name, byte[] A){
		System.out.printf(name + ": ");
		for (int i = 0; i < A.length; i++){ 
            System.out.printf("%2s ", byteToHex(A[i]));
		}
		System.out.println();
	}
	
	public static void printVector(byte[] A){
		for (int i = 0; i < A.length; i++){ 
            System.out.printf("%2s ", byteToHex(A[i]));
		}
		System.out.println();
	}
	
	public static void printMatrix(String name, byte[][] A){
		System.out.println(name);
		for (int i = 0; i < A.length; i++){ 
			printVector(A[i]);
		}
		System.out.println();
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
}
