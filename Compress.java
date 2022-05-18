// Summary: Simple compression algorithm that takes in a string file with standard ASCII 
// characters and converts it into a binary representation with .zzz file termination. Utilizes
// a HashTableChain to maintain a key-value mapping of the string combinations in the file.
// Authors: Alec Henning, Alex Bae
// Date: 12/4/2021

import java.io.*;
import java.util.Scanner;

public class Compress {
    private static int tries = 3;

    public static void main(String[] args) {

        BufferedReader input;
        ObjectOutputStream output;
        PrintWriter outputLog;
        Scanner kb = new Scanner(System.in);

        Boolean run = true;
        while (run == true) {
            try {
                String fileName = args[args.length - 1];

                StringBuilder longestP = new StringBuilder();
                File inputFile = new File(fileName);
                if (inputFile.exists()) {
                    HashTableChain<String, Integer> table = getASCIIHashTableChain(inputFile);
                    int size = 132;
                    long start = System.nanoTime();
                    input = new BufferedReader(new FileReader(inputFile));
                    output = new ObjectOutputStream(new FileOutputStream(fileName + ".zzz"));
                    outputLog = new PrintWriter(new FileOutputStream(fileName + ".zzz" + ".log"));
                    System.out.println("Starting compression...");
                    char p = (char) input.read();
                    char c = (char) input.read();
                    longestP.append(p);

                    while (true) {
                        if (table.get((longestP.toString() + c)) != null) {
                            longestP.append(c);
                            c = (char) input.read();
                        } else {
                            if (table.get((longestP.toString())) != null) {
                                output.writeInt(table.get(longestP.toString()));
                                table.put((longestP.toString() + c), size);
                                size++;
                            } else {
                                table.put((longestP.toString()), size);
                                size++;
                            }
                            p = c;
                            int next = input.read();
                            if (next == -1)
                                break;
                            c = (char) next;
                            longestP = longestP.delete(0, longestP.length());
                            longestP.append(p);

                        }
                    }
                    // output.writeInt(c);

                    long end = System.nanoTime();
                    double elapsedTime = (end - start); // Convert to seconds
                    outputLog.println("Compression of " + fileName);
                    printFileSize(fileName, outputLog);
                    printTime(elapsedTime, outputLog); // print the time
                    outputLog.println("The dictionary contains " + table.size() + " total entries");
                    outputLog.println("The table was rehashed " + table.rehashCount() + " times");
                    System.out.println("Compression complete.");
                    input.close();
                    output.close();
                    outputLog.close();
                    deleteFile(fileName);

                } else {
                    tries--;
                    if (tries >= 0) {
                        System.out.println("File not found try again: ");
                        System.out.println("Tries left: " + tries);
                        System.out.print("Enter filename: ");
                        args[0] = kb.nextLine();
                        main(args);
                    } else {
                        System.out.println("Try again later!");
                        System.exit(0);
                    }
                }
                System.out.println("Would you like to compress another file? (y for yes n for no)");
                if (kb.nextLine().equalsIgnoreCase("y")) {
                    System.out.println("Enter a filename: ");
                    args[0] = kb.nextLine();
                    run = true;
                } else {
                    System.exit(0);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Returns the size of a file
     * 
     * @param fileName the file to get the size of
     * @return the size of the file in bytes
     */
    public static long getFileSize(String fileName) {
        File temp = new File(fileName);
        return temp.length(); // returns file size in bytes
    }

    /**
     * Deletes the previous file that has been compressed into the .zzz file
     * 
     * @param fileName the name of the file to be deleted.
     */
    public static void deleteFile(String fileName) {
        File deleted = new File(fileName);
        deleted.deleteOnExit();
    }

    /**
     * Creates a new HashTableChain and takes in the input file to dynamically
     * adjust initial capacity based on file size.
     * 
     * @param input The input file used to set initial capacity
     * @return the new HashTableChain
     */
    public static HashTableChain<String, Integer> getASCIIHashTableChain(File input) {
        Long fileSize = input.length();
        // System.out.println("FileSize: " + fileSize);
        int factor = (int) (fileSize / 52428800); // creates a factor based on 50Mb
        if (factor == 0) {
            factor = 1;
        }
        int capacity = (int) (101 * Math.pow(2, factor));

        capacity = getNextPrime(capacity);
        int tableSize = 127;

        HashTableChain<String, Integer> table = new HashTableChain<String, Integer>(capacity);
        for (int i = 32; i <= tableSize; i++) {
            String key = Character.toString((char) i);
            int value = i;
            table.put(key, value);
        }
        table.put("\n", 128);
        table.put("\t", 129);
        table.put("\r", 130);
        table.put("\r\n", 131);
        return table;

    }

    /**
     * Checks if the number is a prime number
     * 
     * @param number The number to be checked
     * @return true if the number is prime, false otherwise
     */

    public static boolean isPrime(int number) {
        if (number % 2 == 0) {
            return false;
        }
        for (int i = 3; i * i <= number; i += 2) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Loops through numbers starting at the number sent in and returns the closest
     * prime number
     * 
     * @param numberToCheck the number to start checking from
     * @return the closest prime number
     */
    public static int getNextPrime(int numberToCheck) {
        for (int i = numberToCheck; true; i++) {
            if (isPrime(i)) {
                return i;
            }
        }
    }

    /**
     * prints the file size after determining the best byte hierarchy.
     * 
     * @param fileName  the name of the file to check
     * @param outputLog the file to print the information to
     */
    private static void printFileSize(String fileName, PrintWriter outputLog) {
        long startSize = getFileSize(fileName);
        long endSize = getFileSize(fileName + ".zzz");

        if (startSize / 1024 > 1 && endSize < 1024) {
            outputLog.println(
                    "Compressed from " + startSize / 1024 + " kilobytes to "
                            + endSize
                            + " bytes");
        } else if (startSize / 1024 > 1 && endSize / 1024 > 1) {
            outputLog.println("Compressed from " + startSize / 1024 + " kilobytes to "
                    + endSize / 1024 + " kilobytes");

        } else {
            outputLog.println("Compressed from " + startSize + " bytes to " + endSize
                    + " bytes");

        }

    }

    /**
     * prints the elapsed time after determing the best unit of measurement
     * 
     * @param elapsedTime the elapsed time of the compression algorithm
     * @param outputLog   the file to print the information to
     */
    public static void printTime(double elapsedTime, PrintWriter outputLog) {
        if (((double) elapsedTime / 1000000000) > 1) {
            elapsedTime = (elapsedTime / 1000000000);
            outputLog.println("Compression took " + String.format("%.2f", elapsedTime) + " seconds");
        } else if (((double) elapsedTime / 1000000) > 1) {
            elapsedTime = (elapsedTime / 1000000);
            outputLog.println("Compression took " + String.format("%.2f", elapsedTime) + " milliseconds");
        } else {
            outputLog.println("Compression took " + (int) elapsedTime + " nanoseconds");
        }
    }

}