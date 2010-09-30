package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;

import lettersoup.LetterSoup;
import marvin.Marvin;
import util.Printer;
import util.Util;
import curupira1.Curupira1;

public class TaRusso {
	private static boolean debug = false;
	
	private static InputStreamReader inputStreamReader = new InputStreamReader(System.in);
	private static BufferedReader reader = new BufferedReader(inputStreamReader);
	private static String lineRead;
	
	private static int keyBits = 0;
	private static int ivLength = 0;
	private static int macLength = 0;
	private static byte[] cipherKey = null;
	private static String document;
	private static String assocDocument;
	private static String macDocument;
	private static String ivDocument;
	
	public static void main(String[] args) throws IOException {				
		System.out
				.println("************************************************************");
		System.out
				.println("Bem vindo ao Projeto Tarusso, de Eduardo Russo e Tarik Feres");
		System.out
				.println("************************************************************\n");

		mainMenu();
		
	}

	private static void mainMenu(){
		String instructions = "Por favor, escolha uma das op��es abaixo:\n" +
				"[1] Selecionar um tamanho de chave dentre os valores admiss�veis\n" +
				"[2] Selecionar um tamanho de IV e de MAC entre o m�nimo de 64 bits e o tamanho completo do bloco\n" +
				"[3] Escolher uma senha alfanum�rica (ASCII)\n" +
				"[4] Selecionar um arquivo para ser apenas autenticado\n" +
				"[5] Selecionar um arquivo com seu respectivo MAC para ser validado\n" +
				"[6] Selecionar um arquivo para ser cifrado e autenticado\n" +
				"[7] Selecionar um arquivo cifrado com seus respectivos IV e MAC para ser validado e decifrado\n" +
				"[8] Selecionar um arquivo para ser cifrado e autenticado, e um arquivo correspondente de dados associados para ser autenticado\n" +
				"[9] Selecionar um arquivo cifrado com seus respectivos IV e MAC para ser validado e decifrado, um arquivo correspondente de dados associados para ser autenticado\n" +
				"[0] Fnalizar programa\n" +
				"Op��o: ";
		boolean validValue = false;
		int key;
		System.out.print(instructions);
		while (!validValue) {
			try {
				key = new Integer(reader.readLine().trim());
				switch (key) {
				//Selecionar um tamanho de chave dentre os valores admiss�veis
				case 1:
					keySizeInput();
					System.out.print(instructions);
					break;
				//Selecionar um tamanho de IV e de MAC entre o m�nimo de 64 bits e o tamanho completo do bloco"
				case 2:
					ivSizeInput();
					aLengthInput();
					System.out.print(instructions);
					break;
				//Escolher uma senha alfanum�rica
				case 3:
					cipherKeyInput();
					System.out.print(instructions);
					break;
				//Selecionar um arquivo para ser apenas autenticado
				case 4:
					if(variableAreFilled(true, true, true)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						
						String[] filePath = new String[2];
						document = readDocument("Indique o caminho do arquivo para ser apenas autenticado: ", filePath);
						marvin.setCipher(curupira1);
						marvin.setKey(cipherKey, keyBits);
						marvin.init();
						
						marvin.update(document.getBytes(), document.length());
						byte[] buffer = new byte[macLength / 8];
						buffer = marvin.getTag(buffer, macLength / 8);
						
						//Save .mac file
						filePath[0] = filePath[0].split("\\.")[0] + ".mac";
						filePath[1] = filePath[1].split("\\.")[0] + ".mac";
						
						saveDocument("Autentica��o executada com sucesso.\n" +
								"Arquivo \"" + filePath[1] + "\" salvo na mesma pasta do arquivo original.", filePath[0], new String(buffer));
					}
					System.out.print(instructions);
					break;
				//Selecionar um arquivo com seu respectivo MAC para ser validado
				case 5:
					if(variableAreFilled(true, true, true)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						String[] filePath = new String[2];
						document = readDocument("Indique o caminho do arquivo para ser validado: ", filePath);
						macDocument = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
						
						marvin.setCipher(curupira1);
						marvin.setKey(cipherKey, keyBits);
						marvin.init();

						marvin.update(document.getBytes(), document.length());
						byte[] buffer = new byte[macLength / 8];
						buffer = marvin.getTag(buffer, macLength / 8);
						
						if(debug)
							System.out.println(Printer.getVectorAsPlainText(buffer) + " = " + Printer.getVectorAsPlainText(macDocument.getBytes()) + "?");
						
						if(Printer.getVectorAsPlainText(buffer).equals(Printer.getVectorAsPlainText(macDocument.getBytes())))
							System.out.println("Autentica��o � v�lida: as tags s�o iguais.");
						else
							System.out.println("Autentica��o inv�lida.");	
					}
					System.out.print(instructions);
					break;
				//Selecionar um arquivo para ser cifrado e autenticado
				case 6:
					if(variableAreFilled(true, true, true)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						LetterSoup letterSoup = new LetterSoup();
						
						String[] filePath = new String[2];
						document = readDocument("Indique o caminho do arquivo para ser cifrado e autenticado: ", filePath);	
						
						letterSoup.setCipher(curupira1);
						marvin.setCipher(curupira1);
						letterSoup.setMAC(marvin);
						letterSoup.setKey(cipherKey, keyBits);
						
						SecureRandom rand = new SecureRandom();
						byte[] iv = new byte[ivLength / 8];
						rand.nextBytes(iv);
						
						letterSoup.setIV(iv, ivLength / 8);

						byte[] cData = letterSoup.encrypt(document.getBytes(), document.getBytes().length, null);
						byte[] buffer = new byte[macLength / 8];
						buffer = letterSoup.getTag(new byte[macLength / 8], macLength);
												
						//Save .ciph file
						filePath[0] = filePath[0].split("\\.")[0] + ".ciph";
						filePath[1] = filePath[1].split("\\.")[0] + ".ciph";
						saveDocument("Cifra��o executada com sucesso.\n" +
								"Arquivo \"" + filePath[1] + "\" salvo na mesma pasta do arquivo original.", filePath[0], new String(cData));

						//Save .mac file
						filePath[0] = filePath[0].split("\\.")[0] + ".mac";
						filePath[1] = filePath[1].split("\\.")[0] + ".mac";
						saveDocument("Autentica��o executada com sucesso.\n" +
								"Arquivo \"" + filePath[1] + "\" salvo na mesma pasta do arquivo original.", filePath[0], new String(buffer));
						
						//Save .iv file
						filePath[0] = filePath[0].split("\\.")[0] + ".iv";
						filePath[1] = filePath[1].split("\\.")[0] + ".iv";
						
						saveDocument("Vetor de inicializa��o salvo com sucesso.\n" +
								"Arquivo \"" + filePath[1] + "\" salvo na mesma pasta do arquivo original.", filePath[0], new String(iv));
				}
				System.out.print(instructions);
				break;
				//Selecionar um arquivo cifrado com seus respectivos IV e MAC para ser validado e decifrado
				case 7:
					if(variableAreFilled(true, true, false)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						LetterSoup letterSoup = new LetterSoup();
						
						String[] filePath = new String[2];
						document = readDocument("Indique o caminho do arquivo \".ciph\" para ser decifrado: ", filePath);
						macDocument = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
						ivDocument = readDocument("Indique o caminho do arquivo \".iv\": ", filePath);
						
						letterSoup.setCipher(curupira1);
						marvin.setCipher(curupira1);
						letterSoup.setMAC(marvin);
						letterSoup.setKey(cipherKey, keyBits);
						
						//Set the Letter Soup IV
						letterSoup.setIV(ivDocument.getBytes(), ivDocument.getBytes().length);
						
						byte[] mData = letterSoup.decrypt(document.getBytes(), document.getBytes().length, null);
						byte[] buffer = new byte[macLength / 8];
						buffer = letterSoup.getTag(new byte[macLength / 8], macLength);
						
						
						if(debug)
							System.out.println(Printer.getVectorAsPlainText(buffer) + " = " + Printer.getVectorAsPlainText(macDocument.getBytes()) + "?");
						
						if(Printer.getVectorAsPlainText(buffer).equals(Printer.getVectorAsPlainText(macDocument.getBytes()))){
								System.out.println("Autentica��o � v�lida: as tags s�o iguais.");
								//Save .deciph file
								filePath[0] = filePath[0].split("\\.")[0] + ".deciph";
								filePath[1] = filePath[1].split("\\.")[0] + ".deciph";
								
								saveDocument("Decifra��o executada com sucesso.\n" +
										"Arquivo \"" + filePath[1] + "\" salvo na mesma pasta do arquivo original.", filePath[0], new String(mData));
						}
						else
							System.out.println("Autentica��o inv�lida.");
					}
					System.out.print(instructions);
					break;
				//Selecionar um arquivo para ser cifrado e autenticado, e um arquivo correspondente de dados associados para ser autenticado
				case 8:
					if(variableAreFilled(true, true, true)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						LetterSoup letterSoup = new LetterSoup();
						
						String[] filePath = new String[2];
						String[] assocFilePath = new String[2];
						document = readDocument("Indique o caminho do arquivo para ser cifrado e autenticado: ", filePath);	
						assocDocument = readDocument("Indique o caminho do arquivo de dados associados para ser autenticado: ", assocFilePath);	
						
						letterSoup.setCipher(curupira1);
						marvin.setCipher(curupira1);
						letterSoup.setMAC(marvin);
						letterSoup.setKey(cipherKey, keyBits);
						
						SecureRandom rand = new SecureRandom();
						byte[] iv = new byte[ivLength / 8];
						rand.nextBytes(iv);
						
						letterSoup.setIV(iv, ivLength / 8);

						byte[] cData = letterSoup.encrypt(document.getBytes(), document.getBytes().length, null);
						
						letterSoup.update(assocDocument.getBytes(), assocDocument.getBytes().length);
						
						byte[] buffer = new byte[macLength / 8];
						buffer = letterSoup.getTag(new byte[macLength / 8], macLength);
												
						//Save .ciph file
						filePath[0] = filePath[0].split("\\.")[0] + ".ciph";
						filePath[1] = filePath[1].split("\\.")[0] + ".ciph";
						saveDocument("Cifra��o executada com sucesso.\n" +
								"Arquivo \"" + filePath[1] + "\" salvo na mesma pasta do arquivo original.", filePath[0], new String(cData));

						//Save .mac file
						filePath[0] = filePath[0].split("\\.")[0] + ".mac";
						filePath[1] = filePath[1].split("\\.")[0] + ".mac";
						saveDocument("Autentica��o executada com sucesso.\n" +
								"Arquivo \"" + filePath[1] + "\" salvo na mesma pasta do arquivo original.", filePath[0], new String(buffer));
						
						//Save .iv file
						filePath[0] = filePath[0].split("\\.")[0] + ".iv";
						filePath[1] = filePath[1].split("\\.")[0] + ".iv";
						
						saveDocument("Vetor de inicializa��o salvo com sucesso.\n" +
								"Arquivo \"" + filePath[1] + "\" salvo na mesma pasta do arquivo original.", filePath[0], new String(iv));
				}
					System.out.print(instructions);
					break;
				//Selecionar um arquivo cifrado com seus respectivos IV e MAC para ser validado e decifrado, um arquivo correspondente de dados associados para ser autenticado
				case 9:
					if(variableAreFilled(true, true, false)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						LetterSoup letterSoup = new LetterSoup();
						
						String[] filePath = new String[2];
						String[] assocFilePath = new String[2];
						
						document = readDocument("Indique o caminho do arquivo \".ciph\" para ser decifrado: ", filePath);
						assocDocument = readDocument("Indique o caminho do arquivo de dados associados: ", assocFilePath);	
						macDocument = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
						ivDocument = readDocument("Indique o caminho do arquivo \".iv\": ", filePath);
						
						letterSoup.setCipher(curupira1);
						marvin.setCipher(curupira1);
						letterSoup.setMAC(marvin);
						letterSoup.setKey(cipherKey, keyBits);
						
						//Set the Letter Soup IV
						letterSoup.setIV(ivDocument.getBytes(), ivDocument.getBytes().length);
						
						byte[] mData = letterSoup.decrypt(document.getBytes(), document.getBytes().length, null);
						
						letterSoup.update(assocDocument.getBytes(), assocDocument.getBytes().length);
						
						byte[] buffer = new byte[macLength / 8];
						buffer = letterSoup.getTag(new byte[macLength / 8], macLength);
						
						
						if(debug)
							System.out.println(Printer.getVectorAsPlainText(buffer) + " = " + Printer.getVectorAsPlainText(macDocument.getBytes()) + "?");
						
						if(Printer.getVectorAsPlainText(buffer).equals(Printer.getVectorAsPlainText(macDocument.getBytes()))){
								System.out.println("Autentica��o � v�lida: as tags s�o iguais.");
								//Save .deciph file
								filePath[0] = filePath[0].split("\\.")[0] + ".deciph";
								filePath[1] = filePath[1].split("\\.")[0] + ".deciph";
								
								saveDocument("Decifra��o executada com sucesso.\n" +
										"Arquivo \"" + filePath[1] + "\" salvo na mesma pasta do arquivo original.", filePath[0], new String(mData));
						}
						else
							System.out.println("Autentica��o inv�lida.");
					}
					System.out.print(instructions);
					break;
				case 0:
					validValue = true;
					System.out.println("[programa encerrado]");
					break;
				default:
					System.out.print("Valor inv�lido. " + instructions);
					break;
				}
			} catch (Exception e) {
				System.out.print("Valor inv�lido. " + instructions);
			}
		}
	}

	/**
	 * Fun��o para "Selecionar um tamanho de chave dentre os valores admiss�veis".
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
					System.out.println("Chave ter� " + intValue + " bits.\n");

					validValue = true;
				} else {
					System.out.print("Valor inv�lido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor inv�lido. " + instructions);
			}
		}
	}

	/**
	 * Fun��o para "Escolher uma senha alfanum�rica (ASCII) de at� 12, 18 ou 24
	 * caracteres (completada internamente pelo aplicativo com bytes nulos),
	 * conforme o tamanho escolhido da chave".
	 */
	private static void cipherKeyInput() {
		
		if(variableAreFilled(true, false, false)){
			int maxSize = keyBits / 8;
			cipherKey = new byte[maxSize];
			
			// Instructions string
			String instructions = "Por favor, digite uma senha de at� " + maxSize
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
						System.out.print("Valor inv�lido. " + instructions);
					}
				} catch (Exception e) {
					System.out.print("Valor inv�lido. " + instructions);
				}
			}
		}
	}
	
	/**
	 * Fun��o para"Selecionar um tamanho de IV entre o m�nimo de 64 bits e otamanho completo do bloco"
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
					System.out.println("IV ter� " + intValue + " bits.");

					validValue = true;
				} else {
					System.out.print("Valor inv�lido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor inv�lido. " + instructions);
			}
		}
	}

	/**
	 * Fun��o para"Selecionar um tamanho de MAC entre o m�nimo de 64 bits e o tamanho completo do bloco"
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
					macLength = intValue;

					// Output
					System.out.println("IV ter� " + intValue + " bits.");

					validValue = true;
				} else {
					System.out.print("Valor inv�lido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor inv�lido. " + instructions);
			}
		}
	}
	
	/**
	 * Read a text file.
	 */
	private static String readDocument(String instructions, String[] filePath) {
		boolean validValue = false;
		String text = "";
		
		System.out.print(instructions);
		while (!validValue) {
			try {
				filePath[0] = reader.readLine().trim();
				
				//Get the fileName without it's path
				//Unix
				if(filePath[0].contains("/")){
					filePath[1] = filePath[0].split("/")[filePath[0].split("/").length - 1];
				}
				//Windows
				else if(filePath[0].contains("\\")){
					filePath[1] = filePath[0].split("\\")[filePath[0].split("/").length - 1];		
				}
				
				// Validation
				text = Util.readFile(filePath[0]); 
				if (null != text) {
					// Output
					System.out.println("Arquivo \"" + filePath[1]	+ "\" lido com sucesso.");
					if (debug){
						System.out.println("Conte�do do arquivo: " + text);
					}

					validValue = true;
				} else {
					System.out.print("O arquivo \"" + filePath[1]
							+ "\" n�o foi encontrado. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("O arquivo \"" + filePath[1]
						+ "\" n�o foi encontrado. " + instructions);
			}
		}
		return text;
	}
	
	/**
	 * Save a text file.
	 */
	private static void saveDocument(String message, String filePath, String text) {
		boolean validValue = false;
		
		System.out.println(message);
		while (!validValue) {
			try {
				if (Util.saveFile(filePath, text)) {
					// Output
					System.out.println("Arquivo gravado com sucesso.");

					if (debug){
						System.out.println("Conte�do do arquivo: " + text);
					}

					validValue = true;
				} else {
					System.out.print("Houve um erro na grava��o. Entrando em loop infinito :P");
				}
			} catch (Exception e) {
				System.out.print("Houve um erro na grava��o. Entrando em loop infinito :P");
			}
		}
	}
	
	
	/**
	 * Check if the variables are already filled
	 * @param keyBitsVariable
	 * @param cipherKeyVariable
	 * @param aLengthOrIvVariable
	 * @return
	 */
	private static boolean variableAreFilled(boolean keyBitsVariable, boolean cipherKeyVariable, boolean aLengthOrIvVariable){
		String message = "";
		if(keyBitsVariable)
			if(keyBits == 0)
				message += "\tVoc� precisa escolher o tamanho da chave antes (op��o 1).\n";
		
		if(cipherKeyVariable)
			if(cipherKey == null)
				message += "\tVoc� precisa definir uma senha antes (op��o 3).\n";

		if(aLengthOrIvVariable)
			if(macLength == 0)
				message += "\tVoc� precisa escolher o tamanho de MAC e IV antes (op��o 2).\n";
		
		if (!message.isEmpty()){
			System.out.println("Os seguintes erros foram encontrados:\n" + message);
			return false;
		}
		return true;
	}
}