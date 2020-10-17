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
            System.out.println(e.getMessage());
            System.out.println("Errore nell'istanza dei canali di comunicazione.");
            System.exit(1);
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
        System.out.println("In attesa del nominativo del client.");
        messaggio_client=dati_dal_client.readLine();
        if(messaggio_client==null||messaggio_client.equals("")){
            nome_client=socket_client.getInetAddress().getHostAddress()+":"+socket_client.getPort();
        }
        else{
            nome_client=messaggio_client;
        }
        if(client_disponibili.size()>0){
            client_disponibili.forEach((partner) -> {
                try {
                dati_al_partner=new DataOutputStream(partner.getOutputStream());
                    dati_al_partner.writeBytes(nome_client+" si e' connesso.\n");
                }
                catch (IOException e) {
                    System.out.println(e.getMessage());
                    System.out.println("Errore nella comunicazione col partner del client.");
                    System.exit(1);
                }
            });
        }
        for(;;){
            System.out.println("In attesa del messaggio da parte del client.");
            messaggio_client=dati_dal_client.readLine();
            System.out.println("Messaggio ricevuto.");
            //risposta_server="R/ "+messaggio_client;
            if(dati_dal_client==null||messaggio_client.toUpperCase().equals("FINE")){
                dati_al_client.writeBytes("Chiusura comunicazione\n");
                break;
            }
            else{
                //System.out.println("Invio della risposta al client.");
                //dati_al_client.writeBytes(risposta_server);
                //Socket socket_partner;
                if(client_disponibili.size()>0){
                    client_disponibili.forEach((partner) -> {
                        if(!partner.equals(this.socket_client)){
                        try {
                            dati_al_partner=new DataOutputStream(socket_client.getOutputStream());
                            dati_al_partner.writeBytes("Da: "+nome_client+"Testo: "+messaggio_client+'\n');
                        }
                        catch (IOException e) {
                            try {
                                dati_al_client.writeBytes("Errore durante la comunicazione col partner: chiudere la connessione o attendere un partner.\n");
                            }
                            catch (IOException ex) {
                                System.out.println(ex.getMessage());
                                System.out.println("Errore nella comunicazione col client.");
                                System.exit(1);
                            }
                        }
                    }
                    });
                }
                else{
                    dati_al_client.writeBytes("Partner non connesso: chiudere la connessione o attendere un partner.");
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
        System.out.println("Comunicazione terminata.");
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
    */
}