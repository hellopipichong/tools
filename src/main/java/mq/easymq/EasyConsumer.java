package mq.easymq;

import redis.clients.jedis.Jedis;

import java.util.List;

public class EasyConsumer extends Thread{
    private Jedis jedis;
    private String queueName;

    public EasyConsumer(String ip, int port, String queueName) {
        super();
        jedis = new Jedis(ip, port);
        this.queueName = queueName;
    }

    @Override
    public void run() {
        while (true) {
            List<String> list = jedis.brpop(0, this.queueName);
            for(String s : list) {
                System.out.println(s);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        jedis.close();
    }
}
