package fase1;

import util.Printer;
import util.Util;

public class MarvinTest {
	
	public static void main(String[] args)
	{
		String message = "Mensagem";
//		String key = "0102030405060708090a0b0c";
		String key = "73656e686100000000000000";
		
		Curupira1 c = new Curupira1();
		Marvin m = new Marvin();
		m.setCipher(c);
		byte[] K = Util.convertStringToVector(key);
		m.setKey(K, 96);
		
		m.init();
		
		byte[] M = message.getBytes();
		m.update(M, M.length);
		Printer.printVector(M);
		
		byte[] buffer = new byte[12];
		buffer = m.getTag(buffer, 4);
		
		Printer.printVectorAsPlainText("tag", buffer);		
	}

}
