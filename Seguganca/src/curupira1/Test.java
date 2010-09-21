package curupira1;

import util.Util;
import util.Printer;

public class Test {
	public static void main(String[] args) {
		
		String key = "000000000000000000000000";
		String plain = "000000000000000000000000";
		String cipher = "b48cbb9149131c39995ffb3a";
		
		byte[] cipherKey = Util.convertStringToVector(key);
		byte[] plainText = Util.convertStringToVector(plain);
		byte[] cipherText = Util.convertStringToVector(cipher);
		Printer.printVectorAsPlainText("original plain  ", plainText);
		
		Curupira1 c = new Curupira1();
		
		c.makeKey(cipherKey, cipherKey.length * 8);
		c.encrypt(plainText, cipherText);
		c.decrypt(cipherText, plainText);
	}
}
