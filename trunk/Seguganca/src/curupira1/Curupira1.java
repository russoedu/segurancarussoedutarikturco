package curupira1;

import pcs2055.BlockCipher;
import util.Conversor;
import util.Printer;

public class Curupira1 implements BlockCipher {
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * Local vars
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	private int blockBits;
	private int keyBits;
	private byte[] cipherKey;
	private int t;
	private int numberOfRounds;
	private boolean debug = false;

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
		this.cipherKey = cipherKey;
		this.keyBits = keyBits;
		this.t = keyBits/48;
		this.numberOfRounds = 4 * t + 2;	
	}

	public void encrypt(byte[] mBlock, byte[] cBlock) {				
		// Plain text to matrix
		byte[][] blockMatrix = new byte[3][4];
		Conversor.blockToMatrix(mBlock, blockMatrix, true);
		if(debug)
			Printer.printMatrix("------- Plaintext -------", blockMatrix);
		
		// Cipher key to matrix
		byte[][] key = new byte[3][2 * t];
		Conversor.blockToMatrix(this.cipherKey, key, true);
		if(debug)
			Printer.printMatrix("------- Key -------", key);
		
		//Initial key addition
		keyAdditionLayerSigma(blockMatrix, keySelectionPhi(key));
		
		if(debug)
			Printer.printMatrix("****** Encryption ******\n-- 1st add round key", blockMatrix);
		// round functions
		for (int round = 1; round < numberOfRounds; round ++){
			// Key evolution 'psi
			keyEvolutionPsi(key, round, false);

			// round function
			roundFunctions(blockMatrix, key);
			
			if(debug)
				Printer.printMatrix("--- Round " + round + " ---", blockMatrix);
		}
		//last round function
		nonLinearLayerGama(blockMatrix);
		permutationLayerPi(blockMatrix);
		
		keyEvolutionPsi(key, numberOfRounds, false);
		
		keyAdditionLayerSigma(blockMatrix, keySelectionPhi(key));
		
		if(debug)
			Printer.printMatrix("--- Ciphertext ---", blockMatrix);
		
		byte[] blockReturn = new byte[blockMatrix[0].length * 3];
		Conversor.matrixToBlock(blockReturn, blockMatrix, true);
		Printer.printVector("nosso  ", blockReturn);
	}

	public void decrypt(byte[] cBlock, byte[] mBlock) {
		// TODO
	}
	
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * The CURUPIRA-1 round methods
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	/**
	 * The round functions<br/>
	 * Executes 'gama', 'pi', 'theta' and 'sigma' in sequence
	 * @param textMatrix Matrix of byte
	 * @param key Matrix of byte 
	 */
	private void roundFunctions(byte[][] textMatrix, byte[][] key){
		nonLinearLayerGama(textMatrix);
		permutationLayerPi(textMatrix);
		linearDiffusionLayerTheta(textMatrix);
		keyAdditionLayerSigma(textMatrix, keySelectionPhi(key));
	}
	
	/**
	 * The non-linear layer 'gama'<br/>
	 * gama(a) = b <=> b[i][j] = S[a[i][j]]
	 * @param textMatrix Matrix of byte
	 */
	private void nonLinearLayerGama (byte[][] textMatrix){
		for (int i = 0; i < textMatrix.length; i++){
			for (int j = 0; j < textMatrix[i].length; j++){
				textMatrix[i][j] = this.S(textMatrix[i][j]);
			}
		}
	}
	
	/**
	 * The permutation layer 'pi'<br/>
	 * pi(a) = b <=> b[i][j] = a[i][i ^ j]
	 * @param textMatrix Matrix of byte
	 */
	public void permutationLayerPi(byte[][] textMatrix) {
		for (int i = 1; i < 3; i++) {
			
			//Copy the row
			byte[] rowCopy = new byte[4];
			for(int k = 0; k < 4; k++){
				rowCopy[k] = textMatrix[i][k];
			}
			
			// swap the values
			for (int j = 0; j < 4; j++) {
					textMatrix[i][j] = rowCopy[(i ^ j)];
			}
		}
	}

	/**
	 * The linear diffusion layer 'theta'<br/>
	 * theta(a) = b <=> b = D * a
	 * Calculated using Algorithm 2
	 * @param textMatrix Matrix of byte
	 */
	private void linearDiffusionLayerTheta(byte[][] textMatrix) {
		int v;
		int w;
		for(int j = 0; j < textMatrix[0].length; j++){
			v = xtimes(0xFF & (textMatrix[0][j] ^ textMatrix[1][j] ^ textMatrix[2][j]));
			w = xtimes(v);
			
			textMatrix[0][j] = (byte)(textMatrix[0][j] ^ v); 
			textMatrix[1][j] = (byte)(textMatrix[1][j] ^ w);
			textMatrix[2][j] = (byte)(textMatrix[2][j] ^ v ^ w);
		}	
	}

	/**
	 * The key addition layer 'sigma'[k]<br/>
	 * sigma[k](a) = b <=> b[i][j] = a[i][j] ^ k[i][j]
	 * @param textMatrix Matrix of byte
	 * @param key Matrix of byte
	 */
	private void keyAdditionLayerSigma(byte[][] textMatrix, byte[][] key) {
		for (int i = 0; i < textMatrix.length; i++) {
			for (int j = 0; j < textMatrix[i].length; j++) {
				textMatrix[i][j] = (byte) (textMatrix[i][j] ^ key[i][j]);
			}
		}
	}
	
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * Schedule methods
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	
	/**
	 * The key evolution 'psi'<br/>
	 * Executes 'sigma', 'csi' and 'mi' in sequence
	 * @param key Matrix of byte
	 * @param round int
	 * @param invert boolean
	 * 
	 */
	private void keyEvolutionPsi(byte[][] key, int round, boolean invert){
		constantAdditionLayerSigma(key, round);
		cyclicShiftLayerCsi(key);
		linearDiffusionLayerMi(key, invert);
	}
	/**
	 * The constant addition layer 'sigma'<br/>
	 * @param key Matrix of byte[][]
	 * @param round int
	 */
	private void constantAdditionLayerSigma(byte[][] key, int round){
		byte q[][] = q(round);
		
		for (int i = 0; i < key.length; i++)
			for (int j = 0; j < key[i].length; j++)
				key[i][j] = (byte)(key[i][j] ^ q[i][j]);
	}

	/**
	 * The cyclic shift layer 'csi'<br/>
	 * Csi(a) = b <=> 	b[0][j] = a[0][j]<br/>
	 * 					b[1][j] = a[1][(j + 1) mod 2t]<br/>
	 * 					b[2][j] = a[2][(j - 1) mod 2t]<br/>
	 * @param Matrix of byte[][] a
	 * @return Matrix of byte[][] b
	 */
	private void cyclicShiftLayerCsi(byte[][] key){
		int size = key[0].length;
		//i = 0
		//do nothing
		
		//i = 1 -> shift left
		byte auxKey = key[1][0];
		for(int j = 0; j < size - 1; j++){
			key[1][j] = key[1][(j + 1)];
		}
		key[1][size - 1] = auxKey;
		
		//i = 2 -> shift right
		auxKey = key[2][size - 1];
		for(int j = size - 1; j > 0; j--){
			key[2][j] = key[2][j - 1];
		}
		key[2][0] = auxKey;
	}
	
	/**
	 * The linear diffusion layer 'mi'<br/>
	 * mi(a) = E * a, where E = I + c * C<br/>
	 * Calculated using Algorithm 3
	 * @param Matrix of byte[][]
	 * @param Boolean invert [select E (false) or E^-1 (true)]
	 * @return Matrix of byte[][] b = E * a 
	 */
	private void linearDiffusionLayerMi(byte[][] key, boolean invert){
		int v;
		for(int j = 0; j < key[0].length; j++){
			v = (key[0][j] ^ key[1][j] ^ key[2][j]) & 0xFF;
			
			if (invert) {
				v = (byte)ctimes(v) ^ v;
			}
			else{
				v = ctimes(v);
			}
			
			key[0][j] = (byte)(key[0][j] ^ v); 
			key[1][j] = (byte)(key[1][j] ^ v);
			key[2][j] = (byte)(key[2][j] ^ v);
		}
	}
	
	/**
	 * The key selection 'phi'<br/>
	 * k(r) = phi[r](K) <=> k[0][j](r) = S[K[0][j](r)] and k[i][j](r) = K[i][j](r) for i > 0 and 0 <= j < 4.
	 * @param key Matrix of byte
	 * @return k the truncated cipher key matrix
	 */
	private byte[][] keySelectionPhi(byte[][] key){ 
		byte[][] returnKey = new byte[3][4];
		for(int i = 0; i < key.length; i ++){
			for (int j = 0; j < key[i].length; j++){
				if(i == 0){
					returnKey[i][j] = S(key[0][j]);
				}
				else{
					returnKey[i][j] = key[i][j];
				}
			}
		}
		return returnKey;
	}

	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * General
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	
	/**
	 * xtimes
	 * @param x int
	 * @return xtimes(x) int
	 */
	private int xtimes(int x){		
		x = x << 1;
		if(x >= 0x100)
			x = x ^ 0x14d;
		return x;

	}
	
	/**
	 * ctimes
	 * @param x int
	 * @return ctimes(x) int
	 */
	private int ctimes(int x){		
		return xtimes(xtimes((xtimes((xtimes(x) ^ x)) ^ x)));
	}

	
	/**
	 * The S computing<br/>
	 * Computes S[u] from the mini-boxes P and Q
	 * @param u byte
	 * @return S(u) byte
	 */
	private byte S(byte u) {
		// Although it's not a good Java convention, we used uppercase names to
		// match the paper names
		final byte[] P = {0x03, 0x0F, 0x0E, 0x00, 0x05, 0x04, 0x0B, 0x0C, 0x0D, 0x0A, 0x09, 0x06, 0x07, 0x08, 0x02, 0x01};
		final byte[] Q = {0x09, 0x0E, 0x05, 0x06, 0x0A, 0x02, 0x03, 0x0C, 0x0F, 0x00, 0x04, 0x0D, 0x07, 0x0B, 0x01, 0x08};

		byte uh1 = P[(u >> 4) & 0x0F];
		byte ul1 = Q[u & 0x0F];

		byte uh2 = Q[(uh1 & 0x0C) ^ ((ul1 >> 0x02) & 0x03)];
		byte ul2 = P[((uh1 << 0x02) & 0x0C) ^ (ul1 & 0x03)];

		uh1 = P[(uh2 & 0x0C) ^ ((ul2 >> 0x02) & 0x03)];
		ul1 = Q[((uh2 << 0x02) & 0x0C) ^ (ul2 & 0x03)];

		return (byte) ((uh1 << 0x04) ^ ul1);
	}
	
	/**
	 * The schedule constants q<br/>
	 * q(0) = 0<br/>
	 * q[i][j](s) = S[2t(s-1) + j]		if i=0<br/>
	 * q[i][j](s) = 0 					otherwise
	 * @param s Matrix of byte
	 * @return q(s) Matrix of byte
	 */
	private byte[][] q(int s){
		int t = keyBits / 48;
		byte[][] q = new byte[3][2*t];
		
		//q(0) = 0
		if(s == 0x00){
			for (int i = 0; i < 3; i++){
				for (int j = 0; j < (2 * t); j++){
					q[i][j] = (byte)0x00;
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
						q[i][j] = (byte)0x00;
					}
				}
			}
		}
		return q;
	}
}
