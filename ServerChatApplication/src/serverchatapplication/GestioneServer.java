package serverchatapplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * La classe che gestisce i comandi da eseguire sul server
 * @author Giovanni Ciaranfi
 */
public class GestioneServer implements Runnable{
    private MultiServer multi_server;
    private final ServerSocket server_socket;
    private ArrayList<Socket> client_disponibili;
    private ArrayList<Thread> thread_in_esecuzione;
    private BufferedReader input_tastiera;
    private String comando;
    private String[] comandi=new String[2];
    /**
     * Costruttore parametrizzato della classe
     * @param ms la classe che contiene le variabili per il multiserver
     */
    public GestioneServer(MultiServer ms){
        server_socket=ms.getServerSocket();
        client_disponibili=ms.getSockets();
        thread_in_esecuzione=ms.getThreads();
        input_tastiera=new BufferedReader(new InputStreamReader(System.in));
        multi_server=ms;
    }
    /**
     * Il metodo che gestisce gli input e i metodi dei comandi
     */
    @Override
    public void run(){
        for(;;){
            try {
                comando=input_tastiera.readLine();
                if(comando.split("-").length>1){
                    comandi=comando.split("-");
                }
                else{
                    comandi[0]=comando;
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.err.println("Errore durante la lettura dei comandi.");
                System.exit(0);
            }
            switch(comandi[0].toLowerCase()){
                case "list":
                    mostraPartecipanti();
                    break;
                case "close":
                    if(multi_server.getServerSocket().isClosed()){
                        System.out.println("Server gia' chiuso alle nuove connessioni.");
                        break;
                    }
                    try {
                        multi_server.getServerSocket().close();
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        System.err.println("Errore nella chiusura del server.");
                        System.exit(0);
                    }
                    break;
                case "open":
                    if(!multi_server.getServerSocket().isClosed()){
                        System.out.println("Server gia' aperto alle nuove connessioni.");
                        break;
                    }
                    Thread t=new Thread(multi_server);
                    t.start(); 
                    break;
                case  "send":
                    inviaMessaggio();
                    break;
                case "exit":
                    uscitaEDisconnessione();
                    break;
                case "--exit":
                    System.out.println("Chiusura del server.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Comando non riconosciuto.");
                    break;
            }
        }
    }
    /**
     * Il metodo che mostra la lista dei aprtecipanti alla chat
     */
    private void mostraPartecipanti(){
        if(thread_in_esecuzione.size()<1){
            System.out.println("Nessun client connesso.");
            return;
        }
        for(int i=0;i<thread_in_esecuzione.size();i++){
            System.out.print(i+". "+thread_in_esecuzione.get(i).getName()+" >> ");
            System.out.println(client_disponibili.get(i).getInetAddress().getHostAddress()+":"+client_disponibili.get(i).getPort());
        }
    }
    /**
     * Il metodo che sconnette tutti i client connessi e poi chiude il server
     */
    private void uscitaEDisconnessione(){
        System.out.println("Chiusura del server e delle connessioni.");
        client_disponibili.forEach((client) -> {
            try {
                DataOutputStream dati_al_client=new DataOutputStream(client.getOutputStream());
                dati_al_client.writeBytes("Il server e' stato chiuso.\n");
                dati_al_client.writeBytes("FINE\n");
                client.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.err.println("Errore durante la comunicazione con i client.");
                System.exit(0);
            }
        });
        multi_server.chiudi();
        System.exit(0);
    }
    /**
     * Il metodo che invia un messaggio a tutti gli utenti della chat, da parte 
     * del server
     */
    private void inviaMessaggio(){
        client_disponibili.forEach((client) -> {
            try {
                DataOutputStream dati_al_client=new DataOutputStream(client.getOutputStream());
                dati_al_client.writeBytes(comandi[1]+"\n");
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.err.println("Errore durante la comunicazione con i client.");
                System.exit(0);
            }
        });
    }
}