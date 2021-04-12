package dbquery;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class dbquery {

	public static void main(String[] args) {
		int pagesize = 0;
		String query = "";
		if (args.length < 2) {
			System.err.print("Not enough arguments");
			System.exit(0);
		}
		query = args[0];
		pagesize = Integer.parseInt(args[1]);
		String datafile = "heap." + pagesize;

		String[] queries = new String[2];
		String sensorQuery = query.substring(0, query.length() - 20);

		String dateQuery = query.substring(query.length() - 20);
		dateQuery = dateQuery.substring(0, 10) + " " + dateQuery.substring(10, 18) + " " + dateQuery.substring(18, 20);

		BigInteger bigInt = BigInteger.valueOf(Integer.parseInt(sensorQuery));
		byte[] sensorBytes = bigInt.toByteArray();
		for (byte b : sensorBytes) {
			String byteString = Integer.toBinaryString(b);
			while (byteString.length() < 8) {
				byteString = "0" + byteString;
			}
			queries[0] = byteString;
		}

		queries[1] = "";
		byte[] dateBytes = dateQuery.getBytes();
		for (byte b : dateBytes) {
			String byteString = Integer.toBinaryString(b);
			while (byteString.length() < 8) {
				byteString = "0" + byteString;
			}
			if (!queries[1].equals("")) {
				queries[1] += " ";
			}
			queries[1] += byteString;
		}

		Integer[] intIndex = { 1, 2, 7, 8, 10 };
		ArrayList<Integer> intIndexList = new ArrayList<Integer>(Arrays.asList(intIndex));

		try {
			File file = new File(datafile);
			Scanner s = new Scanner(file);

			long startTime = System.currentTimeMillis();

			int pageBytes = 0;
			ArrayList<String> savedRecords = new ArrayList<String>();
			boolean endOfRecords = false;
			while (s.hasNext()) {
				int recordBytes = 0;
				boolean saveRecord = false;
				String binaryRecord = "";
				// Get the positions of the start and end of the important fields, as well as
				// the end of the record
				ArrayList<Integer> index = new ArrayList<Integer>();
				for (int i = 0; i < 11; i++) {
					String bytes = s.next();
					recordBytes++;
					if (bytes.equals("00000000")) {
						endOfRecords = true;
						break;
					}
					if (intIndexList.contains(i)) {
						index.add(Integer.parseInt(bytes, 2));
					}
					binaryRecord += bytes + " ";
				}

				if (!endOfRecords) {
					// Find and compare Date_Time
					while (recordBytes < index.get(0)) {
						binaryRecord += s.next() + " ";
						recordBytes++;
					}
					String dateTime = "";
					while (recordBytes < index.get(1)) {
						if (dateTime != "") {
							dateTime += " ";
						}
						dateTime += s.next();
						recordBytes++;
					}
					if (queries[1].equals(dateTime)) {
						saveRecord = true;
					}

					if (!saveRecord) {
						// Find and compare Sensor_ID
						while (recordBytes < index.get(2)) {
							binaryRecord += s.next() + " ";
							recordBytes++;
						}
						String sensorID = "";
						while (recordBytes < index.get(3)) {
							if (sensorID != "") {
								sensorID += " ";
							}
							sensorID += s.next();
							recordBytes++;
						}
						if (queries[0].equals(sensorID)) {
							saveRecord = true;
						}
					}

					// Go to the end of the current record
					while (recordBytes < index.get(4)) {
						binaryRecord += s.next() + " ";
						recordBytes++;
					}

					if (saveRecord) {
						// Convert record to ASCII string and store
						String asciiRecord = convertToASCII(binaryRecord);
						savedRecords.add(asciiRecord);
					}
					
				}else {
					while(pageBytes < pagesize - 1) {
						System.out.println(pageBytes);
						s.next();
						pageBytes++;
					}
				}

				pageBytes += recordBytes;
				if (pageBytes == pagesize) {
					// print results from this page
					for (String r : savedRecords) {
						System.out.println(r);
					}
					pageBytes = 0;
				}
				// break;
			}

			long endTime = System.currentTimeMillis();
			long timeTaken = endTime - startTime;
			System.out.println("Time take to search heap: " + timeTaken);

			s.close();
		} catch (FileNotFoundException e) {
			System.err.println("No file found at: " + datafile);
			e.printStackTrace();
		}

	}

	public static String convertToASCII(String record) {
		return "ascii ;p";
	}

}
