package serverchatapplication;

/**
 *
 * @author giova
 */
public class ServerChatApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("serverchatapplication.ServerChatApplication.main()");
        MultiServer multi_server=new MultiServer();
        multi_server.avvia();
    }   
}