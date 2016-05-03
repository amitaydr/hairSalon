package hairSalon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.Calendar;

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
    int i =7;
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
		
		String fName = request.getParameter("fName");
		String lName = request.getParameter("lName");
		String idString = request.getParameter("id");
		int id=-1;
		if(idString!=""){
			try{
			 id = Integer.parseInt(idString);
			}catch (NumberFormatException ex){
				ex.printStackTrace();
			}
		}
		String service = request.getParameter("service");
		String email = request.getParameter("email");
		String datetime = request.getParameter("time");
		PrintWriter out = response.getWriter();
		String prefix="";
		if (!dbcon.customerExists(id)){
			dbcon.addCustomer(id,fName,lName,email);
		}else prefix="welcome back, ";
		if(id==-1){
			prefix="id is not valid!";
		}
		out.println("<html><title>confirmation page</title><body><h1 align='center'>Thank you for choosing wix hair salon!</h1>"+
				"<br>"+prefix+fName+" "+lName+" , your booking for: "+service+ " at: "+datetime+" is approved.<br> have a nice day! </body></html>");
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
        String scheduleReq="";
        try {
			jObj = new JSONObject(sb.toString());
			if (jObj.has("giveSchedule")){
				scheduleReq = jObj.getString("giveSchedule");
			} else {
				form (jObj, response);  //handle a form
			}
		} catch (Exception e) {
			e.printStackTrace();       
		}
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        if(scheduleReq!=""){
        	String slots=dbcon.getTimeSlots();
        	response.getWriter().write(slots);
        }
		  
	}
	
	protected void form (JSONObject jObj, HttpServletResponse response) throws IOException{
		String fname="";
        String lname="";
        int phoneNo=-1;
        String email="";
        String service="";
        try {
			if (jObj.has("fName")){
				fname = jObj.getString("fName");
			}
			if (jObj.has("lName")){
				lname = jObj.getString("lName");
			}
			if (jObj.has("phone")){
				phoneNo = jObj.getInt("phone");
			}
			if (jObj.has("email")){
				email = jObj.getString("email");
			}
			if (jObj.has("service")){
				service = jObj.getString("service");
			}
		} catch (Exception e) {
			e.printStackTrace();       
		}
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String prefix="";
        if(phoneNo != -1){
			if (!dbcon.customerExists(phoneNo)){
    			dbcon.addCustomer(phoneNo,fname,lname,email);
    		}else prefix="welcome back, ";
			response.getWriter().write(prefix+ fname + " " +lname+ ", your booking for: " + service + " at: "+ " is approved!");
        } else {
        	response.getWriter().write("you must enter a phone number to schedule an appointment!");
        }
	}

}
