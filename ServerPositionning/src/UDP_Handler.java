import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;

public class UDP_Handler extends Thread{
	DatagramSocket serverSocket;
	int port_UDP;
    byte[] sendData;
    byte[] receiveData;
    Semaphore sem;
   
    String position = "";
    String address = "";
    InetAddress broadcast = null;
	
	public UDP_Handler(String address, String position, DatagramSocket serverSocket, int port_UDP, Semaphore sem) throws SocketException{
		this.serverSocket = serverSocket;
		this.port_UDP = port_UDP;
		this.sem = sem;
		
        receiveData = new byte[1024];
        sendData = new byte[1024];		
        
    	this.address = address;
		this.position = position;
		this.start();
	}
	
	public void run(){
		sendRequestToAP();
	}
	
	public void sendRequestToAP(){
        String sentence = "GET;"+position+";"+address;
        System.out.println("Message send by the server : "+sentence);
        sendData = sentence.getBytes();
        InetAddress addr = null;
        try {
			addr = InetAddress.getByAddress(new byte[] {(byte)192,(byte)168,(byte)1,(byte)1});
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