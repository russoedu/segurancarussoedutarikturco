<?php
interface BlockCipher {
	/**
	* Block size in bits.
	*/
	function blockBits();
	
	/** 
	 * Key size in bits. 
	 */
	function keyBits();
	
	/** 
	 * Setup the cipher key for this block cipher instance. 
	 * @param cipherKey the cipher key. 
	 * @param keyBits	size of the cipher key in bits. 
	 */
	function makeKey($cipherKey, $keyBits);
	
	/** 
	 * Encrypt exactly one block of plaintext. 
	 * @param mBlock	plaintext block. 
	 * @param cBlock	ciphertext block. 
	 */
	function encrypt($mBlock, $cBlock);
	
	/** 
	 * Decrypt exactly one block of ciphertext. 
	 * @param cBlock	ciphertext block. 
	 * @param mBlock	plaintext block. 
	 */
	function decrypt($cBlock, $mBlock);
}
?>