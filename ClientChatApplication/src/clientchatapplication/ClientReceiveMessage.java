package clientchatapplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author giova
 */
class ClientReceiveMessage implements Runnable{
    Socket socket;
    BufferedReader input_tastiera;
    String messaggio;
    String risposta;
    DataOutputStream dati_al_server;
    BufferedReader dati_dal_server;
    public ClientReceiveMessage(Client c){//sostituire con qualcosa che racchiuda la roba in elenco sotto - classe
        this.messaggio=c.messaggio;
        this.input_tastiera=c.input_tastiera;
        this.dati_al_server=c.dati_al_server;
        this.dati_dal_server=c.dati_dal_server;
        this.risposta=c.risposta;
    }
    @Override
    public void run() {
        for(;;){
            try {
                //System.out.println("Inserire il messaggio da inviare al partner:");
                /*
                possibilità tramite speciale formato el messaggio di inviarlo solo ad
                un determinatopartner?
                */
                //messaggio=input_tastiera.readLine();
                //System.out.println("Invio del messaggio al server.");
                //dati_al_server.writeBytes(messaggio+'\n');
                risposta=dati_dal_server.readLine();
                if(risposta.toUpperCase().equals("FINE")){
                    //System.out.println("Chiusura dell'esecuzione.");
                    //socket.close();
                    return;
                }
                //if(risposta)
                System.out.println("R/: "+risposta);
                /*if(risposta.equals("FINE")){//chiusura bla bla bla
                    System.out.println("Chiusura dell'esecuzione.");
                    socket.close();
                }*/
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("Errore durante la comunicazione col server.");
                System.exit(1);
            }
        }
    }
    /*
    gestisce il ciclo per ricevere messaggi
    */
}