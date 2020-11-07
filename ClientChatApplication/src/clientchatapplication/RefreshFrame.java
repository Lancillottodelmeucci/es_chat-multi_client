package clientchatapplication;

import javax.swing.*;

/**
 *
 * @author giova
 */
public class RefreshFrame implements Runnable{
    private JPanel chat;
    public RefreshFrame(JPanel c){
        chat=c;
    }
    public void run(){
        for(;;){
//            try {
//                wait(500);
//            } catch (InterruptedException e) {
//                System.err.println(e.getMessage());
//                //System.err.println("Errore durante la comunicazione.");
//                System.exit(0);
//            }
//            frame.setVisible(false);
//            frame.setVisible(true);
            SwingUtilities.updateComponentTreeUI(chat);
        }
    }
}
