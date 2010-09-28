package lettersoup;

import marvin.Marvin;
import curupira1.Curupira1;
import pcs2055.AEAD;
import pcs2055.BlockCipher;
import pcs2055.MAC;
import util.Printer;
import util.Util;

public class LetterSoup implements AEAD {

	byte[] R;
	BlockCipher cipher;
	MAC mac;
	byte[][] M = new byte[0][];
	final int n = 12;
	int aLength;
	
	public static void main (String[] args)
	{
		Curupira1 c = new Curupira1();
		Marvin m = new Marvin();
		LetterSoup l = new LetterSoup();
		l.setCipher(c);
		m.setCipher(c);
		l.setMAC(m);
		String keyString = "000000000000000000000000";
		byte[] key = Util.convertStringToVector(keyString);
		l.setKey(key, 96);
		String ivString = "0102030405060708090a0b0c";
		byte[] iv = Util.convertStringToVector(ivString);
		l.setIV(iv, 12);
		String message = "00";
		byte[] M = Util.convertStringToVector(message);
		l.update(M, M.length);
		
		Printer.printVectorAsPlainText("teste", l.getTag(new byte[12], 12));
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
	public byte[] getTag(byte[] tag, int tagBits) {

		byte[] C = lfsrc(this.R);
		Printer.printVectorAsPlainText("C", C);
		
		mac.init(this.R);
		mac.update(C, C.length);
		byte[] A = mac.getTag(tag, tagBits, false);
		
		byte[] T = new byte[n];
		
		cipher.encrypt(A, T);
		
		return T;
	}

	@Override
	public void update(byte[] aData, int aLength) {
		
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
	
	
	
	@Override
	public void setIV(byte[] iv, int ivLength) {
		iv = Util.lpad(iv, n);
		this.R = new byte[12];
		cipher.encrypt(iv, this.R);
		R = Util.xor(R, iv);
	}

	@Override
	public void setKey(byte[] cipherKey, int keyBits) {
		cipher.makeKey(cipherKey, keyBits);
	}
	
	@Override
	public void setCipher(BlockCipher cipher) {
		this.cipher = cipher;

	}
	
	@Override
	public void setMAC(MAC mac) {
		this.mac = mac;
	}
	
	public byte[] lfsrc (byte[] nonce)
	{

		byte[] O = Util.multiplyByPx(nonce);
		byte[][] C = new byte[M.length][];
		for (int i = 0; i < M.length; i++)
		{
			byte[] encrypted = new byte[12];
			cipher.encrypt(O, encrypted);
			
			C[i] = Util.xor(M[i], encrypted);
			O = Util.multiplyByPx(O);
		}
		
		byte[] vectorC = new byte[(C.length - 1) * n + C[C.length - 1].length];
		
		for (int i = 0; i < C.length; i++)
		{
			for (int j = 0; j < C[i].length; j++)
				vectorC[i*n + j] = C[i][j];
		}
		
		return vectorC;
	}
}
