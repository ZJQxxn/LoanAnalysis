package logical;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;

public class AdminService extends UserService {

    //Database connection
    private static MongoClient client=MongoClients.create("mongodb://localhost:27017");

    /**
     * Method to sign up a new user.
     *
     * @param new_name Hashed username of new user.
     * @param new_password Hashed password of new user.
     * @return Return true if successful; otherwise
     * return false.
     */
    public Boolean signUp(String new_name,String new_password){
        MongoDatabase db=client.getDatabase("LoanAnalysis");
        MongoCollection<Document> coll = db.getCollection("System_User");
        //Check username existence
        Document res=coll.find(Filters.eq("_id",new_name)).first();
        if(res==null){
            //Username already exist
            return false;
        }
        //Sign up a new user
        Document doc=new Document("_id",new_name)
                .append("username",new_name)
                .append("password",new_password)
                .append("privilege","MANAGER");
        coll.insertOne(doc);
        return true;
    }

    /**
     * Method to cancel a user.
     *
     * @param username Hashed username.
     * @param password Hashed password.
     * @return Return true if successful; otherwise
     * return false.
     */
    public Boolean cancel(String username,String password)
    {
        MongoDatabase db=client.getDatabase("LoanAnalysis");
        MongoCollection<Document> coll = db.getCollection("System_User");
        Document doc=coll.find(Filters.eq("_id",username)).first();
        if(doc==null){
            //Can't find the user
            return false;
        }
        else{
            if (doc.get("privilege").equals("ADMIN")){
                //Can't cancel a administrator
                return false;
            }
            if (doc.get("password").equals(password)){
                //Password incorrect
                return false;
            }
        }
        DeleteResult res=coll.deleteOne(Filters.eq("_id",username));
        if(res.getDeletedCount()==0){
            //Unknown error
            return false;
        }
        return true;
    }
}
