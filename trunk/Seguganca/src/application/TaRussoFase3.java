package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

import fase2.Keccak;
import fase1.LetterSoup;
import fase1.Marvin;
import interfaces.DigitalSignature;
import interfaces.HashFunction;
import interfaces.KeyEncapsulation;
import interfaces.SpongeRandom;
import fase3.Schnorr;
import fase2.SpongePRNG;
import util.Conversion;
import util.IO;
import util.Random;
import fase3.CramerShoup;
import fase1.Curupira1;

public class TaRussoFase3 {

	// CONSTANTS
	private static final int KEY_SIZE = 96;
	private static final int HASH_BITRATE = 576;
	private static final int HASH_DIVERSIFIER = 64;

	private static final String KEY_FILES_NAME = "public_key";

	// GLOBAL VARIABLES
	private static String password;
	private static String keyFilesPath = "/";

	private static BigInteger q; 
	private static BigInteger p;
	private static BigInteger g;
	private static BigInteger g1;
	private static BigInteger g2;

	private static HashFunction hash;
	private static SpongeRandom sr;

	public static void main(String[] args) throws IOException 
	{ 
		System.out.println("******************************************************************");
		System.out.println("* Bem vindo ao Projeto TaRusso 3, de Eduardo Russo e Tarik Feres *");
		System.out.println("******************************************************************\n");
		init();
		mainMenu();
	}



	private static void mainMenu() {
		String instructions = "\nMENU PRINCIPAL:\n"
			+ "[1] Selecione o arquivo que contem as chaves publicas (nomeadas como public_key.extensao) [default: /]\n"
			+ "[2] Digite a senha (ASCII)\n"
			+ "[3] Selecione um arquivo para ser cifrado\n"
			+ "[4] Selecione um arquivo para ser decifrado\n"
			+ "[5] Selecione um arquivo para ser assinado\n"
			+ "[6] Selecione um arquivo e sua assinatura para a verificacao\n"
			+ "[0] Sair\n" + "OPTION: ";
		boolean validValue = false;
		int key;
		System.out.print(instructions);
		while (!validValue) {
			try {
				key = new Integer(reader.readLine().trim());
				switch (key) {
				case 1: {
					pathInput();
					System.out.print(instructions);
					break;
				}
				// Gera as chaves publicas a partir de uma senha privada
				case 2: {
					passwordInput();                                

					KeyEncapsulation  asymCipher = new CramerShoup();
					asymCipher.setup(p, q, g1, g2, hash, sr);
					BigInteger[] asymPublicKey = asymCipher.makeKeyPair(password);

					// Gera os arquivo .csc, .csd, .csh e .sy 
					String path = keyFilesPath + KEY_FILES_NAME + ".csc";
					saveDocument(
							"Arquivo CSC \""
							+ path
							+ "\" gerado com sucesso!",
							path, Conversion.bigIntegerToByteArray(asymPublicKey[0]));

					path = keyFilesPath + KEY_FILES_NAME + ".csd";
					saveDocument(
							"Arquivo CSD \""
							+ path
							+ "\" gerado com sucesso!",
							path, Conversion.bigIntegerToByteArray(asymPublicKey[1]));

					path = keyFilesPath + KEY_FILES_NAME + ".csh";
					saveDocument(
							"Arquivo CSH \""
							+ path
							+ "\" gerado com sucesso!",
							path, Conversion.bigIntegerToByteArray(asymPublicKey[2]));

					DigitalSignature digSign = new Schnorr();
					digSign.setup(p, q, g, hash, sr);

					BigInteger signPublicKey = digSign.makeKeyPair(password);

					path = keyFilesPath + KEY_FILES_NAME + ".sy";
					saveDocument(
							"Arquivo SY \""
							+ path
							+ "\" gerado com sucesso!",
							path, Conversion.bigIntegerToByteArray(signPublicKey));

					System.out.print(instructions);
					break;
				}

				// Cifra um arquivo
				case 3: {
					byte[] cipherKey = Random.getRandomNumber(KEY_SIZE/8);

					String filePath = encrypt(cipherKey);

					BigInteger[] csPublicKey = new BigInteger[3];

					
					String path = keyFilesPath + KEY_FILES_NAME + ".csc";
					byte[] c = readDocument(path);

					if (c == null) {
						System.out.println("Chave publica Cramer-Shoup C nao encontrada.");
						break;
					}

					csPublicKey[0] = Conversion.byteArrayToBigInteger(c);

					path = keyFilesPath + KEY_FILES_NAME + ".csd";
					byte[] d = readDocument(path);

					if (d == null) {
						System.out.println("Chave publica Cramer-Shoup D nao encontrada.");
						break;
					}

					csPublicKey[1] = Conversion.byteArrayToBigInteger(d);

					path = keyFilesPath + KEY_FILES_NAME + ".csh";
					byte[] h = readDocument(path);

					if (h == null) {
						System.out.println("Chave publica Cramer-Shoup H nao encontrada.");
						break;
					}

					csPublicKey[2] = Conversion.byteArrayToBigInteger(h);

					CramerShoup asymCipher = new CramerShoup();
					asymCipher.setup(p, q, g1, g2, hash, sr);

					BigInteger[] csCryptogram = asymCipher.encrypt(csPublicKey, cipherKey);

					path = filePath + ".csu1";
					saveDocument(
							"Arquivo CSU1 \""
							+ path
							+ "\" gerado com sucesso!",
							path, Conversion.bigIntegerToByteArray(csCryptogram[0]));

					path = filePath + ".csu2";
					saveDocument(
							"Arquivo CSU2 \""
							+ path
							+ "\" gerado com sucesso!",
							path, Conversion.bigIntegerToByteArray(csCryptogram[1]));

					path = filePath + ".cse";
					saveDocument(
							"Arquivo CSE \""
							+ path
							+ "\" gerado com sucesso!",
							path, Conversion.bigIntegerToByteArray(csCryptogram[2]));

					path = filePath + ".csv";
					saveDocument(
							"Arquivo CSV \""
							+ path
							+ "\" gerado com sucesso!",
							path, Conversion.bigIntegerToByteArray(csCryptogram[3]));

					System.out.print(instructions);
					break;
				}
				// Decifra um arquivo com uma chave publica Cramer-Shoup
				case 4: {
					
					BigInteger[] csCryptogram = new BigInteger[4];

					String[] filePath = new String[1];
					byte[] document = readDocument("Digite o caminho do arquivo que serah decifrado: ", filePath);

					filePath[0] = filePath[0].split("\\.")[0] + "." + filePath[0].split("\\.")[1];

					String path = filePath[0] + ".csu1";
					byte[] u1 = readDocument(path);

					if (u1 == null) {
						System.out.println("Arquivo CSU1 nao encontrado.");
						break;
					}

					csCryptogram[0] = Conversion.byteArrayToBigInteger(u1);

					path = filePath[0] + ".csu2";
					byte[] u2 = readDocument(path);
					if (u2 == null) {
						System.out.println("Arquivo CSU2 nao encontrado.");
						break;
					}

					csCryptogram[1] = Conversion.byteArrayToBigInteger(u2);

					path = filePath[0] + ".cse";
					byte[] e = readDocument(path);

					if (e == null) {
						System.out.println("Arquivo CSE nao encontrado.");
						break;
					}

					csCryptogram[2] = Conversion.byteArrayToBigInteger(e);

					path = filePath[0] + ".csv";
					byte[] v = readDocument(path);

					if (v == null) {
						System.out.println("Arquivo CSV nao encontrado.");
						break;
					}

					csCryptogram[3] = Conversion.byteArrayToBigInteger(v);

					KeyEncapsulation asymCipher = new CramerShoup();
					asymCipher.setup(p, q, g1, g2, hash, sr);
					byte[] cipherKey =  asymCipher.decrypt(password, csCryptogram);

					if (cipherKey != null) 
					{
						decrypt(cipherKey, document, filePath[0]);
					} 
					else 
						System.out.println("Criptograma invalido.");

					System.out.print(instructions);
					break;
				}
				
				// Assina um arquivo
				case 5: {

					String[] filePath = new String[1];
					byte[] document = readDocument("Digite o caminho do arquivo a ser assinado: ", filePath);

					DigitalSignature digSign = new Schnorr();
					digSign.setup(p, q, g, hash, sr);
					digSign.init();
					digSign.update(document, document.length);

					BigInteger[] sig = digSign.sign(password);

					String path = filePath[0] + ".se";
					saveDocument(
							"Arquivo SE \""
							+ path
							+ "\" gerado com sucesso!",
							path, Conversion.bigIntegerToByteArray(sig[0]));

					path = filePath[0] + ".ss";
					saveDocument(
							"Arquivo SS \""
							+ path
							+ "\" gerado com sucesso!",
							path, Conversion.bigIntegerToByteArray(sig[1]));

					System.out.print(instructions);
					break;
				}

				// Verifica se um arquivo assinado eh valido
				case 6: {

					init();

					String[] filePath = new String[1];
					byte[] document = readDocument("Digite o caminho do arquivo assinado: ", filePath);

					DigitalSignature digSign = new Schnorr();
					digSign.setup(p, q, g, hash, sr);
					digSign.init();
					digSign.update(document, document.length);

					BigInteger[] sig = new BigInteger[2];

					String path = filePath[0] + ".se";
					byte[] e = readDocument(path);

					if (e == null) {
						System.out.println("Arquivo SE nao encontrado.");
						break;
					}

					sig[0] = Conversion.byteArrayToBigInteger(e);

					path = filePath[0] + ".ss";
					byte[] s = readDocument(path);

					if (s == null) {
						System.out.println("Arquivo SS nao encontrado.");
						break;
					}

					sig[1] = Conversion.byteArrayToBigInteger(s);

					path = keyFilesPath + KEY_FILES_NAME + ".sy";
					byte[] sy = readDocument(path);

					if (sy == null) {
						System.out.println("Chave publica Schnorr nao encontrada");
						break;
					}

					BigInteger y = Conversion.byteArrayToBigInteger(sy);

					if (digSign.verify(y, sig)){
						System.out.println("Arquivo validado com sucesso!");
					} else {
						System.out.println("Arquivo nao foi validado.");;
					}

					System.out.print(instructions);
					break;
				}

				// Exit the application.        
				case 0: {
					validValue = true;
					System.out.println("[Adeus]");
					break;
				}
				default:
					System.out.print("Opcao invalida!\n " + instructions);
					break;
				}
			} catch (Exception e) 
			{

				System.out.print("O programa executou uma operacao ilegal\n" + instructions);
			}
		}
	}

	public static void init() {
		BigInteger w  = BigInteger.valueOf(231L).setBit(2815);
		q  = BigInteger.ONE.setBit(256).subtract(BigInteger.ZERO.setBit(168));
		p  = BigInteger.valueOf(2L).multiply(q).multiply(w).add(BigInteger.ONE);
		g  = BigInteger.valueOf(2L).modPow(BigInteger.valueOf(2L).multiply(w), p);
		g1 = BigInteger.valueOf(2055L).modPow(BigInteger.valueOf(2L).multiply(w), p);
		g2 = BigInteger.valueOf(2582L).modPow(BigInteger.valueOf(2L).multiply(w), p);

		Keccak k = new Keccak();
		k.setBitRate(HASH_BITRATE);
		k.setDiversifier(HASH_DIVERSIFIER);

		hash = k;
		sr = new SpongePRNG(k);
	}

	private static InputStreamReader inputStreamReader = new InputStreamReader(System.in);
	private static BufferedReader reader = new BufferedReader(inputStreamReader);
	private static String lineRead;

	/**
	 * Function for path manipulation.
	 */
	private static void pathInput() {
		// Instructions string
		String instructions = "Digite o caminho da pasta em que as chaves publicas se encontram: ";

		boolean validValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim();

				// Validation
				if (0 < lineRead.length() && !lineRead.equals("")) {
					keyFilesPath = lineRead;

					// Output
					System.out.println("Sucesso!");

					validValue = true;
				} else {
					System.out.print("Nao sucesso... " + instructions);
				}
			} catch (Exception e) {

				System.out.print("O programa executou uma operacao ilegal\n" + instructions);
			}
		}
	}

	/**
	 * Function for Password manipulation.
	 */
	private static void passwordInput() {
		// Instructions string
		String instructions = "Digite sua senha: ";

		boolean validValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim();

				// Validation
				if (0 < lineRead.length() && !lineRead.equals("")) {
					password = lineRead;

					// Output
					System.out.println("Sucesso!");

					validValue = true;
				} else {
					System.out.print("Nao sucesso... " + instructions);
				}
			} catch (Exception e) {

				System.out.print("O programa executou uma operacao ilegal\n" + instructions);
			}
		}
	}

	/**
	 * Read a file from a specified as input absolute path.
	 * @param instructions a string with the instructions for this operation.
	 * @param filePath a buffer to store the file path and manipulate it.
	 * @return 
	 * A byte array buffer representing the read file.
	 */
	private static byte[] readDocument(String instructions, String[] filePath) {
		boolean validValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				filePath[0] = reader.readLine().trim();

				// Validation
				byte[] data = IO.readFile(filePath[0]); 
				if (null != data) {
					// Output
					System.out.println("Arquivo \"" + filePath[0]      + "\" lido com sucesso!");

					return data;
				} else {
					System.out.print("Arquivo \"" + filePath[0] + "\" nao encontrado. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Arquivo \"" + filePath[0] + "\" nao encontrado. " + instructions);
			}
		}
		return null;
	}

	/**
	 * Read file from a specified as parameter path.
	 * @param filePath the path to the file.
	 * @return 
	 * A byte array buffer representing the read file, or null if was not been found.
	 */
	private static byte[] readDocument(String filePath) {
		byte[] data = null;

		try {
			// Validation
			data = IO.readFile(filePath); 
			if (null != data) {
				// Output
				System.out.println("Arquivo \"" + filePath + "\" lido com sucesso!");
			} else 
				System.out.print("Arquivo \"" + filePath + "\" nao encontrado. ");

		} catch (Exception e) {
			System.out.print("Arquivo \"" + filePath + "\" nao encontrado. ");
		}               
		return data;
	}

	/**
	 * Save a text file.
	 * @param message a string with the message for this operation.
	 * @param filePath a string to the path where the file is going to be saved.
	 * @data the data to be saved to the file.
	 */
	private static void saveDocument(String message, String filePath, byte[] data) {
		try {
			if (IO.saveFile(filePath, data)) {
				System.out.println(message);
			} else {
				System.out.print("O programa executou uma operacao ilegal");
			}
		} catch (Exception e) {
			System.out.print("O programa executou uma operacao ilegal");
		}
	}

	private static final int IV_LENGTH = 96;
	private static final int MAC_LENGTH = 96;

	private static String encrypt (byte[] cipherKey) {
		Curupira1 curupira1 = new Curupira1();
		Marvin marvin = new Marvin();
		LetterSoup letterSoup = new LetterSoup();

		// Gets the file to be validated.
		String[] filePath = new String[1];
		byte[] document = readDocument("Digite o caminho do arquivo a ser cifrado: ", filePath);

		// Gets a initialization vector.
		byte[] iv = Random.getRandomNumber(IV_LENGTH/8);

		// Prepares the AEAD instance.
		letterSoup.setCipher(curupira1);
		letterSoup.setKey(cipherKey, cipherKey.length * 8);
		letterSoup.setCipher(curupira1);
		letterSoup.setIV(iv, iv.length);
		letterSoup.setMAC(marvin);
		marvin.setCipher(curupira1);

		// Gets the encrypted data.
		byte[] cData = letterSoup.encrypt(document,     document.length, new byte[document.length]);

		// Gets the tag based on the encrypted data.
		byte[] tag = letterSoup.getTag(new byte[MAC_LENGTH/8], MAC_LENGTH);

		// Save .ciph file
		String newFilePath = filePath[0] + ".ciph";
		saveDocument(
				"Cifrado com sucesso!\n"
				+ "Arquivo \""
				+ newFilePath
				+ "\" gerado com sucesso!",
				newFilePath, cData);

		// Save .mac file
		newFilePath = filePath[0] + ".mac";
		saveDocument(   
				"Autenticado com sucesso!\n"
				+ "Arquivo \""
				+ newFilePath
				+ "\" gerado com sucesso!",
				newFilePath, tag);

		// Save .iv file
		newFilePath = filePath[0] + ".iv";
		saveDocument(
				"Arquivo \""
				+ newFilePath
				+ "\" gerado com sucesso!",
				newFilePath, iv);

		return filePath[0];
	}

	private static boolean decrypt (byte[] key, byte[] document, String path) {

		boolean isValid = false;

		Curupira1 curupira1 = new Curupira1();
		Marvin marvin = new Marvin();
		LetterSoup letterSoup = new LetterSoup();

		marvin.setCipher(curupira1);

		byte[] macDocument = readDocument(path + ".mac");
		byte[] ivDocument = readDocument(path + ".iv");

		byte[] cipherKey = new byte[KEY_SIZE/8];
		for (int i = key.length - 1; i >= 0; i--) {
			cipherKey[i] = key[i];
		}

		letterSoup.setCipher(curupira1);
		letterSoup.setKey(cipherKey, cipherKey.length * 8);
		letterSoup.setIV(ivDocument, ivDocument.length);
		letterSoup.setMAC(marvin);

		byte[] mData = letterSoup.decrypt(document, document.length, new byte[document.length]);

		byte[] tag = letterSoup.getTag(new byte[macDocument.length], macDocument.length * 8);

		if(Conversion.byteToHex(tag).equals(Conversion.byteToHex(macDocument)))
		{
			System.out.println("Arquivo autenticado!");

			// Save .deciph file
			saveDocument(
					"Decifrado com sucesso!\n"
					+ "Arquivo \""
					+ path
					+ "\" gerado com sucesso.",
					path, mData);

			isValid = true;
		}
		else
			System.out.println("Arquivo nao autenticado.");

		return isValid;
	}

}