

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Semaphore;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MobilesListener
 */
@WebServlet("/MobilesListener")
public class MobilesListener extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	 DatagramSocket serverSocket;
	 int port_UDP = 1236;
	 Semaphore sem;
	 Database my_database;
       
    /**
     * @throws SocketException 
     * @see HttpServlet#HttpServlet()
     */
    public MobilesListener() throws SocketException {
        super();
		
        try {
			my_database = new Database();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        serverSocket = new DatagramSocket(port_UDP);
		// serverSocket.setBroadcast(true);
		this.sem = new Semaphore(0, true);
		APListener my_APListener = new APListener(my_database); 
		
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String address = request.getParameter("address");
		String position = request.getParameter("position");
		
		 
		
		UDP_Handler my_UDPHandler = new UDP_Handler(address, position, serverSocket, port_UDP, sem); 
		
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		my_UDPHandler.stop();
		//serverSocket.close();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
