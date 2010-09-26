package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pcs2055.BlockCipher;

import curupira1.Curupira1;

import lettersoup.LetterSoup;
import marvin.Marvin;

import util.Printer;
import util.Util;

public class TaRusso {
	private static boolean debug = false;

	private static InputStreamReader inputStreamReader = new InputStreamReader(System.in);
	private static BufferedReader reader = new BufferedReader(inputStreamReader);
	private static String lineRead;
	
	private static int keyBits = 0;
	private static int ivLength = 0;
	private static int aLength = 0;
	private static byte[] cipherKey = null;
	private static String autenticateDocument;
	private static String macValidateDocument;
	private static String cipherAndAutenticateDocument;
	private static String validateAndDecipherDocument;
	
//	private static String cipherAndAutenticateDocument;
	
	
	
	
	public static void main(String[] args) throws IOException {
		Curupira1 curupira1 = new Curupira1();
		Marvin marvin = new Marvin();
		LetterSoup letterSoup = new LetterSoup();
		
		System.out
				.println("************************************************************");
		System.out
				.println("Bem vindo ao Projeto Tarusso, de Eduardo Russo e Tarik Feres");
		System.out
				.println("************************************************************\n");

//		while(true){
			mainMenu(curupira1, marvin, letterSoup);
//		}
		// Key Size input
//		keySizeInput();

		// IV input
//		ivSizeInput();

		// MAC input
//		aLengthInput();

		// Cipher Key input
//		cipherKeyInput();
	}

	private static void mainMenu(Curupira1 curupira1, Marvin marvin, LetterSoup letterSoup){
		String instructions = "Por favor, escolha uma das opções abaixo:\n" +
				"[1] Selecionar um tamanho de chave dentre os valores admissíveis\n" +
				"[2] Selecionar um tamanho de IV e de MAC entre o mínimo de 64 bits e o tamanho completo do bloco\n" +
				"[3] Escolher uma senha alfanumérica (ASCII)\n" +
				"[4] Selecionar um arquivo para ser apenas autenticado\n" +
				"[5] Selecionar um arquivo com seu respectivo MAC para ser validado\n" +
				"[6] Selecionar um arquivo para ser cifrado e autenticado\n" +
				"[7] Selecionar um arquivo cifrado com seus respectivos IV e MAC para ser validado e decifrado\n" +
				"[8] Selecionar um arquivo para ser cifrado e autenticado, e um arquivo correspondente de dados associados para ser autenticado\n" +
				"[9] Selecionar um arquivo cifrado com seus respectivos IV e MAC para ser validado e decifrado, um arquivo correspondente de dados associados para ser autenticado\n" +
				"Opção: ";
		boolean validValue = false;
		int key;
		System.out.print(instructions);
		while (!validValue) {
			try {
				key = new Integer(reader.readLine().trim());
				switch (key) {
				//Selecionar um tamanho de chave dentre os valores admissíveis
				case 1:
					keySizeInput();
					System.out.print(instructions);
					break;
				//Selecionar um tamanho de IV e de MAC entre o mínimo de 64 bits e o tamanho completo do bloco"
				case 2:
					ivSizeInput();
					aLengthInput();
					System.out.print(instructions);
					break;
				//Escolher uma senha alfanumérica
				case 3:
					cipherKeyInput();
					System.out.print(instructions);
					break;
				//Selecionar um arquivo para ser apenas autenticado
				//Marvin - update(byte[] aData, int aLength)
				//getTag
				case 4:
					autenticateDocument = readDocument("Indique o caminho do arquivo para ser apenas autenticado: ");
//					curupira1.makeKey(cipherKey, keyBits);
//					marvin.setKey(cipherKey, keyBits);
//					marvin.update(autenticateDocument.getBytes(), autenticateDocument.length());
					System.out.print(instructions);
					break;
				//Selecionar um arquivo com seu respectivo MAC para ser validado
				//Marvin - update(byte[] aData, int aLength)
				//Marvin - getTag(byte[] tag)
				case 5:
					System.out.print(instructions);
					break;
				//Selecionar um arquivo para ser cifrado e autenticado
				//LetterSoup - encrypt(byte[] mData, int mLength, byte[] cData)
				case 6:
					System.out.print(instructions);
					break;
				//Selecionar um arquivo cifrado com seus respectivos IV e MAC para ser validado e decifrado
				//Marvin - update(byte[] aData, int aLength)
				//Marvin - getTag(byte[] tag)
				//LetterSoup - decrypt(byte[] cData, int cLength, byte[] mData)
				case 7:
					System.out.print(instructions);
					break;
				//Selecionar um arquivo para ser cifrado e autenticado, e um arquivo correspondente de dados associados para ser autenticado
				//LetterSoup - encrypt(byte[] mData, int mLength, byte[] cData)
				//Marvin - init()
				case 8:
					System.out.print(instructions);
					break;
				//Selecionar um arquivo cifrado com seus respectivos IV e MAC para ser validado e decifrado, um arquivo correspondente de dados associados para ser autenticado
				//Marvin - update(byte[] aData, int aLength)
				//Marvin - getTag(byte[] tag)
				//LetterSoup - decrypt(byte[] cData, int cLength, byte[] mData)
				//Marvin - init()
				case 9:
					System.out.print(instructions);
					break;
				default:
					System.out.print("Valor inválido. " + instructions);
					break;
				}
			} catch (Exception e) {
				System.out.print("Valor inválido. " + instructions);
			}
		}
	}

	/**
	 * Função para "Selecionar um tamanho de chave dentre os valores admissíveis".
	 */
	private static void keySizeInput() {
		// Instructions string
		String instructions = "Por favor, digite um tamanho para as chaves (\"96\", \"144\" ou \"192\" - em bits): ";

		boolean validValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim();
				int intValue = new Integer(lineRead);

				// Validation
				if (intValue == 96 || intValue == 144 || intValue == 192) {

					// Value set
					keyBits = intValue;

					// Output
					System.out.println("Chave terá " + intValue + " bits.\n");

					validValue = true;
				} else {
					System.out.print("Valor inválido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor inválido. " + instructions);
			}
		}
	}

	/**
	 * Função para "Escolher uma senha alfanumérica (ASCII) de até 12, 18 ou 24
	 * caracteres (completada internamente pelo aplicativo com bytes nulos),
	 * conforme o tamanho escolhido da chave".
	 */
	private static void cipherKeyInput() {
		if(keyBits == 0){
			System.out.println("Você precisa escolher o tamanho da chave antes.\n");
		}
		else{
			int maxSize = keyBits / 8;
			cipherKey = new byte[maxSize];
			
			// Instructions string
			String instructions = "Por favor, digite uma senha de até " + maxSize
					+ " caracteres: ";
	
			boolean validValue = false;
	
			System.out.print(instructions);
			while (!validValue) {
				try {
					lineRead = reader.readLine().trim();
					String stringValue = lineRead;
					int stringSize = lineRead.length();
	
					// Validation
					if (0 <= stringSize && stringSize <= maxSize && !stringValue.equals("")) {
						byte[] stringBytes = stringValue.getBytes();
						
						//Fill the cipherKey with the inserted password
						for(int i = 0; i < stringSize; i++){
							cipherKey[i] = stringBytes[i];
							if (debug) {
								Printer.printVector(cipherKey);
							}
						}
						// Output
						System.out.println("Senha adicionada com sucesso");
						if (debug)
							Printer.printVector(cipherKey);
	
						validValue = true;
					} else {
						System.out.print("Valor inválido. " + instructions);
					}
				} catch (Exception e) {
					System.out.print("Valor inválido. " + instructions);
				}
			}
		}
	}
	
	/**
	 * Função para"Selecionar um tamanho de IV entre o mínimo de 64 bits e otamanho completo do bloco"
	 * .
	 */
	private static void ivSizeInput() {
		// Instructions string
		String instructions = "Por favor, digite um tamanho para IV (entre 64 e 96 - em bits): ";

		boolean validValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim();
				int intValue = new Integer(lineRead);

				// Validation
				if (64 <= intValue && intValue <= 96) {

					// Value set
					ivLength = intValue;

					// Output
					System.out.println("IV terá " + intValue + " bits.");

					validValue = true;
				} else {
					System.out.print("Valor inválido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor inválido. " + instructions);
			}
		}
	}

	/**
	 * Função para"Selecionar um tamanho de MAC entre o mínimo de 64 bits e o tamanho completo do bloco"
	 * .
	 */
	private static void aLengthInput() {
		// Instructions string
		String instructions = "Por favor, digite um tamanho para MAC (entre 64 e 96 - em bits): ";

		boolean validValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim();
				int intValue = new Integer(lineRead);

				// Validation
				if (64 <= intValue && intValue <= 96) {

					// Value set
					aLength = intValue;

					// Output
					System.out.println("IV terá " + intValue + " bits.");

					validValue = true;
				} else {
					System.out.print("Valor inválido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor inválido. " + instructions);
			}
		}
	}
	
	/**
	 * Read a text file.
	 */
	private static String readDocument(String instructions) {
		String[] readFile = new String[2];
		boolean validValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				readFile[0] = reader.readLine().trim();

				// Validation
				if (Util.readFile(readFile)) {
					// Output
					System.out.println("Arquivo \"" + readFile[0]	+ "\" lido com sucesso.");

					if (debug){
						System.out.println("Conteúdo do arquivo:");
						System.out.println(readFile[1]);
					}

					validValue = true;
				} else {
					System.out.print("O arquivo \"" + readFile[0]
							+ "\" não foi encontrado. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("O arquivo \"" + readFile[0]
						+ "\" não foi encontrado. " + instructions);
			}
		}
		return readFile[1];
	}
}
