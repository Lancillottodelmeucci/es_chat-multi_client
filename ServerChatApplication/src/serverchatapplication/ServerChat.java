package serverchatapplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author giova
 */
public class ServerChat implements Runnable{
    ServerSocket socket_server=null;
    Socket socket_client=null;
    String messaggio_client=null;
    String nome_client=null;
    String risposta_server=null;//necessario?
    BufferedReader dati_dal_client;
    DataOutputStream dati_al_client;
    ArrayList<Socket> client_disponibili;
    DataOutputStream dati_al_partner;
    public ServerChat(Socket s,ArrayList<Socket> s_a){
        this.client_disponibili=s_a;
        this.socket_client=s;
        try {
            dati_dal_client=new BufferedReader(new InputStreamReader(socket_client.getInputStream()));
            dati_al_client=new DataOutputStream(socket_client.getOutputStream());
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Errore nell'istanza dei canali di comunicazione.");
            System.exit(0);
        }
    }
    public void run() {
        try {
            chat();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
    public void chat() throws IOException{
        //try {
        System.out.println(Thread.currentThread().getName()+" >> "+"In attesa del nominativo del client.");
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
        System.out.println(Thread.currentThread().getName()+" >> "+/*nome_client+*/" connesso.");
        if(client_disponibili.size()>1){
            client_disponibili.forEach((partner) -> {
                if (!partner.equals(this.socket_client)) {
                    try {
                        dati_al_partner=new DataOutputStream(partner.getOutputStream());
                        dati_al_partner.writeBytes(nome_client+" si e' connesso.\n");
                    }
                    catch (IOException e) {
                        System.err.println(e.getMessage());
                        System.err.println(Thread.currentThread().getName()+" >> "+"Errore nella comunicazione col partner del client.");
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
                System.err.println(Thread.currentThread().getName()+" >> "+"Errore nella comunicazione col partner del client.");
                System.exit(0);
            }
        }
        for(;;){
            System.out.println(Thread.currentThread().getName()+" >> "+"In attesa del messaggio da parte del client.");
            messaggio_client=dati_dal_client.readLine();
            System.out.println(Thread.currentThread().getName()+" >> "+"Messaggio ricevuto.");
            //risposta_server="R/ "+messaggio_client;
            if(dati_dal_client==null||messaggio_client.toUpperCase().equals("FINE")){
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
                                System.err.println(Thread.currentThread().getName()+" >> "+"Errore nella comunicazione col partner del client.");
                                System.exit(0);
                            }
                        }
                    });
                }
                break;
            }
            else{
                //System.out.println("Invio della risposta al client.");
                //dati_al_client.writeBytes(risposta_server);
                //Socket socket_partner;
                if(client_disponibili.size()>1){
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
                                System.err.println(Thread.currentThread().getName()+" >> "+"Errore nella comunicazione col client.");
                                System.exit(0);
                            }
                        }
                    }
                    });
                }
                else{
                    dati_al_client.writeBytes("Nessun partner connesso: chiudere la connessione o attendere un partner.\n");
                }
                /*or(int i=0;i<client_disponibili.size();i++){
                    socket_partner=client_disponibili.get(i);
                    if(!socket_partner.equals(this.socket_client)){
                        dati_al_partner=new DataOutputStream(socket_client.getOutputStream());
                        try {
                            dati_al_partner.writeBytes("Da: "+nome_client+"Testo: "+messaggio_client);
                        }
                        catch (IOException e) {
                            dati_al_client.writeBytes("Partner non connesso: chiudere la connessione o attendere un partner.");
                        }
                    }
                }*/
            }
        }
        client_disponibili.remove(socket_client);
        System.out.println(Thread.currentThread().getName()+" >> "+"Comunicazione terminata.");
        dati_al_client.close();
        dati_dal_client.close();
        socket_client.close();
        /*}
        catch (IOException e) {
            System.out.println("Errore durante la comunicazione.");
        }*/
    }
    /*
    il server si avvia (porta 7777): attende due connessioni, avviate 
    connessioni chiede il nome utente; alla prima connessione dice di attendere
    la seconda, alla seconda dice ad entrambi che sono connessi l'un l'altro.
    entrambi possono mandar messaggi contemporaneamente, e li ricevano nel
    formato "Da: [mittente] Testo: [messaggio]" - nel caso di chat tra due.
    
    Creare nuovo formato messaggio in una stringa, in una classe Messaggio
    */
}