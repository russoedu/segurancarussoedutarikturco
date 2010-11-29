package interfaces;

import java.math.BigInteger;

public interface DigitalSignature {
	
	/** 
	 * Define global parameters. 
	 * 
	 * @param p 	the full group order.
	 * @param q 	the cryptographic subgroup order.
	 * @param g 	the subgroup generator.
	 * @param H		the chosen hash function.
	 * @param sr	the chosen sponge-based PRNG.
	 */
	void setup(BigInteger p, BigInteger q, BigInteger g, HashFunction H, SpongeRandom sr);
	
	/** 
	 * Generate a key pair from an ASCII password. 
	 * A suitable private key x (mod q) is kept internally as 
	 * a private attribute; only the corresponding public key 
	 * y = g^x mod p is returned. 
	 * 
	 * @param passwd	the selected ASCII password. 
	 * 
	 * @return a Schnorr public key, i.e. a BigInteger y. 
	 */
	BigInteger makeKeyPair(String passwd);
	
	/** 
	 * Prepare to sign or verify a new message. 
	 */
	void init();
	
	/** 
	 * Update the signature computation with a message chunk. 
	 * 
	 * @param aData	data chunk to authenticate. 
	 * @param aLength	its length in bytes. 
	 */
	void update(byte[] aData, int aLength);
	
	/** 
	 * Complete the data processing and sign the whole message M 
	 * provided. 
	 * 
	 * @param 	passwd	the selected ASCII password from which
	 * 					the private key x (mod q) is computed.
	 * 
	 * @return 	the Schnorr signature, i.e. a BigInteger pair
	 * 			sig = (e, s)where e = H(M || g^r) and
	 * 			s = r Ð xe (mod q) for a suitable random nonce
	 * 			r (mod q).
	 */
	BigInteger[] sign(String passwd);
	
	/** 
	 * Complete the data processing and verify the signature of 
	 * the whole message M provided. 
	 * 
	 * @param y 	the Schnorr public key.
	 * @param sig 	the Schnorr signature, i.e.
	 * 				a BigInteger pair sig = (e, s).
	 *
	 * @return 	whether the signature was successfully verified, 
	 *			i.e. whether H(M || g^s y^e mod p) = e.
	 */
	boolean verify(BigInteger y, BigInteger[] sig);

}
