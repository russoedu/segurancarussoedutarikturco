package marvin;

import util.Printer;
import util.Util;
import curupira1.Curupira1;

public class Test {
	
	public static void main(String[] args)
	{
		String message = "0102030405060708090a0b0c0d";
		String key = "0102030405060708090a0b0c";
		
		Curupira1 c = new Curupira1();
		Marvin m = new Marvin();
		m.setCipher(c);
		
		byte[] K = Util.convertStringToVector(key);
		m.setKey(K, 96);
		
		m.init();
		
		byte[] M = Util.convertStringToVector(message);
		m.update(M, M.length);
		
		byte[] buffer = new byte[12];
		buffer = m.getTag(buffer, 4);
		
		Printer.printVectorAsPlainText("tag", buffer);
		
	}

}
