package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TaRusso {
	private static InputStreamReader inputStreamReader = new InputStreamReader(System.in);
	private static BufferedReader reader = new BufferedReader(inputStreamReader);
	private static String lineRead;
	private static int keyBits;
	private static int ivLength;
	private static int aLength;
	
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
					System.out.println("Chave ter‡ " + intValue + " bits.\n");
					
					validValue = true;
				}
				else{
					System.out.print("Valor inv‡lido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor inv‡lido. " + instructions);
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
					System.out.println("IV ter‡ " + intValue + " bits.\n");
					
					validValue = true;
				}
				else{
					System.out.print("Valor inv‡lido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor inv‡lido. " + instructions);
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
					System.out.println("IV ter‡ " + intValue + " bits.\n");
					
					validValue = true;
				}
				else{
					System.out.print("Valor inv‡lido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor inv‡lido. " + instructions);
			}
		}
	}
}
