package interfaces;

import java.math.BigInteger;

public interface KeyEncapsulation {
	
	/**
	 * Define global parameters.
	 * 
	 * @param p 	the full group order.
	 * @param q 	the cryptographic subgroup order.
	 * @param g1 	the 1st subgroup generator.
	 * @param g2 	the 2nd subgroup generator.
	 * @param H 	the chosen hash function.
	 * @param sr 	the chosen sponge-based PRNG.
	 */
	void setup(BigInteger p, BigInteger q, BigInteger g1, BigInteger g2, HashFunction H, SpongeRandom sr);
	
	/** 
	 * Generate a key pair from an ASCII password. 
	 * A suitable private key sk = (x1, x2, y1, y2, z) is kept 
	 * internally as private attributes; only the corresponding 
	 * public key pk = (c, d, h) is returned, where
	 *	c= g1^x1 g2^x2 mod p,
	 *	d= g1^y1 g2^y2 mod p,
	 *	h= g1^z mod p.
	 *
	 * @param 	passwd		the selected ASCII password.
	 * 
	 * @return 	a Cramer-Shoup public key, i.e. a BigInteger triple
	 *			(c, d, h).
	 */
	BigInteger[] makeKeyPair(String passwd);
	
	/** 
	 * Provide a symmetric key for Cramer-Shoup encryption.
	 * A random value r is internally generated for each
	 * encryption. 
	 * 
	 * @param pk 	the Cramer-Shoup public key, i.e.
	 * 				a BigInteger triple (c, d, h).
	 * @param m 	the symmetric key (to be left-padded
	 * 				with Ò0Ó bits and represented
	 * 				internally as an integer mod p).
	 * 
	 * @return a BigInteger quadruple (u1, u2, e, v).
	 */
	BigInteger[] encrypt(BigInteger[] pk, byte[] m);
	
	/** 
	 * Provide a symmetric key for Cramer-Shoup decryption.
	 * 
	 * @param 	passwd	the selected ASCII password.
	 * @param 	cs		a Cramer-Shoup cryptogram, i.e.
	 * 					a BigInteger quadruple (u1, u2, e, v).
	 * 
	 * @return 	the symmetric key m = e * (u1^z)^(-1) mod p
	 * 			converted to a byte array (without the left-padding
	 * 			of Ò0Ó bits), or else null in case of error.
	 */
	byte[] decrypt(String passwd, BigInteger[] cs);

}
