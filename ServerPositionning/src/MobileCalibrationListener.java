import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Semaphore;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MobilesListener
 */
@WebServlet("/MobileCalibrationListener")
public class MobileCalibrationListener extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	 DatagramSocket serverSocket;
	 int port_UDP = 1236;
	 Semaphore sem;

	 NetworkTools myNetworkTools;
       
    /**
     * @throws SocketException 
     * @see HttpServlet#HttpServlet()
     */
    public MobileCalibrationListener() throws SocketException {
        super();
       
		// serverSocket.setBroadcast(true);
		this.sem = new Semaphore(0, true);
		myNetworkTools = new NetworkTools();
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		serverSocket = new DatagramSocket(port_UDP);
		//String address = request.getParameter("address");
		String x = request.getParameter("x");
		String y = request.getParameter("y");
		String map = request.getParameter("map");
		 
		String mac_Addr = request.getRemoteAddr(); //==> to obtain the mobile's IP Address
		//System.out.println("Mobile IP:"+mac_Addr);
		
		String address = myNetworkTools.getIpFromMacAddress(mac_Addr);
		//System.out.println("Mobile IP found:"+ip+" real ip :"+address);
		
		
		
		MobileCalibration_Process my_UDPHandler = new MobileCalibration_Process(address, x, y, map, serverSocket, port_UDP, sem); 
		my_UDPHandler.start();
		try {
			my_UDPHandler.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		serverSocket.close();
		
		/*try {
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		my_UDPHandler.stop();*/
		
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
