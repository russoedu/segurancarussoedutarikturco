package fase2;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;

import util.Util;

import interfaces.Duplex;
import interfaces.HashFunction;

public class Keccak implements HashFunction, Duplex {

	public static void main(String[] args)
	{
		Keccak k = new Keccak();
		
		String message = "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
		
		byte[] byteArray = Util.convertStringToVector(message);
		
		byteArray = k.keccakf(byteArray);
		
		System.out.println(Util.byteToHex(byteArray));
		
	}
	
	int ntotalbits = 1600;
	
	@Override
	public byte[] getHash(byte[] val) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(int hashBits) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(byte[] aData, int aLength) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] duplexing(byte[] sigma, int sigmaLength, byte[] z, int zLength) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBitRate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCapacity() {
		// TODO Auto-generated method stub
		return 0;
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
					A[x][y] = Util.xor(A[x][y], rotateLeft(a[x_aux][y_aux], 1, 64));
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
			A[x][y] = rotateLeft(a[x][y], bits, 64);
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
	
	byte[] rotateLeft (byte[] in, int nbits, int ntotalbits)
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
					out[x][y][z] = in[z + 8*(x + 5*y)];
		
		return out;
	}
	
	byte[] matrixToBlock(byte[][][] matrix)
	{
		byte[] out = new byte[ntotalbits/8];

		for (int x = 0; x < 5; x ++)
			for (int y = 0; y < 5; y++)
				for (int z = 0; z < 8; z ++)
					out[z + 8*(x + 5*y)] = matrix[x][y][z];
		
		return out;
	}

}
