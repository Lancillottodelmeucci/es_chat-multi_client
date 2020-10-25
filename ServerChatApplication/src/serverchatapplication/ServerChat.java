package serverchatapplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Classe che implementa la gestione del processo server dedicato ad ogni client
 * connessso
 * @author Giovanni Ciaranfi
 */
public class ServerChat implements Runnable{
    private Socket socket_client=null;
    private String messaggio_client=null;
    private String nome_client=null;
    private BufferedReader dati_dal_client;
    private DataOutputStream dati_al_client;
    private ArrayList<Socket> client_disponibili;
    private DataOutputStream dati_al_partner;
    ArrayList<Thread> thread_in_esecuzione;
    /**
     * Costruttore parametrizzato della classe
     * @param s il socket del client connesso
     * @param s_a la lista dei socket connessi alla chats
     * @param tie
     */
    public ServerChat(Socket s,ArrayList<Socket> s_a,ArrayList<Thread> tie){
        thread_in_esecuzione=tie;
        this.client_disponibili=s_a;
        this.socket_client=s;
        try {
            dati_dal_client=new BufferedReader(new InputStreamReader(socket_client.getInputStream()));
            dati_al_client=new DataOutputStream(socket_client.getOutputStream());
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println(Thread.currentThread().getName()+" >> Errore nell'istanza dei canali di comunicazione.");
            System.exit(0);
        }
    }
    /**
     * Il metodo che lancia la comunicazione client-server
     */
    @Override
    public void run() {
        try {
            initClient();
            chat();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println(Thread.currentThread().getName()+" >> Errore durante la comunicazione.");
            System.exit(0);
        }
    }
    /**
     * Il metodo che gestisce la comunicazione tra il client e il server, e che 
     * invia i messaggi agli altri utenti
     * @throws IOException lanciata in caso di errori nell gestione dei flussi di comunicazione
     */
    private void chat() throws IOException{
        for(;;){
            System.out.println(Thread.currentThread().getName()+" >> In attesa del messaggio da parte del client.");
            messaggio_client=dati_dal_client.readLine();
            System.out.println(Thread.currentThread().getName()+" >> Messaggio ricevuto.");
            if(dati_dal_client==null||messaggio_client.toUpperCase().equals("FINE")){
                thread_in_esecuzione.remove(thread_in_esecuzione.indexOf(Thread.currentThread()));
                comunicaDisconnessione();
                break;
            }
            else{
                if(client_disponibili.size()>1){
                    inviaMessaggio();
                }
                else{
                    dati_al_client.writeBytes("Nessun partner connesso: chiudere la connessione o attendere un partner.\n");
                }
            }
        }
        client_disponibili.remove(socket_client);
        System.out.println(Thread.currentThread().getName()+" >> Comunicazione terminata.");
        dati_al_client.close();
        dati_dal_client.close();
        socket_client.close();
    }
    /**
     * Il metodo che gestisce la prima comunicazione tra il client e il server, 
     * il nominativo del client e la connessione agli altri in chat
     * @throws IOException lanciata in caso di errori nell gestione dei flussi di comunicazione
     */
    private void initClient() throws IOException{
        System.out.println(Thread.currentThread().getName()+" >> In attesa del nominativo del client.");
        messaggio_client=dati_dal_client.readLine();
        if(messaggio_client==null||messaggio_client.equals("")){
            nome_client=socket_client.getInetAddress().getHostAddress()+":"+socket_client.getPort();
        }
        else{
            nome_client=messaggio_client;
        }
        System.out.print(Thread.currentThread().getName()+" -> ");
        Thread.currentThread().setName("Thread."+nome_client);
        System.out.println(Thread.currentThread().getName());
        System.out.println(Thread.currentThread().getName()+" >> connesso.");
        if(client_disponibili.size()>1){
            client_disponibili.forEach((partner) -> {
                if (!partner.equals(this.socket_client)) {
                    try {
                        dati_al_partner=new DataOutputStream(partner.getOutputStream());
                        dati_al_partner.writeBytes(nome_client+" si e' connesso.\n");
                    }
                    catch (IOException e) {
                        System.err.println(e.getMessage());
                        System.err.println(Thread.currentThread().getName()+" >> Errore nella comunicazione col partner del client.");
                        System.exit(0);
                    }
                }
            });
        }
        else{
            try {
                dati_al_client.writeBytes("Sei l'unico utente attualemente connesso.\n");
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.err.println(Thread.currentThread().getName()+" >> Errore nella comunicazione col partner del client.");
                System.exit(0);
            }
        }
    }
    /**
     * Il metodo che comunica agli altri client la disconnessione, se presenti
     * @throws IOException lanciata in caso di errori nell gestione dei flussi di comunicazione
     */
    private void comunicaDisconnessione() throws IOException{
        dati_al_client.writeBytes("FINE\n");
        if(client_disponibili.size()>1){
            client_disponibili.forEach((partner) -> {
                if (!partner.equals(this.socket_client)) {
                    try {
                        dati_al_partner=new DataOutputStream(partner.getOutputStream());
                        dati_al_partner.writeBytes(nome_client+" si e' disconnesso.\n");
                    }
                    catch (IOException e) {
                        System.err.println(e.getMessage());
                        System.err.println(Thread.currentThread().getName()+" >> Errore nella comunicazione col partner del client.");
                        System.exit(0);
                    }
                }
            });
        }
    }
    /**
     * Il metodo che invia il messaggioa agli altri client e comunica in caso di
     * nessun altro utente connesso
     */
    private void inviaMessaggio(){
        client_disponibili.forEach((partner) -> {
            if(!partner.equals(this.socket_client)){
                try {
                    dati_al_partner=new DataOutputStream(partner.getOutputStream());
                    dati_al_partner.writeBytes("Da: "+nome_client+"\nTesto: "+messaggio_client+'\n');
                }
                catch (IOException e) {
                    try {
                        System.err.println(e.getMessage());
                        dati_al_client.writeBytes("Errore durante la comunicazione col partner: chiudere la connessione o attendere un partner.\n");
                    }
                    catch (IOException ex) {
                        System.err.println(Thread.currentThread().getName()+" >> "+ex.getMessage());
                        System.err.println(Thread.currentThread().getName()+" >> Errore nella comunicazione col client.");
                        System.exit(0);
                    }
                }
            }
        });
    }
}