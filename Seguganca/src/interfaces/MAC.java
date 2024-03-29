package interfaces;

public interface MAC {
	/** 
	 * Provide the underlying block cipher. 
	 * @param cipher	the underlying block cipher. 
	 */
	void setCipher(BlockCipher cipher);
	
	/** 
	 * Provide the cipher key. 
	 * @param cipherKey the cipher key.
	 * @param keyBits	the key size in bits.
	 */
	void setKey(byte[] cipherKey, int keyBits);
	
	/** 
	 * Prepare to authenticate a new message. 
	 */
	void init();

	/** 
	 * Prepare to authenticate a new message. 
	 */
	void init(byte[] R);
	
	/** 
	 * Update the MAC tag computation with a message chunk. 
	 * @param aData		data chunk to authenticate.
	 * @param aLength	its length in bytes.
	 */
	void update(byte[] aData, int aLength);
	
	/**
	 * Complete if necessary the data processing and
	 * get the MAC tag of the whole message provided.
	 * @param tag 	the MAC tag buffer.
	 * @return MAC tag of the whole message. 
	 * If the tag parameter is null, a new buffer is automatically 
	 * allocated, otherwise the input buffer is returned. 
	 */
	byte[] getTag(byte[] tag, int tagBits);
	
	/**
	 * Complete if necessary the data processing and
	 * get the MAC tag of the whole message provided.
	 * @param tag 	the MAC tag buffer.
	 * @return MAC tag of the whole message. 
	 * If the tag parameter is null, a new buffer is automatically 
	 * allocated, otherwise the input buffer is returned. 
	 */
	byte[] getTag(byte[] tag, int tagBits, boolean encript);
}
