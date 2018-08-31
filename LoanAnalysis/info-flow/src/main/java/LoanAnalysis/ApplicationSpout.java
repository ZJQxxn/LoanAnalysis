package LoanAnalysis;

import com.csvreader.CsvReader;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 *  Spout to inject loan application.
 */
public class ApplicationSpout implements IRichSpout {

    private SpoutOutputCollector collector;
    private TopologyContext context;
    private static Integer index=0;

    private  static String join(String[] list,String delimeter){
        //TODO:Util funcion
        String res=list[0];
        for(int i=1;i<list.length;i++){
            res+=(delimeter+list[i]);
        }
        return res;
    }

    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.context=topologyContext;
        this.collector=spoutOutputCollector;
    }

    public void close() {

    }

    public void activate() {

    }

    public void deactivate() {

    }

    public void nextTuple() {
        try{
            //TODO:File path.
            CsvReader reader=new CsvReader("resources/raw_data/application_test.csv",',');
            reader.readHeaders();
            String[] headers=reader.getHeaders();
            HashMap<String,String> user=new HashMap<String, String>();
            while(reader.readRecord()){
                //TODO: Care about the 'break'
                if(++index>3){
                    break;
                }
                for(String key:headers){
                    user.put(key,reader.get(key).equals("")?"null":reader.get(key));
                }
                collector.emit(new Values(user.clone(),join(headers,",")));
                user.clear();
                Utils.sleep(1000);
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void ack(Object o) {
    }

    public void fail(Object o) {

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("application","headers"));
    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
