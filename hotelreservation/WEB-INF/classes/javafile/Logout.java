import javax.security.auth.login.LoginContext;
import javax.servlet.*;
import java.io.*;
import javax.servlet.http.*;

public class Logout extends HttpServlet{
public void service(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
	response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
	response.setHeader("Pragma", "no-cache");
	try{
	LoginContext logincontext =  AuthenticationServlet.loginContext;
	System.out.println("[+]calling logout..");
	logincontext.logout();
	response.sendRedirect("index.jsp");
	}catch(Exception e){
		System.out.println(e);
	}
}
}