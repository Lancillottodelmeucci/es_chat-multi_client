package clientchatapplication;

import java.util.*;

/**
 * La classe del messaggio da inviare e ricevere
 * @author Giovanni Ciaranfi
 */
public class Messaggio {
    static final int CONNESSIONE=0,INVIO_NOME=1,MESSAGGIO_SERVER=2,MESSAGGIO_CLIENT=3,CLIENTS_FETCH=4,DISCONNESSIONE=5;
    static final String SPLIT_CHARS="#S#";
    static final String SPLIT_MEMBERS="#M#";
    String mittente;
    String destinatario;
    String testo;
    int tipo;
    GregorianCalendar data;
    /**
     * Il costruttore parametrizzato per il metodo reBuild
     */
    public Messaggio(){}
    /**
     * Il costruttore parametrizzato
     * @param m il mittente
     * @param t  il testo del emssaggio
     */
    public Messaggio(String m,int t){//conn disconn fetch nomUt
        if(t==INVIO_NOME){
            testo=m;
            mittente="";
        }
        else{
            testo="";
            mittente=m;
        }
        destinatario="";
        tipo=t;
        data=new GregorianCalendar();
    }
    /**
     * Il costruttore parametrizzato
     * @param t il testo del messaggio
     */
    public Messaggio(String t){//serv
        testo=t;
        mittente="";
        destinatario="";
        tipo=MESSAGGIO_SERVER;
        data=new GregorianCalendar();
    }
    /**
     * Il costruttore parametrizzato
     * @param t il testo del messaggio
     * @param m il mittente
     * @param d il destinatario
     */
    public Messaggio(String t,String d,int tipo){//?
        testo=t;
        mittente="";
        destinatario=d;
        this.tipo=tipo;
        data=new GregorianCalendar();
    }
    /**
     * Il costruttore parametrizzato
     * @param t il testo del messaggio
     * @param m il mittente
     * @param d il destinatario
     * @param tipo il tipo di messaggio
     */
    public Messaggio(String t,String m,String d, int tipo){//mess
        testo=t;
        mittente=m;
        destinatario=d;
        this.tipo=tipo;
        data=new GregorianCalendar();
    }
    /**
     * Il metodo che trasforma il messaggio in stringa per essere inviato
     * @return la stringa
     */
    @Override
    public String toString() {
        String r="";
        r+=mittente+SPLIT_CHARS;
        r+=destinatario+SPLIT_CHARS;
        r+=testo+SPLIT_CHARS;
        r+=tipo;
        return (r);
    }
    /**
     * Il metodo che costruisce un Messaggio da una stringa
     * @param M il messaggio in toString
     * @return il Messaggio
     */
    public final static Messaggio reBuild(String M){
        Messaggio r=new Messaggio();
        String[] campi=M.split(SPLIT_CHARS);
        r.setMittente(campi[0]);
        r.setDestinatario(campi[1]);
        r.setTesto(campi[2]);
        r.setTipo(Integer.parseInt(campi[3]));
        return (r);
    }
    /**
     * Il metodo per impostare il valore
     * @param data la data
     */
    public void setData(GregorianCalendar data) {
        this.data = data;
    }
    /**
     * Il metodo per impostare il valore
     * @param destinatario il destinatario
     */
    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }
    /**
     * Il metodo per impostare il valore
     * @param mittente il mittente
     */
    public void setMittente(String mittente) {
        this.mittente = mittente;
    }
    /**
     * Il metodo per impostare il valore
     * @param testo il testo
     */
    public void setTesto(String testo) {
        this.testo = testo;
    }
    /**
     * Il metodo per impostare il valore
     * @param tipo il tipo
     */
    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}