package clientchatapplication;

import java.awt.Color;
import static java.awt.Component.*;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 * La classe che implemeta le funzionalità relative alla ricezione dei messaggi 
 * dal server e anche da parte di altri client
 * @author Giovanni Ciaranfi
 */
class ClientReceiveMessage implements Runnable{
    private String risposta;
    private BufferedReader dati_dal_server;
    private JPanel chat;
    /**
     * Costruttore parametrizzato
     * @param c il client dal quale ricevere gli oggetti necessari alla comunicazione
     */
    public ClientReceiveMessage(BufferedReader br,JPanel p){
        dati_dal_server=br;
        chat=p;
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
                    System.exit(0);
                }
                visualizzaMessaggio(risposta);
            }
            catch (IOException e) {
                System.err.println(e.getMessage());
                System.err.println("Errore durante la comunicazione col server.");
                System.exit(0);
            }
        }
    }
    public void visualizzaMessaggio(String m){
        JLabel l=new JLabel(m);
        l.setPreferredSize(new Dimension(500, 30));
        l.setHorizontalAlignment(2);
                l.setBorder(new LineBorder(Color.WHITE));
        chat.add(l);
        chat.setPreferredSize(new Dimension(500, chat.getHeight()+30));
    }
}