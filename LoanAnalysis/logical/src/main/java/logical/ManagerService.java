package logical;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;

public class ManagerService extends UserService {
    //TODO:Complete annotations and Javadoc.

    private static MongoClient client=MongoClients.create("mongodb://localhost:27017");
    private static MongoDatabase database=client.getDatabase("LoanAnalysis");
    /**
     * Method to get overview information of bank.
     * @return A hash map of overview information.
     */
    public HashMap<String,Double> getOverview(){
        MongoCollection <Document> coll=database.getCollection("Bank_Overview");
        Document res=coll.find().first();
        HashMap<String,Double> overview=new HashMap<String, Double>();
        overview.put("total_price",Double.valueOf(res.get("total_price").toString()));
        overview.put("accept_price",Double.valueOf(res.get("accept_price").toString()));
        overview.put("total_num",Double.valueOf(res.get("total_num").toString()));
        overview.put("accept_num",Double.valueOf(res.get("accept_num").toString()));
        return overview;
    }

    public HashMap<String,HashMap<String,Double>> getDailyHistory(){
        MongoCollection<Document> coll=database.getCollection("Bank_History");
        HashMap<String,HashMap<String,Double>> history=new HashMap<String, HashMap<String, Double>>();
        HashMap<String,Double> one_day=new HashMap<String, Double>();
        for (Document day:coll.find()){
            one_day.put("total_price",Double.valueOf(day.get("total_price").toString()));
            one_day.put("accept_price",Double.valueOf(day.get("accept_price").toString()));
            one_day.put("total_num",Double.valueOf(day.get("total_num").toString()));
            one_day.put("accept_num",Double.valueOf(day.get("accept_num").toString()));
            history.put(day.getString("_id"),one_day);
        }
        return history;

    }

    public HashMap<String,HashMap<String,Double>> getReasons(){
        //TODO: Invoke function to get reasons.Should consider about the way getting these reasons.
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

    public HashMap<String,Object> getProportion(){
        HashMap<String,Object> proportions=new HashMap<String, Object>();
        MongoCollection<Document> coll=database.getCollection("Bank_Overview");
        Document res=coll.find().first();
        proportions.put("total_num",Double.valueOf(res.get("total_num").toString()));
        proportions.put("accept_num",Double.valueOf(res.get("accept_num").toString()));
        proportions.put("label_proportion",res.get("label_proportion"));
        return proportions;
    }

    public HashMap<String,Object> getBasic(String ID){
        MongoCollection<Document> coll=database.getCollection("Bank_Customer");
        HashMap<String,Object> basic=new HashMap<String, Object>();
        Document res=coll.find(Filters.eq("_id",ID)).first();
        if(res==null){
            basic=null;
        }
        else {
            basic.put("ID",res.get("_id"));
            JSONObject basic_json=JSONObject.fromObject(
                    JSONObject.fromObject(res.get("detail"))
                            .get("basic"));
            Integer cat_count=0;
            Integer exist_count=0;
            if(!(basic_json.get("CODE_GENDER")==null)){
                basic.put("性别",basic_json.get("CODE_GENDER").equals("M")?"男":"女");
                exist_count++;
            }
            if(!(basic_json.get("AMT_INCOME_TOTAL")==null)){
                basic.put("收入",Double.valueOf(basic_json.get("AMT_INCOME_TOTAL").toString()));
                exist_count++;
            }
            if(!(basic_json.get("AMT_CREDIT")==null)){
                basic.put("信用额度",Double.valueOf(basic_json.get("AMT_CREDIT").toString()));
                exist_count++;
            }
            if(!(basic_json.get("OCCUPATION_TYPE")==null)){
                basic.put("职业",basic_json.get("OCCUPATION_TYPE"));
                exist_count++;
            }
            if(!(basic_json.get("NAME_INCOME_TYPE")==null)){
                basic.put("收入类型",basic_json.get("NAME_INCOME_TYPE"));
                exist_count++;
            }
            if(!(basic_json.get("DAYS_BIRTH")==null)){
                basic.put("年龄",-Integer.valueOf(basic_json.get("DAYS_BIRTH").toString())%365);
                exist_count++;
            }
            if(!(basic_json.get("OWN_CAR_AGE")==null)){
                basic.put("车龄",basic_json.get("OWN_CAR_AGE"));
                exist_count++;
            }
            if(!(basic_json.get("CNT_FAM_MEMBERS")==null)){
                basic.put("家庭成员数量",Double.valueOf(basic_json.get("CNT_FAM_MEMBERS").toString()).intValue());
                exist_count++;
            }
            cat_count+=8;
            //DOCUMENT 2 --- DOCUMENT 21
            for(int i=2;i<=21;i++){
                if (basic_json.get(String.format("FLAG_DOCUMENT_%d",i)).equals("1")){
                    exist_count++;
                }
            }
            cat_count+=20;
            //Other information
            if(basic_json.get("FLAG_MOBIL").equals("1")){
                exist_count++;
            }
            if(basic_json.get("FLAG_EMP_PHONE").equals("1")){
                exist_count++;
            }
            if(basic_json.get("FLAG_WORK_PHONE").equals("1")){
                exist_count++;
            }
            if(basic_json.get("FLAG_CONT_MOBILE").equals("1")){
                exist_count++;
            }
            if(basic_json.get("FLAG_PHONE").equals("1")){
                exist_count++;
            }
            if(basic_json.get("FLAG_EMAIL").equals("1")){
                exist_count++;
            }
            cat_count+=6;
            basic.put("资料完善度",String.format("%.2f",exist_count*1.0/cat_count));
        }
        return basic;
    }

    public Object[] getLabel(String ID) {
        MongoCollection<Document> coll=database.getCollection("Bank_Customer");
        Document res=coll.find(Filters.eq("_id",ID)).first();
        JSONArray label_json=JSONArray.fromObject(res.get("label"));
        return label_json.toArray();
    }

    public HashMap<String,Object> getLoanHistory(String ID){
        HashMap<String,Object> history=new HashMap<String, Object>();
        MongoCollection<Document> coll=database.getCollection("Bank_Customer");
        Document res=coll.find(Filters.eq("_id",ID)).first();
        if(!JSONObject.fromObject(res.get("detail")).containsKey("loan_history")){
            history=null;
        }
        else{
            JSONObject history_json=JSONObject.fromObject(
                    JSONObject.fromObject(res.get("detail"))
                            .get("loan_history"));
            for(Object key:history_json.keySet())
            {
                history.put(key.toString(),history_json.get(key));
            }
        }
        return history;
    }

    public HashMap<String,Object> getBureauHistory(String ID){
        HashMap<String,Object> history=new HashMap<String, Object>();
        MongoCollection<Document> coll=database.getCollection("Bank_Customer");
        Document res=coll.find(Filters.eq("_id",ID)).first();
        if(!JSONObject.fromObject(res.get("detail")).containsKey("bureau")){
            history=null;
        }
        else{
            JSONObject history_json=JSONObject.fromObject(
                    JSONObject.fromObject(res.get("detail"))
                            .get("bureau"));
            for(Object key:history_json.keySet())
            {
                history.put(key.toString(),history_json.get(key));
            }
        }
        return history;
    }
}
