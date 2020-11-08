package serverchatapplication;

import java.net.Socket;

/**
 * La classe contenitore per il socket e il thread di ogni client
 * @author Giovanni Ciaranfi
 */
public class Connessioni {
    private Socket socket_client;
    private Thread thread_client;
    /**
     * Il costruttore parametrizzato
     * @param s il socket
     * @param t il thread
     */
    public Connessioni(Socket s,Thread t){
        socket_client=s;
        thread_client=t;
    }
    /**
     * Il metodo che ritorna il socket
     * @return il socket del client
     */
    public Socket getSocket() {
        return socket_client;
    }
    /**
     * Il metodo che ritorna il thread
     * @return il thread del client
     */
    public Thread getThread() {
        return thread_client;
    }
}