package marvin;

import util.Util;
import curupira1.Curupira1;

public class Test {
	
	public static void main(String[] args)
	{
		String message = "00";
		String key = "000000000000000000000000";
		
		Curupira1 c = new Curupira1();
		Marvin m = new Marvin();
		m.setCipher(c);
		
		byte[] K = Util.convertStringToVector(key);
		m.setKey(K, 96);
		
		m.init();
		
		byte[] M = Util.convertStringToVector(message);
		m.update(M, M.length);
		
		byte[] buffer = new byte[12];
		m.getTag(buffer, 12);
		
	}

}
