package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import util.Printer;
import util.Util;

public class TaRusso {
	private static InputStreamReader inputStreamReader = new InputStreamReader(System.in);
	private static BufferedReader reader = new BufferedReader(inputStreamReader);
	private static String lineRead;

	private static boolean debug = false;
	
	private static int keyBits;
	private static int ivLength;
	private static int aLength;
	private static byte[] cipherKey;
	private static String autenticateDocument;
	
	
	public static void main(String[] args) throws IOException {		
		System.out.println("************************************************************");
		System.out.println("Bem vindo ao Projeto TaRusso, de Eduardo Russo e Tarik Feres");
		System.out.println("************************************************************\n");
		
		
		//Key Size input
		keySizeInput();

		//IV input
		ivSizeInput();
		
		//MAC input
		aLengthInput();

		//Cipher Key input
		cipherKeyInput();
		
		//Autenticate Document
		autenticateDocumentInput();
	}
	private static void keySizeInput(){
		//Instructions string
		String instructions = "Por favor, digite um tamanho para as chaves (\"96\", \"144\" ou \"192\" - em bits): ";
		
		boolean validValue = false;
		
		System.out.print(instructions);
		while(!validValue){
			try {
				lineRead = reader.readLine().trim();
				int intValue = new Integer(lineRead);
				
				//Validation
				if(intValue == 96 || intValue == 144 || intValue == 192){
					
					//Value set
					keyBits = intValue;
					
					//Output
					System.out.println("Chave terá " + intValue + " bits.\n");
					
					validValue = true;
				}
				else{
					System.out.print("Valor inválido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor inválido. " + instructions);
			}
		}
	}
	
	private static void ivSizeInput(){
		//Instructions string
		String instructions = "Por favor, digite um tamanho para IV (entre 64 e 96 - em bits): ";
		
		boolean validValue = false;
		
		System.out.print(instructions);
		while(!validValue){
			try {
				lineRead = reader.readLine().trim();
				int intValue = new Integer(lineRead);
				
				//Validation
				if(64 <= intValue && intValue <= 96){
					
					//Value set
					ivLength = intValue;
					
					//Output
					System.out.println("IV terá " + intValue + " bits.");
					
					validValue = true;
				}
				else{
					System.out.print("Valor inválido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor inválido. " + instructions);
			}
		}
	}
	
	private static void aLengthInput(){
		//Instructions string
		String instructions = "Por favor, digite um tamanho para MAC (entre 64 e 96 - em bits): ";
		
		boolean validValue = false;
		
		System.out.print(instructions);
		while(!validValue){
			try {
				lineRead = reader.readLine().trim();
				int intValue = new Integer(lineRead);
				
				//Validation
				if(64 <= intValue && intValue <= 96){
					
					//Value set
					aLength = intValue;
					
					//Output
					System.out.println("IV terá " + intValue + " bits.");
					
					validValue = true;
				}
				else{
					System.out.print("Valor inválido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor inválido. " + instructions);
			}
		}
	}
	
	private static void cipherKeyInput(){
		int maxSize = keyBits / 8;
		
		//Instructions string
		String instructions = "Por favor, digite uma senha de até " + maxSize + " caracteres: ";
		
		boolean validValue = false;
		
		System.out.print(instructions);
		while(!validValue){
			try {
				lineRead = reader.readLine().trim();
				String stringValue = lineRead;
				int stringSize = lineRead.length();
				
				//Validation
				if(0 <= stringSize && stringSize <= maxSize && !stringValue.equals("")){
					for(; stringSize < maxSize; stringSize++){
						stringValue = stringValue.concat("0");
						if(debug){
							System.out.print(stringValue);
							if (stringSize < maxSize - 1)
								System.out.print( " -> ");
						}
					}
					if(debug)
						System.out.print("\n");
					
					//Value set
					cipherKey = stringValue.getBytes();
					
					//Output
					System.out.println("Senha adicionada com sucesso");
					if(debug)
						Printer.printVector(cipherKey);
					
					validValue = true;
				}
				else{
					System.out.print("Valor inválido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor inválido. " + instructions);
			}
		}
	}
	
	private static void autenticateDocumentInput(){
		//Instructions string
		String instructions = "Por favor, insira a localização do arquivo a ser autenticado: ";
		
		String[] readFile = new String[2];
		boolean validValue = false;
		
		System.out.print(instructions);
		while(!validValue){
			try {
				readFile[0] = reader.readLine().trim();
//				int intValue = new Integer(lineRead);
				
				;
				
				//Validation
				if(Util.readFile(readFile)){
					
					//Value set
//					aLength = intValue;
					
					//Output
					System.out.print("Arquivo \"" + readFile[0] + "\" lido com sucesso.");
					
					autenticateDocument = readFile[1];
					
					if(debug)
						System.out.println(readFile[1]);
					
					validValue = true;
				}
				else{
					System.out.print("O arquivo \"" + readFile[0] + "\" não foi encontrado. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("O arquivo \"" + readFile[0] + "\" não foi encontrado. " + instructions);
			}
		}
	}
}
