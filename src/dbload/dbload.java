package dbload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class dbload {

	public static void main(String[] args) {
		int pagesize = 0;
		String datafile = "";
		Integer[] intIndex = {0,2,4,6,7,9};
		ArrayList<Integer> intIndexList = new ArrayList<Integer>(Arrays.asList(intIndex));
		for(int i=0; i < args.length; i++) {
			if(args[i].equals("-p")){
				i++;
				pagesize = Integer.parseInt(args[i]);
			}else {
				datafile = args[i];
			}
		}
		if(pagesize == 0) {
			System.err.print("Pagesize argument required - use -p");
			System.exit(0);
		}
		if(datafile.equals("")) {
			System.err.print("No datafile argument provided");
			System.exit(0);
		}
		
		try {
			File data = new File(datafile);
			Scanner s = new Scanner(data);
			s.nextLine();
			try {
				FileWriter fw = new FileWriter("heap." + pagesize);
				int records = 0;
				while(s.hasNextLine()) {
					String line = s.nextLine();
					ArrayList<String> lineData = new ArrayList<String>(Arrays.asList(line.split(",")));
					ArrayList<String> binaryData = new ArrayList<String>();
					int[] dataLengths = new int[lineData.size()];
					for(int i = 0; i < lineData.size(); i++) {
						String binaryString = "";
						if(intIndexList.contains(i)) {
							BigInteger bigInt = BigInteger.valueOf(Integer.parseInt(lineData.get(i)));
							byte[] bytes = bigInt.toByteArray();
							for(byte b : bytes) {
								String byteString = Integer.toBinaryString(b);
								while(byteString.length() < 8) {
									byteString = "0" + byteString;
								}
								binaryString += byteString + " ";
							}
							dataLengths[i] = bytes.length;
						}else {
							byte[] bytes = lineData.get(i).getBytes();
							for(byte b : bytes) {
								String byteString = Integer.toBinaryString(b);
								while(byteString.length() < 8) {
									byteString = "0" + byteString;
								}
								binaryString += byteString + " ";
							}
							dataLengths[i] = bytes.length;
						}
						binaryData.add(binaryString);
						
					}
					//Put the pointers to each field at the start of each record
					int count = lineData.size();
					for(int length : dataLengths) {
						String binaryCount = Integer.toBinaryString(count);
						fw.write(binaryCount + " ");
						count += length;
					}
					if(records < 10) {
						for(String field : binaryData) {
							fw.write(field);
						}
						records++;
					}else {
						break;
					}
				}
				fw.close();
			} catch (IOException e) {
				System.err.println("Error with the FileWriter");
				e.printStackTrace();
			}
			
			s.close();
		} catch (FileNotFoundException e) {
			System.err.println("No file found at: " + datafile);
			e.printStackTrace();
		}

	}

}
