package fase2;

import java.math.BigInteger;

import util.Util;

import interfaces.Duplex;
import interfaces.HashFunction;

public class Keccak implements HashFunction, Duplex {

	public static void main(String[] args)
	{
		SpongePRNG sponge = new SpongePRNG();

		String message = "7A";
		byte[] byteArray = Util.convertStringToVector(message);
		sponge.init(0);
		sponge.feed(byteArray, byteArray.length);
		
		//k.update(byteArray, byteArray.length);
		//byte[] result = k.getHash(new byte[0]);
		
		
	}
	
	int ntotalbits = 1600;
	int r = 1024;
	int c = 576;
	int d = 0;
	byte[][] M;
	byte[] state = new byte[1600/8];
	
	@Override
	public byte[] getHash(byte[] val) {

		byte[] s = new byte[200];
		
		for (int i = 0; i < M.length; i++)
		{
			s = Util.xor(s, M[i]);
			s = keccakf(s);
		}
		
		val = new byte[r/8];
		
		for (int i = 0; i < r/8; i++)
			val[i] = s[i];
		System.out.println(Util.byteToHex(s));
		
		return val;
	}

	@Override
	public void init(int hashBits) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(byte[] aData, int aLength) {
		M = new byte[(aLength + 3)*8/r + 1][r/8];
		
		for (int i = 0; i < aLength; i++)
		{
			M[i*8/r][i%(r/8)] = aData[i];
		}
		
		int pos = aLength;
		
		M[pos*8/r][pos%(r/8)] = 0x01;
		pos++;
		M[pos*8/r][pos%(r/8)] = (byte)(d);
		pos++;
		M[pos*8/r][pos%(r/8)] = (byte)(r/8);
		pos++;
		M[pos*8/r][pos%(r/8)] = 0x01;
	}

	@Override
	public byte[] duplexing(byte[] sigma, int sigmaLength, byte[] z, int zLength) {
		
		state = Util.xor(state, this.pad(sigma, r/8));
		
		state = keccakf(state);

		for (int i = 0; i < zLength; i++)
			z[i] = state[i];
		
		System.out.println(Util.byteToHex(state));
		
		return z;
	}

	@Override
	public int getBitRate() {
		return r;
	}

	@Override
	public int getCapacity() {
		return c;
	}

	
	byte[] keccakf (byte[] input)
	{
		byte[][][] matrix = blockToMatrix(input);
		
		for (int i = 0; i < 24; i++)
		{
			matrix = theta(matrix);
			matrix = rho(matrix);
			matrix = pi(matrix);
			matrix = chi(matrix);
			matrix = iota(matrix, i);
		}
		
		return matrixToBlock(matrix);
	}
	
	
	byte[][][] theta(byte[][][] a)
	{
		byte[][][] A = new byte[5][5][8];
		
		for (int x = 0; x < 5; x++)
			for (int y = 0; y < 5; y++)
			{
				A[x][y] = a[x][y].clone();
				int x_aux = x == 0 ? 4 : x - 1;
				for (int y_aux = 0; y_aux < 5; y_aux++)
					A[x][y] = Util.xor(A[x][y], a[x_aux][y_aux]);
				
				x_aux = x == 4 ? 0 : x + 1;
				for (int y_aux = 0; y_aux < 5; y_aux++)
					A[x][y] = Util.xor(A[x][y], rotate(a[x_aux][y_aux], 1, 64));
			}
		
		return A;
	}
	
	byte[][][] rho(byte[][][] a)
	{
		byte[][][] A = new byte[5][5][8];
		
		A[0][0] = a[0][0];
		
		int x = 1;
		int y = 0;
		
		for (int t = 0; t < 24; t++)
		{
			int bits = ((t + 1) * (t + 2)/2) % 64;
			A[x][y] = rotate(a[x][y], bits, 64);
			int aux = y;
			y = (2*x + 3*y) % 5;
			x = aux;
		}
		return A;
	}
	
	byte[][][] pi(byte[][][] a)
	{
		byte[][][] A = new byte[5][5][8];
		
		for (int x = 0; x < 5; x++)
			for (int y = 0; y < 5; y++)
			{
				int xlinha = y;
				int ylinha = (2*x + 3*y)%5;
				A[xlinha][ylinha] = a[x][y];
			}
		
		return A;
	}
	
	byte[][][] chi(byte[][][]a)
	{
		byte[][][] A = new byte[5][5][8];
		
		for (int x = 0; x < 5; x++)
			for (int y = 0; y < 5; y++)
			{
				int x_aux1 = (x + 1) % 5;
				int x_aux2 = (x + 2) % 5;
				A[x][y] = Util.xor(a[x][y], Util.and(a[x_aux2][y], Util.not(a[x_aux1][y])));
			}
		
		return A;
	}
	
	byte[][][] iota(byte[][][] a, int roundNumber)
	{
		a[0][0] = Util.xor(a[0][0], RoundConstants.getRoundConstant(roundNumber));
		return a;
	}
	
	byte[] rotate (byte[] in, int nbits, int ntotalbits)
	{
		BigInteger bg = new BigInteger(in);
		
		for (int i = 0; i < nbits; i++)
		{
			bg = bg.shiftLeft(1);
			if (bg.testBit(ntotalbits))
			{
				bg = bg.clearBit(ntotalbits);
				bg = bg.setBit(0);
			}
			else
				bg = bg.clearBit(0);
		}
		
		byte[] aux = bg.toByteArray();
		byte[] out = new byte[ntotalbits/8];
		
		
		for (int i = 1; i <= ntotalbits/8; i++)
		{
			int pos =  aux.length - i;
			
			if (pos >= 0)
				out[ntotalbits/8 - i] = aux[pos];
		}
		
		return out;
	}
	
	byte[][][] blockToMatrix(byte[] in)
	{
		byte[][][] out = new byte[5][5][8];
		
		for (int x = 0; x < 5; x ++)
			for (int y = 0; y < 5; y++)
				for (int z = 0; z < 8; z ++)
					out[x][y][z] = in[(7 - z) + 8*(x + 5*y)];
		
		return out;
	}
	
	byte[] matrixToBlock(byte[][][] matrix)
	{
		byte[] out = new byte[ntotalbits/8];

		for (int x = 0; x < 5; x ++)
			for (int y = 0; y < 5; y++)
				for (int z = 0; z < 8; z ++)
					out[z + 8*(x + 5*y)] = matrix[x][y][7 - z];
		
		return out;
	}
	
	byte[] pad(byte[] input, int length)
	{
		byte[] output = new byte[length];
		
		int i;
		
		for (i = 0; i < input.length; i++)
		{
			output[i] = input[i];
		}
		if (i != length - 1)
		{
			output[i] = 0x01;
			for (int j = i + 1; j < length - 1; j++)
				output[j] = 0x00;
			output[length - 1] = (byte)0x80;
		}
		else
			output[i] = (byte)0x81;
		
		return output;
	}

}
