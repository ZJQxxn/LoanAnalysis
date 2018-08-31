package logical;

import com.mongodb.Block;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class UserService {

    //Databse connection
    private static  MongoClient client=MongoClients.create("mongodb://localhost:27017");

    /**
     * Method to sign in the system.
     * @param username Hashed username.
     * @param password Hashed password.
     * @param privilege User privilege.
     * @return Result of login.
     *      * (1 stands for successfully log in;
     *      * 0 stands for username or password mismatching;
     *      * -1 stands for privilege mismatching
     */
    public   Integer signIn(String username,String password,String privilege){
        MongoDatabase db=client.getDatabase("LoanAnalysis");
        MongoCollection<Document> coll = db.getCollection("System_User");
        Document res=coll.find(Filters.eq("_id",username)).first();
        Integer return_code=0;
        if(res==null){
            //Username doesn't exist
            return_code=0;
        }
        else{
            if(!res.get("password").equals(password)){
                //Password incorrect
                return_code=0;
            }
            else if(!res.get("privilege").equals(privilege)){
                //Privilege incorrect
                return_code=-1;
            }
        }
        //Everything correct
        return_code=1;
        return return_code;
    }
}
