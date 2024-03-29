import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

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
		Integer loc_id = null;
		Statement statement = dbcon.createStatement();
		String queryCheck = "SELECT id FROM location WHERE x='"+ x +"' AND y='"+ y +"' AND map_id="+ map_id;
		System.out.println(queryCheck);
		ResultSet rs = statement.executeQuery(queryCheck);	
		rs.next();
		loc_id = rs.getInt("id");
		if(loc_id != null){
			return loc_id;
		}
		
		String query = "INSERT INTO location (SELECT nextval('seq_location'), '"+ x +"', '"+ y +"', "+ map_id +") RETURNING id";
		System.out.println(query);
		rs = statement.executeQuery(query);	
		rs.next();
		loc_id = rs.getInt("id");
		statement.close();  
		return loc_id;
	}
	
	

	public void insertRssi(int id_loc, int id_ap, double avg_val, double std_dev) throws SQLException{
		Statement statement = dbcon.createStatement();
		String query = "INSERT INTO rssi VALUES ("+ id_loc +", "+ id_ap +", '"+ avg_val +"', '"+ std_dev +"')";
		System.out.println(query);
		statement.executeUpdate(query);	
		statement.close(); 
	}
	
	public void insertTempRssi(int id_ap, String client_mac, double avg_val) throws SQLException{
		Statement statement = dbcon.createStatement();
		String query = "INSERT INTO tempRssi VALUES ("+ id_ap +", '"+ client_mac.toUpperCase() +"', '"+ avg_val +"')";
		System.out.println(query); 
		statement.executeUpdate(query);	
		statement.close(); 
	}	
	
	public int getAp_id(String mac_addr) throws SQLException{
		Statement statement = dbcon.createStatement();
		String query = "SELECT id from accesspoint WHERE mac_addr = '" + mac_addr.toUpperCase() + "'";
		System.out.println(query);
		ResultSet rs = statement.executeQuery(query);
		rs.next();
		int idAp = rs.getInt("id");
		System.out.println(idAp);
		statement.close(); 
		return idAp;
	}
	
	public void purge_tempRssi(String mac_addr) throws SQLException{
		Statement statement = dbcon.createStatement();
		String query = "DELETE FROM tempRssi WHERE client mac = '" + mac_addr.toUpperCase() + "'";
		System.out.println(query); 
		statement.executeUpdate(query);	
		statement.close(); 
	}
	
	public ArrayList<TempRssi> select_tempRSSI(String client_mac) throws SQLException{
		ArrayList<TempRssi> returnList = new ArrayList<TempRssi>();
		
		Statement statement = dbcon.createStatement();
		String query = "SELECT id_ap, avg_val from temprssi WHERE client_mac = '" + client_mac.toUpperCase() + "'";
		System.out.println(query);
		ResultSet rs = statement.executeQuery(query);
		
		while(rs.next()){
			TempRssi temp = new TempRssi(rs.getInt("id_ap"), rs.getDouble("avg_val")); 
			returnList.add(temp);
		}
		
		statement.close(); 
		return returnList;
	}
	
	public HashMap<Integer,Integer> select_rssi(Integer id_ap, Double avg_val_min, Double avg_val_max) throws SQLException{
		HashMap<Integer,Integer> returnList = new HashMap<Integer,Integer>();
		
		Statement statement = dbcon.createStatement();
		String query = "SELECT id_loc from rssi WHERE id_ap = '" + id_ap + "' AND avg_val BETWEEN '" + avg_val_min + "' AND '" + avg_val_max + "'";
		System.out.println(query);
		ResultSet rs = statement.executeQuery(query);
		
		while(rs.next()){
			if(returnList.containsKey(rs.getInt("id_loc"))){
				returnList.put(rs.getInt("id_loc"), returnList.get(rs.getInt("id_loc")) + 1);
			}
			else{
				returnList.put(rs.getInt("id_loc"), 1);
			}
		}
		
		statement.close(); 
		return returnList;
	}
	
	public Location getLocation(int idLocation) throws SQLException{
		Statement statement = dbcon.createStatement();
		String query = "SELECT x, y, map_id from location WHERE id = '" +idLocation + "'";
		System.out.println(query);
		ResultSet rs = statement.executeQuery(query);
		rs.next();
		Location tempLocation =  new Location(rs.getDouble("x"), rs.getDouble("y"), rs.getInt("map_id"));
		statement.close(); 
		return tempLocation;
	}
}

