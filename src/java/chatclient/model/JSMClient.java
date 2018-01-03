package chatclient.model;

import chatclient.view.ViewControl;
import java.util.Date;
import java.util.StringJoiner;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Tobias Mellstrand
 * @date 2017-12-29
 */
public class JSMClient implements ExceptionListener {
    
    private ViewControl viewControl;
    private TopicConnection topicConnection = null;
    private TopicPublisher topicPublisher = null;
    private TopicSession topicSession = null;
    private TopicSubscriber topicSubscriber = null;
    private TopicListener listener = null;
    
    
    public JSMClient() {
    }
    
    public void openConnection(ViewControl viewControl, String username) throws JMSException, NamingException {
        this.viewControl = viewControl;
        this.listener = new TopicListener(this);
        
        Context ctx = new InitialContext();
        TopicConnectionFactory tcf = (TopicConnectionFactory) ctx.lookup("chatConnectionFactory");
        topicConnection = tcf.createTopicConnection();
        topicConnection.setClientID(username);
        topicSession = topicConnection.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
        Topic topic = (Topic) ctx.lookup("chatTopic");
        topicPublisher = topicSession.createPublisher(topic);
        topicSubscriber = topicSession.createDurableSubscriber(topic, username);
        topicSubscriber.setMessageListener(listener);
        topicConnection.setExceptionListener(this);
        topicConnection.start();
        
        sendMessageToTopic("SERVER", username + " has entered the room!");
    }
    
    public void closeConnection(String username) throws JMSException {
        sendMessageToTopic("SERVER", username + " is leaving the room!");
        topicSubscriber.close();
        topicConnection.close();
    }
    
    public void closeAndUnsubscribeConnection(String username) throws JMSException {
        sendMessageToTopic("SERVER", username + " is leaving the room!");
        topicSubscriber.close();
        topicSession.unsubscribe(username);
        topicConnection.close();
    }

    public void sendMessageToTopic(String username, String message) throws JMSException {
        TextMessage tx = topicSession.createTextMessage();
        tx.setText(username + ": " + message);
        topicPublisher.send(tx);
    }
    
    public void sendMessageToView(String message) {
        viewControl.updateChatContent(message);
    }

    @Override
    public void onException(JMSException e) {
        StringJoiner errorMessage = new StringJoiner("\n");
        errorMessage.add(new Date() + "Exception received");
        errorMessage.add("** Error Code: " + e.getErrorCode());
        errorMessage.add("** Error Message: " + e.getMessage());
        System.out.println(errorMessage.toString());
    }

}
