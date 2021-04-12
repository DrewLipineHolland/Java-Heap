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
		final int numFields = 10;
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
				int pageBytes = 0;
				ArrayList<Integer> recordBytes = new ArrayList<Integer>();
				ArrayList<String> binaryRecords = new ArrayList<String>();
				while(s.hasNextLine()) {
					String line = s.nextLine();
					ArrayList<String> lineData = new ArrayList<String>(Arrays.asList(line.split(",")));
					ArrayList<String> binaryData = new ArrayList<String>();
					int[] dataLengths = new int[lineData.size()];
					
					String recordString = "";
					for(int i = 0; i < lineData.size(); i++) {
						String binaryString = "";
						if(intIndexList.contains(i)) {
							int loops = 0;
							BigInteger bigInt = BigInteger.valueOf(Integer.parseInt(lineData.get(i)));
							byte[] bytes = bigInt.toByteArray();
							for(byte b : bytes) {
								String byteString = Integer.toBinaryString(b);
								if(byteString.length() > 8) {
									
									for(int x = byteString.length(); x > 8; x -= 9) {
										String substring1 = byteString.substring(0, x - 9);
										String substring2 = byteString.substring(x - 8, byteString.length());
										byteString = substring1 + " " + substring2;
										loops++;
									}
									while(byteString.length() % 8 != loops) {
										byteString = "0" + byteString;
									}
								}else {
									while(byteString.length() < 8) {
										byteString = "0" + byteString;
									}
								}
								recordString += byteString + " ";
							}
							dataLengths[i] = bytes.length + loops;
						}else {
							byte[] bytes = lineData.get(i).getBytes();
							for(byte b : bytes) {
								String byteString = Integer.toBinaryString(b);
								while(byteString.length() < 8) {
									byteString = "0" + byteString;
								}
								recordString += byteString + " ";
							}
							dataLengths[i] = bytes.length;
						}
						binaryData.add(binaryString);
						
					}
					//Put the pointers to each field at the start of each record
					int count = numFields + 1;
					String fieldPointers = "";
					for(int l = 0; l <= dataLengths.length; l++) {
						BigInteger bigInt = BigInteger.valueOf(count);
						byte[] bytes = bigInt.toByteArray();
						for(byte b : bytes) {
							String byteString = Integer.toBinaryString(b);
							while(byteString.length() < 8) {
								byteString = "0" + byteString;
							}
							fieldPointers += byteString + " ";
						}
						if(l != dataLengths.length) {
							count += dataLengths[l];
						}
					}
					pageBytes += count;
					int lastRecordCount = 0;
					if(recordBytes.size() > 0) {
						lastRecordCount += recordBytes.get(recordBytes.size()-1);
					}
					recordBytes.add(count + lastRecordCount);
					
					recordString = fieldPointers + recordString;
					binaryRecords.add(recordString);
					
					if(pageBytes > pagesize) {
						//write the page
						for(int k = 0; k < binaryRecords.size() - 1; k++) {
							fw.write(binaryRecords.get(k));
						}
						//Fill out the rest of the page with empty bytes
						for(int p = recordBytes.get(recordBytes.size()-2); p < pagesize; p++) {
							fw.write("00000000 ");
						}
						//next page
						pageBytes = recordBytes.get(recordBytes.size() - 1) - recordBytes.get(recordBytes.size() - 2);
						recordBytes = new ArrayList<Integer>();
						recordBytes.add(pageBytes);
						
						String nextRecordString = binaryRecords.get(binaryRecords.size() - 1);
						binaryRecords = new ArrayList<String>();
						binaryRecords.add(nextRecordString);
						
						break;
					}else if(pageBytes == pagesize) {
						//write the page
						for(int k = 0; k < binaryRecords.size(); k++) {
							fw.write(binaryRecords.get(k));
						}
						//next page
						pageBytes = 0;
						recordBytes = new ArrayList<Integer>();
						binaryRecords = new ArrayList<String>();
						
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
