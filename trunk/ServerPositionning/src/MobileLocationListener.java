import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MobilesListener
 */
@WebServlet("/MobileLocationListener")
public class MobileLocationListener extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	 DatagramSocket serverSocket;
	 int port_UDP = 1236;
    /**
     * @throws SocketException 
     * @see HttpServlet#HttpServlet()
     */
    public MobileLocationListener() throws SocketException {
        super();
        
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		serverSocket = new DatagramSocket(port_UDP);
		String address = request.getParameter("address");
		MobileLocation_Process my_UDPHandler = new MobileLocation_Process(address, serverSocket, port_UDP);
		my_UDPHandler.start();
		try {
			my_UDPHandler.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		serverSocket.close();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//treatment server : compute position with the database
		
		
		//purge database
		
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
