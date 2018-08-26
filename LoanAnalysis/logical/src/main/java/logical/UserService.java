package logical;

public class UserService {
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
    public   Integer signIn(String username,String password,Integer privilege){
        //TODO:Invoke function to check username and password.
        return 1;
    }
}
