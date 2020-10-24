package clientchatapplication;

/**
 *
 * @author giova
 */
public class ClientChatApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Client c=new Client();
        c.connetti();
        c.comunica();
    }
}