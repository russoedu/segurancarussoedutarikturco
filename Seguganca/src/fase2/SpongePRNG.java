package fase2;
import util.Util;
import interfaces.SpongeRandom;

public class SpongePRNG implements SpongeRandom{
	
	byte[] bin;
	byte[] bout;
	Keccak keccak;
	int rho;

	public SpongePRNG(Keccak k)
	{
		keccak = k;
	}
	
	@Override
	public void feed(byte[] sigma, int sigmaLength) {
		
		byte[] aux = Util.concat(bin, sigma);
		
		byte[][] M = new byte[aux.length/rho + 1][];
		
		for (int i = 0; i < M.length; i++)
		{
			M[i] = new byte[ i == M.length - 1 ? aux.length % rho : rho ];
			for (int j = 0; j < M[i].length; j++)
				M[i][j] = aux[i*rho + j];
		}
		
		for (int i = 0; i < M.length - 1; i++)
		{
			keccak.duplexing(M[i], M[i].length, new byte[0], 0);
		}
		bin = M[M.length - 1];
		bout = new byte[0];
		
	}

	@Override
	public byte[] fetch(byte[] z, int zLength) {
		
		while (bout.length < zLength)
		{
			bout = Util.concat(bout, keccak.duplexing(bin, bin.length, new byte[keccak.getBitRate()/8], keccak.getBitRate()/8));
			bin = new byte[0];
		}
		
		for (int i = 0; i < zLength; i++)
			z[i] = bout[i];
		
		byte[] aux = new byte[bout.length - zLength];
		for (int i = 0; i < bout.length - zLength; i++)
			aux[i] = bout[i + zLength];
		bout = aux;
		
		return z;
	}

	@Override
	public void forget() {
		
		byte[] z = keccak.duplexing(bin, bin.length, new byte[keccak.getBitRate()], keccak.getBitRate());
		bin = new byte[0];
		
		for (int i = 0; i < keccak.getCapacity()/keccak.getBitRate(); i++)
			z = keccak.duplexing(z, z.length, new byte[keccak.getBitRate()], keccak.getBitRate());
		
		bout = new byte[0];
		
	}

	@Override
	public void getBitRate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getCapacity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(int hashBits) {
		keccak.init(hashBits);
		bin = new byte[0];
		bout = new byte[0];
		rho = keccak.getBitRate()/8 - 1;
		
	}

}
