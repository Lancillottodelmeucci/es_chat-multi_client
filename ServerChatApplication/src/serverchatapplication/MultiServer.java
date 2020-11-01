package serverchatapplication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * La classe che permette a più client di connettersi alla chat
 * @author Giovanni Ciaranfi
 */
public class MultiServer implements Runnable{
    private ServerSocket server_socket=null;
    private ArrayList<Socket> client_disponibili=new ArrayList();
    private ArrayList<Thread> thread_in_esecuzione=new ArrayList();
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
     * e crea il ciclo per ricevere la connessione da parte di più client
     */
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
            System.out.println("Nuovo thread in attesa di un client.");
            Socket client_socket;
            try {
                client_socket=server_socket.accept();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                client_socket=null;
                break;
            }
            client_disponibili.add(client_socket);
            Thread t;
            ServerChat server_thread=new ServerChat(client_socket,client_disponibili,thread_in_esecuzione);
            t=new Thread(server_thread);
            thread_in_esecuzione.add(t);
            t.start();
        }
    }
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
    public ServerSocket getServerSocket(){
        return (server_socket);
    }
    public ArrayList<Thread> getThreads(){
        return (thread_in_esecuzione);
    }
    public ArrayList<Socket> getSockets(){
        return (client_disponibili);
    }
}