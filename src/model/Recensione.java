package model;

public class Recensione {

    //ATTRIBUTI
    private static int contatore = 0;
    private int id;
    private int voto;
    private String commento;
    private Utente autore;
    private Utente destinatario;

    //COSTRUTTORE
    public Recensione(int voto,String commento,Utente autore,Utente destinatario){
        this.id=++contatore;
        this.voto=voto;
        this.commento=commento;
        this.autore=autore;
        this.destinatario=destinatario;
    }

    /**
     * Ricostruisce una recensione già salvata, mantenendone l'identificativo.
     * Serve alle DAO basate su file quando rileggono le recensioni dal disco.
     */
    public Recensione(
            int id,
            int voto,
            String commento,
            Utente autore,
            Utente destinatario
    ) {
        this.id = id;
        this.voto = voto;
        this.commento = commento;
        this.autore = autore;
        this.destinatario = destinatario;

        if (id > contatore) {
            contatore = id;
        }
    }
    
    //GETTER E SETTER
    public int getId(){
        return id;
    }
    public int getVoto(){
        return voto;
    }
    public void setVoto(int voto){
        this.voto=voto;
    }
    public String getCommento(){
        return commento;
    }
    public void setCommento(String commento){
        this.commento=commento;
    }
    public Utente getAutore(){
        return autore;
    }
    public Utente getDestinatario(){
        return destinatario;
    }
    
    //METODI
    public boolean isValida(){
        return voto>=1 && voto <=5;
    }
    public boolean riguarda(Utente utente){
        return destinatario == utente;
    }

    //TOSTRING
    public String toString(){
        return "Recensione #" + id + ": voto=" + voto + ", commento=" + commento;
    }
}
