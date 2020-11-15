package clientchatapplication;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import java.util.HashMap;
import static javax.swing.ScrollPaneConstants.*;

/**
 * La classe che gestisce l'interfaccia grafica inerente all'accesso e alla 
 * chat
 * @author Giovanni Ciaranfi
 */
public class InterfacciaUtente extends JFrame{
    private JPanel mainPanel;
    private JPanel chat;
    private JPanel invioMessaggi;
    private JTextField inserimento;
    private JButton invio;
    private JScrollPane membriInChat;
    private JScrollPane scrollChat;
    private JTabbedPane multiChat;
    private HashMap<String,JPanel> pannelliChat;
    private final String nome_server="127.0.0.1";
    private final int porta_server=7777;
    private Socket socket;
    private String messaggio;
    private Messaggio risposta;
    private DataOutputStream dati_al_server;
    private BufferedReader dati_dal_server;
    private String nome;
    private JPanel accesso;
    private JLabel esito;
    private JTextField input;
    private JButton send;
    private DefaultListModel<String> dlm=new DefaultListModel<>();
    private JList<String> membri;
    private JCheckBox jcb;
    /**
     * Il costruttore della classe che genera il contenuto per l'accesso e per 
     * la chat
     */
    public InterfacciaUtente(){
        initSocket();
        initComponent();
    }
    /**
     * Il metodo che crea i componenti per la chat
     */
    private void initComponent(){
        this.setBounds(new Rectangle(800, 537));
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        pannelliChat=new HashMap<>();
        mainPanel=new JPanel(null);
        mainPanel.setBackground(Color.CYAN);
        multiChat=new JTabbedPane();
        multiChat.setTabPlacement(JTabbedPane.TOP);
        multiChat.setBounds(0, 0, 650, 455);
        mainPanel.add(multiChat);
        chat=new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        chat.setBackground(Color.PINK);
        pannelliChat.put("mainGroupChat", chat);
        scrollChat=new JScrollPane(chat);
        scrollChat.setBounds(0, 0, 570, 455);
        scrollChat.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        scrollChat.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollChat.setWheelScrollingEnabled(true);
        multiChat.add("mainGroupChat",scrollChat);
        multiChat.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        invioMessaggi=new JPanel(null);
        invioMessaggi.setBackground(Color.GREEN);
        invioMessaggi.setBounds(0, 455, 650, 45);
        inserimento=new JTextField();
        inserimento.setBounds(0, 0, 580, 45);
        inserimento.setEnabled(false);
        invioMessaggi.add(inserimento);
        invio=new JButton("Invia");
        invio.setBounds(580, 0, 70, 45);
        invio.setBackground(Color.ORANGE);
        /*
        improvement:
        creare una classe separata per gli eventi
        */
        invio.addActionListener((ActionEvent ev) -> {
            messaggio=inserimento.getText();
            if(messaggio==null||messaggio.equals("")){
                return;
            }
            Messaggio appoM=new Messaggio(messaggio, nome, multiChat.getTitleAt(multiChat.getSelectedIndex()), Messaggio.MESSAGGIO_CLIENT);
            inserimento.setText("");
            inserimento.requestFocus();
            if(appoM.getTesto().equalsIgnoreCase("FINE")&&!appoM.getDestinatario().equals("mainGroupChat")){
                /*
                improvememt:
                gestire anche il nuovo click sulle persone con chat rimosse
                */
                multiChat.remove(multiChat.getSelectedIndex());
                return;
            }
            try {
                /*
                feauture:
                inserire un messaggio di tipo ping verso il server per sapere se posso inviare o meno il messaggio
                */
                JLabel l=new JLabel("<html>"+messaggio+"</html>");
                //l.setBorder(new LineBorder(Color.BLACK));
                l.setPreferredSize(new Dimension(625, 25));
                l.setHorizontalAlignment(4);
                JPanel appo=pannelliChat.get(multiChat.getTitleAt(multiChat.getSelectedIndex()));
                appo.add(l);
                appo.setPreferredSize(new Dimension(500, chat.getHeight()+25));
                /*
                improvement:
                aumentare solo se necessario, contando il numero di label e sommando la loro altezza
                usare instanceof se necessario sulle jlabel
                gestire in modo differente la dimensione delle etichette per poter leggere anche quelle
                più lunghe di una riga
                */
                SwingUtilities.updateComponentTreeUI(appo);
                dati_al_server.writeBytes(appoM.toString()+"\n");
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.err.println("Errore durante la comunicazione.");
                System.exit(0);
            }
        });
        invio.setEnabled(false);
        invioMessaggi.add(invio);
        mainPanel.add(invioMessaggi);
        membriInChat=new JScrollPane(null);
        membriInChat.setBounds(650, 0, 150, 500);
        membriInChat.setBackground(Color.YELLOW);
        mainPanel.add(membriInChat);
        initAccess();
        this.setVisible(true);
    }
    /**
     * Il metodo che crea i componenti per l'accesso
     */
    private void initAccess(){
        accesso=new JPanel(null);
        accesso.setBackground(Color.BLUE);
        this.add(accesso);
        esito=new JLabel("Inserisci un nome utente a tua scelta:");
        esito.setBounds(200, 100, 400, 50);
        accesso.add(esito);
        input=new JTextField();
        input.setBounds(200, 150, 400, 50);
        accesso.add(input);
        jcb=new JCheckBox("Entra con un nome casuale.");
        jcb.addActionListener((e) -> {
            input.setEnabled(!jcb.isSelected());
            if(jcb.isSelected()){
                send.requestFocus();
            }
            else{
                input.requestFocus();
            }
        });
        jcb.setBounds(200, 200, 400, 50);
        accesso.add(jcb);
        send=new JButton("Verifica");
        send.setBounds(350, 250, 100, 50);
        send.addActionListener((ActionEvent ev) -> {
            try {
                if(jcb.isSelected()){
                    dati_al_server.writeBytes(new Messaggio("", Messaggio.INVIO_NOME).toString()+'\n');
                }
                else{
                    if(input.getText().equals("")||input.getText()==null){
                        return;
                    }
                    dati_al_server.writeBytes(new Messaggio(input.getText(), Messaggio.INVIO_NOME).toString()+'\n');
                }
                risposta=Messaggio.reBuild(dati_dal_server.readLine());
                if(!risposta.getTesto().equals("OK")){
                    esito.setText(risposta.getTesto());
                }
                else{
                    risposta=Messaggio.reBuild(dati_dal_server.readLine());
                    nome=risposta.getTesto();
                    this.remove(accesso);
                    inserimento.setEnabled(true);
                    invio.setEnabled(true);
                    this.add(mainPanel);
                    this.setTitle("Connesso come "+risposta.getTesto());
                    fetchClients();
                    SwingUtilities.updateComponentTreeUI(this);
                    startProcesses();
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.err.println("Errore durante la comunicazione.");
                System.exit(0);
            }
        });
        input.requestFocus();
        accesso.add(send);
    }
    /**
     * Il metodo che inizializza il socket e la connessione al server
     */
    private void initSocket(){
        try {
            socket=new Socket(nome_server,porta_server);
            dati_al_server=new DataOutputStream(socket.getOutputStream());
            dati_dal_server=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch(UnknownHostException e){
            System.err.println(e.getMessage());
            System.err.println("Host non riconosciuto.");
            System.exit(0);
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Errore durante la connessione.");
            System.exit(0);
        }
    }
    /**
     * Il metodo che lancia il thread per la ricezione dei messaggi
     */
    private void startProcesses(){
        ClientReceiveMessage c_r_m=new ClientReceiveMessage(dati_dal_server,dlm,multiChat,pannelliChat,nome);
        Thread t_r=new Thread(c_r_m);
        t_r.start();
    }
    /**
     * Il metodo che recupera la lista degli utenti gia' connessi
     * @throws IOException nel caso di errore di comunicazione col server
     */
    public void fetchClients() throws IOException{
        risposta=Messaggio.reBuild(dati_dal_server.readLine());
        membri=new JList<>(dlm);
        membri.setLayoutOrientation(JList.VERTICAL);
        SwingUtilities.updateComponentTreeUI(membriInChat);
        if(risposta.getTesto()!=null&&!risposta.getTesto().equals("")){
            String[] utenti=risposta.getTesto().split(Messaggio.SPLIT_MEMBERS);
            for (String u : utenti) {
                dlm.addElement(u);
            }
        }
        membri.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList l = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    int i = l.locationToIndex(evt.getPoint());
                    if(!pannelliChat.containsKey(dlm.get(i))){
                        creaChatPrivata(dlm.get(i));
                    }
                }
            }
        });
        membri.setBounds(0, 0, 150, 500);
        membriInChat.setViewportView(membri);
        SwingUtilities.updateComponentTreeUI(membriInChat);
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