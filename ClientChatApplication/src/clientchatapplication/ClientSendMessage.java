package clientchatapplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * La classe che implemeta le funzionalità relative all'invio dei messaggi al 
 * server
 * @author Giovanni Ciaranfi
 */
class ClientSendMessage implements Runnable{
    private final BufferedReader input_tastiera;
    private String messaggio;
    private final DataOutputStream dati_al_server;
    /**
     * Costruttore parametrizzato
     * @param c il client dal quale ricevere gli oggetti necessari alla comunicazione
     */
    public ClientSendMessage(Client c){
        this.messaggio=c.messaggio;
        this.input_tastiera=c.input_tastiera;
        this.dati_al_server=c.dati_al_server;
    }
    /**
     * Il metodo che permette l'invio di messaggi al server in un ciclo infinito
     */
    @Override
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