<?php
include_once '../pcs2055/BlockCipher.php';
include_once '../util/Matrix.php';

class Curupira1 implements BlockCipher {
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * Local vars
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	
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
	
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * Inherited abstract methods
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	
	function blockBits() {
		return $this->blockBits;
	}
	
	function keyBits() {
		return $this->keyBits;
	}
	
	function makeKey($cipherKey, $keyBits) {
		$this->cipherKey = $cipherKey;
		$this->keyBits = $keyBits;
		
		$t = $keyBits / 48;
		
		for($i = 0; $i < 3; $i++) {
			for($j = 0; $j < (2 * $t); $j++) {
				$auxByteArray = $cipherKey [$i + 3 * $j];
				$this->K[$i][$j] = $auxByteArray[0];
			}
		}
	
	}
	
	function encrypt($mBlock, $cBlock) {
		// TODO
	}
	
	function decrypt($cBlock, $mBlock) {
		// TODO
	}
	
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * The CURUPIRA-1 round methods
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	
	/**
	 * The non-linear layer 'gama'
	 * gama(a) = b <=> b[i][j] = S[a[i][j]]
	 * @param Matrix of byte[][] a
	 * @return Matrix of byte[][] b
	 */
	function nonLinearLayerGama($matrix){
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
	function permutationLayerPi($matrix) {
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
	 * @param Matrix of [][]byte a
	 * @return Matrix of byte[][] b = D*a
	 */
	function linearDiffusionLayerTheta($matrix) {
		$D = array (array (0x03, 0x02, 0x02 ), array (0x04, 0x05, 0x04 ), array (0x06, 0x06, 0x07 ) );
		return Matrix::multiply ( $D, $matrix );
	}
	
	/**
	 * The key addition 'sigma'[k]
	 * sigma[k](a) = b <=> b[i][j] = a[i][j] ^ k[i][j]
	 * @param Matrix of byte[][] k
	 * @param Matrix of byte[][] a
	 * @return Matrix of byte[][]
	 */
	function keyAdditionLayerSigma($k, $a) {
		for($i = 0; $i < count ( $a ); $i ++) {
			for($j = 0; $j < count ( $a [$i] ); $j ++) {
				$a [$i] [$j] = $a [$i] [$j] ^ $k [$i] [$j];
			}
		}
		return $a;
	}
	
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * Schedule methods
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
/**
	 * The ciclic shift Csi
	 * Csi(a) = b <=> 	b[0][j] = a[0][j]
	 * 					b[1][j] = a[1][(j + 1) mod 2t]
	 * 					b[2][j] = a[2][(j - 1) mod 2t] 
	 * @param Matrix of byte[][] a
	 * @return Matrix of byte[][] b
	 */
	function ciclicShiftCsi($a){
		//i = 0
		$b = array($a[0]);
		//i = 1
		for($j = 0; $j < (count($a) + 1); $j++){
			$b[1][$j] = $a[1][($j + 1) % (count($a) + 1)];
		}
		//i = 2
		for($j = 0; $j < (count($a) + 1); $j++){
			if ($j == 0){
				$b[2][$j] = $a[2][count($a)];
			}
			else{
				$b[2][$j] = $a[2][$j - 1];
			}
		}
		return $b;
	}
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * General
	 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	
	/**
	 * The S computing
	 * Computes S[u] from the mini-boxes P and Q
	 * @param byte $u
	 * @return byte S[$u]
	 */
	function S($u) {
		$uh1 = $this->P[($u >> 4) & 0x0F];
		$ul1 = $this->Q[$u & 0x0F];

		$uh2 = $this->Q[($uh1 & 0x0C) ^ (($ul1 >> 0x02) & 0x03)];
		$ul2 = $this->P[(($uh1 << 0x02) & 0x0C) ^ ($ul1 & 0x03)];

		$uh1 = $this->P[($uh2 & 0x0C) ^ (($ul2 >> 0x02) & 0x03)];
		$ul1 = $this->Q[(($uh2 << 0x02) & 0x0C) ^ ($ul2 & 0x03)];

		return (($uh1 << 0x04) ^ $ul1);
	}
	
	/**
	 * The schedule constants q
	 * q[i][j](s) = S[2t(s-1) + j]		if i=0
	 * q[i][j](s) = 0 					otherwise
	 * @param Matrix of byte[][] s
	 * @return Matrix of byte[][] q(s)
	 */
	function q($s){
		$t = $this->keyBits / 48;
		$q = array();
		//q(0) = 0
		if($s == 0x00){
			for ($i = 0; $i < 3; $i++){
				for ($j = 0; $j < (2 * $t); $j++){
					$q[$i][$j] = 0x00;
				}
			}
		}
		else
		{
			for ($i = 0; $i < 3; $i++){
				for ($j = 0; $j < (2 * $t); $j++){
					//i = 0
					if($i == 0){
						$auxByteArray = $this->S((0x02 * $t * ($s - 0x01) + $j));
						$q[$i][$j] = $auxByteArray;
					}
					//otherwise
					else{
						$q[$i][$j] = 0x00;
					}
				}
			}
		}
		return $q;
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

function xtimes($x) 
	{
		return (($x << 1) ^ ((($x >> 7) & 1) * 0x1b));
	}

$hexa1 = xtimes(0xFF);
$hexa2 = 0x02;

$hexasum = ($hexa1 + $hexa2)  % 0xFF;

echo $hexa1;
//$curupira = new Curupira1 ();
//
////$matrix1 = array (array (0x01, 0x02, 0x03 ), array (0x04, 0x05, 0x06 ), array (0x07, 0x08, 0x09 ) );
////$matrix2 = array (array (0x01, 0x02, 0x03, 0x04 ), array (0x05, 0x06, 0x07, 0x08 ), array (0x09, 0x0A, 0x0B, 0x0C ) );
////$myMatrix = mkmatrix ( 3, 4 );
////
////Matrix::print_matrix ( $myMatrix, "MyMatrix" );
////Matrix::print_matrix ( $matrix2, "Matrix 2" );
////Matrix::print_matrix ( $curupira->permutationPi ( $myMatrix ), "minha matriz" );
//
//$myKey = mk_matrix ( 12, 1 );
//$myMatrix = mk_matrix ( 3, 4 );
//$curupira->makeKey ( $myKey, 96 );
//
//Matrix::print_matrix ( $myMatrix, "matrix" );
//$matrix = $curupira->ciclicShiftCsi($myMatrix);
//Matrix::print_matrix ( $matrix, "matrix" );

?>