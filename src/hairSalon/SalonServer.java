package hairSalon;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.DBconnector;

import org.json.*;


/**
 * Servlet implementation class SalonServer
 */
@WebServlet("/SalonServer")
public class SalonServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
    DBconnector dbcon;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SalonServer() {
        super();
        dbcon = new DBconnector();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		StringBuilder sb = new StringBuilder();
        BufferedReader br = request.getReader();
        String str = null;
        while ((str = br.readLine()) != null) {
            sb.append(str);
        }
        JSONObject jObj;
        try {
			jObj = new JSONObject(sb.toString());
			if (jObj.has("giveSchedule")){
				schedule(response);     //handle a schedule request
			} else {
				form (jObj, response);  //handle a form
			}
		} catch (Exception e) {
			e.printStackTrace();       
		}  
	}
	
	/**
	 * handles a request for open time slots
	 * @param response needed in order to write back
	 * @throws IOException
	 */
	protected void schedule (HttpServletResponse response) throws IOException{
		response.setContentType("application/json");
		HashMap<Integer,String> slots=dbcon.getTimeSlots();
		if (slots.size()==0){
			dbcon.newWeek ();
			System.out.println("calling newWeek");
			schedule(response);
		}else{
			JSONObject jo = new JSONObject(slots);
	    	response.getWriter().write(jo.toString());
		}
	}
	
	/**
	 * Handles form data accepted from client and sends appropriate data to database via DBConnector
	 * @param jObj the JSON with the data
	 * @param response needed in order to write back
	 * @throws IOException
	 */
	protected void form (JSONObject jObj, HttpServletResponse response) throws IOException{
		boolean success = false;
		String fname="";
        int phoneNo=-1;
        String email="";
        String service="";
        int slotIndex=-1;
        try {
			if (jObj.has("fName")){
				fname = jObj.getString("fName");
			}else System.out.println("no fname field in recieved json");
			if (jObj.has("phone")){
				phoneNo = jObj.getInt("phone");
			}else System.out.println("no phone field in recieved json");
			if (jObj.has("email")){
				email = jObj.getString("email");
			}else System.out.println("no email field in recieved json");
			if (jObj.has("service")){
				service = jObj.getString("service");
			}else System.out.println("no service field in recieved json");
			if (jObj.has("slotIndex")){
				slotIndex = jObj.getInt("slotIndex");
				System.out.println("slotIndex recived: "+slotIndex);
			}else System.out.println("no slotIndex field in recieved json");
		} catch (Exception e) {
			e.printStackTrace();       
		}
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String prefix="";
        if(phoneNo != -1 && slotIndex != -1){
			if (!dbcon.customerExists(phoneNo)){
    			dbcon.addCustomer(phoneNo,fname,email);
    		}else prefix="welcome back, ";
			hairService ser = new hairService(service);
			success = dbcon.addAppointment(phoneNo, slotIndex ,ser); //the database uses phoneNo as primary key (so dbcon uses it as id)
			if(success) response.getWriter().write(prefix+ fname + ", your booking for: " + service+ " is approved!");
			else response.getWriter().write("this time slot is not availble, or your service needs more time");
			
        } else {
        	response.getWriter().write("you must enter a phone number and a date to schedule an appointment!");
        }
	}

}
