package clientchatapplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author giova
 */
class ClientSendMessage implements Runnable{
    Socket socket;
    BufferedReader input_tastiera;
    String messaggio;
    String risposta;
    DataOutputStream dati_al_server;
    BufferedReader dati_dal_server;
    public ClientSendMessage(Client c){
        this.messaggio=c.messaggio;
        this.input_tastiera=c.input_tastiera;
        this.dati_al_server=c.dati_al_server;
        this.dati_dal_server=c.dati_dal_server;
        this.risposta=c.risposta;
    }
    public void run() {
        for(;;){
            try {
                System.out.println("Inserire il messaggio da inviare al partner:");
                messaggio=input_tastiera.readLine();
                dati_al_server.writeBytes(messaggio+'\n');
                if(messaggio.toUpperCase().equals("FINE")){
                    System.out.println("Chiusura dell'esecuzione.");
                    return;
                }
            }
            catch (IOException e) {
                System.err.println(e.getMessage());
                System.err.println("Errore durante la comunicazione col server.");
                System.exit(0);
            }
        }
    }
}