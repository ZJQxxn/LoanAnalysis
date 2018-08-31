package LoanAnalysis;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;

public class App {
    //TODO: Annotations
    public static void main(String[] args) throws Exception{
        Config conf=new Config();
        conf.setDebug(false);
        //Config topology
        TopologyBuilder builder=new TopologyBuilder();
        builder.setSpout("application",new ApplicationSpout());
        builder.setBolt("check",new CheckBolt()).shuffleGrouping("application");
        //builder.setBolt("save",new SaveBolt()).shuffleGrouping("check");

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("LoanAnalysisTest", conf, builder.createTopology());

        Thread.sleep(10000);
        cluster.killTopology("LoanAnalysisTest");
        cluster.shutdown();
    }
}
