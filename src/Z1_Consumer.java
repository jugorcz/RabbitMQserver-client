import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;

public class Z1_Consumer {

    public static void main(String[] argv) throws Exception {

        // info
        System.out.println("Z1 CONSUMER");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // queue
        String QUEUE_NAME = "queue1";
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // consumer (handle msg)
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body)
                    throws IOException {

                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);

                try {
                    System.out.println("Processing...");
                    int timeToSleep = Integer.parseInt(message);
                    Thread.sleep(timeToSleep * 1000);
                    System.out.println("Done.\n");
                } catch (InterruptedException exception) {
                }

                long deliveryTag = envelope.getDeliveryTag();
                channel.basicAck(deliveryTag, false);

            }
        };

        // start listening
        System.out.println("Waiting for messages...");
        channel.basicQos(1);
        channel.basicConsume(QUEUE_NAME, false, consumer);

        // channel.basicAck(envelope.getDeliveryTag(), false);

        // don’t close while listening (consumer)
//        channel.close();
//        connection.close();
    }
}