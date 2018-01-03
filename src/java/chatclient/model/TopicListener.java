package chatclient.model;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * @author Tobias Mellstrand
 * @date 2017-12-29
 */
public class TopicListener implements MessageListener {
    
    private JSMClient jsmClient = null;

    public TopicListener(JSMClient jsmClient) {
        this.jsmClient = jsmClient;
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage tm = (TextMessage) message;    
            jsmClient.sendMessageToView(tm.getText());
        } catch (JMSException jmse) {
            System.err.println("Failed to receive message: " + jmse);
        }
    }
    
}
