package mq.easymq;

public class EasyMqTest {

    public static void main(String[] args) throws InterruptedException {
        EasyConsumer consumer = new EasyConsumer("127.0.0.1", 6379, "message_queue");
        consumer.start();

        EasyProducer producer = new EasyProducer("127.0.0.1", 6379, "message_queue");
        producer.produce("andy");
        producer.produce("jerry");
        producer.produce("forest");

        Thread.currentThread().join(1000000);
    }
}
