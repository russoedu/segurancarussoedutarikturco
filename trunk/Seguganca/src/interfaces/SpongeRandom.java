package interfaces;

interface SpongeRandom {

	/** 
	 * Get the PRNG bit-rate r. 
	 */
	void getBitRate();

	/** 
	 * Get the PRNG capacity c. 
	 */
	void getCapacity();

	/** 
	 * Initialize the PRNG. 
	 * 
	 * @param hashBits	the desired hash size in bits (used
	 * only to specify the desired security level; 0 = default). 
	 */
	void init(int hashBits);

	/** 
	 * Feed an entropy seed to the PRNG. 
	 * 
	 * @param sigma			entropy input (may be null). 
	 * @param sigmaLength	its length in bytes (may be zero; 
	 *						must not exceed the bit-rate). 
	 */
	void feed(byte[] sigma, int sigmaLength);

	/** 
	 * Extract a pseudo-random block from the PRNG.
	 * 
	 * @param	z			PRNG output (may be null).
	 * @param	zLength	its length in bytes (may be zero).
	 * 
	 * @return	pseudo-random block of the desired length.
	 *			If z is null, a new buffer is allocated,
	 *			otherwise the same buffer is returned.
	 */
	byte[] fetch(byte[] z, int zLength);

	/** 
	 * Enforce forward security by cleaning up the PRNG. 
	 * 
	 */
	void forget();
	
}