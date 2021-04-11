package dbload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class dbload {

	public static void main(String[] args) {
		int pagesize = 0;
		String datafile = "";
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
			try {
				FileWriter fw = new FileWriter("heap." + pagesize);
				while(s.hasNextLine()) {
					String line = s.nextLine();
					ArrayList<String> lineData = new ArrayList<String>(Arrays.asList(line.split(",")));
					ArrayList<String> binaryData = new ArrayList<String>();
					for(String currData : lineData) {
						char[] charData = currData.toCharArray();
						String binaryString = "";
						for(char c : charData) {
							binaryString += Integer.toBinaryString(c) + " "; 
						}
						binaryData.add(binaryString);
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
