<?php
class Matrix {
	/**
	 * Multiplie two matrices
	 * @param Matrix a
	 * @param Matrix b
	 * @return resultant matrix
	 * Code downloaded from: http://ryan.ifupdown.com/2010/01/19/matrix-multiplication-in-java/
	 */
	static function multiply($a, $b) {
		$aRows = count ( $a );
		$aColumns = count ( $a [0] );
		$bRows = count ( $b );
		$bColumns = count ( $b [0] );
		$resultant = array ();
		
		if ($aColumns != $bRows) {
			die ( "A:Rows: " + $aColumns + " did not match B:Columns " + $bRows + "." );
		}
		for($i = 0; $i < $aRows; $i ++) { // aRow
			for($j = 0; $j < $bColumns; $j ++) { // bColumn
				for($k = 0; $k < $aColumns; $k ++) { // aColumn
					$resultant [$i] [$j] += $a [$i] [$k] * $b [$k] [$j];
				}
			}
		}
		return $resultant;
	}
	
	static function print_matrix($A, $matrixName) {
		echo "Matriz $matrixName:\n";
		for($i = 0; $i < count ( $A ); $i ++) {
			$rowCopy = $A [$i];
			for($j = 0; $j < count ( $A [$i] ); $j ++) {
				echo $A [$i] [$j] . "\t";
			}
			echo "\n";
		}
	}
}
?>