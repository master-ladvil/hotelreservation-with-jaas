import java.io.IOException;
import java.net.PasswordAuthentication;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class LorduCallback implements CallbackHandler{
    private String user = null;
    private String mobile = null;
    public LorduCallback(String user, String mobile){
        this.user = user;
        this.mobile = mobile;
        System.out.println("[+]inside callback handler constructor..");
    }
    public void handle(Callback[] callbackArray) throws IOException,UnsupportedCallbackException{
        System.out.println("[+] inside callback handle method....");
        int counter = 0;
        while(counter < callbackArray.length){
            if(callbackArray[counter] instanceof NameCallback){
                NameCallback nameCallback = (NameCallback) callbackArray[counter++];
                nameCallback.setName(user);
            }else if (callbackArray[counter] instanceof PasswordCallback){
                PasswordCallback passwordCallback = (PasswordCallback) callbackArray[counter++];
                passwordCallback.setPassword(mobile.toCharArray());
            }
        }
    }
}