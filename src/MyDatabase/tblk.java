

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class tblk {
	static BPlussTree bt=new BPlussTree();

	public static void main(String[] args) throws IOException {

		FileInputStream fis = null;
		BufferedWriter fos = null;
		String strLine;
		long total=0;
		long total1=0;
		String []x = new String[2]; 
		int abc=0;


		try{
			// create new file input stream
			fis = new FileInputStream("C:/Users/POOJA/Desktop/db/ReadData.txt");
			File file =new File("C:/Users/POOJA/Desktop/db/index.txt");

			PrintStream fs = new PrintStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {

				// Print the content on the console
				final byte[] utf32Bytes = strLine.getBytes("UTF-8");
				x = strLine.split(" ");
				abc = utf32Bytes.length+2;
				if (total1+abc>3072)
				{
					//total=total+abc;
					total1=0;

					bt.btreestart();

					//	new FileOutputStream(file,false).close();
				

				}

				String hex = Long.toHexString(total);


				fs.print(hex+"   ");
				// System.out.println (x[0]);
				fs.println(x[0]);
				total=total+abc;
				total1=total1+abc;
				//  System.out.println(abc+ "   "+hex+"   ");

			}
			if(total1!=0)
			{

				//	bt.btreestart();
				System.out.println(bt.toString());

				System.out.println(bt.search("38417813544394A"));

			}
			//Close the input stream

			String find="Not found";
			System.out.println(bt.toString());
			System.out.println(bt.search("38417813544394A"));
			find=(bt.search("38417813544394A")).toString();
			System.out.println(find);
			abc a= new abc();
			//	a.find(find);
			br.close();

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

