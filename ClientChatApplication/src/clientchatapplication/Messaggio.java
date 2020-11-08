/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientchatapplication;

import java.util.*;

/**
 *
 * @author giova
 */
public class Messaggio {
    static final int CONNESSIONE=0,MESSAGGIO_SERVER=1,MESSAGGIO_CLIENT=2,CLIENTS_FETCH=3,DISCONNESSIONE=4;
    static final String SPLIT_CHARS="#S#";
    static final String SPLIT_MEMBERS="#M#";
    String mittente;
    String destinatario;
    String testo;
    int tipo;
    GregorianCalendar data;
    public Messaggio(){}
    public Messaggio(String m,int t){//conn disconn fetch
        testo="";
        mittente=m;
        destinatario="";
        tipo=t;
        data=new GregorianCalendar();
    }
    public Messaggio(String t){//serv
        testo=t;
        mittente="";
        destinatario="";
        tipo=MESSAGGIO_SERVER;
        data=new GregorianCalendar();
    }
    public Messaggio(String t,String m,String d){//?
        testo=t;
        mittente=m;
        destinatario=d;
        data=new GregorianCalendar();
    }
    public Messaggio(String t,String m,String d, int tipo){//mess
        testo=t;
        mittente=m;
        destinatario=d;
        this.tipo=tipo;
        data=new GregorianCalendar();
    }
    public String toString() {
        String r="";
        r+=mittente+SPLIT_CHARS;
        r+=destinatario+SPLIT_CHARS;
        r+=testo+SPLIT_CHARS;
        r+=tipo;
        return (r);
    }
    public final static Messaggio reBuild(String M){
        Messaggio r=new Messaggio();
        String[] campi=M.split(SPLIT_CHARS);
        r.setMittente(campi[0]);
        r.setDestinatario(campi[1]);
        r.setTesto(campi[2]);
        r.setTipo(Integer.parseInt(campi[3]));
        return (r);
    }
    public void setData(GregorianCalendar data) {
        this.data = data;
    }
    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }
    public void setMittente(String mittente) {
        this.mittente = mittente;
    }
    public void setTesto(String testo) {
        this.testo = testo;
    }
    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}