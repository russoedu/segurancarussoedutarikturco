package marvin;

import pcs2055.BlockCipher;
import pcs2055.MAC;

public class Marvin implements MAC {
	
	byte[] tag;
	BlockCipher cipher;
	byte[] cipherKey;
	int keyBits;
	byte[] aData;
	int aLength;
	int n;
	
	byte c = 0x2A;

	@Override
	public byte[] getTag(byte[] tag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
		int MACLentgh = 8;
		int c = 0x2A;
		String[] O = new String[MACLentgh];
		String[] A = new String[MACLentgh];
		
		//String R = xor(offsetGeneration(lpad(bin(c))),lpad(bin(c)));
		
		for (int i = 1; i < MACLentgh; i++)
		{
			//O[i] = R . (x^w)^i;
			//A[i] = square(rpad(aData[i]) ^ O[i]);
		}

	}

	@Override
	public void setCipher(BlockCipher cipher) {
		// TODO Auto-generated method stub
		this.cipher = cipher;

	}

	@Override
	public void setKey(byte[] cipherKey, int keyBits) {
		// TODO Auto-generated method stub
		this.cipherKey = cipherKey;
		this.keyBits = keyBits;

	}

	@Override
	public void update(byte[] aData, int aLength) {
		// TODO Auto-generated method stub
		this.aData = aData;
		this.aLength = aLength;

	}
	
	byte square(int data)
	{
		// TODO
		return 0x00;
	}
	
	byte[] encrypt (byte[] block)
	{
		return new byte[0];
	}
	
	String offsetGeneration(String data)
	{
		// TODO
		return "";
	}
	
	String rpad(String binaryString)
	{
		while (binaryString.length() < n)
			binaryString = binaryString + '0';
		return binaryString;
	}
	
	String lpad(String binaryString)
	{
		while (binaryString.length() < n)
			binaryString = '0' + binaryString;
		return binaryString;
	}

	String bin(int value)
	{
		return Integer.toBinaryString(value);
	}
	
	String xor(String binaryString1, String binaryString2)
	{
		String binaryStringReturn = "";
		for (int i = 0; i < binaryString1.length(); i++)
			binaryStringReturn = (binaryString1.charAt(i) != binaryString2.charAt(i)) ? binaryStringReturn + '1' : binaryStringReturn + '0';
		
		return binaryStringReturn;
	}
	
}
