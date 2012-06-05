import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;

public class MobileLocation_Process extends Thread{
	DatagramSocket serverSocket;
	int port_UDP;
    byte[] sendData;
    byte[] receiveData;
    Semaphore sem;
   
    String address = "";
	
	public MobileLocation_Process(String address, DatagramSocket serverSocket, int port_UDP){
		this.serverSocket = serverSocket;
		this.port_UDP = port_UDP;
		
        receiveData = new byte[1024];
        sendData = new byte[1024];		
        
    	this.address = address;
	}
	
	public void run(){
		sendRequestToAP();
		System.out.println("End MobileLocation_Process");
	}
	
	public void sendRequestToAP(){
		String sentence = "GET;"+ address;
        System.out.println("Message send by the server : "+sentence);
        sendData = sentence.getBytes();
        InetAddress addr = null;
        try {
			addr = InetAddress.getByAddress(new byte[] {(byte)192,(byte)168,(byte)1,(byte)255});
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, addr, port_UDP);
        try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
}