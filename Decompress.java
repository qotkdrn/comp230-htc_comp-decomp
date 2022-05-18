// Summary: Simple Decompression algorithm that reads binary input from a file and converts it back
// into its string representation. Utilizes a standard perfect hashtable that doubles at 0.75 load
// factor.
// Authors: Alec Henning, Alex Bae
// Date: 12/4/2021

import java.io.*;
import java.util.Hashtable;
import java.util.Scanner;

public class Decompress {
    static int tries = 3;

    public static void main(String[] args) {

        PrintWriter output;
        PrintWriter outputLog;
        ObjectInputStream input;
        Scanner kb = new Scanner(System.in);
        boolean run = true;
        while (run == true) {

            try {
                String fileName = args[args.length - 1];

                input = new ObjectInputStream(new FileInputStream(fileName));
                String newFileName = fileName.substring(0, fileName.length() - 4);
                output = new PrintWriter(new FileOutputStream(newFileName));
                outputLog = new PrintWriter(new FileOutputStream(newFileName + ".log"));

                long start = System.nanoTime();
                int tableSize = 127;
                int doubled = 0;
                int capacity = 101;
                Hashtable<Integer, String> table = new Hashtable<>(capacity);
                for (int i = 32; i <= tableSize; i++) {
                    int key = i;
                    String value = Character.toString((char) i);
                    table.put(key, value);
                }
                table.put(128, "\n");
                table.put(129, "\t");
                table.put(130, "\r");
                table.put(131, "\r\n");

                try {
                    System.out.println("Starting decompression...");
                    int size = 132;
                    int q = input.readInt();
                    output.print(table.get(q));

                    while (true) {
                        int p = input.readInt();
                        if (table.get(p) != null) {
                            table.put(size, table.get(q) + table.get(p).charAt(0));
                            size++;
                            output.print(table.get(p));
                        } else {
                            output.print(table.get(q) + table.get(q).charAt(0));
                            table.put(p, table.get(q) + table.get(q).charAt(0));
                            size++;
                        }
                        q = p;

                        if (table.size() > capacity * 0.75) {
                            doubled += 1;
                            capacity *= 2;

                        }

                    }
                } catch (EOFException e) {
                    System.out.println("Decompression complete.");

                }
                long end = System.nanoTime();
                long elapsedTime = (end - start);
                outputLog.println("Decompression of " + fileName);
                printTime(elapsedTime, outputLog); // print the time
                outputLog.println("The table was doubled " + doubled + " times");
                input.close();
                output.close();
                outputLog.close();
                deleteFile(fileName);
                deleteFile(fileName + ".log");

            } catch (FileNotFoundException e) {
                tries--;
                if (tries >= 0) {
                    System.out.println("File not found try again: ");
                    System.out.println("Tries left: " + tries);
                    System.out.print("Enter filename: ");
                    args[0] = kb.nextLine();
                    main(args);
                    kb.close();
                } else {
                    System.out.println("Try again later!");
                    System.exit(0);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
            System.out.println("Would you like to decompress another file? (y for yes n for no)");
            if (kb.nextLine().equalsIgnoreCase("y")) {
                System.out.println("Enter a filename: ");
                args[0] = kb.nextLine();
                run = true;
            } else {
                System.exit(0);
            }
        }

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
     * prints the elapsed time after determing the best unit of measurement
     * 
     * @param elapsedTime the elapsed time of the compression algorithm
     * @param outputLog   the file to print the information to
     */
    public static void printTime(double elapsedTime, PrintWriter outputLog) {
        if (((double) elapsedTime / 1000000000) > 1) {
            elapsedTime = (elapsedTime / 1000000000);
            outputLog.println("Decompression took " + String.format("%.2f", elapsedTime) + " seconds");
        } else if (((double) elapsedTime / 1000000) > 1) {
            elapsedTime = (elapsedTime / 1000000);
            outputLog.println("Decompression took " + String.format("%.2f", elapsedTime) + " milliseconds");
        } else {
            outputLog.println("Decompression took " + (int) elapsedTime + " nanoseconds");
        }
    }

}
