package chatclient.view;

import chatclient.model.JSMClient;
import java.util.Calendar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.jms.InvalidClientIDException;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 *
 * @author Tobias Mellstrand
 * @date 2017-12-29
 */
public class ViewControl extends JFrame implements ActionListener {
    
    private static final String START = "Start";
    private static final String CLOSE = "Close";
    private static final String CLOSEUNSUB ="Unsubscribe and Close";
    private static final String SEND = "Send";
    private final JSMClient jsmClient;
    private JButton closeButton;
    private JButton closeUnsubButton;
    private JButton sendButton;
    private JButton startButton;
    private JLabel messageLabel;
    private JLabel nameLabel;
    private JScrollPane scrollPane;
    private JTextArea chatContent;
    private JTextField nameField;
    private JTextField messageField;
    private String username;
    
    
    public ViewControl(JSMClient jsmClient) {
        this.jsmClient = jsmClient;
        initComponents();
    }
    
    private void initComponents() {
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat window");
        
        messageLabel = new JLabel();
        messageLabel.setText("Message:");
        nameLabel = new JLabel();
        nameLabel.setText("Name:");
        
        closeButton = new JButton();
        closeButton.setText(CLOSE);
        closeButton.addActionListener(this);
        closeButton.setActionCommand(CLOSE);
        closeButton.setVisible(false);
        closeUnsubButton = new JButton();
        closeUnsubButton.setText(CLOSEUNSUB);
        closeUnsubButton.addActionListener(this);
        closeUnsubButton.setActionCommand(CLOSEUNSUB);
        closeUnsubButton.setVisible(false);
        sendButton = new JButton();
        sendButton.setText(SEND);
        sendButton.addActionListener(this);
        sendButton.setActionCommand(SEND);
        sendButton.setVisible(false);
        startButton = new JButton();
        startButton.setText(START);
        startButton.addActionListener(this);
        startButton.setActionCommand(START);
        
        nameField = new JTextField();
        messageField = new JTextField();
        messageField.setEnabled(false);
        chatContent = new JTextArea();
        chatContent.setColumns(20);
        chatContent.setRows(5);
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(chatContent);
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(nameLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(startButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(closeButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(closeUnsubButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sendButton))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(messageLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(messageField, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(messageLabel)
                    .addComponent(messageField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sendButton)
                    .addComponent(startButton)
                    .addComponent(closeButton)
                    .addComponent(closeUnsubButton))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();
        
    }
    
    public void initOpenChatComponents() {
        this.startButton.setVisible(false);
        this.closeButton.setVisible(true);
        this.closeUnsubButton.setVisible(true);
        this.sendButton.setVisible(true);
        this.nameField.setEditable(false);
        this.chatContent.setEnabled(false);
        this.messageField.setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        switch (ae.getActionCommand()) {
            case START:
                startChat();
                break;
            case CLOSE:
                closeChat();
                break;
            case CLOSEUNSUB:
                closeAndUnsubscribeChat();
                break;
            case SEND:
                sendMessage(ae);
                break;
            default:
                break;
        }
        
    }
    
    private void startChat() {
        try {
            this.username = nameField.getText();
            jsmClient.openConnection(this, username);
            initOpenChatComponents();
        } catch(InvalidClientIDException icide) {
            showErrorMessage("Please type valid name", icide.getMessage());
        } catch(NamingException ne) {
            showErrorMessage("Could not find chat room", ne.getResolvedName().toString());
        } catch(JMSException ex) {
            showErrorMessage("General open connection error", ex.getErrorCode() + " - " + ex.getMessage());
        }
    }
    
    private void closeChat() {
        try {
            jsmClient.closeConnection(username);
            this.dispose();
        } catch(Exception e) {
            showErrorMessage("Closing connection failed: ", e.getMessage());
        }
    }
    
    private void closeAndUnsubscribeChat() {
        try {
            jsmClient.closeAndUnsubscribeConnection(username);
            this.dispose();
        } catch(Exception e) {
            showErrorMessage("Closing connection failed: ", e.getMessage());
        }
    }
    
    private void sendMessage(ActionEvent ae) {
        String message = messageField.getText();
        if(message != null) {
            try {
                jsmClient.sendMessageToTopic(username, messageField.getText() + "\t" + getTimeString(ae.getWhen()));
                this.messageField.setText("");
            } catch(Exception e) {
                showErrorMessage("Sending message failed: ", e.getMessage());
            }
        } else {
            showErrorMessage("Please type message", "Empty field");
        }
    }
    
    private String getTimeString(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM HH:mm");
        Calendar date = Calendar.getInstance(new Locale("sv", "SV"));
        date.setTimeInMillis(timeStamp);
        return "(" + sdf.format(date.getTime()) + ")";
    }
    
    public void updateChatContent(String message) {
        String content = chatContent.getText();
        content += "\n" + message;
        this.chatContent.setText(content);
    }

    private void showErrorMessage(String error, String message) {
        JOptionPane.showMessageDialog(null, message + "\n" + error, "Error", ERROR_MESSAGE);
    }
    
}
