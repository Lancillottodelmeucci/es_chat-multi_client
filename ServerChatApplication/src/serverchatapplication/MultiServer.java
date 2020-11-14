package serverchatapplication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * La classe che permette a più client di connettersi alla chat
 * @author Giovanni Ciaranfi
 */
public class MultiServer implements Runnable{
    private ServerSocket server_socket=null;
    //private ArrayList<Socket> client_disponibili=new ArrayList();//da rimuovere
    //private ArrayList<Thread> thread_in_esecuzione=new ArrayList();//da rimuovere
    private HashMap<String,Connessioni> utenti_connessi=new HashMap();
    /**
     * Il costruttore della classe che apre il socket del server
     */
    public MultiServer(){
        try {
            server_socket=new ServerSocket(7777);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Errore nell'istanza del server.");
            System.exit(0);
        }
    }
    /**
     * Il metodo che apre la porta sulla quale attivare il servizio della chat, 
     * qual'ora fosse chiusa, e crea il ciclo per ricevere la connessione da 
     * parte di più client
     */
    @Override
    public void run(){
        if(server_socket.isClosed()){
            try {
                server_socket=new ServerSocket(7777);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.err.println("Errore nell'istanza del server.");
                System.exit(0);
            }
        }
        for(;;){
            System.out.println("Server in attesa di un nuovo client.");//Server in attesa di un nuovo client
            Socket client_socket;
            try {
                client_socket=server_socket.accept();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                client_socket=null;
                break;
            }
            Thread t;
            ServerChat server_thread=new ServerChat(client_socket,utenti_connessi);
            t=new Thread(server_thread);
            //Connessioni c=new Connessioni(client_socket, t);//da commentare
            t.start();
        }
    }
    /**
     * Il metodo che chiude il socket del server
     */
    public void chiudi(){
        try {
            server_socket.close();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Errore nella chiusura del server.");
            System.exit(0);
        }
    }
    /**
     * 
     * @return il socket del server
     */
    public ServerSocket getServerSocket(){
        return (server_socket);
    }
    public HashMap<String,Connessioni> getUtentiConnessi(){
        return (utenti_connessi);
    }
}