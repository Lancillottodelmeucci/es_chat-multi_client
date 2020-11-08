package clientchatapplication;

import java.awt.Color;
import java.awt.Component;
import static java.awt.Component.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.*;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
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
    private Messaggio messaggio;
    private JList<String> membri;
    DefaultListModel<String> dlm;
    private JTabbedPane multiChat;
    private HashMap<String,JPanel> pannelliChat;
    public String nome;
    /**
     * Costruttore parametrizzato
     * @param c il client dal quale ricevere gli oggetti necessari alla comunicazione
     */
    public ClientReceiveMessage(BufferedReader br,JPanel p,DefaultListModel<String> d,JTabbedPane t,HashMap<String,JPanel> h,String n){
        dati_dal_server=br;
        chat=p;
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
                risposta=dati_dal_server.readLine();
                messaggio=Messaggio.reBuild(risposta);
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
                l=new JLabel("<html>"+(m.mittente.equals("")?"":"Da "+m.mittente+": ")+m.testo+"</html>");
                break;
        }
        l.setPreferredSize(new Dimension(625, 25));
        l.setHorizontalAlignment(2);
        //l.setBorder(new LineBorder(Color.WHITE));
        JPanel appo;//=pannelliChat.get(multiChat.getTitleAt(multiChat.getSelectedIndex()));
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
        appo.add(l);
        appo.setPreferredSize(new Dimension(500, chat.getHeight()+25));
        SwingUtilities.updateComponentTreeUI(appo);
//        chat.add(l);
//        chat.setPreferredSize(new Dimension(500, chat.getHeight()+25));
//        SwingUtilities.updateComponentTreeUI(chat);
    }
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
//        s.setPreferredSize(new Dimension(570, 455));
        multiChat.add(u,s);
        SwingUtilities.updateComponentTreeUI(multiChat);
    }
}