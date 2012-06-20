import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
	 NetworkTools myNetworkTools;
	 Database my_database = null;
	 
    /**
     * @throws SocketException 
     * @see HttpServlet#HttpServlet()
     */
    public MobileLocationListener() throws SocketException {
        super();
        myNetworkTools = new NetworkTools();
          try {
  			my_database = new Database();
  		} catch (SQLException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse v)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		serverSocket = new DatagramSocket(port_UDP);
		//String address = request.getParameter("address");
		
		String ip_Addr = request.getRemoteAddr(); //==> to obtain the mobile's IP Address
		
		String address = myNetworkTools.getIpFromMacAddress(ip_Addr);

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
		ArrayList<TempRssi> tempRssiList = null;
		HashMap<Integer, Integer> locationIdList = new HashMap<Integer, Integer>();
		try {
			tempRssiList = my_database.select_tempRSSI(address);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Iterator<TempRssi> ite = tempRssiList.iterator();
		
		while(ite.hasNext()){
			TempRssi temp =  ite.next();
			
			Double avg_val_min = temp.avg_val - 5;
			Double avg_val_max = temp.avg_val + 5;
			
			
			try {
				locationIdList.putAll(my_database.select_rssi(temp.ap_id, avg_val_min, avg_val_max));
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	
		int max = 0;
		int idLocationMax = 0;
		for(Iterator<Integer> ii = locationIdList.keySet().iterator(); ii.hasNext();) { 
			Integer key = ii.next(); 
			Integer value = locationIdList.get(key); 
			if(value > max){
				max = value;
				idLocationMax = key;
			}
		}
		
		Location my_location = null;
		try {
			my_location = my_database.getLocation(idLocationMax);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//purge database tempRSSI
		/*try {
			my_database.purge_tempRssi(mac_Addr);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		PrintWriter out = response.getWriter();
		out.println("Position (x, y, map) : ("+my_location.getX()+", "+my_location.getY()+","+my_location.getMap_id()+")");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
