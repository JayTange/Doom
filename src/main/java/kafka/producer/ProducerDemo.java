package kafka.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author tangj
 * @date 2018/7/29 20:15
 */
public class ProducerDemo {
    static Properties properties = new Properties();

    static String topic = "consumer";

    static KafkaProducer<String, String> producer = null;

    static {
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.0.90.53:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(properties);
    }

    public static void main(String args[]) throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(5);
        ProducerRecord<String, String> record = null;
        for (int i = 0; i < 6; i++) {
            record = new ProducerRecord<>(topic, 0, System.currentTimeMillis(), i + "", "janti" + i);
            service.submit(new ProducerThread(producer, record));
        }

    }

    public static void sendMsg() throws Exception {
        ProducerRecord<String, String> record = null;
        try {

            for (int i = 0; i < 100; i++) {

                record = new ProducerRecord<String, String>(topic, 1, System.currentTimeMillis(), i + "", "message" + i);
                producer.send(record, (metadata, e) -> {
                    if (null != e) {
                        e.printStackTrace();
                    }
                    if (null != metadata) {

                        System.out.println(String.format("offset: %s, partition:%s, topic:%s  timestamp:%s", metadata.offset(), metadata.partition(), metadata.topic(), metadata.timestamp()));
                    }
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}