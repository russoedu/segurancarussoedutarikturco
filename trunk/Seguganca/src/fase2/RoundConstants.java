package fase2;

import util.Util;

public class RoundConstants {
	
	public static byte[] getRoundConstant (int roundNumber)
	{
		switch (roundNumber)
		{
			case 0:
				return Util.convertStringToVector("0000000000000001");
			case 1:
				return Util.convertStringToVector("0000000000008082");
			case 2:
				return Util.convertStringToVector("800000000000808A");
			case 3:
				return Util.convertStringToVector("8000000080008000");
			case 4:
				return Util.convertStringToVector("000000000000808B");
			case 5:
				return Util.convertStringToVector("0000000080000001");
			case 6:
				return Util.convertStringToVector("8000000080008081");
			case 7:
				return Util.convertStringToVector("8000000000008009");
			case 8:
				return Util.convertStringToVector("000000000000008A");
			case 9:
				return Util.convertStringToVector("0000000000000088");
			case 10:
				return Util.convertStringToVector("0000000080008009");
			case 11:
				return Util.convertStringToVector("000000008000000A");
			case 12:
				return Util.convertStringToVector("000000008000808B");
			case 13:
				return Util.convertStringToVector("800000000000008B");
			case 14:
				return Util.convertStringToVector("8000000000008089");
			case 15:
				return Util.convertStringToVector("8000000000008003");
			case 16:
				return Util.convertStringToVector("8000000000008002");
			case 17:
				return Util.convertStringToVector("8000000000000080");
			case 18:
				return Util.convertStringToVector("000000000000800A");
			case 19:
				return Util.convertStringToVector("800000008000000A");
			case 20:
				return Util.convertStringToVector("8000000080008081");
			case 21:
				return Util.convertStringToVector("8000000000008080");
			case 22:
				return Util.convertStringToVector("0000000080000001");
			case 23:
				return Util.convertStringToVector("8000000080008008");
			default:
				return new byte[0];
		}
	}

}
