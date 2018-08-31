package LoanAnalysis;

import com.csvreader.CsvReader;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.sf.json.JSONObject;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 *  Class to check whether the user can afford a loan or not.
 */
public class SaveBolt implements IRichBolt {
    //TODO: Annotations and reconstruct the fucntions.
    //To connect with mongoDB database.
    private static MongoClient client=MongoClients.create("mongodb://localhost:27017");
    private static MongoDatabase database=client.getDatabase("LoanAnalysis");
    private static MongoCollection<Document>user_coll=database.getCollection("test");
    private static MongoCollection<Document> history_coll=database.getCollection("Bank_Overview");
    private static MongoCollection<Document> overview_coll=database.getCollection("Bank_Overview");

    //To handle with Storm structure.
    private OutputCollector collector;

    //Some control parameters.
    private static HashMap<String,Object> history_map= new HashMap<String, Object>();
    private static HashMap<String,Object> bureau_map= new HashMap<String, Object>();
    private static Integer past_days=0;
    private static Integer limit=200+new Random().nextInt(200);
    private static Integer index=0;


    private static void _initHistory(){
        try{
            //TODO:History file path.
            CsvReader reader=new CsvReader("",',');
            reader.readHeaders();
            String[] headers=reader.getHeaders();
            String id="";

            HashMap<String,String> info=new HashMap<String, String>();
            HashMap<String,Object> each_prev=new HashMap<String, Object>();
            //Read top 100000 lines
            Integer index=0;

            while(reader.readRecord() && index<100000){
                for(String key:headers){
                    info.put(key,reader.get(key)==null?"":reader.get(key));
                }
                each_prev.put(reader.get("SK_ID_PREV"),info.clone());
                info.clear();
                Boolean same=(id.equals(reader.get("SK_ID_CURR")));
                //A new user
                if(!same){
                    //The first line
                    if(id.equals("")){
                        id=reader.get("SK_ID_CURR");
                    }
                    history_map.put(id,each_prev.clone());
                    id=reader.get("SK_ID_CURR");
                    each_prev.clear();
                    index++;
                }
            }
            System.out.println("Finished init save bolt");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static void _initBureau(){
        try{
            //TODO:Bureau file path.
            CsvReader reader=new CsvReader("",',');
            reader.readHeaders();
            String[] headers=reader.getHeaders();
            String id="";

            HashMap<String,String> info=new HashMap<String, String>();
            HashMap<String,Object> each_prev=new HashMap<String, Object>();
            //Read top 100000 lines
            Integer index=0;

            while(reader.readRecord() && index<100000){
                for(String key:headers){
                    info.put(key,reader.get(key)==null?"":reader.get(key));
                }
                each_prev.put(reader.get("SK_ID_BUREAU"),info.clone());
                info.clear();
                Boolean same=(id.equals(reader.get("SK_ID_CURR")));
                //A new user
                if(!same){
                    //The first line
                    if(id.equals("")){
                        id=reader.get("SK_ID_CURR");
                    }
                    bureau_map.put(id,each_prev.clone());
                    id=reader.get("SK_ID_CURR");
                    each_prev.clear();
                    index++;
                }
            }
            System.out.println("Finished init save bolt");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static ArrayList<String> classification(JSONObject basic){
        ArrayList<String> labels=new ArrayList<String>();
        //Process age
        Integer age=0;
        if(basic.containsKey("DAYS_BIRTH") && !basic.get("DAYS_BIRTH").equals("")){
            age=-Integer.valueOf(basic.get("DAYS_BIRTH").toString())%365;
        }
        if (age<25){
            labels.add("25岁以下");
        }
        else if(age<35){
            labels.add("25至35岁");
        }
        else if(age<45){
            labels.add("35至45岁");
        }
        else if(age<60){
            labels.add("45至60岁");
        }
        else{
            labels.add("60岁以上");
        }

        //Process loan type
        if(basic.containsKey("NAME_CONTRACT_TYPE")){
            if (basic.get("NAME_CONTRACT_TYPE").equals("Cash loans")){
                labels.add("现金贷款");
            }
            else{
                labels.add("循环贷款");
            }
        }


        //Process car and house
        if(basic.containsKey("FLAG_OWN_CAR") && basic.containsKey("FLAG_OWN_REALTY")){
            String tmp="";
            if (basic.get("FLAG_OWN_CAR").equals("Y")){
                tmp="有车";
            }
            else{
                tmp="无车";
            }
            if (basic.get("FLAG_OWN_REALTY").equals("Y")){
                tmp+="有房";
            }
            else{
                tmp+="无房";
            }
            labels.add(tmp);
        }



        //Process gender
        if(basic.containsKey("CODE_GENDER")){
            if (basic.get("CODE_GENDER").equals("M")){
                labels.add("男性");
            }
            else{
                labels.add("女性");
            }
        }


        //Process income
        if(basic.containsKey("AMT_INCOME_TOTAL")){
            Double income=Double.valueOf(basic.get("AMT_INCOME_TOTAL").toString());
            if (income<50000){
                labels.add("5万以下");
            }
            else if(income<100000){
                labels.add("5万至10万");
            }
            else if(income<200000){
                labels.add("10万至20万");
            }
            else if(income<400000){
                labels.add("20万至40万");
            }
            else{
                labels.add("40万以上|");
            }
        }


        //Process education
        if(basic.containsKey("NAME_EDUCATION_TYPE")){
            if (basic.get("NAME_EDUCATION_TYPE").equals("Academic degree")){
                labels.add("专业学位");
            }
            else if(basic.get("NAME_EDUCATION_TYPE").equals("Higher education")){
                labels.add("高等教育");
            }
            else if(basic.get("NAME_EDUCATION_TYPE").equals("Incomplete higher")){
                labels.add("未完成高等教育");
            }
            else if(basic.get("NAME_EDUCATION_TYPE").equals("Lower secondary")){
                labels.add("中等教育");
            }
            else if(basic.get("NAME_EDUCATION_TYPE").equals("Secondary / secondary special")){
                labels.add("专科教育");
            }
            else{
                labels.add("其他教育水平");
            }
        }



        //Process marriage
        if(basic.containsKey("NAME_FAMILY_STATUS")){
            if (basic.get("NAME_FAMILY_STATUS").equals("Separated")){
                labels.add("离婚");
            }
            else if(basic.get("NAME_FAMILY_STATUS").equals("Single / not married")){
                labels.add("未婚");
            }
            else if(basic.get("NAME_FAMILY_STATUS").equals("Married") ||
                    basic.get("NAME_FAMILY_STATUS").equals("Civil marriage")){
                labels.add("已婚");
            }
            else{
                labels.add("其他婚姻状况");
            }
        }


        //Process occupation
        if(basic.containsKey("OCCUPATION_TYPE")){
            if(basic.get("OCCUPATION_TYPE").equals("Accountants") ||
                    basic.get("OCCUPATION_TYPE").equals("HR staff")||
                    basic.get("OCCUPATION_TYPE").equals("Core staff")){
                labels.add("金融行业人员");
            }
            else if(basic.get("OCCUPATION_TYPE").equals("Cleaning staff")||
                    basic.get("OCCUPATION_TYPE").equals("High skill tech staff")||
                    basic.get("OCCUPATION_TYPE").equals("IT staff")){
                labels.add("技术行业人员");
            }
            else if(basic.get("OCCUPATION_TYPE").equals("Cleaning staff") ||
                    basic.get("OCCUPATION_TYPE").equals("Cooking staff") ||
                    basic.get("OCCUPATION_TYPE").equals("Drivers")||
                    basic.get("OCCUPATION_TYPE").equals("Laborers")||
                    basic.get("OCCUPATION_TYPE").equals("Low-skill Laborers")){
                labels.add("服务行业人员");
            }
            else if(basic.get("OCCUPATION_TYPE").equals("")){
                labels.add("职业未知");
            }
            else{
                labels.add("其他职业");
            }
        }
        return labels;
    }

    //TODO:When to use it?
    private static Boolean laterDate(String date1,String date2){
        String[] d1=date1.split("-");
        String[] d2=date2.split("-");
        if(Integer.valueOf(d1[0])<Integer.valueOf(d2[0])){
            return false;
        }
        else if(Integer.valueOf(d1[0])>Integer.valueOf(d2[0])){
            return true;
        }
        else{
            if(Integer.valueOf(d1[1])<Integer.valueOf(d2[1])){
                return false;
            }
            else if(Integer.valueOf(d1[1])>Integer.valueOf(d2[1])){
                return true;
            }
            else{
                if(Integer.valueOf(d1[2])<Integer.valueOf(d2[2])){
                    return false;
                }
                else if(Integer.valueOf(d1[2])>Integer.valueOf(d2[2])){
                    return true;
                }
                else{
                    return true;
                }
            }
        }
    }

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector=outputCollector;
        _initHistory();//Initialize history map.
        _initBureau();//Initialize bureau map
    }

    public void execute(Tuple tuple) {
        //TODO: Correct the collection name
        JSONObject application=JSONObject.fromObject(tuple.getStringByField("application"));
        //TODO:Use test samples, so need to add 'TARGET'
        String target=tuple.getStringByField("check_result");
        application.put("TARGET",target);
        JSONObject history=null;
        if (history_map.containsKey(application.get("SK_ID_CURR").toString())){
            history=JSONObject.fromObject(history_map.get(application.get("SK_ID_CURR").toString()));
        }
        JSONObject bureau=null;
        if (bureau_map.containsKey(application.get("SK_ID_CURR").toString())){
            bureau=JSONObject.fromObject(history_map.get(application.get("SK_ID_CURR").toString()));
        }
        //Add this new user.
        JSONObject detail=new JSONObject();
        detail.put("basic",application);
        if(history!=null){
            detail.put("loan_history",history);
        }
        if(bureau!=null){
            detail.put("bureau",bureau);
        }
        Document new_user=new Document();
        new_user.append("_id",application.get("SK_ID_CURR"));
        new_user.append("detail",detail);
        new_user.append("label",classification(application));
        user_coll.insertOne(new_user);

        //Update information in overview collection
        Double loan_price=application.get("AMT_GOODS_PRICE").equals("")?0.0
                :Double.valueOf(application.get("AMT_GOODS_PRICE").toString());
        Integer loan_count=history.keySet().size();
        Document overview=overview_coll.find().first();
        overview_coll.updateOne(Filters.eq("_id",overview.get("_id")),
                Updates.set("total_price",
                        Double.valueOf(overview.get("total_price").toString())+loan_price));
        overview_coll.updateOne(Filters.eq("_id",overview.get("_id")),
                Updates.set("total_num",
                        Integer.valueOf(overview.get("total_num").toString())+loan_count));
        overview_coll.updateOne(Filters.eq("_id",overview.get("_id")),
                Updates.set("accept_num",
                        Integer.valueOf(overview.get("accept_num").toString())+loan_count));

        //Updated information in history
        //TODO:Add into history and be careful about the date(i.e past_days).
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Calendar now=Calendar.getInstance();
        now.add(Calendar.DAY_OF_YEAR,-past_days);
        Date date=now.getTime();
        Document res=history_coll.find(Filters.eq("_id",format.format(date))).first();
        if(res==null){
            //A new day
            history_coll.insertOne(new Document("_id",format.format(date))
                    .append("total_num","1")
                    .append("accept_num",target.equals("0")?"1":"0")
                    .append("total_num",loan_price)
                    .append("accept_num",target.equals("0")?loan_price:"0.0")
                    .append("daily_history",new ArrayList<Object>()));
        }
        else{
            //Push into today's history
            history_coll.updateOne(Filters.eq("_id", format.format(date)),
                    Updates.combine(
                            Updates.push("daily_history",
                                    new Document("loan_id",application.get("SK_ID_CURR"))
                                            .append("price",loan_price)
                                            .append("status",target.equals("0")?"ACCEPTED":"DENIED")
                            )
                    )
            );
            //Update daily overview
            history_coll.updateOne(Filters.eq("_id", format.format(date)),
                    Updates.combine(Updates.set("total_num",
                            Integer.valueOf(res.get("total_num").toString())+1))
            );
            history_coll.updateOne(Filters.eq("_id", format.format(date)),
                    Updates.combine(Updates.set("total_price",
                            Double.valueOf(res.get("total_price").toString())+loan_price))
            );
            if(target.equals("0")){
                //If the application accepted.
                history_coll.updateOne(Filters.eq("_id", format.format(date)),
                        Updates.combine(Updates.set("accept_num",
                                Integer.valueOf(res.get("accept_num").toString())+1))
                );
                history_coll.updateOne(Filters.eq("_id", format.format(date)),
                        Updates.combine(Updates.set("accept_num",
                                Double.valueOf(res.get("accept_num").toString())+loan_price))
                );
            }
        }
        if(++index>limit){
            index=0;
            limit=200+new Random().nextInt(200);
        }
        //collector.emit(new Values(application,history));
    }

    public void cleanup() {

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
