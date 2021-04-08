package dbload;

import java.io.File;
import java.io.FileNotFoundException;
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
			while(s.hasNextLine()) {
				String line = s.nextLine();
				ArrayList<String> lineData = new ArrayList<String>(Arrays.asList(line.split(",")));
			}
			s.close();
		} catch (FileNotFoundException e) {
			System.err.println("No file found at: " + datafile);
			e.printStackTrace();
		}

	}

}
