package model;
import java.util.ArrayList;
import java.util.List;

public abstract class Annuncio {

    //ATTRIBUTI
    private static int contatore=0;
    private int idAnnuncio;
    private String descrizione;
    private CategoriaCarta categoria;
    private StatoAnnuncio stato;
    private Utente creatore;
    private List<CartaFisica> carte;

    //COSTRUTTORE
    public Annuncio(String descrizione,CategoriaCarta categoria, Utente creatore){
        this.idAnnuncio=++contatore;
        this.descrizione=descrizione;
        this.categoria=categoria;
        this.creatore=creatore;
        this.stato= StatoAnnuncio.DISPONIBILE;
        this.carte=new ArrayList<>();
    }

    //GETTER E SETTER
    public int getIdAnnuncio(){
        return idAnnuncio;
    }
    public String getDescrizione(){
        return descrizione;
    }
    public void setDescrizione(String descrizione){
        this.descrizione=descrizione;
    }

    public CategoriaCarta getCategoria() {
        return categoria;
    }
    public void setCategoria(CategoriaCarta categoria){
        this.categoria=categoria;
    }

    public Utente getCreatore() {
        return creatore;
    }
    public StatoAnnuncio getStato() {
        return stato;
    }

    public List<CartaFisica> getCarte() {
        return carte;
    }

    //METODI
    public void setStato(StatoAnnuncio stato) {
        this.stato = stato;
    }

    public void aggiungiCarta(CartaFisica carta){
        carte.add(carta);
    }

    public boolean rimuoviCarta(CartaFisica carta){
        return carte.remove(carta);
    }

    public void avviaTrattativa(){
        stato= StatoAnnuncio.IN_TRATTATIVA;
    }

    public void concludi(){
        stato= StatoAnnuncio.CONCLUSO;
    }

    //TOSTRING
    public String toString(){
        return "Annuncio #" + idAnnuncio + ":" + descrizione + "[" + stato + "]";
    }
}