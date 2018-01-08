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
 * 
 * Handles the JMS MessageQueue events
 * Also prints messages to clients when clients connects/disconnects
 * from the topic, i.e. the "chat room"
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
    
    /**
     * Open a connection to the predefined topic and sets listeners
     * Creates a durable subscription to the topic
     * @param viewControl Reference to GUI
     * @param username The clients name
     * @throws JMSException Internal errors of JSM
     * @throws NamingException Error for lookup of Topic or Factory
     */
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
    
    /**
     * Close session and close the connection to the factory
     * @param username
     * @throws JMSException 
     */
    public void closeConnection(String username) throws JMSException {
        sendMessageToTopic("SERVER", username + " is leaving the room!");
        topicSubscriber.close();
        topicConnection.close();
    }
    
    /**
     * Close session and close the connection to the factory
     * Unsubscribe the user from the topic
     * @param username
     * @throws JMSException 
     */
    public void closeAndUnsubscribeConnection(String username) throws JMSException {
        sendMessageToTopic("SERVER", username + " is leaving the room!");
        topicSubscriber.close();
        topicSession.unsubscribe(username);
        topicConnection.close();
    }

    /**
     * Send message to the topic
     * @param username
     * @param message
     * @throws JMSException 
     */
    public void sendMessageToTopic(String username, String message) throws JMSException {
        TextMessage tx = topicSession.createTextMessage();
        tx.setText(username + ": " + message);
        topicPublisher.send(tx);
    }
    
    /**
     * Send message to the view handler
     * @param message 
     */
    public void sendMessageToView(String message) {
        viewControl.updateChatContent(message);
    }

    /**
     * ExceptionListener on the 'topicConnection' set in openConnection() 
     * @param e 
     */
    @Override
    public void onException(JMSException e) {
        StringJoiner errorMessage = new StringJoiner("\n");
        errorMessage.add(new Date() + "Exception received");
        errorMessage.add("** Error Code: " + e.getErrorCode());
        errorMessage.add("** Error Message: " + e.getMessage());
        System.out.println(errorMessage.toString());
    }

}
