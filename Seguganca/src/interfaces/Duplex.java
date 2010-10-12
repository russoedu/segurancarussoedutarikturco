package interfaces;

public interface Duplex {
	
	/** 
	 * Get the duplex bit-rate r. 
	 */
	int getBitRate();
	
	/** 
	 * Get the duplex capacity c. 
	 */
	int getCapacity();

	/** 
	 * Prepare for full-duplex operation at the desired security 
	 * level. 
	 *
	 * @param hashBits	the desired hash size in bits (used 
	 *					only to specify the desired security level; 
	 *					0 = default).
	 */
	void init(int hashBits);

	/** 
	 * Perform a duplex operation.
	 * 
	 * @param sigma			data input (may be null; will be padded
	 *						if shorter than the bit-rate).
	 *
	 * @param sigmaLength	its length in bytes (may be zero; must
	 *						not exceed the bit-rate).
	 *
	 * @param z				data output (may be null).
	 *
	 * @param zLength		its length in bytes (may be zero; must
	 *						not exceed the bit-rate).
	 * 
	 * @return				duplexing value truncated to the desired length.
	 *						If z is null, a new buffer is allocated,
	 *						otherwise the input buffer is returned.
	 */
	byte[] duplexing(byte[] sigma, int sigmaLength, byte[] z, int zLength);
	
}