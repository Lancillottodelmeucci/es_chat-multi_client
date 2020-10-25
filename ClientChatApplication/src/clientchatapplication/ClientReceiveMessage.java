package clientchatapplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * La classe che implemeta le funzionalità relative alla ricezione dei messaggi 
 * dal server e anche da parte di altri client
 * @author Giovanni Ciaranfi
 */
class ClientReceiveMessage implements Runnable{
    private String risposta;
    private BufferedReader dati_dal_server;
    /**
     * Costruttore parametrizzato
     * @param c il client dal quale ricevere gli oggetti necessari alla comunicazione
     */
    public ClientReceiveMessage(Client c){
        this.dati_dal_server=c.dati_dal_server;
        this.risposta=c.risposta;
    }
    /**
     * Il metodo che permette la ricezione dei messaggi dal server e dagli altri
     * client in un ciclo infinito
     */
    @Override
    public void run() {
        for(;;){
            try {
                risposta=dati_dal_server.readLine();
                if(risposta.toUpperCase().equals("FINE")){
                    return;
                }
                System.out.println(risposta);
            }
            catch (IOException e) {
                System.err.println(e.getMessage());
                System.err.println("Errore durante la comunicazione col server.");
                System.exit(0);
            }
        }
    }
}