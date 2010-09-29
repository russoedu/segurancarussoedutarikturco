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
	byte[] H = new byte[0];
	final int n = 12;
	int aLength;
	int mLength;
	
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
		String ivString = "000000000000000000000000";
		byte[] iv = Util.convertStringToVector(ivString);
		l.setIV(iv, 12);
		String message = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
		byte[] M = Util.convertStringToVector(message);

		Printer.printVectorAsPlainText("C", l.encrypt(M, M.length, new byte[12]));
		
		String associatedData = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
		byte[] H = Util.convertStringToVector(associatedData);
		l.update(H, H.length);
		
		Printer.printVectorAsPlainText("T", l.getTag(new byte[8], 8));
		
		//l.update(M, M.length);
		
		//Printer.printVectorAsPlainText("teste", l.getTag(new byte[12], 12));
	}
	
	@Override
	public byte[] decrypt(byte[] cData, int cLength, byte[] mData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] encrypt(byte[] mData, int mLength, byte[] cData) {
		
		this.mLength += mLength;
		
		byte[] vectorM = new byte[this.mLength];
		int position = -1;
		
		for (int i = 0; i < this.M.length; i++)
		{
			for (int j = 0; j < this.M[i].length; j++)
			{
				position = i * n + j;
				vectorM[i*n + j] = this.M[i][j];
			}
		}
		
		for (int i = 0; i < mLength; i++)
		{
			position++;
			vectorM[position] = mData[i];
		}
		
		byte[][] newM = new byte[(this.mLength - 1)/n + 1][];
		
		for (int i = 0; i <= (this.mLength - 1)/n; i++)
		{
			int size = (this.mLength - i*n) >= n ? n : this.mLength - i*n;
			
			newM[i] = new byte[size];
			for (int j = 0; j < size; j++)
			{
				newM[i][j] = vectorM[i * n + j];
			}
		}
		
		this.M = newM;
		
		byte[] C = lfsrc(this.R);

		mac.init(this.R);
		mac.update(C, C.length);
		
		return C;
		
	}

	@Override
	public byte[] getTag(byte[] tag, int tagBits) {
		
		byte[] A = mac.getTag(tag, tagBits, false);
		
		if (H.length != 0)
		{
			byte[] L = new byte[12];
			cipher.encrypt(new byte[12], L);
			mac.init(L);
			mac.update(this.H, this.H.length);
			byte[] D = new byte[12];
			D = mac.getTag(D, tagBits, false);
			cipher.Sct(D, D);
			A = Util.xor(A, D);
		}
		
		byte[] T = new byte[n];
		
		cipher.encrypt(A, T);
		
		for (int i = 0; i < tagBits; i++)
			tag[i] = T[i];
		
		return tag;
	}

	@Override
	public void update(byte[] aData, int aLength) {
		this.H = aData;
		
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
