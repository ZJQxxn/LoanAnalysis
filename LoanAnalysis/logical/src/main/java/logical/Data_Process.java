package logical;

/**
 * Class to pre-process all the data and save into database.
 */

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.sf.json.JSONObject;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.*;

public class Data_Process {
    //TODO:Complete annotations and move this class out of this project.
    private static HashMap<String,Object> labels_map=new HashMap<String, Object>();
    private static String[] labels={"年龄","贷款类型","财产情况","性别","收入水平","受教育水平","婚姻状况","职业"};
    private  static void init(){
        HashMap<String,HashMap<String,Integer>> category=new HashMap<String, HashMap<String,Integer>>();
        HashMap<String,Integer> info=new HashMap<String, Integer>();
        info.put("总数",0);
        info.put("通过数",0);

        category.put("25岁以下",info);
        category.put("25至35岁",info);
        category.put("35至45岁",info);
        category.put("45至60岁",info);
        category.put("60岁以上",info);
        labels_map.put("年龄",category.clone());
        category.clear();
        category.put("现金贷款",info);
        category.put("循环贷款",info);
        labels_map.put("贷款类型",category.clone());
        category.clear();
        category.put("有车有房",info);
        category.put("有车无房",info);
        category.put("无车有房",info);
        category.put("无车无房",info);
        labels_map.put("财产情况",category.clone());
        category.clear();
        category.put("男性",info);
        category.put("女性",info);
        labels_map.put("性别",category.clone());
        category.clear();
        category.put("5万以下",info);
        category.put("5万至10万",info);
        category.put("10万至20万",info);
        category.put("20万至40万",info);
        category.put("40万以上",info);
        labels_map.put("收入水平",category.clone());
        category.clear();
        category.put("专业学位",info);
        category.put("未完成高等教育",info);
        category.put("高等教育",info);
        category.put("中等教育",info);
        category.put("专科教育",info);
        category.put("其他教育水平",info);
        labels_map.put("受教育水平",category.clone());
        category.clear();
        category.put("离婚",info);
        category.put("未婚",info);
        category.put("已婚",info);
        category.put("其他婚姻状况",info);
        labels_map.put("婚姻状况",category.clone());
        category.clear();
        category.put("金融行业人员",info);
        category.put("技术行业人员",info);
        category.put("服务行业人员",info);
        category.put("职业未知",info);
        category.put("其他职业",info);
        labels_map.put("职业",category.clone());
        category.clear();
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

    private void processHistory(){
        init();
        MongoClient client=MongoClients.create("mongodb://localhost:27017");
        MongoDatabase db=client.getDatabase("LoanAnalysis");


        MongoCollection<Document> user_coll = db.getCollection("Bank_Customer");
        MongoCollection<Document> history_coll = db.getCollection("Bank_History");
        MongoCollection<Document> overview_coll = db.getCollection("Bank_Overview");

        HashMap<String,HashMap<String,Integer>> category=new HashMap<String, HashMap<String,Integer>>();
        Integer total_num=0;
        Integer accept_num=0;
        Double total_price=0.0;
        Double accept_price=0.0;
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");

        Integer history_count=0;
        Integer past_days=0;
        Integer limit=200+new Random().nextInt(200);
        ArrayList<String> lables_list=null;

        MongoCursor<Document> cursor=user_coll.find().iterator();
        while (cursor.hasNext()){
            Document user=cursor.next();
            JSONObject json=JSONObject.fromObject(user.get("detail"));

            //Process current loan
            JSONObject basic=null;
            if (json.containsKey("basic")){
                basic=JSONObject.fromObject(json.get("basic"));
                lables_list=classification(basic);

                //Update overview labels
                for(String i:lables_list){
                    HashMap<String,Integer> tmp=new HashMap<String, Integer>();
                    //Application accept
                    if(basic.get("TARGET").equals("0")){
                        if (!category.containsKey(i)){
                            tmp.put("总数",1);
                            tmp.put("通过数",1);
                            category.put(i,tmp);
                        }
                        else{
                            tmp.put("总数",Integer.valueOf(
                                    JSONObject.fromObject(category.get(i)).get("总数").toString())+1);
                            tmp.put("通过数",Integer.valueOf(
                                    JSONObject.fromObject(category.get(i)).get("通过数").toString())+1);
                            category.put(i,tmp);
                        }
                    }
                    //Application denied
                    else{
                        if (!category.containsKey(i)){
                            tmp.put("总数",1);
                            tmp.put("通过数",1);
                            category.put(i,tmp);
                        }
                        else{
                            tmp.put("总数",Integer.valueOf(
                                    JSONObject.fromObject(category.get(i)).get("总数").toString())+1);
                            tmp.put("通过数",Integer.valueOf(
                                    JSONObject.fromObject(category.get(i)).get("通过数").toString()));
                            category.put(i,tmp);
                        }
                    }
                }


                //Update lables for each user.
                user_coll.updateOne(Filters.eq("_id",user.get("_id"))
                        ,Updates.combine(Updates.set("label",lables_list)));
                if(basic.containsKey("AMT_GOODS_PRICE")){
                    Double price=Double.valueOf(basic.get("AMT_GOODS_PRICE").toString().equals("")?
                            "0.0":basic.get("AMT_GOODS_PRICE").toString());
                    total_price+=price;
                    total_num+=1;
                    //Loan accepted
                    if(basic.get("TARGET").toString().equals("0")){
                        accept_price+=price;
                        accept_num+=1;
                    }
                }

            }
            //Process previous loans
            JSONObject previous=null;
            if (json.containsKey("loan_history")){
                previous=JSONObject.fromObject(json.get("loan_history"));
                for(Object each:previous.keySet().toArray()){
                    Integer tmp_count=0;//To count haw many previous loan applications was accepted.
                    Integer incre_count=0;//To count the number of previous loan applications.
                    JSONObject each_app=JSONObject.fromObject(previous.get(each));
                    if (each_app.containsKey("AMT_APPLICATION")){
                        Double app_price=Double.valueOf(each_app.get("AMT_APPLICATION").toString().equals("")?
                                "0.0":each_app.get("AMT_APPLICATION").toString());
                        total_price+=app_price;
                        total_num+=1;
                        incre_count+=1;
                        if(app_price>0.0){
                            accept_num+=1;
                            accept_price+=app_price;
                            tmp_count+=1;
                        }
                    }
                    //Update overview labels
                    for(String i:lables_list){
                        HashMap<String,Integer> tmp=new HashMap<String, Integer>();
                        //Application accept
                        if (!category.containsKey(i)){
                            tmp.put("总数",1);
                            tmp.put("通过数",1);
                            category.put(i,tmp);
                        }
                        else{
                            tmp.put("总数",Integer.valueOf(
                                    JSONObject.fromObject(category.get(i)).get("总数").toString())+incre_count);
                            tmp.put("通过数",Integer.valueOf(
                                    JSONObject.fromObject(category.get(i)).get("通过数").toString())+tmp_count);
                            category.put(i,tmp);
                        }
                    }
                }
            }


            //Add into history
            if((++history_count)>limit){
                limit=200+new Random().nextInt(200);
                past_days++;
                history_count=0;
            }
            Calendar now=Calendar.getInstance();
            now.add(Calendar.DAY_OF_YEAR,-past_days);
            Date date=now.getTime();

            if(history_coll.find(Filters.eq("_id",format.format(date))).first()==null){
                history_coll.insertOne(
                        new Document("_id", format.format(date))
                                .append("total_num","0")
                                .append("accept_num","0")
                                .append("total_price","0.0")
                                .append("accept_price","0.0")
                                .append("daily_history",new ArrayList<Object>())
                );
            }
            if(basic!=null){
                Document daily=history_coll.find(Filters.eq("_id",format.format(date))).first();
                String status="DENIED";
                String tmp_price=basic.get("AMT_GOODS_PRICE").toString().equals("")?"0.0"
                        :basic.get("AMT_GOODS_PRICE").toString();
                if(basic.get("TARGET").equals("0")){
                    history_coll.updateOne(Filters.eq("_id",format.format(date)),
                            Updates.combine(
                                    Updates.set("accept_num",Integer.valueOf(daily.get("accept_num").toString())+1)
                            )
                    );
                    history_coll.updateOne(Filters.eq("_id",format.format(date)),
                            Updates.combine(
                                    Updates.set("accept_price",Double.valueOf(daily.get("accept_price").toString())+
                                            Double.valueOf(tmp_price))
                            )
                    );
                    status="ACCEPTED";
                }
                history_coll.updateOne(Filters.eq("_id",format.format(date)),
                        Updates.combine(
                                Updates.set("total_num",Integer.valueOf(daily.get("total_num").toString())+1)
                        )
                );
                history_coll.updateOne(Filters.eq("_id",format.format(date)),
                        Updates.combine(
                                Updates.set("total_price",Double.valueOf(daily.get("total_price").toString())+
                                        Double.valueOf(tmp_price))
                        )
                );
                history_coll.updateOne(Filters.eq("_id", format.format(date)),
                        Updates.combine(
                                Updates.push("daily_history",
                                        new Document("loan_id",user.get("_id"))
                                                .append("price",Double.valueOf(tmp_price))
                                                .append("status",status)
                                )
                        )
                );

            }
        }

        System.out.println("Finish add into history");

        //Process bank overview
        JSONObject map=JSONObject.fromObject(labels_map);
        for(String key:category.keySet()){
            for(String k:labels_map.keySet()){
                JSONObject tmp=JSONObject.fromObject(map.get(k));
                if (tmp.containsKey(key)){
                    tmp.put(key,category.get(key));
                    map.put(k,tmp);
                    break;
                }

            }
        }
        if(overview_coll.find().first()==null){
            overview_coll.insertOne(new Document("total_num","0")
                    .append("total_price","0.0")
                    .append("accept_num","0")
                    .append("accept_price","0.0")
                    .append("lable_proportion",null));
        }
        Document overview=overview_coll.find().first();
        overview_coll.deleteOne(Filters.eq("_id",overview.get("_id")));
        overview_coll.insertOne(new Document("total_num",total_num.toString())
                .append("total_price",total_price.toString())
                .append("accept_num",accept_num.toString())
                .append("accept_price",accept_price.toString())
                .append("lable_proportion",map));

        System.out.println(total_num);
        System.out.println(accept_num);
        System.out.println(total_price);
        System.out.println(accept_price);


        client.close();
    }

    private void processFeatures(){
        MongoClient client=MongoClients.create("mongodb://localhost:27017");
        MongoDatabase db=client.getDatabase("LoanAnalysis");


        MongoCollection<Document> user_coll = db.getCollection("Bank_Customer");
        HashMap<String,HashMap<String,Integer>> feature_proportion=new HashMap<String, HashMap<String, Integer>>();
        HashMap<String,Integer> each_feature=new HashMap<String, Integer>();
        Integer yes_accept=0;
        Integer yes_num=0;
        Integer no_num=0;
        Integer no_accept=0;
        for(Document each:user_coll.find()){
            JSONObject basic=JSONObject.fromObject(
                    JSONObject.fromObject(each.get("detail"))
                            .get("basic"));
            Double value=basic.get("EXT_SOURCE_1").equals("")?0.0
                    :Double.valueOf(basic.get("EXT_SOURCE_1").toString());
            if(value>0.4){
                yes_num++;
                if(basic.get("TARGET").equals("0")){
                    yes_accept++;
                }
            }
            else{
                no_num++;
                if(basic.get("TARGET").equals("0")){
                    no_accept++;
                }
            }
        }
        System.out.println("Yes num "+yes_num);
        System.out.println("Yes accept "+yes_accept);
        System.out.println("No num "+no_num);
        System.out.println("No accept "+no_accept);

        client.close();
    }

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

    public static void main(String[] args){
        MongoClient client=MongoClients.create("mongodb://localhost:27017");
        MongoDatabase db=client.getDatabase("LoanAnalysis");
        MongoCollection<Document> history_coll = db.getCollection("Bank_History");
        MongoCursor<Document> cursor=history_coll.find().iterator();
        String latest="0-0-0";
        while(cursor.hasNext()){
            String date=cursor.next().get("_id").toString();
            if(laterDate(date,latest)){
                latest=date;
            }
        }
        System.out.println(latest);
        Calendar day=Calendar.getInstance();
        String[] tmp=latest.split("-");
        //System.out.println(d);
        client.close();
    }

}
