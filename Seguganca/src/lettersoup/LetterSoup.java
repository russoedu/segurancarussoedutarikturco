package lettersoup;

import pcs2055.AEAD;
import pcs2055.BlockCipher;
import pcs2055.MAC;

public class LetterSoup implements AEAD {

	byte[] cipherKey;
	int keyBits;
	BlockCipher cipher;
	MAC mac;
	
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
}
