package serverchatapplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe che implementa la gestione del processo server dedicato ad ogni client
 * connessso
 * @author Giovanni Ciaranfi
 */
public class ServerChat implements Runnable{
    private Socket socket_client=null;
    private String messaggio_client=null;
    private Messaggio messaggio;
    private String nome_client=null;
    private BufferedReader dati_dal_client;
    private DataOutputStream dati_al_client;
    private ArrayList<Socket> client_disponibili;
    private DataOutputStream dati_al_partner;
    private ArrayList<Thread> thread_in_esecuzione;
    private HashMap<String,Connessioni> utenti_connessi;
    /**
     * Costruttore parametrizzato della classe
     * @param s il socket del client connesso
     * @param s_a la lista dei socket connessi alla chat
     * @param tie la lista dei thread per ogni singolo client
     * @param Connessioni lista delle connessioni completa
     */
    public ServerChat(Socket s,ArrayList<Socket> s_a,ArrayList<Thread> tie,HashMap<String,Connessioni> uc){
        thread_in_esecuzione=tie;
        this.client_disponibili=s_a;
        utenti_connessi=uc;
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
            thread_in_esecuzione.remove(thread_in_esecuzione.indexOf(Thread.currentThread()));
            client_disponibili.remove(socket_client);
            utenti_connessi.remove(nome_client);
            comunicaDisconnessione();
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
            messaggio=Messaggio.reBuild(messaggio_client);
            System.out.println(Thread.currentThread().getName()+" >> Messaggio ricevuto.");
            if(dati_dal_client==null||messaggio.testo.toUpperCase().equals("FINE")){
                thread_in_esecuzione.remove(thread_in_esecuzione.indexOf(Thread.currentThread()));
                utenti_connessi.remove(nome_client);
                dati_al_client.writeBytes(new Messaggio("FINE").toString()+"\n");
                comunicaDisconnessione();
                break;
            }
            else{
                if(client_disponibili.size()>1){
                    inviaMessaggio();
                }
                else{
                    dati_al_client.writeBytes(new Messaggio("Nessun partner connesso: chiudere la connessione o attendere un partner.").toString()+"\n");
                }
            }
        }
        client_disponibili.remove(socket_client);
        utenti_connessi.remove(Thread.currentThread().getName());
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
        Connessioni c=new Connessioni(socket_client, Thread.currentThread());//utenti_connessi.get(Thread.currentThread().getName());;
        System.out.println(Thread.currentThread().getName()+" >> In attesa del nominativo del client.");
        messaggio_client=dati_dal_client.readLine();
        while(!nomeCorretto(messaggio_client)){
            dati_al_client.writeBytes("Nome utente gia' esistente, inserirne uno nuovo:\n");
            messaggio_client=dati_dal_client.readLine();
        }
        dati_al_client.writeBytes("OK\n");
        if(messaggio_client==null||messaggio_client.equals("")){
            nome_client=socket_client.getInetAddress().getHostAddress()+":"+socket_client.getPort();
        }
        else{
            nome_client=messaggio_client;
        }
        dati_al_client.writeBytes(nome_client+"\n");
        System.out.print(Thread.currentThread().getName()+" -> ");
        String testo="Invia a tutti"+Messaggio.SPLIT_MEMBERS;
        if(!utenti_connessi.isEmpty()){
            Object[] utenti=utenti_connessi.keySet().toArray();
            for (Object u : utenti) {
                String s=(String)u;
                testo+=s+Messaggio.SPLIT_MEMBERS;
            }
        }
        testo=testo.substring(0, testo.lastIndexOf(Messaggio.SPLIT_MEMBERS));
        dati_al_client.writeBytes(new Messaggio(testo).toString()+"\n");
        Thread.currentThread().setName("Thread."+nome_client);
        System.out.println(Thread.currentThread().getName());
        System.out.println(Thread.currentThread().getName()+" >> connesso.");
        client_disponibili.add(socket_client);
        thread_in_esecuzione.add(Thread.currentThread());
        utenti_connessi.put(nome_client, c);
        if(client_disponibili.size()>1){
            client_disponibili.forEach((partner) -> {
                if (!partner.equals(this.socket_client)) {
                    try {
                        dati_al_partner=new DataOutputStream(partner.getOutputStream());
                        dati_al_partner.writeBytes(new Messaggio(nome_client,Messaggio.CONNESSIONE).toString()+"\n");
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
                dati_al_client.writeBytes(new Messaggio("Sei l'unico utente attualemente connesso.").toString()+"\n");
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.err.println(Thread.currentThread().getName()+" >> Errore nella comunicazione col partner del client.");
                System.exit(0);
            }
        }
    }
    /**
     * Il metodo che controlla l'univocita' del nomitativo
     * @param n il nome scelto dal client
     * @return l'esito del controllo
     */
    private boolean nomeCorretto(String n){
        for (Thread t : thread_in_esecuzione) {
            if(t.getName().substring(7).equals(n)){
                return(false);
            }
        }
        if(n.equals("mainGroupChat")){
            return (false);
        }
        return (true);
    }
    /**
     * Il metodo che comunica agli altri client la disconnessione, se presenti
     * @throws IOException lanciata in caso di errori nell gestione dei flussi di comunicazione
     */
    private void comunicaDisconnessione(){
        if(client_disponibili.size()>=1){
            client_disponibili.forEach((partner) -> {
                if (!partner.equals(this.socket_client)) {
                    try {
                        dati_al_partner=new DataOutputStream(partner.getOutputStream());
                        dati_al_partner.writeBytes(new Messaggio(nome_client,Messaggio.DISCONNESSIONE).toString()+"\n");
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
        utenti_connessi.forEach((k, c) -> {
            if(!k.equals(nome_client)&&(messaggio.destinatario.equals("mainGroupChat")||k.equals(messaggio.destinatario)||messaggio.destinatario.equals("Invia a tutti"))){
                try {
                    dati_al_partner=new DataOutputStream(c.getSocket().getOutputStream());
                    dati_al_partner.writeBytes(messaggio.toString()+'\n');
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