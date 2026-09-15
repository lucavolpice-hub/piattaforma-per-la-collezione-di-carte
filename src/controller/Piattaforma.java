package controller;

import java.util.ArrayList;
import java.util.List;

import model.Annuncio;
import model.PropostaScambio;
import model.Utente;

public class Piattaforma {

    private final List<Utente> utenti;
    private final List<Annuncio>annuncio;
    private final List<PropostaScambio>proposteScambio;

    public Piattaforma(List<Annuncio> annuncio, List<PropostaScambio> proposteScambio) {
        this.annuncio = annuncio;
        this.proposteScambio = proposteScambio;
        utenti = new ArrayList<>();
    }

    /**
     * Restituisce gli utenti registrati.
     * <p>
     * I futuri controller useranno questa lista per cercare,
     * aggiungere o rimuovere utenti.
     */
    public List<Utente> getUtenti() {
        return utenti;
    }

    public List<Annuncio> getAnnuncio() {
        return annuncio;
    }

    public List<PropostaScambio> getProposteScambio() {
        return proposteScambio;
    }

    /**
     * Inserisce un utente nell'archivio centrale.
     * <p>
     * Il controllo su username vuoto o duplicato non viene fatto qui:
     * sarà responsabilità di ControllerUtenti, quando verrà creato.
     */
    public void aggiungiUtente(Utente utente) {
        if (utente != null) {
            utenti.add(utente);
        }
    }

    /**
     * Rimuove un utente dall'archivio.
     *
     * @return true se l'utente era presente ed è stato rimosso
     */
    public boolean rimuoviUtente(Utente utente) {
        return utenti.remove(utente);
    }
}