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
				.println("Bem vindo ao Projeto TaRusso, de Eduardo Russo e Tarik Feres");
		System.out
				.println("************************************************************\n");

		mainMenu();
		
	}

	private static void mainMenu(){
		String instructions = "Por favor, escolha uma das opcoes abaixo:\n" +
				"[1] Selecionar um tamanho de chave dentre os valores admissiveis\n" +
				"[2] Selecionar um tamanho de IV e de MAC entre o mínimo de 64 bits e o tamanho completo do bloco\n" +
				"[3] Escolher uma senha alfanumérica (ASCII)\n" +
				"[4] Selecionar um arquivo para ser apenas autenticado\n" +
				"[5] Selecionar um arquivo com seu respectivo MAC para ser validado\n" +
				"[6] Selecionar um arquivo para ser cifrado e autenticado\n" +
				"[7] Selecionar um arquivo cifrado com seus respectivos IV e MAC para ser validado e decifrado\n" +
				"[8] Selecionar um arquivo para ser cifrado e autenticado, e um arquivo correspondente de dados associados para ser autenticado\n" +
				"[9] Selecionar um arquivo cifrado com seus respectivos IV e MAC para ser validado e decifrado, um arquivo correspondente de dados associados para ser autenticado\n" +
				"[0] Finalizar programa\n" +
				"Opcao: ";
		boolean validValue = false;
		int key;
		System.out.print(instructions);
		while (!validValue) {
			try {
				key = new Integer(reader.readLine().trim());
				switch (key) {
				//Selecionar um tamanho de chave dentre os valores admissiveis
				case 1:
					keySizeInput();
					System.out.print(instructions);
					break;
				//Selecionar um tamanho de IV e de MAC entre o mínimo de 64 bits e o tamanho completo do bloco"
				case 2:
					ivSizeInput();
					macLengthInput();
					System.out.print(instructions);
					break;
				//Escolher uma senha alfanumérica
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
						filePath[0] = filePath[0] + ".mac";
						
						saveDocument("Arquivo \"" + filePath[0] + "\" foi autenticado e salvo.\n", filePath[0], new String(buffer));
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
						
						String savedMac = Printer.getVectorAsPlainText(macDocument.getBytes());
						
						if(debug)
							System.out.println(Printer.getVectorAsPlainText(buffer) + " = " + savedMac + "?");
						
						if(Printer.getVectorAsPlainText(buffer).equals(savedMac))
							System.out.println("Qapla'! O arquivo foi validado.\n");
						else
							System.out.println("Autenticacao invalida: as tags nao sao iguais.\n");	
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
												
						System.out.println("Arquivo foi cifrado e autenticado.");
						
						String newFilePath = "";
						//Save .ciph file
						newFilePath = filePath[0] + ".ciph";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.", newFilePath, new String(cData));

						//Save .mac file
						newFilePath = filePath[0] + ".mac";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.", newFilePath, new String(buffer));
						
						//Save .iv file
						newFilePath = filePath[0] + ".iv";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.\n", newFilePath, new String(iv));
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
						
						byte[] savedIv = ivDocument.getBytes();
						byte[]cData = document.getBytes();
						String savedMac = Printer.getVectorAsPlainText(macDocument.getBytes());

						letterSoup.setIV(savedIv, savedIv.length);
						
						byte[] mData = letterSoup.decrypt(cData, cData.length, null);
						byte[] buffer = new byte[macLength / 8];
						buffer = letterSoup.getTag(new byte[macLength / 8], macLength);
						
						
						if(debug)
							System.out.println(Printer.getVectorAsPlainText(buffer) + " = " + savedMac + "?");
						
						if(Printer.getVectorAsPlainText(buffer).equals(savedMac)){
								System.out.println("Qapla'! O arquivo foi validado e decifrado.");
								//Save original file
								String newFilePath = filePath[0].split("\\.")[0] + "." + filePath[0].split("\\.")[1];
								
								saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.\n", newFilePath, new String(mData));
						}
						else
							System.out.println("Autenticacao invalida.\n");
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
										
						System.out.println("Arquivo foi cifrado e autenticado.");
						
						String newFilePath = "";
						//Save .ciph file
						newFilePath = filePath[0] + ".ciph";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.", newFilePath, new String(cData));

						//Save .mac file
						newFilePath = filePath[0] + ".mac";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.", newFilePath, new String(buffer));
						
						//Save .iv file
						newFilePath = filePath[0] + ".iv";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.\n", newFilePath, new String(iv));
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
						
						byte[] savedIv = ivDocument.getBytes();
						byte[]cData = document.getBytes();
						String savedMac = Printer.getVectorAsPlainText(macDocument.getBytes());
						
						letterSoup.setIV(savedIv, savedIv.length);
						
						byte[] mData = letterSoup.decrypt(cData, cData.length, null);
						
						letterSoup.update(assocDocument.getBytes(), assocDocument.getBytes().length);
						
						byte[] buffer = new byte[macLength / 8];
						buffer = letterSoup.getTag(new byte[macLength / 8], macLength);
						
						
						if(debug)
							System.out.println(Printer.getVectorAsPlainText(buffer) + " = " + savedMac + "?");
						
						if(Printer.getVectorAsPlainText(buffer).equals(savedMac)){
								System.out.println("Qapla'! O arquivo foi validado e decifrado.");
								//Save original file
								String newFilePath = filePath[0].split("\\.")[0] + "." + filePath[0].split("\\.")[1];
								saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.\n", newFilePath, new String(mData));
						}
						else
							System.out.println("Autenticacao invalida.\n");
					}
					System.out.print(instructions);
					break;
				case 0:
					validValue = true;
					System.out.println("[programa encerrado]");
					break;
				default:
					System.out.print("Valor invalido. " + instructions);
					break;
				}
			} catch (Exception e) {
				System.out.print("Valor invalido. " + instructions);
			}
		}
	}

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
					System.out.println("Chave tera " + intValue + " bits.\n");

					validValue = true;
				} else {
					System.out.print("Valor invalido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor invalido. " + instructions);
			}
		}
	}

	private static void cipherKeyInput() {
		
		if(variableAreFilled(true, false, false)){
			int maxSize = keyBits / 8;
			cipherKey = new byte[maxSize];
			
			// Instructions string
			String instructions = "Por favor, digite uma senha de ate " + maxSize
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
						if (debug)
							System.out.println("Senha adicionada com sucesso\n");
							Printer.printVector(cipherKey);
	
						validValue = true;
					} else {
						System.out.print("Valor invalido. " + instructions);
					}
				} catch (Exception e) {
					System.out.print("Valor invalido. " + instructions);
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

					if(debug)
						System.out.println("IV tera " + intValue + " bits.");

					validValue = true;
				} else {
					System.out.print("Valor invalido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor invalido. " + instructions);
			}
		}
	}

	/**
	 * Função para"Selecionar um tamanho de MAC entre o mínimo de 64 bits e o tamanho completo do bloco"
	 * .
	 */
	private static void macLengthInput() {
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

					if(debug)
						System.out.println("MAC tera " + intValue + " bits.\n");

					validValue = true;
				} else {
					System.out.print("Valor invalido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor invalido. " + instructions);
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
				
				// Validation
				text = Util.readFile(filePath[0]); 
				if (null != text) {
					// Output
					if (debug){
						System.out.println("Arquivo \"" + filePath[0]	+ "\" lido com sucesso.");
						System.out.println("Conteudo do arquivo: " + text);
					}

					validValue = true;
				} else {
					System.out.print("O arquivo \"" + filePath[0] + "\" nao foi encontrado. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("O arquivo \"" + filePath[0] + "\" nao foi encontrado. " + instructions);
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

					if (debug){
						System.out.println("Arquivo gravado com sucesso.");
						System.out.println("Conteudo do arquivo: " + text);
					}

					validValue = true;
				} else {
					System.out.print("Houve um erro na gravacao. Entrando em loop infinito :P");
				}
			} catch (Exception e) {
				System.out.print("Houve um erro na gravacao. Entrando em loop infinito :P");
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
				message += "\tVoce precisa escolher o tamanho da chave antes (opçãcao 1).\n";
		
		if(cipherKeyVariable)
			if(cipherKey == null)
				message += "\tVoce precisa definir uma senha antes (opçãcao 3).\n";

		if(aLengthOrIvVariable)
			if(macLength == 0)
				message += "\tVoce precisa escolher o tamanho de MAC e IV antes (opcação 2).\n";
		
		if (!message.isEmpty()){
			System.out.println("Os seguintes erros foram encontrados:\n" + message);
			return false;
		}
		return true;
	}
}
