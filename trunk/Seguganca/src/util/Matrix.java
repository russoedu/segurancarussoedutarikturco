package util;

public class Matrix {
	/**
	 * Multiplie two matrices
	 * @param Matrix a
	 * @param Matrix b
	 * @return resultant matrix
	 * Code downloaded from: http://ryan.ifupdown.com/2010/01/19/matrix-multiplication-in-java/
	 */
	public static byte[][] multiply(byte a[][], byte b[][]) {
		int aRows = a.length, aColumns = a[0].length, bRows = b.length, bColumns = b[0].length;
		if (aColumns != bRows) {
			throw new IllegalArgumentException("A:Rows: " + aColumns
					+ " did not match B:Columns " + bRows + ".");
		}
		byte[][] resultant = new byte[aRows][bColumns];
		for (int i = 0; i < aRows; i++) { // aRow
			for (int j = 0; j < bColumns; j++) { // bColumn
				for (int k = 0; k < aColumns; k++) { // aColumn
					resultant[i][j] += a[i][k] * b[k][j];
				}
			}
		}
		return resultant;
	}
}
