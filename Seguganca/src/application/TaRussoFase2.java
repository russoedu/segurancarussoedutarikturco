package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import util.Printer;
import util.Util;
import fase2.Keccak;
import fase2.SpongePRNG;

public class TaRussoFase2 {
	private static boolean debug = false;
	
	private static InputStreamReader inputStreamReader = new InputStreamReader(System.in);
	private static BufferedReader reader = new BufferedReader(inputStreamReader);
	private static String lineRead;
	
	private static int bitrate = 1024;
	private static int diversifier = 0;
	private static int randomNumberLength = 0;
	private static Keccak k = new Keccak();
	private static SpongePRNG spongePRNG = new SpongePRNG(k);
	
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
	 * Escolher os parametros de Keccak[r,c,d] dentre os valores admissiveis:
	 * 		r pertence a {64x1, 64x2, 64x3, . . . , 64x24}
	 * 		c = 1600 - r
	 * 		d pertence a {0,1,2,...,255}
	 * Os valores default sao:
	 * 		r = 1024
	 * 		c = 576
	 * 		d = 0
	 * Selecionar um arquivo e calcular o seu resumo Keccak[r,c,d]
	 * Selecionar um arquivo contendo uma semente de entropia e 
	 * 		calcular um numero especificado de bytes pseudo-aleatorios a partir dessa semente,
	 * 			usando o gerador pseudo-aleatorio SpongePRNG
	 * 				instanciado com a funcao de hash Keccak e complementacao pad10*1.
	 */

	private static void mainMenu(){
		k.setBitRate(bitrate);
		k.setDiversifier(diversifier);
		
		boolean validValue = false;
		int key;
		printInstructions();
		while (!validValue) {
			try {
				key = new Integer(reader.readLine().trim());
				switch (key) {
				//Escolher os parametros de Keccak
				case 1:
					bitrateInput();
					diversifierInput();
					printInstructions();
					break;
				//Selecionar um arquivo para calcular seu resumo Keccak
				case 2:

					byte[] mDataaa = readDocument("Indique o caminho do arquivo para ter seu resumo keccak gerado: ");
					
					k.init(bitrate);
					k.update(mDataaa, mDataaa.length);
					byte[] hash = k.getHash(new byte[0]);
					
					System.out.println("Resumo gerado:");
					Printer.printVector(hash);
					printInstructions();
					System.out.println("");
					break;
				//Selecionar um arquivo com uma semente de entropia e calcular um numero pseudo-aleatorio SpongePRNG
				case 3:
					
					byte[] seed = readDocument("Indique o caminho do arquivo contendo a semente de entropia: ");
					randomNumberLengthInput();
					
					spongePRNG.init(randomNumberLength);
					spongePRNG.feed(seed, seed.length);
					
					byte[] random = spongePRNG.fetch(new byte[randomNumberLength], randomNumberLength);
					
					System.out.println("Numero pseudo-aleatorio gerado:");
					Printer.printVector(random);
					printInstructions();
					
					break;
				case 0:
					validValue = true;
					System.out.println("[programa encerrado]");
					break;
				default:
					System.out.print("Valor invalido. ");
					printInstructions();
					break;
				}
			} catch (Exception e) {
				System.out.print("Valor invalido. ");
				printInstructions();
			}
		}
	}
	
	private static void printInstructions()
	{
		String instructions = "Por favor, escolha uma das opcoes abaixo:\n" +
		"[1] Escolher os parametros de Keccak (Default: r = 1024, c = 576 e d = 0)\n" +
		"[2] Selecionar um arquivo para calcular seu resumo Keccak\n" +
		"[3] Selecionar um arquivo com uma semente de entropia e calcular um numero pseudo-aleatorio SpongePRNG\n" +
		"[0] Finalizar programa\n" +
		"[r | " + bitrate + "] [c | " + (1600 - bitrate) + "] [d | " + diversifier + "]\n" +
		"Opcao: ";
		System.out.print(instructions);
		
	}

	/**
	 * Read a text file.
	 */
	private static byte[] readDocument(String instructions) {
		boolean validValue = false;
		System.out.print(instructions);
		String file = "";
		while (!validValue) {
			try {
				file = reader.readLine().trim();
				
				// Validation
				byte[] data = Util.readFile(file); 
				if (null != data) {
					
					if (debug){
						System.out.println("Arquivo \"" + file	+ "\" lido com sucesso.");
						System.out.println("Conteudo do arquivo: " + Printer.getVectorAsPlainText(data));
					}
					
					return data;
				} else {
					System.out.print("O arquivo \"" + file + "\" nao foi encontrado. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("O arquivo \"" + file + "\" nao foi encontrado. " + instructions);
			}
		}
		return null;
	}
	
	/**
	 * Choose Bitrate
	 */
	private static void bitrateInput() {
		// Instructions string
		String instructions = "Por favor, digite um tamanho para Bitrate (Multiplo de 64, entre 64 e 1536 - em bits): ";

		boolean validValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim();
				int intValue = new Integer(lineRead);

				// Validation
				if (64 <= intValue && intValue <= 1536 && intValue % 64 == 0) {

					// Value set
					bitrate = intValue;

					System.out.println("Bitrate tera " + intValue + " bits.\n");
					System.out.println("Capacity tera " + (1600 - intValue) + " bits.\n");

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
	 * Choose Diversifier
	 */
	private static void diversifierInput() {
		// Instructions string
		String instructions = "Por favor, digite um tamanho para Diversifier (Entre 0 e 255 - em bits): ";

		boolean validValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim();
				int intValue = new Integer(lineRead);

				// Validation
				if (0 <= intValue && intValue <= 255) {

					// Value set
					diversifier = intValue;

					System.out.println("Diversifier tera " + intValue + " bits.\n");

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
	 * Choose Random Number length
	 */
	private static void randomNumberLengthInput() {
		// Instructions string
		String instructions = "Por favor, digite quantos bytes serao gerados (Maior que 0): ";

		boolean validValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim();
				int intValue = new Integer(lineRead);

				// Validation
				if (0 < intValue) {

					// Value set
					randomNumberLength = intValue;

					System.out.println("Serao gerados " + intValue + " bytes.\n");

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
