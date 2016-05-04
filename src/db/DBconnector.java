package db;

import hairSalon.hairService;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DBconnector {
	
	private Connection con;
	private Statement st;
	private ResultSet rs;
	final long HOUR = 3600000; //in milliseconds
    final long DAY = 86400000;
    final long WEEK = 604800000;
		
	public DBconnector(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hair","root","root1");
			st = con.createStatement();
		}catch(Exception e){
			System.out.println("error "+ e);
		}
	}
	
/**
 * adds a new customer to the database
 * @param id
 * @param name
 * @param email
 */
	
	public void addCustomer(int id, String name, String email){
			String quary = "INSERT INTO hair.customers (id,name,email) VALUE ("+id+",'"+name+"','"+email+"')";
			try {
				st.execute(quary);					
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
	}
/**
 * 
 * @param id customer id to check (phone no. is the id used for customers)
 * @return if the customer already exists in the database
 */
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
	
/**
 * 
 * @return an arrayList of all the open time slots available
 */
	public HashMap<Integer,String> getTimeSlots() {
		String query = "SELECT * FROM hair.schedule WHERE schedule.isFreeSlot=1";
		HashMap<Integer,String> ans=new HashMap<Integer,String>();
		try {
			rs = st.executeQuery(query);
			
			while(rs.next()){
				ans.put(rs.getInt("index"),rs.getString("dateTime"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ans;
	}
	
/**
 * reserve time slots according to request and service duration
 * @param id the customer id
 * @param index index of time slot to start from
 * @param service witch service is required
 * @return if time slots were reserved or not
 */
	public boolean addAppointment(int id, int index, hairService service){
		try {
			if(checkOpenSlot(index,service)){
				for(int i=0; i<service.getDuration();i++){
					System.out.println("round- "+i);
					PreparedStatement pst = con.prepareStatement("UPDATE hair.schedule SET customerID="+id+", service='"+service.getName()+"', isFreeSlot=0 WHERE schedule.index="+(index+i)+";");
					/*pst.setInt(1,id);
					pst.setString(2,service.getName());
					pst.setInt(3,index+i);*/
					pst.execute();
				}
			}else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	
}
	/**
	 * check if the time slots required by this service (depends on duration) are in the database and are free
	 * @param index of the time slot to begin with
	 * @param service the required service
	 * @return if it is open
	 */
	private boolean checkOpenSlot(int index, hairService service) {
		int duration = service.getDuration();
		boolean ans = true;
		try {
			for (int i=0;i<duration;i++){
				PreparedStatement query = con.prepareStatement("SELECT * FROM hair.schedule WHERE schedule.isFreeSlot=1 AND schedule.index = ?");
				query.setObject(1,index + i);
				rs = query.executeQuery();
				ans = ans && rs.next();
				System.out.println("check open slot loop ans:" + ans + ", i="+i + ", index ="+ index + ", duration=" + duration);
				if(!ans) return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return ans;
	}
/**
 * when all time slots are full- opens up new time slots for the next week starting from the next Sunday
 * opens slots from Sunday to Thursday, from 8:00 to 16:00
 * 
 */
	public void newWeek(){
		Calendar calNextSunday = Calendar.getInstance();
	    calNextSunday.set(Calendar.HOUR, 0);
	    calNextSunday.set(Calendar.MINUTE, 0);
	    calNextSunday.set(Calendar.SECOND, 0);
	    while(calNextSunday.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
	        calNextSunday.add(Calendar.DATE, 1);
	    }
		String s = "INSERT INTO hair.schedule (dateTime,isFreeSlot) VALUE (?,1)";
		for (int i=0; i<5 ; i++){
			for(int j=8; j<17; j++){
				try {
					PreparedStatement pst = con.prepareStatement(s);
					pst.setObject(1,new java.sql.Timestamp(calNextSunday.getTimeInMillis() + i*DAY + j*HOUR));
					pst.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			try {//add a slot that is not available after each day so no appointmens for over an hour will be set on the end of the day
				PreparedStatement pst = con.prepareStatement(s);
				pst.setObject(0,new java.sql.Timestamp(calNextSunday.getTimeInMillis() + i*DAY + 17*HOUR)); 
				pst.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

	
	

}
