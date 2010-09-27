package lettersoup;

import curupira1.Curupira1;
import pcs2055.AEAD;
import pcs2055.BlockCipher;
import pcs2055.MAC;
import util.Printer;
import util.Util;

public class LetterSoup implements AEAD {

	byte[] cipherKey;
	int keyBits;
	BlockCipher cipher;
	MAC mac;
	byte[][] M;
	
	public static void main (String[] args)
	{
		LetterSoup l = new LetterSoup();
		byte[] nonce = new byte[12];
		byte[] nonce2 = new byte[12];
		Curupira1 c = new Curupira1();
		byte[] key = new byte[12];
		c.makeKey(key, 96);
		c.encrypt(nonce, nonce2);
		Printer.printVectorAsPlainText("nonce", nonce2);
		l.LFSRC(nonce2);
	}
	
	@Override
	public byte[] decrypt(byte[] cData, int cLength, byte[] mData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] encrypt(byte[] mData, int mLength, byte[] cData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getTag(byte[] tag) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void update(byte[] aData, int aLength) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void setIV(byte[] iv, int ivLength) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setKey(byte[] cipherKey, int keyBits) {
		this.cipherKey = cipherKey;
		this.keyBits = keyBits;
	}
	
	@Override
	public void setCipher(BlockCipher cipher) {
		this.cipher = cipher;

	}
	
	@Override
	public void setMAC(MAC mac) {
		this.mac = mac;
	}
	
	public void LFSRC (byte[] nonce)
	{

		byte[] O = Util.multiplyByPx(nonce);
		byte[][] C = new byte[M.length][];
		for (int i = 0; i < M.length; i++)
		{
			
			byte[] ciphered = new byte[12];
			cipher.encrypt(O, ciphered);
			
			C[i] = Util.xor(M[i], ciphered);
		}
	}
}
