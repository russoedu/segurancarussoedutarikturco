package marvin;

import curupira1.Curupira1;
import pcs2055.BlockCipher;
import pcs2055.MAC;
import util.Util;

public class Marvin implements MAC {
	
	byte[] tag;
	BlockCipher cipher;
	byte[] aData;
	int aLength;
	int n;

	@Override
	public byte[] getTag(byte[] tag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {

	}
	
	void eu() {
		
		aLength = 1;
		n = 12;
		byte[][] A = new byte[aLength + 1][];
		A[0] = new byte[12];
		
		byte[] c = new byte[aLength];
		c[0] = 0x2A;
		byte[][] M = new byte[aLength][];
		M[0] = new byte[aLength];
		M[0][0] = 0x00;
		
		byte[] paddedC = lpad(c);
		byte[] cipherC = new byte[12];
		
		
		byte[] key = new byte[12];
		for (int i = 0; i < 12; i++)
			key[i] = 0x00;
		
		cipher.makeKey(key, 96);
		cipher.encrypt(paddedC, cipherC);

		byte[] R = xor(paddedC, cipherC);
		byte[] O = multiplyByPx(R);
		
		byte[] paddedM = rpad(M[0]);
		cipher.Sct(A[0], paddedM);
		
		for (int i = 1; i <= aLength; i++)
		{
			A[i] = new byte[12];
			cipher.Sct(A[i], xor(paddedM, O));
			A[0] = xor(A[0], A[i]);
			O = multiplyByPx(O);
		}
		
		byte[] teste = new byte[1];
		teste[0] = (byte)0x80;
		
		byte[] teste2 = new byte[1];
		teste2[0] = (byte)0x08;
		
		A[0] = xor(A[0], xor(R, xor(rpad(teste), lpad(teste2))));
		
		byte[] ciphered = new byte[12];
		
		cipher.encrypt(A[0], ciphered);
	}
	
	public static void main(String[] args)
	{
		Curupira1 c = new Curupira1();
		Marvin ae = new Marvin();
		ae.setCipher(c);
		ae.eu();
		
	}

	@Override
	public void setCipher(BlockCipher cipher) {
		// TODO Auto-generated method stub
		this.cipher = cipher;

	}

	@Override
	public void setKey(byte[] cipherKey, int keyBits) {
		this.cipher.makeKey(cipherKey, keyBits);
	}

	@Override
	public void update(byte[] aData, int aLength) {
		// TODO Auto-generated method stub
		this.aData = aData;
		this.aLength = aLength;

	}
	
	byte[] rpad(byte[] message)
	{
		byte[] rightPaddedMessage = new byte[n];
		
		for (int i = 0; i < message.length; i++)
			rightPaddedMessage[i] = message[i];
		for (int i = message.length; i < n; i++)
			rightPaddedMessage[i] = 0;
		
		return rightPaddedMessage;
	}
	
	byte[] lpad(byte[] message)
	{
		byte[] leftPaddedMessage = new byte[n];
		
		for (int i = 0; i < n - message.length; i++)
			leftPaddedMessage[i] = 0;
		for (int i = n - message.length; i < n; i++)
		{
			leftPaddedMessage[i] = message[n - i - 1];
		}
		
		return leftPaddedMessage;
	}
	
	byte[] xor (byte[] a, byte[] b)
	{
		byte[] output = new byte[a.length];
		
		for (int i = 0; i < a.length; i++)
			output[i] = (byte)(a[i] ^ b[i]);
		
		return output;
			
	}
	
	byte[] multiplyByPx(byte[] input)
	{
		byte[] output = new byte[input.length];
		
		for (int i = 0; i < 9; i++)
		{
			output[i] = input[i + 1];
		}
		
		output[9] = (byte)(input[10] ^ T1(input[0]));
		output[10] = (byte)(input[11] ^ T0(input[0]));
		output[11] = input[0];
		
		return output;
	}
	
	byte T1 (byte U)
	{
		return (byte)(U ^ (U >> 3) ^ (U >> 5));
		
	}
	
	byte T0 (byte U)
	{
		return (byte)((U << 5) ^ (U << 3));
	}
	
	byte[] bin(int input)
	{	
		String binaryString = Integer.toBinaryString(input);

		byte[] output = new byte[(binaryString.length()/4) + 1];
		
		int i = 0;
		while(binaryString.length() != 0)
		{
			if (binaryString.length() >= 4)
			{
				String binaryByteString = binaryString.substring(0, 3);
				output[i] = (byte)Integer.parseInt(binaryByteString, 2);
				binaryString = binaryString.substring(4);
			}
			else
			{
				for (int j = binaryString.length(); j < 4; j++)
					binaryString += '0';
				output[i] = (byte)Integer.parseInt(binaryString, 2);
				binaryString = "";
			}
			i++;
		}
		
		return output;
	}
	
}
