import java.io.IOException;
import java.util.Map;
import java.sql.*;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class LorduLogin implements LoginModule {
    public static  String Uname = null;
    public static  String Mobile = null;
    public String name;
    public String mobile;
    private Subject subject  = null;
    private LorduPrinciple lorduPrinciple = null;
    private boolean authFlag = false;
    private CallbackHandler callbackHandler =null;
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
            Map<String, ?> options) {
                System.out.println("[+]inside initialize.....");
        this.callbackHandler = callbackHandler;
        this.subject = subject;
    }
    public Connection connecttodb(){
        Connection con = null;
        try{
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
        return con;
    }
    public void addmobile(String name,String mobile){
        int flag = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            con = connecttodb();
            String query = String.format("select mobile from client where fullname = '%s';", name);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				Mobile = rs.getString("mobile");
                Uname = name;
			}

        }catch(Exception e){
            System.out.println(e);
            
        }

    }

    public boolean login() throws LoginException {
        System.out.println("[+] inside login...");
        Callback[] callbackArray = new Callback[2];
        callbackArray[0] = new NameCallback("Full Name : ");
        callbackArray[1] = new PasswordCallback("Mobile :", false);
        try {
            callbackHandler.handle(callbackArray);
        } catch (IOException  | UnsupportedCallbackException e) {
            e.printStackTrace();
        }
        name = ((NameCallback) callbackArray[0]).getName();
        mobile = new String(((PasswordCallback) callbackArray[1]).getPassword());
        addmobile(name, mobile);
        if(Uname.equals(name)&&mobile.equals(Mobile)){
            
            System.out.println("Authentication Success...");
            authFlag = true;
        }else{
            authFlag = false;
            throw new FailedLoginException("Authenticaion Failure...");
        }
        return authFlag;
    }


    public boolean commit() throws LoginException {
        System.out.println("[+]control inside commit...");
        lorduPrinciple = new LorduPrinciple(name);
        if(!subject.getPrincipals().contains(lorduPrinciple)){
            subject.getPrincipals().add(lorduPrinciple);
        }
        if(subject.getPrincipals().contains(lorduPrinciple)){
            System.out.println("Added principal to subject...");
        }
        return authFlag;
    }


    public boolean abort() throws LoginException {

        System.out.println("[+] abort module implementing");
	  if(subject != null && lorduPrinciple !=null && subject.getPrincipals().contains(lorduPrinciple)){
	  	subject.getPrincipals().remove(lorduPrinciple);  
	}
		subject = null;
		lorduPrinciple = null;
		AuthenticationServlet.loginContext = null;
		return true;
		
    }


    public boolean logout() throws LoginException {

        System.out.println("[+]Logout module implementing....");
        subject.getPrincipals().remove(lorduPrinciple);
        lorduPrinciple = null;
        AuthenticationServlet.loginContext = null;
        subject = null;
        name = null;
        mobile = null;
        Uname = null;
        Mobile=null;
        return true;
    }
    
}
