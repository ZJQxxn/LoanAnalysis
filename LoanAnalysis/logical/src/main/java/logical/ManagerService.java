package logical;

import java.util.ArrayList;
import java.util.HashMap;

public class ManagerService extends UserService {
    /**
     * Method to get overview information of bank.
     * @return A hash map of overview information.
     */
    public HashMap<String,Double> getOverview(){
        //TODO: Invoke function to get overview.
        HashMap<String,Double> overview=new HashMap<String, Double>();
        /*******************************************/
        overview.put("apply_price",1000.0);
        overview.put("accept_price",500.0);
        overview.put("apply_num",500.0);
        overview.put("accept_num",100.0);
        /*******************************************/
        return overview;
    }

    public HashMap<String,HashMap<String,Double>> get7Day(){
        //TODO：Invoke function to get 7 days history.
        HashMap<String,HashMap<String,Double>> history=new HashMap<String, HashMap<String, Double>>();
        /*******************************************/
        HashMap <String,Double> h=new HashMap<String,Double>();
        h.put("apply_price",1000.0);
        h.put("accept_price",500.0);
        h.put("apply_num",500.0);
        h.put("accept_num",100.0);
        for(int i=0;i<7;i++){
            history.put(String.format("%d",i),h);
        }
        /*******************************************/
        return history;

    }

    public HashMap<String,HashMap<String,Double>> getReasons(){
        //TODO: Invoke function to get reasons.
        HashMap<String,HashMap<String,Double>> reasons=new HashMap<String, HashMap<String, Double>>();
        /*******************************************/
        HashMap <String,Double> h=new HashMap<String,Double>();
        h.put("reason1",0.9);
        h.put("reason2",0.8);
        h.put("reason3",0.7);
        h.put("reason4",0.6);
        h.put("reason5",0.5);
        reasons.put("accepted",h);
        reasons.put("denied",h);
        /*******************************************/
        return reasons;
    }

    public HashMap<String,HashMap<String,Object>> getProportion(){
        //TODO: Invoke functions to get proportions.
        HashMap<String,HashMap<String,Object>> proportions=new HashMap<String, HashMap<String, Object>>();
        /*******************************************/
        HashMap <String,Double> h=new HashMap<String,Double>();
        h.put("cat1",0.5);
        h.put("cat2",0.6);
        h.put("cat3",0.3);
        h.put("cat4",0.9);
        HashMap <String,Object> t=new HashMap<String,Object>();
        t.put("category_num",3);
        t.put("detail",h);
        proportions.put("age",t);
        proportions.put("loan_type",t);
        proportions.put("car_house",t);
        proportions.put("income_type",t);
        proportions.put("educate",t);
        proportions.put("marrige",t);
        proportions.put("occupation",t);
        /*******************************************/
        return proportions;
    }

    public HashMap<String,Object> getBasic(String ID){
        //TODO: Get user basic info given its id.
        HashMap<String,Object> basic=new  HashMap<String,Object>();
        basic.put("ID","123");
        basic.put("age",20);
        basic.put("gender",1);
        basic.put("info_proportion",0.6);
        return basic;
    }

    public HashMap<String,Object> getLabel(String ID) {
        //TODO:Invoke fucntions
        HashMap<String, Object> labels = new HashMap<String, Object>();
        ArrayList<String> list = new ArrayList<String>();
        list.add("man");
        list.add("middle-age");
        list.add("single");
        list.add("actor");
        list.add("graduated");
        labels.put("label_num", 5);
        labels.put("detail", list);
        return labels;
    }

    public HashMap<String,Object> getLoanHistory(String ID){
        //TODO:Invoke functions
        HashMap<String,Object> history=new HashMap<String,Object>();
        HashMap<String,Object> h=new HashMap<String,Object>();
        for(int i=0;i<5;i++)
        {
            h.put(String.format("%d",i),"Some attributes");
        }
        history.put("count",5);
        history.put("detail",h);
        return history;
    }

    public HashMap<String,Object> getConsumeHistory(String ID){
        //TODO:Invoke functions
        HashMap<String,Object> history=new HashMap<String,Object>();
        HashMap<String,Object> h=new HashMap<String,Object>();
        for(int i=0;i<5;i++)
        {
            h.put(String.format("%d",i),"Some attributes");
        }
        history.put("count",5);
        history.put("detail",h);
        return history;
    }
}
