import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.SQLException;


public class APListenerThread extends Thread{
	 DatagramSocket serverSocket;
	 int port_UDP = 1237;
	 byte[] receiveData;
	 private Database my_database;
	 boolean run;
	 
	 public APListenerThread(){
		 System.out.println("Listener AP Launched");
		 receiveData = new byte[1024];
		 run = true;
		 
		 try {
			my_database = new Database();
		} catch (SQLException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		 
		 
		 try {
			 serverSocket = new DatagramSocket(port_UDP);
		 } catch (SocketException e2) {
			 // TODO Auto-generated catch block
			 e2.printStackTrace();
		 }
	}

	public void run(){
		while(run){          
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
	        if(strSentence[0].equals("RSSIO")){
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
				} 
		       	catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        else if(strSentence[0].equals("RSS")){
	        	int ap_id = 0;
				try {
					ap_id = my_database.getAp_id(strSentence[2]);
					System.out.println("ap_id: " + ap_id);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	
	        	try {
		        	my_database.insertTempRssi(ap_id, strSentence[1], Double.parseDouble(strSentence[3]));
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

	public void stopListener(){
		run = false;
		serverSocket.close();
	}
}
