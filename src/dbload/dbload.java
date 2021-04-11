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
			try {
				FileWriter fw = new FileWriter("heap." + pagesize);
				while(s.hasNextLine()) {
					String line = s.nextLine();
					ArrayList<String> lineData = new ArrayList<String>(Arrays.asList(line.split(",")));
					ArrayList<String> binaryData = new ArrayList<String>();
					for(int i = 0; i < lineData.size(); i++) {
						String binaryString = "";
						if(intIndexList.contains(i)) {
							binaryString += Integer.toBinaryString(Integer.parseInt(lineData.get(i))) + " ";
						}else {
							char[] charData = lineData.get(i).toCharArray();
							for(char c : charData) {
								binaryString += Integer.toBinaryString(c) + " "; 
							}
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
