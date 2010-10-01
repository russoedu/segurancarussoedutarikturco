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
	
	public static void main(String[] args) throws IOException {				
		System.out
				.println("****************************************************************");
		System.out
				.println("* Bem vindo ao Projeto TaRusso, de Eduardo Russo e Tarik Feres *");
		System.out
				.println("****************************************************************\n");

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
					macLengthInput();
					ivSizeInput();
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
						byte[] aData = readDocument("Indique o caminho do arquivo para ser apenas autenticado: ", filePath);
						marvin.setCipher(curupira1);
						marvin.setKey(cipherKey, keyBits);
						marvin.init();
						
						marvin.update(aData, aData.length);
						byte[] buffer = new byte[macLength / 8];
						buffer = marvin.getTag(buffer, macLength / 8);
						
						//Save .mac file
						filePath[0] = filePath[0] + ".mac";
						System.out.println("Arquivo foi autenticado.");
						saveDocument("Arquivo \"" + filePath[0] + "\" foi salvo.\n", filePath[0], buffer);
					}
					System.out.print(instructions);
					break;
				//Selecionar um arquivo com seu respectivo MAC para ser validado
				case 5:
					if(variableAreFilled(true, true, false)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						String[] filePath = new String[2];
						byte[] aData = readDocument("Indique o caminho do arquivo para ser validado: ", filePath);
						
						byte[] savedMac;
						
						if(useDefaultValues(true, false)){
							filePath[0] = filePath[0] + ".mac";
							savedMac = readDocument(filePath);
							if(null == savedMac){
								System.out.println("Arquivo \".mac\" n„o foi encontrado!");
								savedMac = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
							}
						}
						else{
							savedMac = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
						}
						
						marvin.setCipher(curupira1);
						marvin.setKey(cipherKey, keyBits);
						marvin.init();

						marvin.update(aData, aData.length);
						
						int bufferSize = savedMac.length;
						byte[] buffer = new byte[bufferSize];
						buffer = marvin.getTag(buffer, bufferSize * 8);
						
						if(debug){
							Printer.printVector("buffer   ", buffer);
							Printer.printVector("saved mac", savedMac);
							System.out.println(buffer);
							System.out.println(savedMac);
						}
						
						if(Printer.getVectorAsPlainText(buffer).equals(Printer.getVectorAsPlainText(savedMac)))
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
						byte[] mData = readDocument("Indique o caminho do arquivo para ser cifrado e autenticado: ", filePath);	
						
						letterSoup.setCipher(curupira1);
						marvin.setCipher(curupira1);
						letterSoup.setMAC(marvin);
						letterSoup.setKey(cipherKey, keyBits);
						
						SecureRandom rand = new SecureRandom();
						byte[] iv = new byte[ivLength / 8];
						rand.nextBytes(iv);

						letterSoup.setIV(iv, ivLength / 8);

						byte[] cData = letterSoup.encrypt(mData, mData.length, null);
						
						byte[] buffer = new byte[macLength / 8];
						buffer = letterSoup.getTag(new byte[macLength / 8], macLength);
												
						System.out.println("Arquivo foi cifrado e autenticado.");
						
						String newFilePath = "";
						//Save .ciph file
						newFilePath = filePath[0] + ".ciph";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.", newFilePath, cData);

						//Save .mac file
						newFilePath = filePath[0] + ".mac";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.", newFilePath, buffer);
						
						//Save .iv file
						newFilePath = filePath[0] + ".iv";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.\n", newFilePath, iv);
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
						byte[]cData = readDocument("Indique o caminho do arquivo \".ciph\" para ser decifrado: ", filePath);
						
						byte[] savedMac;
						byte[] savedIv;
						
						if(useDefaultValues(true, true)){
							filePath[0] = filePath[0].substring(0, filePath[0].length() - 5) + ".mac";
							savedMac = readDocument(filePath);
							if(null == savedMac){
								System.out.println("Arquivo \".mac\" n„o foi encontrado!");
								savedMac = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
							}
							
							filePath[0] = filePath[0].substring(0, filePath[0].length() - 4) + ".iv";
							savedIv = readDocument(filePath);
							if(null == savedIv){
								System.out.println("Arquivo \".iv\" n„o foi encontrado!");
								savedIv = readDocument("Indique o caminho do arquivo \".iv\": ", filePath);
							}
						}
						else{
							savedMac = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
							savedIv = readDocument("Indique o caminho do arquivo \".iv\": ", filePath);
						}
						
						letterSoup.setCipher(curupira1);
						marvin.setCipher(curupira1);
						letterSoup.setMAC(marvin);
						letterSoup.setKey(cipherKey, keyBits);

						letterSoup.setIV(savedIv, savedIv.length);
						
						byte[] mData = letterSoup.decrypt(cData, cData.length, null);
						
						int bufferSize = savedMac.length;
						byte[] buffer = new byte[bufferSize];
						buffer = letterSoup.getTag(new byte[bufferSize], bufferSize * 8);
						
						
						if(debug){
							Printer.printVector("buffer   ", buffer);
							Printer.printVector("saved mac", savedMac);
						}
						
						if(Printer.getVectorAsPlainText(buffer).equals(Printer.getVectorAsPlainText(savedMac))){
								System.out.println("Qapla'! O arquivo foi validado e decifrado.");
								//Save original file
								String newFilePath = filePath[0].split("\\.")[0] + "." + filePath[0].split("\\.")[1];
								
								saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.\n", newFilePath, mData);
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
						byte[] mData = readDocument("Indique o caminho do arquivo para ser cifrado e autenticado: ", filePath);	
						byte[] assocData = readDocument("Indique o caminho do arquivo de dados associados para ser autenticado: ", assocFilePath);	
						
						letterSoup.setCipher(curupira1);
						marvin.setCipher(curupira1);
						letterSoup.setMAC(marvin);
						letterSoup.setKey(cipherKey, keyBits);
						
						SecureRandom rand = new SecureRandom();
						byte[] iv = new byte[ivLength / 8];
						rand.nextBytes(iv);
						
						letterSoup.setIV(iv, ivLength / 8);

						byte[] cData = letterSoup.encrypt(mData, mData.length, null);
						
						letterSoup.update(assocData, assocData.length);
						
						byte[] buffer = new byte[macLength / 8];
						buffer = letterSoup.getTag(new byte[macLength / 8], macLength);
										
						System.out.println("Arquivo foi cifrado e autenticado.");
						
						String newFilePath = "";
						//Save .ciph file
						newFilePath = filePath[0] + ".ciph";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.", newFilePath, cData);

						//Save .mac file
						newFilePath = filePath[0] + ".mac";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.", newFilePath, buffer);
						
						//Save .iv file
						newFilePath = filePath[0] + ".iv";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.\n", newFilePath, iv);
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
						
						byte[]cData = readDocument("Indique o caminho do arquivo \".ciph\" para ser decifrado: ", filePath);

						
						byte[] savedMac;
						byte[] savedIv;
						
						if(useDefaultValues(true, true)){
							filePath[0] = filePath[0].substring(0, filePath[0].length() - 5) + ".mac";
							savedMac = readDocument(filePath);
							if(null == savedMac){
								System.out.println("Arquivo \".mac\" n„o foi encontrado!");
								savedMac = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
							}
							
							filePath[0] = filePath[0].substring(0, filePath[0].length() - 4) + ".iv";
							savedIv = readDocument(filePath);
							if(null == savedIv){
								System.out.println("Arquivo \".iv\" n„o foi encontrado!");
								savedIv = readDocument("Indique o caminho do arquivo \".iv\": ", filePath);
							}
						}
						else{
							savedMac = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
							savedIv = readDocument("Indique o caminho do arquivo \".iv\": ", filePath);
						}
						
						byte[] assocData = readDocument("Indique o caminho do arquivo de dados associados: ", assocFilePath);	
						
						letterSoup.setCipher(curupira1);
						marvin.setCipher(curupira1);
						letterSoup.setMAC(marvin);
						letterSoup.setKey(cipherKey, keyBits);
						
						letterSoup.setIV(savedIv, savedIv.length);
						
						byte[] mData = letterSoup.decrypt(cData, cData.length, null);
						
						letterSoup.update(assocData, assocData.length);
						
						int bufferSize = savedMac.length;
						byte[] buffer = new byte[bufferSize];
						buffer = letterSoup.getTag(new byte[bufferSize], bufferSize * 8);
						
						
						if(debug){
							Printer.printVector("buffer   ", buffer);
							Printer.printVector("saved mac", savedMac);
						}
						
						if(Printer.getVectorAsPlainText(buffer).equals(Printer.getVectorAsPlainText(savedMac))){
								System.out.println("Qapla'! O arquivo foi validado e decifrado.");
								//Save original file
								String newFilePath = filePath[0].split("\\.")[0] + "." + filePath[0].split("\\.")[1];
								saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.\n", newFilePath, mData);
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

					if(debug)
						System.out.println("Chave tera " + intValue + " bits.\n");

					System.out.println();
					
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
						if (debug){
							System.out.println("Senha adicionada com sucesso\n");
							Printer.printVector(cipherKey);
						}
						
						System.out.println();
	
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

					System.out.println();
					
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
	private static byte[] readDocument(String instructions, String[] filePath) {
		boolean validValue = false;
		System.out.print(instructions);
		while (!validValue) {
			try {
				filePath[0] = reader.readLine().trim();
				
				// Validation
				byte[] data = Util.readFile(filePath[0]); 
				if (null != data) {
					
					if (debug){
						System.out.println("Arquivo \"" + filePath[0]	+ "\" lido com sucesso.");
						System.out.println("Conteudo do arquivo: " + data);
					}
					
					return data;
				} else {
					System.out.print("O arquivo \"" + filePath[0] + "\" nao foi encontrado. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("O arquivo \"" + filePath[0] + "\" nao foi encontrado. " + instructions);
			}
		}
		return null;
	}
	
	/**
	 * Read a text file.
	 */
	private static byte[] readDocument(String[] filePath) {
		boolean validValue = false;
		while (!validValue) {
			try {				
				// Validation
				byte[] data = Util.readFile(filePath[0]); 
				if (null != data) {
					
					if (debug){
						System.out.println("Arquivo \"" + filePath[0]	+ "\" lido com sucesso.");
						System.out.println("Conteudo do arquivo: " + data);
					}
					
					return data;
				} else {
					return null;
				}
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Save a text file.
	 */
	private static void saveDocument(String message, String filePath, byte[] text) {
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
	
	/**
	 * Give the user an option to use de default file paths for .mac and .iv files
	 * @param mac if you want to show the option for .mac files
	 * @param iv if you want to show the option for .iv files
	 * @return True if user chose YES or false otherwise
	 */
	private static boolean useDefaultValues(boolean mac, boolean iv){
		String instructions = "Deseja usar os caminhos padroes para o(s) arquivo(s) ";
		if(mac)
			instructions += "\".mac\"";

		if(iv)
			instructions += "e \".iv\"";
		
		instructions += "? (s para sim, n para nao): ";

		boolean validValue = false;
		boolean returnValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim().toLowerCase();

				// Validation
				if (lineRead.equals("s")){
					returnValue = true;
					validValue = true;					
				}
				else if(lineRead.equals("n")){
					returnValue = false;
					validValue = true;										
				}	
				else{
					System.out.print("Valor invalido. " + instructions);
					validValue = false;										
				}
				
			} catch (Exception e) {
				System.out.print("Valor invalido. " + instructions);
			}
		}
		return returnValue;
	}
}
