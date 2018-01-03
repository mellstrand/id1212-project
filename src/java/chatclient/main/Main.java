package chatclient.main;

import chatclient.view.ViewControl;
import chatclient.model.JSMClient;
import java.awt.EventQueue;

/**
 *
 * @author Tobias Mellstrand
 * @date 2017-12-29
 */
public class Main {

    public static void main(String[] args) {
        
        EventQueue.invokeLater(() -> {
            new ViewControl(new JSMClient()).setVisible(true);
        });
    }
    
}
