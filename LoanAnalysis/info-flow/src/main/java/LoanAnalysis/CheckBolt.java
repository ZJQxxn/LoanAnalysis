package LoanAnalysis;

import com.csvreader.CsvWriter;
import net.sf.json.JSONObject;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *  Class to aggregate all the attributes needed.
 */
public class CheckBolt implements IRichBolt {
    //TODO: Annotations.

    private OutputCollector collector;
    private static final String filePath="resources/app_log/";

    private static boolean createJsonFile(String jsonString, String filePath, String fileName) {
        // 标记文件生成是否成功
        boolean flag = true;

        // 拼接文件完整路径
        String fullPath = filePath + File.separator + fileName + ".json";

        // 生成json格式文件
        try {
            // 保证创建一个新文件
            File file = new File(fullPath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();
            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(jsonString);
            write.flush();
            write.close();
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        // 返回是否成功的标记
        return flag;
    }

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector=outputCollector;
    }

    public void execute(Tuple tuple) {
        JSONObject application=JSONObject.fromObject(tuple.getValueByField("application"));
        String[] headers=tuple.getValueByField("headers").toString().split(",");
        String[] feature_list=new String[headers.length];
        for(int i=0;i<headers.length;i++){
            feature_list[i]=application.get(headers[i]).toString();
        }
        try {
            CsvWriter writer = new CsvWriter(String.format(filePath+"application_features_%s.csv", application.get("SK_ID_CURR")),
                    ',', Charset.forName("UTF-8"));
            writer.writeRecord(headers);
            writer.writeRecord(feature_list);
            System.out.println(feature_list[0]);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        //Boolean create_res=createJsonFile(application.toString(),filePath,
        //        String.format("application_features_%s",application.get("SK_ID_CURR")));
        //System.out.println(create_res);
        /**
        String res=null;
        try {
            //Execute a python file and get the result.
            //TODO: Need a python file to check the application.
            String [] cmd={"python","",""};
            Process pr = Runtime.getRuntime().exec(cmd);
            InputStream fis=pr.getInputStream();
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);
            while((res=br.readLine())!=null)
            {
                System.out.println(String.format("%s result %s",application.get("SK_ID_CURR"),res));
            }
            pr.waitFor();
            //TODO:Need to store the result into a log file.Maybe should delete the features json file.
        } catch (Exception e) {
            e.printStackTrace();
        }
       collector.emit(new Values(application,res));*/
    }

    public void cleanup() {
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("application","check_result"));
    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
