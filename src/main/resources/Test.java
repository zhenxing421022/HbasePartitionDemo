import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Test {
    public static Configuration getConfiguration() throws LoginException {
        //System.setProperty("hadoop.home.dir", "E:\\develop\\hadoop-common-2.2.0-bin-master");
        Configuration conf = HBaseConfiguration.create();
        //conf.addResource("hbase-site-hw.xml");
    /*	Configuration conf = new Configuration();
		conf.set("hbase.zookeeper.quorum","shyp-bigdata-b-cn01,shyp-bigdata-b-cn04,shyp-bigdata-b-cn02,shyp-bigdata-b-cn05,shyp-bigdata-b-cn03");
		conf.set("hbase.zookeeper.property.clientPort","24002");
		conf.set("hbase.security.authentication", "kerberos");
		conf.set("zookeeper.znode.parent", "/hbase");
		conf.set("hbase.rootdir", "hdfs://hacluster/hbase");
		conf.set("hbase.regionserver.kerberos.principal", "hbase/hadoop.hadoop_b.com@HADOOP_B.COM");*/
        if (User.isHBaseSecurityEnabled(conf)) {
            String confDirPath = System.getProperty("user.dir") + "/src/";
            System.err.println("-----" + confDirPath);
            // set zookeeper server pricipal
            System.setProperty("zookeeper.sasl.clientconfig", "client");
            System.setProperty("zookeeper.server.principal", "zookeeper/hadoop.hadoop_b.com");
            // jaas.conf file, it is included in the client pakcage file
            System.setProperty("java.security.auth.login.config", confDirPath + "jaas.conf");
            // set the kerberos server info,point to the kerberosclient
            // configuration file.
            System.setProperty("java.security.krb5.conf", confDirPath + "krb5.conf");
            // set "user.keytab" as the download keytab file name, 注[1]
            conf.set("username.client.keytab.file", confDirPath + "user.keytab");
            // set "hbaseuser1" as the new create user name
            conf.set("username.client.kerberos.principal", "wg_B@HADOOP_B.COM");

            try {
                User.login(conf, "username.client.keytab.file", "username.client.kerberos.principal", "xx");
                ZKUtil.loginClient(conf, "username.client.keytab.file", "username.client.kerberos.principal", "xx");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return conf;
    }

    public static void main(String[] args) throws IOException, LoginException {
        System.out.println();
        HConnection connection = HConnectionManager.createConnection(getConfiguration());
        System.err.println(Arrays.asList(connection.getTableNames()));
    }
}
