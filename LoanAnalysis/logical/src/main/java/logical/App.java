package logical;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        ManagerService servicer=new ManagerService();
        System.out.println(servicer.getDailyHistory());
    }
}
