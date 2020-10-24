package serverchatapplication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * La classe che permette a più client di connettersi alla chat
 * @author Giovanni Ciaranfi
 */
public class MultiServer {
    ServerSocket server_socket;
    ArrayList<Socket> client_disponibili=new ArrayList();
    /**
     * Il metodo che apre la porta sulla quale attivare il servizio della chat, 
     * e crea il ciclo per ricevere la connessione da parte di più client
     */
    public void avvia(){
        try {
            server_socket=new ServerSocket(7777);
            for(;;){
                System.out.println("Nuovo thread in attesadi un client.");
                Socket client_socket=server_socket.accept();
                client_disponibili.add(client_socket);
                ServerChat server_thread=new ServerChat(client_socket,client_disponibili);
                Thread t=new Thread(server_thread);
                t.start();
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Errore nell'istanza del server.");
            System.exit(0);
        }
        try {
            server_socket.close();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Errore nella chiusura del server.");
            System.exit(0);
        }
    }
}