package marvin;

import pcs2055.BlockCipher;
import pcs2055.MAC;

public class Marvin implements MAC {
	
	byte[] tag;
	BlockCipher cipher;
	byte[][] M = new byte[0][];
	int aLength;
	final int n = 12;
	byte[] R;

	@Override
	public byte[] getTag(byte[] tag, int tagBits) {
		// TODO Auto-generated method stub
		
		byte[][] A = new byte[(aLength-1)/12 + 2][];
		A[0] = new byte[n];
		byte[] O = multiplyByPx(this.R);
		
		for (int i = 1; i <= (this.aLength-1)/12 + 1; i++)
		{
			byte[] paddedM = rpad(this.M[i - 1]);
			A[i] = new byte[n];
			this.cipher.Sct(A[i], xor(paddedM, O));
			A[0] = xor(A[0], A[i]);
			O = multiplyByPx(O);
		}

		A[0] = xor(A[0], xor(this.R, xor(rpad(rightBinAndSetOne((this.n - tagBits)*8)), lpad(leftBin(this.aLength*8)))));
		
		byte[] ciphered = new byte[n];
		
		this.cipher.encrypt(A[0], ciphered);

		return ciphered;
	}

	@Override
	public void init() {
		byte[] c = new byte[1];
		c[0] = 0x2A;
	
		byte[] paddedC = lpad(c);
		byte[] cipherC = new byte[n];

		this.cipher.encrypt(paddedC, cipherC);

		this.R = xor(paddedC, cipherC);

	}

	@Override
	public void setCipher(BlockCipher cipher) {
		this.cipher = cipher;

	}

	@Override
	public void setKey(byte[] cipherKey, int keyBits) {
		this.cipher.makeKey(cipherKey, keyBits);
	}

	@Override
	public void update(byte[] aData, int aLength) {
		// TODO Auto-generated method stub
		
		if (aLength == 0)
			return;
		
		this.aLength += aLength;
		
		byte[] vectorM = new byte[this.aLength];
		int position = -1;
		
		for (int i = 0; i < this.M.length; i++)
		{
			for (int j = 0; j < this.M[i].length; j++)
			{
				position = i * n + j;
				vectorM[i*n + j] = this.M[i][j];
			}
		}
		
		for (int i = 0; i < aLength; i++)
		{
			position++;
			vectorM[position] = aData[i];
		}
		
		byte[][] newM = new byte[(this.aLength - 1)/n + 1][];
		
		for (int i = 0; i <= (this.aLength - 1)/n; i++)
		{
			int size = (this.aLength - i*n) >= n ? n : this.aLength - i*n;
			
			newM[i] = new byte[size];
			for (int j = 0; j < size; j++)
			{
				newM[i][j] = vectorM[i * n + j];
			}
		}
		
		this.M = newM;
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
		return (byte)(U ^ ((U & 0xFF ) >>> 3) ^ ((U & 0xFF) >>> 5));
	}
	
	byte T0 (byte U)
	{
		return (byte)((U << 5) ^ (U << 3));
	}
	
	byte[] rightBinAndSetOne(int input)
	{	
		String binaryString = "";
		if (input != 0)
			binaryString = Integer.toBinaryString(input);
		
		binaryString += '1';

		byte[] output = new byte[(binaryString.length()/4) + 1];
		
		int i = 0;
		while(binaryString.length() != 0)
		{
			if (binaryString.length() >= 8)
			{
				String binaryByteString = binaryString.substring(0, 3);
				output[i] = (byte)Integer.parseInt(binaryByteString, 2);
				binaryString = binaryString.substring(4);
			}
			else
			{
				for (int j = binaryString.length(); j < 8; j++)
					binaryString += '0';
				output[i] = (byte)Integer.parseInt(binaryString, 2);
				binaryString = "";
			}
			i++;
		}
		
		return output;
	}
	
	byte[] leftBin(int input)
	{	
		int size = (Integer.toBinaryString(input).length() - 1)/8 + 1;

		byte[] output = new byte[size];

		int i = 1;
		while(Integer.highestOneBit(input) != 0)
		{
			output[size - i] = (byte)input;
			
			input = input >> 8;
		}
		
		return output;
	}
	
}
