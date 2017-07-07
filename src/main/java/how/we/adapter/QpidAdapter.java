package how.we.adapter;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.json.JsonObject;

import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;

import java.io.File;

public class QpidAdapter {

    public static void receive(JsonObject info) {
        if (info == null) {
            System.out.println("error, null subscribe info");
            return;
        }
        String ip = info.getString("BROKER_IP", null);
        String port = Integer.toString(info.getInt("BROKER_PORT", 4703));
        String userId = info.getString("APPID", null);
        String queueId = info.getString("QUEUE_ID", null);
        if (ip == null || port == null || userId == null || queueId == null) {
            System.err.println("invalid subscribe info: " + info.toString());
            return;
        }
        System.out.println("amqp started:" + "userId:" + userId + ",queueId:" + queueId + ",ip:" + ip + ",port:" + port);

        String path = System.getProperty("user.dir");
        System.setProperty("javax.net.ssl.keyStore", path + File.separator + "user.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "importkey");
        System.setProperty("javax.net.ssl.trustStore", path + File.separator + "root.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "importkey");

        // 地址变量  
        String brokerOpts = "?brokerlist='tcp://"+ip+":"+port+"?ssl='true'&ssl_verify_hostname='false''";
        String connectionString = "amqp://"+userId+":"+"xxxx@xxxx/"+brokerOpts;
        System.out.println("connection string:" + connectionString);
        // 建立连接
        AMQConnection conn = null;
        try {
            conn = new AMQConnection(connectionString);
            conn.start();
            // 获取session
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // 获取队列
            Destination queue = new AMQAnyDestination("ADDR:"+queueId+";{create:sender}");
            MessageConsumer consumer = session.createConsumer(queue);

            while(true) {
                Message m = consumer.receive(60000);
                // message为空的情况,
                if(m == null){
                    System.out.println("Get NULL message, pause for 1 miniute!");
                    //                    sleep(60000);
                    continue;
                }
                // message格式是否正确
                if(m instanceof BytesMessage) {
                    BytesMessage tm = (BytesMessage)m;
                    int length = new Long(tm.getBodyLength()).intValue();
                    if(length > 0){
                        byte[] keyBytes = new byte[length];
                        tm.readBytes(keyBytes);
                        String messages = new String(keyBytes);
                        System.out.println(messages);
                    }else{
                        System.out.println("Get zero length message");
                    }
                }else{
                    System.out.println("Message is not in Byte format!");
                }
            }
        } catch (Exception e){
            System.out.println(e);
        } finally{
            try {
                if(conn != null)
                {
                    conn.close();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println("[AMQP]No data from SVA,connection closed!");
        }
    }
}
