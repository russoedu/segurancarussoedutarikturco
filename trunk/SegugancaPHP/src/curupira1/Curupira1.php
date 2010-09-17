<?php
include_once '../pcs2055/BlockCipher.php';
include_once '../util/Matrix.php';

class Curupira1 implements BlockCipher {
	/**
	 * The block bits
	 * @var int
	 */
	private $blockBits;
	/**
	 * The key bits
	 * @var int
	 */
	private $keyBits;
	/**
	 * The cypher key
	 * @var byte
	 */
	private $cipherKey;
	
	// Although it's not a good PHP convention, we used uppercase names to
	// match the algorithm names
	/**
	 * The P array
	 * @var array of bytes
	 */
	private $P = array (0x03, 0x0F, 0x0E, 0x00, 0x05, 0x04, 0x0B, 0x0C, 0x0D, 0x0A, 0x09, 0x06, 0x07, 0x08, 0x02, 0x01 );
	/**
	 * The Q array
	 * @var array of bytes
	 */
	private $Q = array (0x09, 0x0E, 0x05, 0x06, 0x0A, 0x02, 0x03, 0x0C, 0x0F, 0x00, 0x04, 0x0D, 0x07, 0x0B, 0x01, 0x08 );
	/**
	 * The K key matrix
	 * @var matrix of bytes
	 */
	private $K;
	

	function blockBits() {
		return $this->blockBits;
	}
	
	function keyBits() {
		return $this->keyBits;
	}
	
	function K() {
		return $this->K;
	}
	
	function makeKey($cipherKey, $keyBits) {
		$this->cipherKey = $cipherKey;
		$this->keyBits = $keyBits;
		
		$t = $keyBits / 48;
		
		for($row = 0; $row < 3; $row ++) {
			for($column = 0; $column < (2 * $t); $column ++) {
				$auxByteArray = $cipherKey [$row + 3 * $column];
				$this->K [$row] [$column] = $auxByteArray[0];
			}
		}
	
	}
	
	function encrypt($mBlock, $cBlock) {
		// TODO
	}
	
	function decrypt($cBlock, $mBlock) {
		// TODO
	}
	
	
	/**
	 * The nonlinear layer 'gama'
	 * gama(a) = b <=> b[i][j] = S[a[i][j]]
	 * @param Matrix of byte[][] a
	 * @return Matrix of byte[][] b
	 */
	function nonLinearGama($matrix){
		for ($i = 0; $i < count($matrix); $i++){
			for ($j = 0; $j < count($matrix[$i]); $j++){
				$matrix[$i][$j] = $this->S($matrix[$i][$j]);
			}
		}
		return matrix;
	}
	
	/**
	 * The permutation layer 'pi'
	 * pi(a) = b <=> b[i][j] = a[i][i ^ j]
	 * @param byte[][] A
	 * @return byte[][] permuted A
	 */
	function permutationPi($matrix) {
		for($i = 0; $i < count ( $matrix ); $i ++) {
			$rowCopy = $matrix [$i];
			for($j = 0; $j < count ( $matrix [$i] ); $j ++) {
				$matrix [$i] [$j] = $rowCopy [$i ^ $j];
			}
		}
		return $matrix;
	}
	
	/**
	 * The linear diffusion layer 'theta'
	 * theta(a) = b <=> b = D * a
	 * @param Matrix a[][]
	 * @return D*a = b
	 */
	function diffusionTheta($matrix) {
		$D = array (array (0x03, 0x02, 0x02 ), array (0x04, 0x05, 0x04 ), array (0x06, 0x06, 0x07 ) );
		return Matrix::multiply ( $D, $matrix );
	}
	
	/**
	 * The key addition 'sigma'[k]
	 * sigma[k](a) = b <=> b[i][j] = a[i][j] ^ k[i][j]
	 */
	function additionSigma($k, $a) {
		for($i = 0; $i < count ( $a ); $i ++) {
			for($j = 0; $j < count ( $a [$i] ); $j ++) {
				$a [$i] [$j] = $a [$i] [$j] ^ $k [$i] [$j];
			}
		}
		return $a;
	}
}

function mk_matrix($rows, $cols) {
	$count = 1;
	$mx = array ();
	for($i = 0; $i < $rows; $i ++) {
		for($j = 0; $j < $cols; $j ++) {
			$mx [$i] [$j] = $count ++;
		}
	}
	return ($mx);
}

$curupira = new Curupira1 ();

//$matrix1 = array (array (0x01, 0x02, 0x03 ), array (0x04, 0x05, 0x06 ), array (0x07, 0x08, 0x09 ) );
//$matrix2 = array (array (0x01, 0x02, 0x03, 0x04 ), array (0x05, 0x06, 0x07, 0x08 ), array (0x09, 0x0A, 0x0B, 0x0C ) );
//$myMatrix = mkmatrix ( 3, 4 );
//
//Matrix::print_matrix ( $myMatrix, "MyMatrix" );
//Matrix::print_matrix ( $matrix2, "Matrix 2" );
//Matrix::print_matrix ( $curupira->permutationPi ( $myMatrix ), "minha matriz" );


$myKey = mk_matrix ( 24, 1 );
Matrix::print_matrix ( $myKey, "cipherKey" );
$curupira->makeKey ( $myKey, 192 );
Matrix::print_matrix ( $curupira->K (), "K" );

?>