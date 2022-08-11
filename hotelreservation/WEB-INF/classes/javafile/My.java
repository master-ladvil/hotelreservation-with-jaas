
import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.text.*;
import javax.security.auth.login.LoginContext;



public class My extends HttpServlet {
	public static Connection con = null;
	public static String uname = null;
	public static String mobile = null;
	public LoginContext logincontext;
	public boolean globalflag = true;
	public My() {
		try {
			System.out.println("[+]inside my constructor..");
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/hotelreserve1_0", "postgres", "pwd");
			if (con != null) {
				System.out.println("connection estabished");
			} else {
				System.out.println("Connection failed");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		
		int s = checkcontext();
		if(s == 0){
			request.getRequestDispatcher("/index.jsp").include(request, response);
		}
		String roomno = request.getParameter("roomno");
		String sdate = request.getParameter("sdate");
		String edate = request.getParameter("edate");
		int flag = reserveroom(roomno,sdate,edate);
		if(flag == 1){
			response.sendRedirect("member.jsp");
		}else{
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.print("couldnt reserve room sorry");
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

			System.out.println("[+]inside get method..");
			My.mobile = request.getParameter("mobile");
			System.out.println("mobile -> " + mobile);
			try{
			logincontext = AuthenticationServlet.loginContext;
			}catch(Exception e){
				System.out.println(e);
			}
			
			if(logincontext == null ){
				request.getRequestDispatcher("error.jsp").include(request,response);
			}
			else if(logincontext.getSubject().getPrincipals().iterator().next().getName().equals("admin")){
				request.getRequestDispatcher("Admin").include(request,response);
			}
			else{
			System.out.println("[+]got context obj with name ->  "+logincontext.getSubject().getPrincipals().iterator().next().getName());
			//int s = checkcontext();
			//if(s==1){
				showrooms(uname, mobile, response);
			//else{response.sendRedirect("error.jsp");}
			}

	}

	public void showrooms(String name, String mobile, HttpServletResponse response) {
		Statement stmt;
		ResultSet rs = null;
		try {
			System.out.println("[+]inside showrooms..");
			String query = String.format(
					"SELECT room.id,capacity,rtype,price FROM room JOIN capacity ON room.cid=capacity.id JOIN rtype ON room.tid = rtype.id WHERE isavailablle = true;");
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("<head>");
			out.println("<body>");
			out.println("<table>");
			out.println("<tr>");
			out.println("<td><b>roomno</b></td>");
			out.println("<td><b>capacity</b></td>");
			out.print("<td><b>roomtype</b></td>");
			out.println("<td><b>price</b></td>");
			while (rs.next()) {
				out.println("<tr>");
				out.print("<td>" + rs.getString("id") + "</td>");
				out.print("<td>" + rs.getString("capacity") + "</td>");
				out.print("<td>" + rs.getString("rtype") + "</td>");
				out.println("<td>" + rs.getString("price") + "</td>");
				out.println("</tr>");
			}
			out.println("<form method='post' action=\"My\">");
			out.println("<table>");
			out.println("<tr>");
			out.println("<td><h2>Select a room number</td></h2></tr>");
			out.println("<tr><td><input type = 'text' name = 'roomno'></td></tr>");
			out.println("<tr><td><h4>Enter the start date(YYYY-mm-dd)</h4></td></tr>");
			out.println("<tr><td><input type=\"date\" placeholder=\"yyyy-mm-dd\" id=\"sdate\" name=\"sdate\"\r\n"
					+ "       value=\"2022-07-01\"\r\n"
					+ "       min=\"2022-07-01\" max=\"2022-07-31\"></td></tr>");
			out.println("<tr><td><h4>Enter the end date(YYYY-mm-dd)</h4></td></tr>");
			out.println("<tr><td><input type=\"date\" placeholder = \"yyyy-mm-dd\" id = \"edate\" name = \"edate\""
					+ "	value=\"2022-07-01\"\r\n"
					+ "min= \"2022-07-01\" max =\"2022-07-31\"></td></tr>");
			out.println("<tr><td><input type = 'submit' value = 'reserve'>");
			out.println("</table></body></html>");

		} catch (Exception e) {
			System.out.println(e);
		}
	}
	public int reserveroom(String roomno,String sdate, String edate) {
		Statement stmt;
        ResultSet rs = null;
        try{
            String query = String.format("select id from client where mobile = '%s';",mobile);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            int clid = rs.getInt("id");
            String rquery = String.format("insert into reservation(rid,clid,sdate,edate) values('%s','%s','%s','%s');",roomno,clid,sdate,edate);
            stmt.executeUpdate(rquery);
            System.out.println("reserved room "+roomno);
            String upquery = String.format("update room set isavailablle = false where id = '%s';",roomno);
            stmt.executeUpdate(upquery);
            return 1; 

        }catch(Exception e){
            System.out.println(e);
			return 0;
        }
    
	}
  
	public void addsession(HttpServletRequest request)throws ServletException{
		HttpSession session = request.getSession();
		session.setAttribute("username",uname);
	}
	public int checkcontext()throws ServletException{
		System.out.println("[+]checking context....");
		if(logincontext == null){
			return 0;
		}
		String currentuser = logincontext.getSubject().getPrincipals().iterator().next().getName();
        if(currentuser != uname){
            return 0;
        }
        else if(currentuser.equals(uname)){
            return 1;
        }
        return 0;		
	}

}
