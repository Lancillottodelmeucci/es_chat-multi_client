package clientchatapplication;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.*;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

/**
 * La classe che implemeta le funzionalità relative alla ricezione dei messaggi 
 * dal server e anche da parte di altri client
 * @author Giovanni Ciaranfi
 */
class ClientReceiveMessage implements Runnable{
    //private String rispostaObsoleta;
    //private Messaggio risposta;
    private BufferedReader dati_dal_server;
    private Messaggio messaggio;
    private JList<String> membri;
    DefaultListModel<String> dlm;
    private JTabbedPane multiChat;
    private HashMap<String,JPanel> pannelliChat;
    public String nome;
    /**
     * Costruttore parametrizzato
     * @param br il canale per la recezione dei messaggi
     * @param d la lista degli utenti connessi
     * @param t l'insieme dei pannelli delle chat
     * @param h la lista delle chat su cui aggiungere i messaggi
     * @param n il nome del client
     */
    public ClientReceiveMessage(BufferedReader br,DefaultListModel<String> d,JTabbedPane t,HashMap<String,JPanel> h,String n){
        dati_dal_server=br;
        dlm=d;
        multiChat=t;
        pannelliChat=h;
        nome=n;
    }
    /**
     * Il metodo che permette la ricezione dei messaggi dal server e dagli altri
     * client in un ciclo infinito
     */
    @Override
    public void run() {
        for(;;){
            try {
                //risposta=dati_dal_server.readLine();
                messaggio=Messaggio.reBuild(dati_dal_server.readLine());
                if(messaggio.testo.toUpperCase().equals("FINE")){
                    System.exit(0);
                }
                visualizzaMessaggio(messaggio);
            }
            catch (IOException e) {
                System.err.println(e.getMessage());
                System.err.println("Errore durante la comunicazione col server.");
                System.exit(0);
            }
        }
    }
    /**
     * Il metodo che permette di visualizzare il messaggio ricevuto e gestisce 
     * la lista dei client connessi
     * @param m il messaggio ricevuto
     */
    public void visualizzaMessaggio(Messaggio m){
        JLabel l;
        switch(m.tipo){
            case Messaggio.DISCONNESSIONE:
                l=new JLabel("<html>"+m.mittente+" si e' disconnesso</html>");
                dlm.removeElement(m.mittente);
                break;
            case Messaggio.CONNESSIONE:
                l=new JLabel("<html>"+m.mittente+" si e' connesso.</html>");
                dlm.addElement(m.mittente);
                break;
            default:
                if(m.destinatario.equals(nome)||m.destinatario.equals("Invia a tutti")){
                    l=new JLabel("<html>"+m.testo+"</html>");
                }
                else{
                    l=new JLabel("<html>"+(m.mittente.equals("")?"":"Da "+m.mittente+": ")+m.testo+"</html>");
                }
                break;
        }
        l.setPreferredSize(new Dimension(625, 25));
        l.setHorizontalAlignment(2);
        JPanel appo;
        if(m.destinatario.equals(nome)||m.destinatario.equals("Invia a tutti")){
            if(!pannelliChat.containsKey(m.mittente)){
                creaChatPrivata(m.mittente);
            }
            appo=pannelliChat.get(m.mittente);
        }
        else if(m.mittente==null||m.mittente.equals("")){
            appo=pannelliChat.get("mainGroupChat");
        }
        else{
            appo=pannelliChat.get("mainGroupChat");
        }
        /*
        se il messaggio arriva a una chat diversa da quella in cui sono cambiare il suo colore di sfondo
        e rimetterlo al suo default quando ci clicco sopra
        */
        appo.add(l);
        appo.setPreferredSize(new Dimension(500, appo.getHeight()+25));
        SwingUtilities.updateComponentTreeUI(appo);
    }
    /**
     * Il metodo che crea il pannello di una nuova chat privata
     * @param u il nome dell'utente
     */
    public void creaChatPrivata(String u){
        JPanel c;
        c=new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        c.setBackground(Color.PINK);
        pannelliChat.put(u, c);
        JScrollPane s;
        s=new JScrollPane(c);
        s.setBounds(0, 0, 570, 455);
        s.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        s.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        s.setWheelScrollingEnabled(true);
        multiChat.add(u,s);
        SwingUtilities.updateComponentTreeUI(multiChat);
    }
}