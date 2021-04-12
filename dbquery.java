package dbquery;

import java.util.ArrayList;
import java.util.Arrays;

public class dbquery {

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
		String[] datafileSub = datafile.split(".");
		if(pagesize != Integer.parseInt(datafileSub[-1])) {
			System.err.print("Pagesize must be the same as that of the file provided");
			System.exit(0);
		}

	}

}
