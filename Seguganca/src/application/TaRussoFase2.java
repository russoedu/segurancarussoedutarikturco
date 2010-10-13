package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;

import util.Printer;
import util.Util;
import fase1.Curupira1;
import fase1.LetterSoup;
import fase1.Marvin;

public class TaRussoFase2 {
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
				.println("******************************************************************");
		System.out
				.println("* Bem vindo ao Projeto TaRusso 2, de Eduardo Russo e Tarik Feres *");
		System.out
				.println("******************************************************************\n");

		mainMenu();
		
	}
	
	/*
	 * Escolher os parâmetros de Keccak[r,c,d] dentre os valores admissíveis:
	 * 		r pertence a {8×1, 8×2, 8×3, . . . , 8×128}
	 * 		c = 1600 - r
	 * 		d pertence a {0,1,2,...,255}
	 * Os valores default são:
	 * 		r = 1024
	 * 		c = 576
	 * 		d = 0
	 * Selecionar um arquivo e calcular o seu resumo Keccak[r,c,d]
	 * Selecionar um arquivo contendo uma semente de entropia e 
	 * 		calcular um número especificado de bytes pseudo-aleatórios a partir dessa semente,
	 * 			usando o gerador pseudo-aleatório SpongePRNG
	 * 				instanciado com a função de hash Keccak e complementação pad10*1.
	 */

	private static void mainMenu(){
		String instructions = "Por favor, escolha uma das opcoes abaixo:\n" +
				"[1] Escolher os parâmetros de Keccak\n" +
				"[2] Selecionar um arquivo para calcular seu resumo Keccak\n" +
				"[3] Selecionar um arquivo com uma semente de entropia e calcular um número pseudo-aleatorio SpongePRNG\n" +
				"[0] Finalizar programa\n" +
				"[r | XXXXX] [c | XXXXX] [d | XXXX]\n" +
				"Opcao: ";
		boolean validValue = false;
		int key;
		System.out.print(instructions);
		while (!validValue) {
			try {
				key = new Integer(reader.readLine().trim());
				switch (key) {
				//Selecionar um tamanho de chave dentre os valores admissiveis e escolher uma senha alfanumeŽrica
				case 1:
					cipherKeyInput();
					System.out.print(instructions);
					break;
				//Selecionar um tamanho de IV e de MAC entre o minimo de 64 bits e o tamanho completo do bloco"
				case 2:
					macLengthInput();
					ivSizeInput();
					System.out.print(instructions);
					break;
				//Selecionar um arquivo para ser apenas autenticado
				case 3:
					if(variableAreFilled(true, true)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						
						String[] filePath = new String[2];
						byte[] aData = readDocument("Indique o caminho do arquivo para ser apenas autenticado: ", filePath);
						marvin.setCipher(curupira1);
						marvin.setKey(cipherKey, keyBits);
						marvin.init();
						
						marvin.update(aData, aData.length);
						byte[] buffer = new byte[macLength / 8];
						buffer = marvin.getTag(buffer, macLength);
						
						//Save .mac file
						filePath[0] = filePath[0] + ".mac";
						System.out.println("Arquivo foi autenticado.");
						saveDocument("Arquivo \"" + filePath[0] + "\" foi salvo.\n", filePath[0], buffer);
					}
					System.out.print(instructions);
					break;
				//Selecionar um arquivo com seu respectivo MAC para ser validado
				case 4:
					if(variableAreFilled(true, false)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						String[] filePath = new String[2];
						byte[] aData = readDocument("Indique o caminho do arquivo para ser validado: ", filePath);
						
						byte[] savedMac;
						
						if(useDefaultValues(true, false)){
							String defaultDocument = filePath[0] + ".mac";
							savedMac = readDocument(defaultDocument);
							if(null == savedMac){
								System.out.println("Arquivo \".mac\" não foi encontrado!");
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
						
						if(debug)
							System.out.println(bufferSize);
						
						byte[] buffer = new byte[bufferSize];
						buffer = marvin.getTag(buffer, bufferSize * 8);
						
						if(debug){
							Printer.printVector("buffer   ", buffer);
							Printer.printVector("saved mac", savedMac);
						}
						
						if(Printer.getVectorAsPlainText(buffer).equals(Printer.getVectorAsPlainText(savedMac)))
							System.out.println("Qapla'! O arquivo foi validado.\n");
						else
							System.out.println("Autenticacao invalida: as tags nao sao iguais.\n");	
					}
					System.out.print(instructions);
					break;
				//Selecionar um arquivo para ser cifrado e autenticado
				case 5:
					if(variableAreFilled(true, true)){
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
				case 6:
					if(variableAreFilled(true, false)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						LetterSoup letterSoup = new LetterSoup();
						
						String[] filePath = new String[2];
						byte[]cData = readDocument("Indique o caminho do arquivo \".ciph\" para ser decifrado: ", filePath);
						
						byte[] savedMac;
						byte[] savedIv;
						
						if(useDefaultValues(true, true)){
							String defaultDocument = filePath[0].substring(0, filePath[0].length() - 5) + ".mac";
							savedMac = readDocument(defaultDocument);
							if(null == savedMac){
								System.out.println("Arquivo \".mac\" não foi encontrado!");
								savedMac = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
							}
							
							defaultDocument = defaultDocument.substring(0, defaultDocument.length() - 4) + ".iv";
							savedIv = readDocument(defaultDocument);
							if(null == savedIv){
								System.out.println("Arquivo \".iv\" não foi encontrado!");
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
				case 7:
					if(variableAreFilled(true, true)){
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
				case 8:
					if(variableAreFilled(true, false)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						LetterSoup letterSoup = new LetterSoup();
						
						String[] filePath = new String[2];
						String[] assocFilePath = new String[2];
						
						byte[]cData = readDocument("Indique o caminho do arquivo \".ciph\" para ser decifrado: ", filePath);

						
						byte[] savedMac;
						byte[] savedIv;
						
						if(useDefaultValues(true, true)){
							String defaultDocument = filePath[0].substring(0, filePath[0].length() - 5) + ".mac";
							savedMac = readDocument(defaultDocument);
							if(null == savedMac){
								System.out.println("Arquivo \".mac\" não foi encontrado!");
								savedMac = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
							}
							
							defaultDocument = defaultDocument.substring(0, defaultDocument.length() - 4) + ".iv";
							savedIv = readDocument(defaultDocument);
							if(null == savedIv){
								System.out.println("Arquivo \".iv\" não foi encontrado!");
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

	private static void cipherKeyInput() {
		int maxSize = 192 / 8;
		int medSize = 144 / 8;
		int minSize = 96 / 8;
		
		boolean hexaPass = useHexadecimalPassword();
		
		String instructions;
		if(hexaPass){
			instructions = "Por favor, digite uma senha hexadecimal de ate " + maxSize * 2 + " caracteres: ";
		}
		else{
			instructions = "Por favor, digite uma senha em ASCII de ate " + maxSize + " caracteres: ";
		}

		// Instructions string

		boolean validValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim();
				String stringValue = lineRead;
				
				int stringSize;
				if(hexaPass){
					stringSize = lineRead.length() / 2;
				}
				else{
					stringSize = lineRead.length();
				}
				
				if(debug)
					System.out.println("Tamanho da chave: " + stringSize);

				// Validation
				if (0 <= stringSize && stringSize <= maxSize && !stringValue.equals("")) {
					//Create the key size dinamicly
					if(stringSize <= minSize){
						keyBits = minSize * 8;
						maxSize = minSize;
					}
					else if(stringSize <= medSize){
						keyBits = medSize * 8;
						maxSize = medSize;
					}
					else{
						keyBits = maxSize * 8;
					}
					cipherKey = new byte[maxSize];
					
					byte[] stringBytes;
					if(hexaPass){
						stringBytes = Util.convertStringToVector(stringValue);
						//Fill the cipherKey with the inserted password
						for(int i = 0; i < stringSize; i++){
							cipherKey[i] = stringBytes[i];
						if (debug)
							Printer.printVector(cipherKey);
						}	
					}						
					else{
						stringBytes = stringValue.getBytes();
						//Fill the cipherKey with the inserted password
						for(int i = 0; i < stringSize; i++){
							cipherKey[i] = stringBytes[i];
						if (debug)
							Printer.printVector(cipherKey);
						}
					}
					
					// Output
					System.out.println("Senha adicionada com sucesso. Chave de " + keyBits + " bits criada.\n");

					if (debug)
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
	
	/**
	 * Give the user an option to use an hexadecimal or ASCII password
	 * @return True if user chose hexadecimal, or false for ASCII
	 */
	private static boolean useHexadecimalPassword(){
		String instructions = "Como você deseja inserir a senha? ";
		String subInstructions = "Escolha (a)lfanumerica ou (h)exadecimal: ";
		
		
		boolean validValue = false;
		boolean returnValue = false;

		System.out.print(instructions + subInstructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim().toLowerCase();

				// Validation
				if (lineRead.equals("h")){
					returnValue = true;
					validValue = true;					
				}
				else if(lineRead.equals("a")){
					returnValue = false;
					validValue = true;										
				}	
				else{
					System.out.print("Valor invalido. " + subInstructions);										
				}
				
			} catch (Exception e) {
				System.out.print("Valor invalido. " + subInstructions);
			}
		}
		return returnValue;
	}
	
	/**
	 * Chose IV size
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
	 * Choose MAC size
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
						System.out.println("Conteudo do arquivo: " + Printer.getVectorAsPlainText(data));
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
	private static byte[] readDocument(String filePath) {
		boolean validValue = false;
		while (!validValue) {
			try {				
				// Validation
				byte[] data = Util.readFile(filePath); 
				if (null != data) {
					
					if (debug){
						System.out.println("Arquivo \"" + filePath	+ "\" lido com sucesso.");
						System.out.println("Conteudo do arquivo: " + Printer.getVectorAsPlainText(data));
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
	private static void saveDocument(String message, String filePath, byte[] data) {
		boolean validValue = false;
		
		System.out.println(message);
		while (!validValue) {
			try {
				if (Util.saveFile(filePath, data)) {
					// Output

					if (debug){
						System.out.println("Arquivo gravado com sucesso.");
						System.out.println("Conteudo do arquivo: " + Printer.getVectorAsPlainText(data));
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
	private static boolean variableAreFilled(boolean cipherKeyVariable, boolean aLengthOrIvVariable){
		String message = "";
		if(cipherKeyVariable)
			if(cipherKey == null)
				message += "\tVoce precisa definir uma senha antes (op‹cao 1).\n";

		if(aLengthOrIvVariable)
			if(macLength == 0)
				message += "\tVoce precisa escolher o tamanho de MAC e IV antes (opca‹o 2).\n";
		
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
			instructions += " e \".iv\"";
		
		instructions += "? Escolha (s)im ou (n)ao: ";

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
