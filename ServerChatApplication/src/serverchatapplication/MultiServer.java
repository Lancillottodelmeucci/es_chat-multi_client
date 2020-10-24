package serverchatapplication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author giova
 */
public class MultiServer {
    ServerSocket server_socket;
    ArrayList<Socket> client_disponibili=new ArrayList();
    public void avvia(){
        try {
            server_socket=new ServerSocket(7777);
            for(int i=0;i<2;i++){//;;
                System.out.println("Nuovo thread in attesa.");
                Socket client_socket=server_socket.accept();
                client_disponibili.add(client_socket);
                ServerChat server_thread=new ServerChat(client_socket,client_disponibili);
                Thread t=new Thread(server_thread);
                t.start();
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Errore nell'istanza del server.");
            System.exit(1);
        }
        try {
            server_socket.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Errore nella chiusura del server.");
            System.exit(1);
        }
    }
}