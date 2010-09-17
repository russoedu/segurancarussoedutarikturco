package curupira1;

import pcs2055.BlockCipher;
import util.Matrix;

public class Curupira1 implements BlockCipher {
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
	private byte[] P = { 0x03, 0x0F, 0x0E, 0x00, 0x05, 0x04, 0x0B, 0x0C, 0x0D,
			0x0A, 0x09, 0x06, 0x07, 0x08, 0x02, 0x01 };
	/**
	 * The Q array
	 */
	private byte[] Q = { 0x09, 0x0E, 0x05, 0x06, 0x0A, 0x02, 0x03, 0x0C, 0x0F,
			0x00, 0x04, 0x0D, 0x07, 0x0B, 0x01, 0x08 };
	/**
	 * The K key matrix
	 */
	private byte[][] K;

	public int blockBits() {
		return this.blockBits;
	}

	public int keyBits() {
		return this.keyBits;
	}

	public byte[][] K() {
		return this.K;
	}

	public void makeKey(byte[] cipherKey, int keyBits) {
		this.cipherKey = cipherKey;
		this.keyBits = keyBits;
		
		int t = keyBits / 48;
		
		for(int row = 0; row < 3; row ++) {
			for(int column = 0; column < (2 * t); column ++) {
				this.K [row] [column] = cipherKey [row + 3 * column];
			}
		}
	}

	public void encrypt(byte[] mBlock, byte[] cBlock) {
		// TODO
	}

	public void decrypt(byte[] cBlock, byte[] mBlock) {
		// TODO
	}

	/**
	 * The nonlinear layer 'gama' gama(a) = b <=> b[i][j] = S[a[i][j]]
	 * 
	 * @param byte u
	 * @return byte S[u]
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
	 * The permutation layer 'pi' pi(a) = b <=> b[i][j] = a[i][i ^ j]
	 * 
	 * @param Matrix
	 *            of byte[][] A
	 * @return Matrix of byte[][] permuted A
	 */
	public byte[][] permutationPi(byte[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			byte[] rowCopy = matrix[i];
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] = rowCopy[i ^ j];
			}
		}
		return matrix;
	}

	/**
	 * The linear diffusion layer 'theta' theta(a) = b <=> b = D * a
	 * 
	 * @param Matrix
	 *            of byte a[][]
	 * @return D*a = b
	 */
	public byte[][] diffusionTheta(byte[][] matrix) {
		byte[][] D = { { 0x03, 0x02, 0x02 }, { 0x04, 0x05, 0x04 },
				{ 0x06, 0x06, 0x07 } };
		return Matrix.multiply(D, matrix);
	}

	/**
	 * The key addition 'sigma'[k] sigma[k](a) = b <=> b[i][j] = a[i][j] ^
	 * k[i][j]
	 * 
	 * @param Matrix
	 *            of byte k[][]
	 * @param Matrix
	 *            of byte a[][]
	 * @return Matrix of byte [][]
	 */
	public byte[][] additionSigma(byte[][] k, byte[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				a[i][j] = (byte) (a[i][j] ^ k[i][j]);
			}
		}
		return a;
	}
}
