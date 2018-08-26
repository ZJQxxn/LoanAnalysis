package logical;

public class AdminService extends UserService {
    /**
     * Method to sign up a new user.
     *
     * @param new_name Hashed username of new user.
     * @param new_password Hashed password of new user.
     * @return Return true if successful; otherwise
     * return false.
     */
    public Boolean signUp(String new_name,String new_password){
        //TODO: Invoke function to sign up.
        return false;
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
        //TODO: Invoke function to cancel a user.
        return false;
    }
}
