import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.SQLException;

public class APListener extends Thread{
	 DatagramSocket serverSocket;
	 int port_UDP = 1237;
	 byte[] receiveData;
	 private Database my_database;
	 
	 public APListener(Database my_database){
		 System.out.println("Listener AP Launched");
		 this.my_database = my_database;
		 this.start();
	 }
	 
	 public void run(){
		try {
			serverSocket = new DatagramSocket(port_UDP);
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		receiveData = new byte[1024];
		 while(true){          
			 DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	         try {
				serverSocket.receive(receivePacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	         String sentence = new String( receivePacket.getData());
	         System.out.println("RECEIVED: " + sentence);
	         
	         

	         String strSentence[]=sentence.split(";");
	         if(strSentence[0].equals("RSSI")){
	        	int id_loc = 0;
	        	int ap_id = 0;
	        	
				try {
					ap_id = my_database.getAp_id(strSentence[5]);
					System.out.println("ap_id: " + ap_id);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	
	        	try {
					id_loc = my_database.insertLocation(Double.parseDouble(strSentence[1]), Double.parseDouble(strSentence[2]), Integer.parseInt(strSentence[3]));
					System.out.println("location added - id_loc: " + id_loc);
	        	} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        	try {
					my_database.insertRssi(id_loc, ap_id, Double.parseDouble(strSentence[6]), 0);
					System.out.println("RSSI added");
	        	} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        	try {
					my_database.insertTempRssi(ap_id, strSentence[4], Double.parseDouble(strSentence[6]));
					System.out.println("tempRSSI added");
	        	} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	         }
		 }
	}

}
