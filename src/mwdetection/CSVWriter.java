package mwdetection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

public class CSVWriter {

    public static void write2CSV(String filePath, ArrayList<? extends ArrayList<?>> contents, ArrayList<String> header) throws IOException {
        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath));

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
        ) {
            if(header!= null)csvPrinter.printRecord(header);

            for(int loop = 0; loop < contents.size();loop++){
                csvPrinter.printRecord(contents.get(loop));
            }
            csvPrinter.flush();
        }
    }

    public static void write2CSV(String filePath, ArrayList<? extends ArrayList<?>> contents, ArrayList<String> header,char delimitlator) throws IOException {
        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath));

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.newFormat(delimitlator).withRecordSeparator("\n"));
        ) {
            if(header!= null)csvPrinter.printRecord(header);

            for(int loop = 0; loop < contents.size();loop++){
                csvPrinter.printRecord(contents.get(loop));
            }
            csvPrinter.flush();
        }
    }

    public static ArrayList< ArrayList<String>> readCSV2String (String filePath, boolean hasHeader, String delimilator){
        ArrayList< ArrayList<String>> contents = new  ArrayList< ArrayList<String>>();
        try {
            Scanner scanner = new Scanner(new File(filePath));
            while(scanner.hasNextLine()) {
                if (hasHeader){
                    scanner.nextLine();
                    hasHeader = false; // so that it will read starting from 2nd line
                }else{
                    ArrayList<String> oneLine = new ArrayList<String>();
                    String line =  scanner.nextLine();
                    if(!line.contains(delimilator))continue;
                    String[] parts = line.split(delimilator);

                    for(int i=0; i < parts.length ; i++){
                        oneLine.add(parts[i]);
                    }
                    contents.add(oneLine);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("ReadCSV2String - File not found : " + filePath);
            return null;
        }
        return contents;
    }

    public static <T> ArrayList<ArrayList<T>> transpose(ArrayList<ArrayList<T>> table) {
        ArrayList<ArrayList<T>> ret = new ArrayList<ArrayList<T>>();
        final int N = table.get(0).size();
        for (int i = 0; i < N; i++) {
            ArrayList<T> col = new ArrayList<T>();
            for (ArrayList<T> row : table) {
                col.add(row.get(i));
            }
            ret.add(col);
        }
        return ret;
    }
}
