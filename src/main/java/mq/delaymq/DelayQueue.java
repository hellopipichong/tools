package mq.delaymq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;

public class DelayQueue<T> {

    static class TaskItem<T> {
        public String id;
        public T msg;
    }

    private Type taskType = new TypeReference<TaskItem<T>>() {
    }.getType();

    private Jedis jedis;
    private String queueName;

    public DelayQueue(Jedis jedis, String queueName) {
        this.jedis = jedis;
        this.queueName = queueName;
    }

    public void delay(T msg, long delayTime) {
        TaskItem<T> task = new TaskItem<>();
        task.id = UUID.randomUUID().toString();
        task.msg = msg;
        jedis.zadd(queueName, System.currentTimeMillis() + delayTime, JSON.toJSONString(task));
    }

    public void loop() {
        while (!Thread.interrupted()) {
            Set<String> set = jedis.zrangeByScore(queueName, 0, System.currentTimeMillis(), 0, 1);
            if (set.isEmpty()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }

            String s = set.iterator().next();
            if (jedis.zrem(queueName, s) > 0) {
                TaskItem<T> task = JSON.parseObject(s, taskType);
                System.out.println(task.msg);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        DelayQueue<String> delayQueue = new DelayQueue<>(jedis, "delay_queue");

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 10; i++)
                delayQueue.delay("Mr.Wang's Hub" + i, 5000);
        });

        Thread consumer = new Thread(() -> {
            delayQueue.loop();
        });

        consumer.start();
        producer.start();
        Thread.currentThread().join(100000000);
    }

}
