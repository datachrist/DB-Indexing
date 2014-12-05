import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
public class delete
{
	
	 delete()
		{
			
	String 		path1="C:/Users/POOJA/Desktop/db/index.txt";
			Path xpath=Paths.get(path1);
			try
			{
				Files.delete(xpath);
			}
			catch (Exception e)
			{
				System.out.println("deleted");
			}
		}
	 public static void main(String args[])
		{
		 delete t=new delete();
		
		}
	}