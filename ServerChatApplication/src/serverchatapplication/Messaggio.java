package serverchatapplication;

import java.util.GregorianCalendar;

/**
 * La classe del messaggio da inviare e ricevere
 * @author Giovanni Ciaranfi
 */
public class Messaggio {
    static final int CONNESSIONE=0,INVIO_NOME=1,MESSAGGIO_SERVER=2,MESSAGGIO_CLIENT=3,CLIENTS_FETCH=4,DISCONNESSIONE=5;
    static final String SPLIT_CHARS="#S#";
    static final String SPLIT_MEMBERS="#M#";
    private String mittente;
    private String destinatario;
    private String testo;
    private int tipo;
    private GregorianCalendar data;
    /**
     * Il costruttore per il metodo reBuild
     */
    private Messaggio(){}
    /**
     * Il costruttore parametrizzato utilizzato in caso di messaggio di: 
     * connessione, disconnessione, fetch dei client e invio del nome utente
     * @param t_or_m la stringa che in base al tipo di messaggio costituisce il testo o il mittente
     * @param t  il tipo del emssaggio
     */
    public Messaggio(String t_or_m,int t){
        if(t==INVIO_NOME){
            testo=t_or_m;
            mittente="";
        }
        else{
            testo="";
            mittente=t_or_m;
        }
        destinatario="";
        tipo=t;
        data=new GregorianCalendar();
    }
    /**
     * Il costruttore parametrizzato utilizzato dal server per l'invio di alcune
     * comunicazioni
     * @param t il testo del messaggio
     */
    public Messaggio(String t){
        testo=t;
        mittente="";
        destinatario="";
        tipo=MESSAGGIO_SERVER;
        data=new GregorianCalendar();
    }
    /**
     * Il costruttore parametrizzato dei messaggi che vengono inviati dagli utenti
     * @param t il testo del messaggio
     * @param m il mittente
     * @param d il destinatario
     * @param tipo il tipo di messaggio
     */
    public Messaggio(String t,String m,String d, int tipo){
        testo=t;
        mittente=m;
        destinatario=d;
        this.tipo=tipo;
        data=new GregorianCalendar();
    }
    /**
     * Il metodo che trasforma il messaggio in stringa per essere inviato
     * @return la stringa contentenente il messaggio
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
     * Il metodo che costruisce un messaggio da una stringa
     * @param M il messaggio in stringa
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
     * Il metodo per impostare il valore della data
     * @param data la data
     */
    private void setData(GregorianCalendar data) {
        this.data = data;
    }
    /**
     * Il metodo per recuperare il valore del campo dal messaggio
     * @return la data
     */
    public GregorianCalendar getData() {
        return data;
    }
    /**
     * Il metodo per impostare il valore del destinatario
     * @param destinatario il destinatario
     */
    private void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }
    /**
     * Il metodo per recuperare il valore del campo dal messaggio
     * @return il destinatario
     */
    public String getDestinatario() {
        return destinatario;
    }
    /**
     * Il metodo per impostare il valore del mittente
     * @param mittente il mittente
     */
    private void setMittente(String mittente) {
        this.mittente = mittente;
    }
    /**
     * Il metodo per recuperare il valore del campo dal messaggio
     * @return il mittente
     */
    public String getMittente() {
        return mittente;
    }
    /**
     * Il metodo per impostare il valore del testo
     * @param testo il testo
     */
    private void setTesto(String testo) {
        this.testo = testo;
    }
    /**
     * Il metodo per recuperare il valore del campo dal messaggio
     * @return il testo
     */
    public String getTesto() {
        return testo;
    }
    /**
     * Il metodo per impostare il valore del tipo
     * @param tipo il tipo
     */
    private void setTipo(int tipo) {
        this.tipo = tipo;
    }
    /**
     * Il metodo per recuperare il valore del campo dal messaggio
     * @return il tipo
     */
    public int getTipo() {
        return tipo;
    }
}