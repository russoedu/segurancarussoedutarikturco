package curupira1;

import util.Util;
import util.Printer;

public class Test {
	public static void main(String[] args) {
		
		String key = "555555555555555555555555555555555555555555555555";
		String plain = "005555550055555500555555";
		String cipher = "590a20b72b05194ae4a1a6f2";
		
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
