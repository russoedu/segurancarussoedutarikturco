package fase3;

import java.math.BigInteger;
import java.security.SecureRandom;

import interfaces.DigitalSignature;
import interfaces.HashFunction;
import interfaces.SpongeRandom;

public class Schnorr implements DigitalSignature {
	
	BigInteger p;
	BigInteger q;
	BigInteger g;
	HashFunction H;
	SpongeRandom sr;
	int hashBits = 512;

	@Override
	public void setup(BigInteger p, BigInteger q, BigInteger g, HashFunction H,
			SpongeRandom sr) {

		this.p = p;
		this.q = q;
		this.g = g;
		this.H = H;
		this.sr = sr;

	}

	@Override
	public BigInteger makeKeyPair(String passwd) {

		sr.init(hashBits);
		sr.feed(passwd.getBytes(), passwd.length());
		
		BigInteger x = toBigIntegerModQ(sr.fetch(new byte[hashBits/8], hashBits/8));
		BigInteger y = g.modPow(x, p);
		
		return y;
	}

	@Override
	public void init() {
		H.init(hashBits);
	}

	@Override
	public void update(byte[] aData, int aLength) {
		H.update(aData, aLength);
	}

	@Override
	public BigInteger[] sign(String passwd) {

		sr.init(hashBits);
		sr.feed(passwd.getBytes(), passwd.length());
		BigInteger x = toBigIntegerModQ(sr.fetch(new byte[hashBits/8], hashBits/8));
		
		sr.init(hashBits);
		byte[] seed = SecureRandom.getSeed(32);
		sr.feed(seed, seed.length);
		BigInteger k = toBigIntegerModQ(sr.fetch(new byte[hashBits/8], hashBits/8));
		
		BigInteger r = g.modPow(k, p);
		
		// e, s
		BigInteger[] retorno = new BigInteger[2];
		
		byte[] R = r.toByteArray();
		H.update(R, R.length);
		retorno[0] = toBigIntegerModQ(H.getHash(new byte[hashBits/8]));
		
		retorno[1] = k.subtract(x.multiply(retorno[0]).mod(q)).mod(p);
		
		return retorno;
	}

	@Override
	public boolean verify(BigInteger y, BigInteger[] sig) {

		BigInteger m = g.modPow(sig[1], p).multiply(y.modPow(sig[0], p)).mod(p);
        
		byte[] M = m.toByteArray();
        H.update(M, M.length);
        BigInteger e = toBigIntegerModQ(H.getHash(new byte[hashBits/8]));
		
		return sig[0].compareTo(e) == 0;
	}
	
	BigInteger toBigIntegerModQ(byte[] byteArray)
	{
		byte[] positiveByteArray = new byte[byteArray.length + 1]; 
		positiveByteArray[0] = 0;
		
		for (int i = 0 ; i < byteArray.length; i++)
			positiveByteArray[i + 1] = byteArray[i];
		
		return (new BigInteger(positiveByteArray)).mod(q);
	}

}
