package serverchatapplication;

/**
 *
 * @author Giovanni Ciaranfi
 */
public class ServerChatApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MultiServer ms=new MultiServer();
        Thread t=new Thread(ms);
        t.start();
        Thread cmd=new Thread(new GestioneServer(ms));
        cmd.start();
    }   
}