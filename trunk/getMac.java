import java.io.*;

public class getMac
{
	public static void main(String[] args)
	{
		System.out.println("getMac");

		try
		{
			FileInputStream fstream = new FileInputStream("/proc/net/arp");

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null)
			{
				System.out.println(strLine);
				String[] splitted = strLine.split(" +");
				
				System.out.println("Elem 0:"+splitted[0]);
				System.out.println("Elem 1:"+splitted[1]);
				System.out.println("Elem 2:"+splitted[2]);
				System.out.println("Elem 3:"+splitted[3]);
				
			}
			
			in.close();
		}
		catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}
	}
}


