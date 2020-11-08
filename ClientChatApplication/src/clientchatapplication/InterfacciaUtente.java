package clientchatapplication;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import java.util.HashMap;
import static javax.swing.ScrollPaneConstants.*;

/**
 *
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
    public String messaggio;
    public String risposta;
    public DataOutputStream dati_al_server;
    public BufferedReader dati_dal_server;
    public String nome;
    public InterfacciaUtente(){
        initSocket();
        initComponent();
    }
    private void initComponent(){
        this.setBounds(new Rectangle(800, 537));
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        pannelliChat=new HashMap<>();
        mainPanel=new JPanel(null);
        //this.add(mainPanel);
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
        invio.addActionListener((ActionEvent ev) -> {
            messaggio=inserimento.getText();
            if(messaggio==null||messaggio.equals("")){
                return;
            }
            inserimento.setText("");
            inserimento.requestFocus();
            try {
                JLabel l=new JLabel("<html>"+messaggio+"</html>");
                //l.setBorder(new LineBorder(Color.BLACK));
                l.setPreferredSize(new Dimension(625, 25));
                l.setHorizontalAlignment(4);
                JPanel appo=pannelliChat.get(multiChat.getTitleAt(multiChat.getSelectedIndex()));
                appo.add(l);
                appo.setPreferredSize(new Dimension(500, chat.getHeight()+25));
                SwingUtilities.updateComponentTreeUI(appo);
                dati_al_server.writeBytes(new Messaggio(messaggio, nome, multiChat.getTitleAt(multiChat.getSelectedIndex()), Messaggio.MESSAGGIO_CLIENT).toString()+"\n");
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
    JPanel accesso;
    JLabel esito;
    JTextField input;
    JButton send;
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
        send=new JButton("Verifica");
        send.setBounds(350, 200, 100, 50);
        send.addActionListener((ActionEvent ev) -> {
            String rispServ;
            try {
                dati_al_server.writeBytes(input.getText()+'\n');
                rispServ=dati_dal_server.readLine();
                if(!rispServ.equals("OK")){
                    esito.setText(rispServ);
                }
                else{
                    rispServ=dati_dal_server.readLine();
                    nome=rispServ;
                    this.remove(accesso);
                    inserimento.setEnabled(true);
                    invio.setEnabled(true);
                    fetchClients();
                    this.add(mainPanel);
                    this.setTitle("Connesso come "+rispServ);
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
    private void startProcesses(){
        ClientReceiveMessage c_r_m=new ClientReceiveMessage(dati_dal_server,chat,dlm,multiChat,pannelliChat,nome);
        Thread t_r=new Thread(c_r_m);
        t_r.start();
    }
    DefaultListModel<String> dlm=new DefaultListModel<>();
    JList<String> membri;
    public void fetchClients() throws IOException{
        risposta=dati_dal_server.readLine();
        messaggio=Messaggio.reBuild(risposta).testo;
        membri=new JList<>(dlm);
        membri.setLayoutOrientation(JList.VERTICAL);
        SwingUtilities.updateComponentTreeUI(membriInChat);
        if(messaggio!=null&&!messaggio.equals("")){
            String[] utenti=messaggio.split(Messaggio.SPLIT_MEMBERS);
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