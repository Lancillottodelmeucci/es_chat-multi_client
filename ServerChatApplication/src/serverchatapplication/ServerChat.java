package serverchatapplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Classe che implementa la gestione del processo server dedicato ad ogni client
 * connessso
 * @author Giovanni Ciaranfi
 */
public class ServerChat implements Runnable{
    private Socket socket_client=null;
    //private String messaggio_client=null;//da rimuovere
    private Messaggio messaggio;
    private String nome_client=null;
    private BufferedReader dati_dal_client;
    private DataOutputStream dati_al_client;
    //private ArrayList<Socket> client_disponibili;//da rimuovere
    private DataOutputStream dati_al_partner;//è possibile spostarlo?
    //private ArrayList<Thread> thread_in_esecuzione;//da rimuovere
    private HashMap<String,Connessioni> utenti_connessi;
    /**
     * Costruttore parametrizzato della classe
     * @param s il socket del client connesso
     * @param s_a la lista dei socket connessi alla chat
     * @param tie la lista dei thread per ogni singolo client
     * @param uc lista delle connessioni completa
     */
    public ServerChat(Socket s,HashMap<String,Connessioni> uc){
        //thread_in_esecuzione=tie;
        //this.client_disponibili=s_a;
        utenti_connessi=uc;
        this.socket_client=s;
        try {
            dati_dal_client=new BufferedReader(new InputStreamReader(socket_client.getInputStream()));
            dati_al_client=new DataOutputStream(socket_client.getOutputStream());
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println(Thread.currentThread().getName()+" >> Errore nell'istanza dei canali di comunicazione.");
            //System.exit(0);
            //inserire variabile che se false termina il run subito - non posso chiudere tutto il programma
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
            //thread_in_esecuzione.remove(thread_in_esecuzione.indexOf(Thread.currentThread()));
            //client_disponibili.remove(socket_client);
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
            //messaggio_client=dati_dal_client.readLine();
            messaggio=Messaggio.reBuild(dati_dal_client.readLine());
            System.out.println(Thread.currentThread().getName()+" >> Messaggio ricevuto.");
            if(messaggio.testo==null||messaggio.testo.toUpperCase().equals("FINE")){
                //thread_in_esecuzione.remove(thread_in_esecuzione.indexOf(Thread.currentThread()));
                utenti_connessi.remove(nome_client);
                dati_al_client.writeBytes(new Messaggio("FINE").toString()+"\n");
                comunicaDisconnessione();
                break;
            }
            else{
//                if(utenti_connessi.size()>1){
                    inviaMessaggio();
//                }
//                else{
//                    dati_al_client.writeBytes(new Messaggio("Nessun partner connesso: chiudere la connessione o attendere un partner.").toString()+"\n");
//                }
            }
        }
        //client_disponibili.remove(socket_client);
        //utenti_connessi.remove(Thread.currentThread().getName());
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
        //messaggio_client=dati_dal_client.readLine();
        messaggio=Messaggio.reBuild(dati_dal_client.readLine());
        while(!correttezzaNome(messaggio.testo)){
            dati_al_client.writeBytes(new Messaggio("Nome utente gia' esistente o non valido, inserirne uno nuovo:").toString()+"\n");
            //messaggio_client=dati_dal_client.readLine();
            messaggio=Messaggio.reBuild(dati_dal_client.readLine());
        }
        dati_al_client.writeBytes(new Messaggio("OK").toString()+"\n");
        if(messaggio.testo==null||messaggio.testo.equals("")){
            //nome_client=socket_client.getInetAddress().getHostAddress()+":"+socket_client.getPort();
            generaNomeUtente();
        }
        else{
            nome_client=messaggio.testo;
        }
        dati_al_client.writeBytes(new Messaggio(nome_client).toString()+"\n");
        invioListaUtentiConnessi();
        System.out.print(Thread.currentThread().getName()+" -> ");
        Thread.currentThread().setName("Thread."+nome_client);
        System.out.println(Thread.currentThread().getName());
        System.out.println(Thread.currentThread().getName()+" >> connesso.");
        //client_disponibili.add(socket_client);
        //thread_in_esecuzione.add(Thread.currentThread());
        utenti_connessi.put(nome_client, c);
        if(utenti_connessi.size()>1){
            comunicaConnessione();
        }
        else{
            try {
                dati_al_client.writeBytes(new Messaggio("Sei l'unico utente attualemente connesso.").toString()+"\n");
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.err.println(Thread.currentThread().getName()+" >> Errore nella comunicazione col client.");
                //System.exit(0);
            }
        }
    }
    public void comunicaConnessione(){
        utenti_connessi.forEach((k,partner) -> {
            if (!k.equals(nome_client)) {
                try {
                    dati_al_partner=new DataOutputStream(partner.getSocket().getOutputStream());
                    dati_al_partner.writeBytes(new Messaggio(nome_client,Messaggio.CONNESSIONE).toString()+"\n");
                }
                catch (IOException e) {
                    System.err.println(e.getMessage());
                    System.err.println(Thread.currentThread().getName()+" >> Errore nella comunicazione con "+k+".");
                    //System.exit(0);
                }
            }
        });
    }
    public void generaNomeUtente(){
        String charSet="";
        charSet+="abcdefghijklmnopqrstuvwxyz";
        charSet+=charSet.toUpperCase();
        charSet+="0123456789-_";
        nome_client="";
        for(int i=0;i<16;i++){
            nome_client+=charSet.charAt((int)(Math.random()*charSet.length()));
        }
        while(utenti_connessi.containsKey(nome_client)){
            nome_client="";
            for(int i=0;i<16;i++){
                nome_client+=charSet.charAt((int)(Math.random()*charSet.length()));
            }
        }
    }
    public void invioListaUtentiConnessi() throws IOException{
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
    }
    /**
     * Il metodo che controlla l'univocita' del nomitativo
     * @param n il nome scelto dal client
     * @return l'esito del controllo
     */
    private boolean correttezzaNome(String n){
        /*for (Thread t : thread_in_esecuzione) {
            if(t.getName().substring(7).equals(n)){
                return(false);
            }
        }*/
        if(n.equals("")||n==null){
            return (true);
        }
        if(!Pattern.matches("^[\\w_-]{3,}$", n)){
            return (false);
        }
        if(utenti_connessi.containsKey(n)){
            return (false);
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
        if(!utenti_connessi.isEmpty()){
            utenti_connessi.forEach((k,partner) -> {
                if (!k.equals(nome_client)) {
                    try {
                        dati_al_partner=new DataOutputStream(partner.getSocket().getOutputStream());
                        dati_al_partner.writeBytes(new Messaggio(nome_client,Messaggio.DISCONNESSIONE).toString()+"\n");
                    }
                    catch (IOException e) {
                        System.err.println(e.getMessage());
                        System.err.println(Thread.currentThread().getName()+" >> Errore nella comunicazione con "+k+".");
                        //System.exit(0);
                    }
                }
            });
        }
    }
    /**
     * Il metodo che invia il messaggioa agli altri client e comunica in caso di
     * nessun altro utente connesso
     */
    private void inviaMessaggio() throws IOException{
        if(messaggio.destinatario.equals("mainGroupChat")||messaggio.destinatario.equals("Invia a tutti")){
            if(utenti_connessi.size()>1){
                utenti_connessi.forEach((k, c) -> {
                    if(!k.equals(nome_client)){
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
                                System.err.println(Thread.currentThread().getName()+" >> Errore nella comunicazione con "+k+".");
                                //System.exit(0);
                            }
                        }
                    }
                });
            }
            else{
                dati_al_client.writeBytes(new Messaggio("Nessun partner connesso: chiudere la connessione o attendere un partner.").toString()+"\n");
            }
        }
        else{
            if(utenti_connessi.containsKey(messaggio.destinatario)){
                dati_al_partner=new DataOutputStream(utenti_connessi.get(messaggio.destinatario).getSocket().getOutputStream());
                dati_al_partner.writeBytes(messaggio.toString()+'\n');
            }
            else{
                dati_al_client.writeBytes(new Messaggio("Partner non connesso.",messaggio.destinatario, nome_client, Messaggio.MESSAGGIO_SERVER).toString()+"\n");
            }
        }
    }
}