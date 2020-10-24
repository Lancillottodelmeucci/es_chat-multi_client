package clientchatapplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author giova
 */
public class Client {
    String nome_server="127.0.0.1";
    int porta_server=7777;
    Socket socket;
    BufferedReader input_tastiera;
    String messaggio;
    String risposta;
    DataOutputStream dati_al_server;
    BufferedReader dati_dal_server;
    ClientSendMessage c_s_m;
    ClientReceiveMessage c_r_m;
    public Socket connetti(){
        System.out.println("Client in esecuzione.");
        try {
            input_tastiera=new BufferedReader(new InputStreamReader(System.in));
            socket=new Socket(nome_server,porta_server);
            dati_al_server=new DataOutputStream(socket.getOutputStream());
            dati_dal_server=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Inserire nome utente a scelta:");
            messaggio=input_tastiera.readLine();
            dati_al_server.writeBytes(messaggio+'\n');
        }
        catch(UnknownHostException e){
            System.err.println("Host non riconosciuto.");
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Errore durante la connessione.");
            System.exit(0);
        }
        return(socket);
    }
    public void comunica(){
        c_s_m=new ClientSendMessage(this);
        c_r_m=new ClientReceiveMessage(this);
        Thread t_s=new Thread(c_s_m);
        Thread t_r=new Thread(c_r_m);
        t_r.start();
        t_s.start();
    }
}