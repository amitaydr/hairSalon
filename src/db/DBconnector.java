package db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBconnector {
	
	private Connection con;
	private Statement st;
	private ResultSet rs;
		
	public DBconnector(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hair","root","root1");
			st = con.createStatement();
		}catch(Exception e){
			System.out.println("error "+ e);
		}
	}
	
	public void printData(){
		String quary = "SELECT * FROM hair.customers";
		try {
			rs = st.executeQuery(quary);
			while(rs.next()){
				String name = rs.getString("name");
				String id = rs.getString("id");
				System.out.println("name: "+ name +"\nid: " + id);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
		
	public void addCustomer(int id, String name, String surname, String email){
			String quary = "INSERT INTO hair.customers (id,name,surName,email) VALUE ("+id+",'"+name+"','"+surname+"','"+email+"')";
			try {
				st.execute(quary);					
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
	}
	
	public boolean customerExists (int id){
		String quary = "SELECT * FROM hair.customers WHERE customers.id="+id;
		try {
			rs = st.executeQuery(quary);
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getTimeSlots() {
		String quary = "SELECT * FROM hair.schedule WHERE schedule.isFreeSlot=1";
		String ans="\n";
		try {
			rs = st.executeQuery(quary);
			while(rs.next()){
				ans += "date: " + rs.getString("date")+ " ; time: " + rs.getString("time") + "\n";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ans;
	}
	
/*	public void newWeek(Date sunday){
		String s = "";
		for (int i=0; i<7 ; i++){
			for(int j=10; j<17; j++){
				s = s + "INSERT INTO hair.schedule (date,time,isFreeSlot) VALUE ("+sunday.getYear()+"-"+sunday.getMonth()+"-"+sunday.getDate()+","+j+":00, 1)\n";
			}
			//sunday.increment;
		}
		
	}*/

	

}
