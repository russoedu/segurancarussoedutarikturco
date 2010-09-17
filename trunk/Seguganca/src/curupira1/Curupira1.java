package curupira1;

import pcs2055.BlockCipher;
import util.Matrix;

public class Curupira1 implements BlockCipher {
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * Local vars
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	
	/**
	 * The block bits
	 */
	private int blockBits;
	/**
	 * The key bits
	 */
	private int keyBits;
	/**
	 * The cipher key
	 */
	private byte[] cipherKey;

	// Although it's not a good Java convention, we used uppercase names to
	// match the paper names
	/**
	 * The P array
	 */
	private byte[] P = { 0x03, 0x0F, 0x0E, 0x00, 0x05, 0x04, 0x0B, 0x0C, 0x0D, 0x0A, 0x09, 0x06, 0x07, 0x08, 0x02, 0x01 };
	/**
	 * The Q array
	 */
	private byte[] Q = { 0x09, 0x0E, 0x05, 0x06, 0x0A, 0x02, 0x03, 0x0C, 0x0F, 0x00, 0x04, 0x0D, 0x07, 0x0B, 0x01, 0x08 };
	/**
	 * The K key matrix
	 */
	private byte[][] K;

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
		
		int t = keyBits / 48;
		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < (2 * t); j++) {
				this.K[i][j] = cipherKey [i + 3 * j];
			}
		}
	}

	public void encrypt(byte[] mBlock, byte[] cBlock) {
		// TODO
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
	 * @param Matrix of byte[][] a
	 * @return Matrix of byte[][] b
	 */
	public byte[][] nonLinearLayerGama (byte[][] matrix){
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[i].length; j++){
				matrix[i][j] = this.S(matrix[i][j]);
			}
		}
		return matrix;
	}
	
	/**
	 * The permutation layer 'pi'
	 * pi(a) = b <=> b[i][j] = a[i][i ^ j]
	 * @param Matrix of byte[][] A
	 * @return Matrix of byte[][] permuted A
	 */
	public byte[][] permutationLayerPi(byte[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			byte[] rowCopy = matrix[i];
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] = rowCopy[i ^ j];
			}
		}
		return matrix;
	}

	/**
	 * The linear diffusion layer 'theta'
	 * theta(a) = b <=> b = D * a
	 * @param Matrix of [][]byte a
	 * @return Matrix of byte[][] b = D*a
	 */
	public byte[][] linearDiffusionLayerTheta(byte[][] a) {
		byte[][] D = { { 0x03, 0x02, 0x02 }, { 0x04, 0x05, 0x04 }, { 0x06, 0x06, 0x07 } };
		return Matrix.multiply(D, a);
	}

	/**
	 * The key addition 'sigma'[k]
	 * sigma[k](a) = b <=> b[i][j] = a[i][j] ^ k[i][j]
	 * @param Matrix of byte[][] k
	 * @param Matrix of byte[][] a
	 * @return Matrix of byte[][]
	 */
	public byte[][] keyAdditionLayerSigma(byte[][] k, byte[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				a[i][j] = (byte) (a[i][j] ^ k[i][j]);
			}
		}
		return a;
	}
	
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * Schedule methods
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	/**
	 * The ciclic shift Csi
	 * Csi(a) = b <=> 	b[0][j] = a[0][j]
	 * 					b[1][j] = a[1][(j + 1) mod 2t]
	 * 					b[2][j] = a[2][(j - 1) mod 2t] 
	 * @param Matrix of byte[][] a
	 * @return Matrix of byte[][] b
	 */
	public byte[][] ciclicShiftCsi(byte[][] a){
		byte[][] b = new byte[3][a.length +1];
		//i = 0
		b[0] = a[0];
		//i = 1
		for(int j = 0; j < (a.length + 1); j++){
			b[1][j] = a[1][(j + 1) % (a.length + 1)];
		}
		//i = 2
		for(int j = 0; j < (a.length + 1); j++){
			if (j == 0){
				b[2][j] = a[2][a.length];
			}
			else{
				b[2][j] = a[2][j - 1];
			}
		}
		return b;
	}
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * General
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	
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
	public byte[][] q(byte s){
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
	
}
