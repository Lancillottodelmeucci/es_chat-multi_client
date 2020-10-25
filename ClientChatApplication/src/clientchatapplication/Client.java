package clientchatapplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * La classe che gestisce i thread per la comunicazione col server e che 
 * istanzia il socket e i canali di comunicazione
 * @author Giovanni Ciaranfi
 */
public class Client {
    private String nome_server="127.0.0.1";
    private int porta_server=7777;
    private Socket socket;
    public BufferedReader input_tastiera;
    public String messaggio;
    public String risposta;
    public DataOutputStream dati_al_server;
    public BufferedReader dati_dal_server;
    private ClientSendMessage c_s_m;
    private ClientReceiveMessage c_r_m;
    /**
     * Il metodo che permette al client di connettersi al server che deve essere
     * in attesa di connessioni
     * @return il socket del client
     */
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
            System.err.println(e.getMessage());
            System.err.println("Host non riconosciuto.");
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Errore durante la connessione.");
            System.exit(0);
        }
        return(socket);
    }
    /**
     * Il metodo che lancia i thread per l'invio e la ricezione dei messaggi
     */
    public void comunica(){
        c_s_m=new ClientSendMessage(this);
        c_r_m=new ClientReceiveMessage(this);
        Thread t_s=new Thread(c_s_m);
        Thread t_r=new Thread(c_r_m);
        t_r.start();
        t_s.start();
    }
}