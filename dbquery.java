package dbquery;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;

public class dbquery {

	public static void main(String[] args) {
		String pagesize = "";
		String query = "";
		if(args.length < 2) {
			System.err.print("Not enough arguments");
			System.exit(0);
		}
		query = args[0];
		pagesize = args[1];
		String datafile = "heap." + pagesize;

		String[] queries = new String[2];
		String sensorQuery = query.substring(0, query.length() - 20);
		
		String dateQuery = query.substring(query.length() - 20);
		dateQuery = dateQuery.substring(0, 10) + " " + dateQuery.substring(10, 18) + " " + dateQuery.substring(18, 20);
		
		BigInteger bigInt = BigInteger.valueOf(Integer.parseInt(sensorQuery));
		byte[] sensorBytes = bigInt.toByteArray();
		for(byte b : sensorBytes) {
			String byteString = Integer.toBinaryString(b);
			while(byteString.length() < 8) {
					byteString = "0" + byteString;
			}
			queries[0] = byteString;
		}
		
		queries[1] = "";
		byte[] dateBytes = dateQuery.getBytes();
		for(byte b : dateBytes) {
			String byteString = Integer.toBinaryString(b);
			while(byteString.length() < 8) {
				byteString = "0" + byteString;
			}
			if(!queries[1].equals("")) {
				queries[1] += " ";
			}
			queries[1] += byteString;
		}
		
		Scanner s = new Scanner(datafile);
		s.close();

	}

}
