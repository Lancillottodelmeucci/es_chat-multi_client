package clientchatapplication;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.ScrollPaneConstants.*;
import javax.swing.border.LineBorder;

/**
 *
 * @author Giovanni Ciaranfi
 */
public class InterfacciaUtente extends JFrame{
    private JPanel mainPanel;
    private JPanel chatDisponibili;
    private JPanel chat;
    private JPanel invioMessaggi;
    private JTextField inserimento;
    private JButton invio;
    private JPanel membriInChat;
    private JScrollPane scrollChat;
    
    private final String nome_server="127.0.0.1";
    private final int porta_server=7777;
    private Socket socket;
    public String messaggio;
    public String risposta;
    public DataOutputStream dati_al_server;
    public BufferedReader dati_dal_server;
    public InterfacciaUtente(){
        initSocket();
        initComponent();
    }
    private void initComponent(){
        this.setBounds(new Rectangle(800, 537));//heigh: 500 + 37 (dimensione dei sopra)
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainPanel=new JPanel(null);
        //this.add(mainPanel);
        mainPanel.setBackground(Color.CYAN);
        chatDisponibili=new JPanel(null);
        chatDisponibili.setBounds(0, 0, 80, 500);
        chatDisponibili.setBackground(Color.GRAY);
        mainPanel.add(chatDisponibili);
        chat=new JPanel(new FlowLayout(FlowLayout.LEFT));
//        chat.setBounds(new Rectangle(570, 455));
        chat.setPreferredSize(new Dimension(500, 0));
        chat.setBackground(Color.PINK);
        scrollChat=new JScrollPane(chat);
        scrollChat.setBounds(80, 0, 570, 455);
//        scrollChat.setPreferredSize(new Dimension(570, 455));
        scrollChat.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
//        scrollChat.createVerticalScrollBar();
        scrollChat.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollChat);
        invioMessaggi=new JPanel(null);
        invioMessaggi.setBackground(Color.GREEN);
        invioMessaggi.setBounds(80, 455, 570, 45);
        //JLabel inserimento=new JLabel("Scrivi qui...");
        inserimento=new JTextField("Scrivi qui...");
        inserimento.setBounds(0, 0, 500, 45);
        //inserimento.setBackground(Color.RED);
        inserimento.setEnabled(false);
        invioMessaggi.add(inserimento);
        invio=new JButton("Invia");
        invio.setBounds(500, 0, 70, 45);
        invio.setBackground(Color.ORANGE);
        invio.addActionListener((ActionEvent ev) -> {
            messaggio=inserimento.getText();
            inserimento.setText("");
            try {
                dati_al_server.writeBytes(messaggio+"\n");
                JLabel l=new JLabel(messaggio);
                l.setBorder(new LineBorder(Color.BLACK));
                l.setPreferredSize(new Dimension(500, 30));
                l.setHorizontalAlignment(4);
                chat.add(l);
                chat.setPreferredSize(new Dimension(chat.getWidth(), chat.getHeight()+30));
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.err.println("Errore durante la comunicazione.");
                System.exit(0);
            }
        });
        invio.setEnabled(false);
        invioMessaggi.add(invio);
        mainPanel.add(invioMessaggi);
        membriInChat=new JPanel(null);
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
        //accesso.setBounds(300, 225, 200, 150);
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
                    this.remove(accesso);
                    inserimento.setEnabled(true);
                    invio.setEnabled(true);
                    this.add(mainPanel);
                    this.setTitle("Connesso come "+rispServ);
                    SwingUtilities.updateComponentTreeUI(this);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.err.println("Errore durante la comunicazione.");
                System.exit(0);
            }
            startProcesses();
        });
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
        //ClientSendMessage c_s_m=new ClientSendMessage();
        ClientReceiveMessage c_r_m=new ClientReceiveMessage(dati_dal_server,chat);
        //Thread t_s=new Thread(c_s_m);
        Thread t_r=new Thread(c_r_m);
        t_r.start();
        //t_s.start();
        RefreshFrame r_f=new RefreshFrame(chat);
        Thread t_refresh=new Thread(r_f);
        /*new Thread(() -> {
            this.setVisible(false);
            this.setVisible(true);
        });*/
        t_refresh.start();
    }
}