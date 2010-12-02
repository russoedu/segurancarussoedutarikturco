package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class IO {
        /**
         * Reads a file from disk.
         * @param filePath the path to the file.
         * @return A byte array with the file data.
         * @throws IOException if it cannot find the path
         */
        public static byte[] readFile(String filePath) throws java.io.IOException {
                File file = new File(filePath);
                FileInputStream fis = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fis.read(data, 0, (int) file.length());
                return data;
        }

        /**
         * Saves a file to disk.
         * @param filePath the path to the new file.
         * @param data the contents of the new file.
         * @return
         * True if it has saved the file succesfully.
         * False if it has not saved the file.
         * @throws IOException if it cannot find the path
         */
        public static boolean saveFile(String filePath, byte[] data)
                        throws IOException {
                File file = new File(filePath);
                FileOutputStream fis = new FileOutputStream(file);
                fis.write(data);
                fis.close();
                return true;
        }
}