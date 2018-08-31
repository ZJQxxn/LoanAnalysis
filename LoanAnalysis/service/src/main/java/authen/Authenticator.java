package authen;

import java.util.HashMap;

public class Authenticator {
    //TODO:Annotation
    private static HashMap<String,Integer> current_users=new HashMap<>();

    public static void add(String token,Integer privilege){
        synchronized (current_users){
            current_users.put(token,privilege);
        }
    }

    public static void cancel(String token){
        synchronized (current_users){
            if (current_users.containsKey(token)){
                current_users.remove(token);
            }
        }
    }

    public static Boolean authenticate(String token,Integer privilege){
        if(!current_users.containsKey(token)){
            return false;
        }
        return current_users.get(token).equals(privilege);
    }

}
