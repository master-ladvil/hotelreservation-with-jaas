import java.io.Serializable;
import java.security.Principal;

public class LorduPrinciple implements Principal, Serializable{
    private static final long seriallVersionUID =1L;
    private final String name;
    public LorduPrinciple(String name){
        this.name = name;
        System.out.println("principle constructor; name -> " + name);
    }

    public String getName(){
        System.out.println("LorduPrinciple.getName...");
        return name;
    }
    public boolean equals(Object object){
        System.out.println(
            "[+]equal function checking name and principle name"
        );
        boolean flag = false;
        if(object instanceof LorduPrinciple) {flag = name.equals(((LorduPrinciple) object).getName());}
        return flag;
    }
}