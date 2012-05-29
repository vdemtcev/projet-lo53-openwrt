import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;


public class Database {
	private Connection dbcon;  // Connection for scope of ShowBedroc
	
	public Database() throws SQLException{
		String loginUser = "lo53";
		String loginPasswd = "lo53";
		String loginUrl = "jdbc:postgresql://localhost/lo53";
		
		// Load the PostgreSQL driver
		try {
			Class.forName("org.postgresql.Driver");
			dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			System.out.println("Database connected");
		}
		catch (ClassNotFoundException ex){
			System.err.println("ClassNotFoundException: " + ex.getMessage());
			try {
				throw new ServletException("Class not found Error");
			} catch (ServletException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
			}
		}
		catch (SQLException ex){
	       System.err.println("SQLException: " + ex.getMessage());
	    }
	}
	
	public int insertLocation(double x, double y, int map_id) throws SQLException{
		Statement statement = dbcon.createStatement();
		String query = "INSERT INTO location VALUES (SELECT nextval('seq_location'), "+ x +", "+ y +", "+ map_id +")";
		ResultSet rs = statement.executeQuery(query);	
		statement.close();  
		return rs.getInt("id");
	}

	public void insertRssi(int id_loc, int id_ap, double avg_val, double std_dev) throws SQLException{
		Statement statement = dbcon.createStatement();
		String query = "INSERT INTO rssi VALUES (SELECT nextval("+ id_loc +", "+ id_ap +", "+ avg_val +", "+ std_dev +")";
		ResultSet rs = statement.executeQuery(query);	
		statement.close(); 
	}
	
	public void insertTempRssi(int id_ap, String client_mac, double avg_val) throws SQLException{
		Statement statement = dbcon.createStatement();
		String query = "INSERT INTO tempRssi VALUES ("+ id_ap +", '"+ client_mac +"', '"+ avg_val +"')";
		ResultSet rs = statement.executeQuery(query);	
		statement.close(); 
	}	
	
	public int getAp_id(String mac_addr) throws SQLException{
		Statement statement = dbcon.createStatement();
		String query = "SELECT id from accesspoint WHERE mac_addr = '" + mac_addr + "')";
		ResultSet rs = statement.executeQuery(query);	
		statement.close(); 
		return rs.getInt("id");
	}
}

