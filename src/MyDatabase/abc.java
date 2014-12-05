package MyDatabase;



import java.io.*;
import java.io.RandomAccessFile; 


public class abc {

	public void find(String findstring) throws IOException {

		FileInputStream fis1 = null;
		BufferedWriter fos = null;
		String strLine;
		long total=0;
		String []x = new String[2]; 
		String line = findstring;  
		
		

		try{
			// create new file input stream
			RandomAccessFile rm = new RandomAccessFile("C:/Users/Aditya/Desktop/Subjects/Db/Assignment/CS6360Asg5TestData.txt","rw");
			
			fis1 = new FileInputStream("C:/Users/Aditya/Desktop/Subjects/Db/Assignment/CS6360Asg5TestData.txt");
			//PrintStream fs = new PrintStream(new File("C:/Users/Ekta/Desktop/indexreadout.txt"));
			
			BufferedReader br1 = new BufferedReader(new InputStreamReader(fis1));
		//	System.out.println("Enter index:");
			String x1 = findstring;
			
			
			 while((line = br1.readLine()) != null)
			    {
				 x = line.split("   ");
   
				 if (x[1].equalsIgnoreCase(x1))
				 {
				 System.out.println("1:"+x[1]);
				 System.out.println("2:"+x[0]);
				 int hex2int = (int)Long.parseLong(x[0],16);
				 rm.seek(hex2int);
			//	 rm.seek(28);
				 System.out.println(rm.readLine());
			    }
			    
			    }
			

			//Close the input stream
			
		}catch(Exception ex){
			// if any error occurs
			ex.printStackTrace();
		}finally{

			// releases all system resources from the streams
			
			if(fis1!=null)
				fis1.close();
			
		}
	}
}

