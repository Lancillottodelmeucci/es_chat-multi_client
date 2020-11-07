package serverchatapplication;

import java.net.Socket;

/**
 *
 * @author Giovanni Ciaranfi
 */
public class Connessioni {
    private Socket socket_client;
    private Thread thread_client;
    public Connessioni(Socket s,Thread t){
        socket_client=s;
        thread_client=t;
    }
    public Socket getSocket() {
        return socket_client;
    }
    public Thread getThread() {
        return thread_client;
    }
}