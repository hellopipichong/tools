package mq.easymq;

import redis.clients.jedis.Jedis;

public class EasyProducer {
    private Jedis jedis;
    private String queueName;

    public EasyProducer(String ip, int port, String queueName) {
        jedis = new Jedis(ip, port);
        this.queueName = queueName;
    }

    public void produce(String value) {
        jedis.lpush(queueName, value);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        jedis.close();
    }
}
