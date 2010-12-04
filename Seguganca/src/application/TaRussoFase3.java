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
        private static final boolean debug = false;
        
        // CONSTANTS
        private static final int KEY_SIZE = 96;
        private static final int HASH_BITRATE = 576;
        private static final int HASH_DIVERSIFIER = 64;
        
        private static final String KEY_FILES_NAME = "public_key";
        
        // GLOBAL VARIABLES
        private static String password;
        private static String keyFilesPath = "/Keys/";
        
    private static BigInteger q; 
        private static BigInteger p;
        private static BigInteger g;
        private static BigInteger g1;
        private static BigInteger g2;
        
        private static HashFunction hash;
        private static SpongeRandom sr;

        public static void main(String[] args) throws IOException 
        {
                System.out
                                .println("************************************************************************************************************************");
                System.out
                                .println("                                      CIFRADOR HIBRIDO COMPLETO - TERCEIRA FASE");
                System.out
                                .println("                                              EDUARDO RUSSO | TARIK SAVIETTO FERES");
                System.out
                                .println("************************************************************************************************************************\n");
                
                init();
                mainMenu();
        }

        /************************************************************************************************************/
        /********************************************** MAIN PROGRAM ************************************************/
        /************************************************************************************************************/
        
        private static void mainMenu() {
                String instructions = "\nMAIN MENU:\n"
                                + "[1] Select path [default: /]\n"
                                + "[2] Select password (ASCII)\n"
                                + "[3] Select a file to be encrypted\n"
                                + "[4] Select a file to be decrypted\n"
                                + "[5] Select a file to be signed\n"
                                + "[6] Select a file and its digital signature to verify\n"
                                + "[0] Quit program\n" + "OPTION: ";
                boolean exitFlag = false;
                int key;
                System.out.print(instructions);
                while (!exitFlag) {
                        try {
                                key = new Integer(reader.readLine().trim());
                                switch (key) {
                                case 1: {
                                        pathInput();
                                        
                                        System.out.print(instructions);
                                        break;
                                }
                                
                                // Select Password (ASCII)
                                case 2: {
                                        passwordInput();                                
                                        
                                        KeyEncapsulation  asymCipher = new CramerShoup();
                                        asymCipher.setup(p, q, g1, g2, hash, sr);
                                        BigInteger[] asymPublicKey = asymCipher.makeKeyPair(password);
                                        
                                        // Save .csc file. 
                                        String path = keyFilesPath + KEY_FILES_NAME + ".csc";
                                        saveDocument(
                                                                        "CSC file \""
                                                                        + path
                                                                        + "\" was generated and saved successfully.",
                                                                        path, Conversion.bigIntegerToByteArray(asymPublicKey[0]));
                                        
                                        // Save .csd file. 
                                        path = keyFilesPath + KEY_FILES_NAME + ".csd";
                                        saveDocument(
                                                                        "CSD file \""
                                                                        + path
                                                                        + "\" was generated and saved successfully.",
                                                                        path, Conversion.bigIntegerToByteArray(asymPublicKey[1]));
                                        
                                        // Save .csh file. 
                                        path = keyFilesPath + KEY_FILES_NAME + ".csh";
                                        saveDocument(
                                                                        "CSH file \""
                                                                        + path
                                                                        + "\" was generated and saved successfully.",
                                                                        path, Conversion.bigIntegerToByteArray(asymPublicKey[2]));
                                        
                                        DigitalSignature digSign = new Schnorr();
                                        digSign.setup(p, q, g, hash, sr);
                                        
                                        BigInteger signPublicKey = digSign.makeKeyPair(password);
                                        
                                        // Save .sy file. 
                                        path = keyFilesPath + KEY_FILES_NAME + ".sy";
                                        saveDocument(
                                                                        "SY file \""
                                                                        + path
                                                                        + "\" was generated and saved successfully.",
                                                                        path, Conversion.bigIntegerToByteArray(signPublicKey));
                                        
                                        System.out.print(instructions);
                                        break;
                                }
                                // Select a file to encrypt
                                case 3: {
                                        byte[] cipherKey = Random.getRandomNumber(KEY_SIZE/8);
                                        
                                        // Encrypts a file with the provided key. 
                                        String filePath = encrypt(cipherKey);
                                        
                                        // Populates the public key buffer for Cramer-Shoup
                                        BigInteger[] csPublicKey = new BigInteger[3];
                                        
                                        // Gets c
                                        String path = keyFilesPath + KEY_FILES_NAME + ".csc";
                                        byte[] c = readDocument(path);
                                        
                                        if (c == null) {
                                                System.out.println("The .csc file was not found. Aborting process.");
                                                break;
                                        }
                                                
                                        csPublicKey[0] = Conversion.byteArrayToBigInteger(c);
                                        
                                        // Gets d
                                        path = keyFilesPath + KEY_FILES_NAME + ".csd";
                                        byte[] d = readDocument(path);
                                        
                                        if (d == null) {
                                                System.out.println("The .csd file was not found. Aborting process.");
                                                break;
                                        }
                                        
                                        csPublicKey[1] = Conversion.byteArrayToBigInteger(d);
                                        
                                        // Gets h
                                        path = keyFilesPath + KEY_FILES_NAME + ".csh";
                                        byte[] h = readDocument(path);
                                        
                                        if (h == null) {
                                                System.out.println("The .csh file was not found. Aborting process.");
                                                break;
                                        }
                                        
                                        csPublicKey[2] = Conversion.byteArrayToBigInteger(h);
                                        
                                        // Prepares a CramerShoup instance.
                                        CramerShoup asymCipher = new CramerShoup();
                                        asymCipher.setup(p, q, g1, g2, hash, sr);
                                        
                                        // Encrypt the symmetric key used for the message encryption.
                                        BigInteger[] csCryptogram = asymCipher.encrypt(csPublicKey, cipherKey);
                                        
                                        // Save .csu1 file. 
                                        path = filePath + ".csu1";
                                        saveDocument(
                                                                        "CSU1 file \""
                                                                        + path
                                                                        + "\" was generated and saved successfully.",
                                                                        path, Conversion.bigIntegerToByteArray(csCryptogram[0]));
                                        
                                        // Save .csu2 file. 
                                        path = filePath + ".csu2";
                                        saveDocument(
                                                                        "CSU2 file \""
                                                                        + path
                                                                        + "\" was generated and saved successfully.",
                                                                        path, Conversion.bigIntegerToByteArray(csCryptogram[1]));
                                        
                                        // Save .cse file. 
                                        path = filePath + ".cse";
                                        saveDocument(
                                                                        "CSE file \""
                                                                        + path
                                                                        + "\" was generated and saved successfully.",
                                                                        path, Conversion.bigIntegerToByteArray(csCryptogram[2]));
                                        
                                        // Save .csv file. 
                                        path = filePath + ".csv";
                                        saveDocument(
                                                                        "CSV file \""
                                                                        + path
                                                                        + "\" was generated and saved successfully.",
                                                                        path, Conversion.bigIntegerToByteArray(csCryptogram[3]));
                                        
                                        System.out.print(instructions);
                                        break;
                                }
                                // Select a file to be decrypted.
                                case 4: {
                                        // Gets the CramerShoup cryptogram.
                                        BigInteger[] csCryptogram = new BigInteger[4];
                                        
                                        String[] filePath = new String[1];
                                        byte[] document = readDocument("Enter the \".ciph\" file to be decrypted: ", filePath);
                                        
                                        filePath[0] = filePath[0].split("\\.")[0] + "." + filePath[0].split("\\.")[1];
                                        
                                        // Gets u1
                                        String path = filePath[0] + ".csu1";
                                        byte[] u1 = readDocument(path);
                                        
                                        if (u1 == null) {
                                                System.out.println("The .csu1 file was not found. Aborting process.");
                                                break;
                                        }
                                        
                                        csCryptogram[0] = Conversion.byteArrayToBigInteger(u1);
                                        
                                        // Gets u2
                                        path = filePath[0] + ".csu2";
                                        byte[] u2 = readDocument(path);
                                        if (u2 == null) {
                                                System.out.println("The .csu2 file was not found. Aborting process.");
                                                break;
                                        }
                                                
                                        csCryptogram[1] = Conversion.byteArrayToBigInteger(u2);
                                        
                                        // Gets e
                                        path = filePath[0] + ".cse";
                                        byte[] e = readDocument(path);
                                        
                                        if (e == null) {
                                                System.out.println("The .cse file was not found. Aborting process.");
                                                break;
                                        }
                                                
                                        csCryptogram[2] = Conversion.byteArrayToBigInteger(e);
                                        
                                        // Gets v
                                        path = filePath[0] + ".csv";
                                        byte[] v = readDocument(path);
                                        
                                        if (v == null) {
                                                System.out.println("The .csv file was not found. Aborting process.");
                                                break;
                                        }
                                        
                                        csCryptogram[3] = Conversion.byteArrayToBigInteger(v);
                                        
                                        // Prepares a CramerShoup instance.
                                        KeyEncapsulation asymCipher = new CramerShoup();
                                        asymCipher.setup(p, q, g1, g2, hash, sr);
                                        byte[] cipherKey =  asymCipher.decrypt(password, csCryptogram);
                                        
                                        if (cipherKey != null) 
                                        {
                                                decrypt(cipherKey, document, filePath[0]);
                                        } 
                                        else 
                                                System.out.println("Crammer-Shoup cryptogram was not valid because password is invalid or the cryptogram was corrupted.");
                                        
                                        System.out.print(instructions);
                                        break;
                                }
                                // Select a file to be signed
                                case 5: {
                                        // Gets the file to be signed.
                                        String[] filePath = new String[1];
                                        byte[] document = readDocument("Enter the file to be signed: ", filePath);
                                        
                                        // Prepares the Schnorr instance.
                                        DigitalSignature digSign = new Schnorr();
                                        digSign.setup(p, q, g, hash, sr);
                                        digSign.init();
                                        digSign.update(document, document.length);
                                        
                                        // Get the digital signature.
                                        BigInteger[] sig = digSign.sign(password);
                                        
                                        // Save the signature files (.se and .ss).
                                        String path = filePath[0] + ".se";
                                        saveDocument(
                                                                        "SE file \""
                                                                        + path
                                                                        + "\" was generated and saved successfully.",
                                                                        path, Conversion.bigIntegerToByteArray(sig[0]));
                                        
                                        path = filePath[0] + ".ss";
                                        saveDocument(
                                                                        "SS file \""
                                                                        + path
                                                                        + "\" was generated and saved successfully.",
                                                                        path, Conversion.bigIntegerToByteArray(sig[1]));
                                        
                                        System.out.print(instructions);
                                        break;
                                }
                                
                                // Select a file and its digital signature to verify.
                                case 6: {
                                        // Gets the file to be verified.
                                	
                                		init();
                                	
                                        String[] filePath = new String[1];
                                        byte[] document = readDocument("Enter the file to be signed: ", filePath);
                                
                                        // Prepares the Schnorr instance.
                                        DigitalSignature digSign = new Schnorr();
                                        digSign.setup(p, q, g, hash, sr);
                                        digSign.init();
                                        digSign.update(document, document.length);
                                        
                                        // Gets the signature files.
                                        BigInteger[] sig = new BigInteger[2];
                                        
                                        // Gets e
                                        String path = filePath[0] + ".se";
                                        byte[] e = readDocument(path);
                                        
                                        if (e == null) {
                                                System.out.println("The .se file was not found. Aborting process.");
                                                break;
                                        }
                                        
                                        sig[0] = Conversion.byteArrayToBigInteger(e);
                                        
                                        // Gets s
                                        path = filePath[0] + ".ss";
                                        byte[] s = readDocument(path);
                                        
                                        if (s == null) {
                                                System.out.println("The .ss file was not found. Aborting process.");
                                                break;
                                        }
                                        
                                        sig[1] = Conversion.byteArrayToBigInteger(s);
                                        
                                        // Gets the Schnorr public key y.
                                        path = keyFilesPath + KEY_FILES_NAME + ".sy";
                                        byte[] sy = readDocument(path);
                                        
                                        if (sy == null) {
                                                System.out.println("The .sy file was not found. Aborting process.");
                                                break;
                                        }
                                        
                                        BigInteger y = Conversion.byteArrayToBigInteger(sy);
                                        
                                        // Verifies the digital signature.
                                        if (digSign.verify(y, sig)){
                                                System.out.println("The file provided was VERIFIED succesfully.");
                                        } else {
                                                System.out.println("The file provided was NOT VERIFIED.");;
                                        }
                                
                                        System.out.print(instructions);
                                        break;
                                }
                                
                                // Exit the application.        
                                case 0: {
                                        exitFlag = true;
                                        System.out.println("[Shutting down...]");
                                        break;
                                }
                                default:
                                        System.out.print("Invalid Option!\n " + instructions);
                                        break;
                                }
                        } catch (Exception e) 
                        {
                                if (debug)
                                        System.out.println("Exception = " + e);
                                
                                System.out.print("Ooops! Some exception has occurred!\n" + instructions);
                        }
                }
        }
        
        /************************************************************************************************************/
        /******************************************** INITIALIZATION ************************************************/
        /************************************************************************************************************/
        
        /**
         * Initialize global variables.
         */
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
        
        /************************************************************************************************************/
        /************************************** INPUT/OUTPUT MANIPULATION *******************************************/
        /************************************************************************************************************/
        private static InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        private static BufferedReader reader = new BufferedReader(inputStreamReader);
        private static String lineRead;
        
        /**
         * Function for path manipulation.
         */
        private static void pathInput() {
                // Instructions string
                String instructions = "Enter a the path where public key, the asymmetric cryptogram and the digital signature will be saved to: ";

                boolean validValue = false;

                System.out.print(instructions);
                while (!validValue) {
                        try {
                                lineRead = reader.readLine().trim();

                                // Validation
                                if (0 < lineRead.length() && !lineRead.equals("")) {
                                        keyFilesPath = lineRead;
                                        
                                        // Output
                                        System.out.println("New path registered on the system.");
                                        if (debug)
                                                System.out.println("Path string provided: " + keyFilesPath);

                                        validValue = true;
                                } else {
                                        System.out.print("Invalid entry! " + instructions);
                                }
                        } catch (Exception e) {
                                if (debug)
                                        System.out.println("Exception = " + e);
                                
                                System.out.print("Ooops! Some exception has occurred!\n" + instructions);
                        }
                }
        }
        
        /**
         * Function for Password manipulation.
         */
        private static void passwordInput() {
                // Instructions string
                String instructions = "Enter a password: ";

                boolean validValue = false;

                System.out.print(instructions);
                while (!validValue) {
                        try {
                                lineRead = reader.readLine().trim();

                                // Validation
                                if (0 < lineRead.length() && !lineRead.equals("")) {
                                        password = lineRead;
                                        
                                        // Output
                                        System.out.println("Password registered on the system.");
                                        if (debug)
                                                System.out.println("Password string provided: " + password);

                                        validValue = true;
                                } else {
                                        System.out.print("Invalid entry! " + instructions);
                                }
                        } catch (Exception e) {
                                if (debug)
                                        System.out.println("Exception = " + e);
                                
                                System.out.print("Ooops! Some exception has occurred!\n" + instructions);
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
                                        System.out.println("File \"" + filePath[0]      + "\" succefully read.");
                                        if (debug){
                                                System.out.println("File Content: " + Conversion.byteToHex(data));
                                        }
                                        
                                        return data;
                                } else {
                                        System.out.print("File \"" + filePath[0] + "\" not found. " + instructions);
                                }
                        } catch (Exception e) {
                                if (debug)
                                        System.out.println("Exception = " + e);
                                
                                System.out.print("File \"" + filePath[0] + "\" not found. " + instructions);
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
                                System.out.println("File \"" + filePath + "\" succefully read.");
                                
                                if (debug)
                                        System.out.println("File Content: " + Conversion.byteToHex(data));
                        } else 
                                System.out.print("File \"" + filePath + "\" not found. ");
        
                } catch (Exception e) {
                        if (debug)
                                System.out.println("Exception = " + e);
                        
                        System.out.print("File \"" + filePath + "\" not found. ");
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
                                // Output
                                System.out.println(message);
                                if (debug) {
                                        System.out.println("File Content: " + Conversion.byteToHex(data));
                                }
                        } else {
                                System.out.print("Ooops! Some exception has occurred!");
                        }
                } catch (Exception e) {
                        if (debug)
                                System.out.println("Exception = " + e);
                        
                        System.out.print("Ooops! Some exception has occurred!");
                }
        }
        
        /************************************************************************************************************/
        /*********************************** ENCRYPTION AND DECRYPTION FUNCTIONS ************************************/
        /************************************************************************************************************/
        private static final int IV_LENGTH = 96;
    private static final int MAC_LENGTH = 96;
        
        private static String encrypt (byte[] cipherKey) {
                Curupira1 curupira1 = new Curupira1();
                Marvin marvin = new Marvin();
                LetterSoup letterSoup = new LetterSoup();

                // Gets the file to be validated.
                String[] filePath = new String[1];
                byte[] document = readDocument("Enter the file to be Encrypted and Authenticated: ", filePath);
                
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
                                                "Encryption... DONE.\n"
                                                + "Ecnrypted file \""
                                                + newFilePath
                                                + "\" was generated and saved successfully.",
                                                newFilePath, cData);

                // Save .mac file
                newFilePath = filePath[0] + ".mac";
                saveDocument(   
                                                "Authentication... DONE.\n"
                                                + "MAC file \""
                                                + newFilePath
                                                + "\" was generated and saved successfully.",
                                                newFilePath, tag);

                // Save .iv file
                newFilePath = filePath[0] + ".iv";
                saveDocument(
                                                ""
                                                + "IV file \""
                                                + newFilePath
                                                + "\" was generated and saved successfully.",
                                                newFilePath, iv);
                
                return filePath[0];
        }
        
        private static boolean decrypt (byte[] key, byte[] document, String path) {
                
                boolean isValid = false;
                
                Curupira1 curupira1 = new Curupira1();
                Marvin marvin = new Marvin();
                LetterSoup letterSoup = new LetterSoup();
                
                marvin.setCipher(curupira1);

                // Gets the encrypted, the MAC and the IV files.
                byte[] macDocument = readDocument(path + ".mac");
                byte[] ivDocument = readDocument(path + ".iv");

                // Prepares the cipherkey (because key represents an integer, it might has leading zeros)
                byte[] cipherKey = new byte[KEY_SIZE/8];
                for (int i = key.length - 1; i >= 0; i--) {
                        cipherKey[i] = key[i];
                }
                
                // Prepares the AEAD instance.
                letterSoup.setCipher(curupira1);
                letterSoup.setKey(cipherKey, cipherKey.length * 8);
                letterSoup.setIV(ivDocument, ivDocument.length);
                letterSoup.setMAC(marvin);
                
                // Gets the decrypted data.
                byte[] mData = letterSoup.decrypt(document, document.length, new byte[document.length]);
                
                // Gets the tag related to the encrypted data.
                byte[] tag = letterSoup.getTag(new byte[macDocument.length], macDocument.length * 8);

                if (debug)
                        System.out.println("Calculated MAC:     " + Conversion.byteToHex(tag) + 
                                                           "\nProvided MAC:     " + Conversion.byteToHex(macDocument));
                
                // Compares the calculated MAC tag with the provided one.
                if(Conversion.byteToHex(tag).equals(Conversion.byteToHex(macDocument)))
                {
                        System.out.println("File and MAC do MATCH. The file is was validated.");
                        
                        // Save .deciph file
                        saveDocument(
                                                        "Decryption... DONE.\n"
                                                        + "Decrypted file \""
                                                        + path
                                                    + "\" was generated and saved in the same folder.",
                                                    path, mData);
                        
                        isValid = true;
                }
                else
                        System.out.println("File and MAC do NOT MATCH. The file could not be validated.");
                
                return isValid;
        }
        
}