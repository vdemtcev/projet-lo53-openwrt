import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;

public class MobileCalibration_Process extends Thread{
	DatagramSocket serverSocket;
	int port_UDP;
    byte[] sendData;
    byte[] receiveData;
    Semaphore sem;
   
    String x = "";
    String y = "";
    String map = "";
    String address = "";
    InetAddress broadcast = null;
	
	public MobileCalibration_Process(String address, String x, String y, String map, DatagramSocket serverSocket, int port_UDP, Semaphore sem) throws SocketException{
		this.serverSocket = serverSocket;
		this.port_UDP = port_UDP;
		this.sem = sem;
		
        receiveData = new byte[1024];
        sendData = new byte[1024];		
        
    	this.address = address;
		this.x = x;
		this.y = y;
		this.map = map;
	}
	
	public void run(){
		sendRequestToAP();
		System.out.println("End MobileCalibration_Process1");
	}
	
	public void sendRequestToAP(){
        String sentence = "GETOFF;"+ x +";"+ y +";"+ map +";"+ address;
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