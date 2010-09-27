package util;

public class Printer {
	public static void printVector(String name, byte[] A){
		System.out.printf(name + ": ");
		for (int i = 0; i < A.length; i++){ 
            System.out.printf("%2s ", Util.byteToHex(A[i]));
		}
		System.out.println();
	}
	
	public static void printVector(byte[] A){
		System.out.printf("|");
		for (int i = 0; i < A.length; i++){ 
            System.out.printf("%2s", Util.byteToHex(A[i]));
            if (i < (A.length - 1)){
            	System.out.printf(" ");
            }
		}
		System.out.println("|");
	}
	
	public static void printVectorAsPlainText(String name, byte[] A){
		System.out.printf(name + ": ");
		for (int i = 0; i < A.length; i++){ 
            System.out.printf("%2s", Util.byteToHex(A[i]));
		}
		System.out.println();
	}
	
	public static String getVectorAsPlainText(byte[] A){
		String vectorString = new String();
		for (int i = 0; i < A.length; i++){ 
            vectorString += Util.byteToHex(A[i]);
		}
		return vectorString;
	}
	
	public static void printMatrix(String name, byte[][] A){
		System.out.println(name);
		for (int i = 0; i < A.length; i++){ 
			printVector(A[i]);
		}
		System.out.println();
	}
}
