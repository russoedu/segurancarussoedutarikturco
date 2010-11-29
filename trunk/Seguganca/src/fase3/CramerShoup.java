package fase3;

import java.math.BigInteger;
import java.security.SecureRandom;

import interfaces.HashFunction;
import interfaces.KeyEncapsulation;
import interfaces.SpongeRandom;

public class CramerShoup implements KeyEncapsulation {

	BigInteger p;
	BigInteger q;
	BigInteger g1;
	BigInteger g2;
	HashFunction H;
	SpongeRandom sr;
	int hashBits = 512;
	
	@Override
	public void setup(BigInteger p, BigInteger q, BigInteger g1, BigInteger g2, HashFunction H, SpongeRandom sr) {		
		this.p = p;
		this.q = q;
		this.g1 = g1;
		this.g2 = g2;
		this.H = H;
		this.sr = sr;
	}

	@Override
	public BigInteger[] makeKeyPair(String passwd) {
		
		// c, d, h
		BigInteger[] retorno = new BigInteger[3];

		sr.init(hashBits);
		sr.feed(passwd.getBytes(), passwd.length());

		BigInteger x1 = toBigIntegerModQ(sr.fetch(new byte[hashBits/8], hashBits/8));
		BigInteger x2 = toBigIntegerModQ(sr.fetch(new byte[hashBits/8], hashBits/8));
		BigInteger y1 = toBigIntegerModQ(sr.fetch(new byte[hashBits/8], hashBits/8));
		BigInteger y2 = toBigIntegerModQ(sr.fetch(new byte[hashBits/8], hashBits/8));
		BigInteger z = toBigIntegerModQ(sr.fetch(new byte[hashBits/8], hashBits/8));

		retorno[0] = ((g1.modPow(x1, p)).multiply(g2.modPow(x2, p))).mod(p);
		retorno[1] = ((g1.modPow(y1, p)).multiply(g2.modPow(y2, p))).mod(p);
		retorno[2] = g1.modPow(z, p);
		
		return retorno;
	}

	@Override
	public BigInteger[] encrypt(BigInteger[] pk, byte[] m) {

		// u1, u2, e, v
		BigInteger[] retorno = new BigInteger[4];
		
		byte[] seed = SecureRandom.getSeed(32);
		
		sr.init(hashBits);
		sr.feed(seed, seed.length);
		
		BigInteger r = toBigIntegerModQ(sr.fetch(new byte[hashBits/8], hashBits/8));
		
		// u1
		retorno[0] = g1.modPow(r, p);
		// u2
		retorno[1] = g2.modPow(r, p);
		// e
		retorno[2] = pk[2].modPow(r, p).multiply(toBigIntegerModP(m)).mod(p);
		
		byte[] data = toByteArray(retorno[0], retorno[1], retorno[2]);
		H.init(hashBits);
		H.update(data, data.length);
		BigInteger alfa = toBigIntegerModQ(H.getHash(new byte[hashBits/8]));
		
		// v
		retorno[3] = pk[0].modPow(r, p).multiply(pk[1].modPow(r.multiply(alfa).mod(p), p)).mod(p);
		
		return retorno;
	}

	@Override
	public byte[] decrypt(String passwd, BigInteger[] cs) {
		
		H.init(hashBits);
		byte[] data = toByteArray(cs[0], cs[1], cs[2]);
		H.update(data, data.length);
		
		BigInteger alfa = toBigIntegerModQ(H.getHash(new byte[hashBits/8]));
		
		sr.init(hashBits);
		sr.feed(passwd.getBytes(), passwd.length());

		BigInteger x1 = toBigIntegerModQ(sr.fetch(new byte[hashBits/8], hashBits/8));
		BigInteger x2 = toBigIntegerModQ(sr.fetch(new byte[hashBits/8], hashBits/8));
		BigInteger y1 = toBigIntegerModQ(sr.fetch(new byte[hashBits/8], hashBits/8));
		BigInteger y2 = toBigIntegerModQ(sr.fetch(new byte[hashBits/8], hashBits/8));
		BigInteger z = toBigIntegerModQ(sr.fetch(new byte[hashBits/8], hashBits/8));
		
		BigInteger possivelV = cs[0].modPow(x1.add(y1.multiply(alfa).mod(q)).mod(q), p).multiply(cs[1].modPow(x2.add(y2.multiply(alfa).mod(q)).mod(q), p)).mod(p);
		
		byte[] m = new byte[0];
		
		if(cs[3].compareTo(possivelV) == 0)
			m = cs[2].multiply(cs[0].modPow(z, p).modInverse(p)).mod(p).toByteArray();

		return m;
	}
	
	BigInteger toBigIntegerModQ(byte[] byteArray)
	{
		byte[] positiveByteArray = new byte[byteArray.length + 1]; 
		positiveByteArray[0] = 0;
		
		for (int i = 0 ; i < byteArray.length; i++)
			positiveByteArray[i + 1] = byteArray[i];
		
		return (new BigInteger(positiveByteArray)).mod(q);
	}
	
	BigInteger toBigIntegerModP(byte[] byteArray)
	{
		byte[] positiveByteArray = new byte[byteArray.length + 1]; 
		positiveByteArray[0] = 0;
		
		for (int i = 0 ; i < byteArray.length; i++)
			positiveByteArray[i + 1] = byteArray[i];
		
		return (new BigInteger(positiveByteArray)).mod(p);
	}
	
	byte[] toByteArray(BigInteger a, BigInteger b, BigInteger c)
	{
		byte[] A = a.toByteArray();
		byte[] B = b.toByteArray();
		byte[] C = c.toByteArray();
		
		byte[] retorno = new byte[A.length + B.length + C.length];
		
		for (int i = 0; i < A.length; i++)
			retorno[i] = A[i];
		for (int i = 0; i < B.length; i++)
			retorno[A.length + i] = B[i];
		for (int i = 0; i < C.length; i++)
			retorno[A.length + B.length + i] = C[i];
		
		return retorno;
	}

}
