

import java.io.*;

public class indexread {
	public static void main(String[] args) throws IOException {

		FileInputStream fis = null;
		BufferedWriter fos = null;
		String strLine;
		long total=0;
		String []x = new String[2]; 
		

		try{
			// create new file input stream
			fis = new FileInputStream("C:/Users/POOJA/Desktop/db/ReadData.txt");
			PrintStream fs = new PrintStream(new File("C:/Users/POOJA/Desktop/db/index.txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
				   final byte[] utf32Bytes = strLine.getBytes("UTF-8");
				   x = strLine.split(" ");
				   int abc = utf32Bytes.length+2;
				  
				   String hex = Long.toHexString(total);
				  System.out.print(abc+ "   "+hex+"   ");
				  fs.print(hex+"   ");
				  System.out.println (x[0]);
				  fs.println(x[0]);
				  total=total+abc;
			}

			//Close the input stream
			br.close(); 
			BPlussTree bt=new BPlussTree();
			bt.btreestart();
		}catch(Exception ex){
			// if any error occurs
			ex.printStackTrace();
		}finally{

			// releases all system resources from the streams
			if(fis!=null)
				fis.close();
			if(fos!=null)
				fos.close();
		}
	}
}

