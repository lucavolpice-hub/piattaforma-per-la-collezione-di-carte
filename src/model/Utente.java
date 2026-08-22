package model;
import java.util.ArrayList;
import java.util.List;
public class Utente {

    //ATTRIBUTI
    private String username;
    private String password;
    private Inventario inventario;
    private List<Annuncio> annunciCreati;
    private List<Recensione> recensioniRicevute;
    private List<ProposteScambio> proposteEffettuate;

    //COSTRUTTORE
    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
        this.inventario = new Inventario();
        this.annunciCreati = new ArrayList<>();
        this.recensioniRicevute = new ArrayList<>();
        this.proposteEffettuate = new ArrayList<>();
    }

    //GETTER E SETTER
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public List<Annuncio> getAnnunciCreati() {
        return annunciCreati;
    }

    public List<Recensione> getRecensioniRicevute() {
        return recensioniRicevute;
    }

    public List<ProposteScambio> getProposteEffettuate() {
        return proposteEffettuate;
    }

    //METODI
    public boolean autentica(String usernameInserito, String passwordInserita) {
        return username.equals(usernameInserito) && password.equals(passwordInserita);
    }

    public void aggiungiAnnuncio(Annuncio annuncio) {
        annunciCreati.add(annuncio);
    }

    public void aggiungiProposta(PropostaScambio proposta) {
        proposteEffettuate.add(proposta);
    }

    public void aggiungiRecensioneRicevuta(Recensione recensione) {
        recensioniRicevute.add(recensione);
    }

    public double calcolaMediaRecensioni() {
        if (recensioniRicevute.size() == 0) {
            return 0.0;
        }
        double somma = 0;
        for (Recensione r : recensioniRicevute) {
            somma += r.getVoto();
        }
        return somma / recensioniRicevute.size();
    }

    //TOSTRING
    public String toString() {
        return "Utente:" + username;
    }
}