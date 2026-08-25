package controller;

import java.util.ArrayList;
import java.util.List;

import model.Annuncio;
import model.AnnuncioScambio;
import model.AnnuncioVendita;
import model.CategoriaCarta;
import model.Utente;

/**
 * Gestisce la creazione, la ricerca e la chiusura degli annunci.
 */
public class ControllerAnnunci {

    private final Piattaforma piattaforma;
    private final ControllerUtenti controllerUtenti;

    public ControllerAnnunci(Piattaforma piattaforma,
                             ControllerUtenti controllerUtenti) {
        this.piattaforma = piattaforma;
        this.controllerUtenti = controllerUtenti;
    }

    /**
     * Crea un annuncio di vendita e lo registra sia nella piattaforma
     * sia nella lista personale dell'utente che lo ha creato.
     *
     * @return l'annuncio creato, oppure null se i dati non sono validi
     */
    public AnnuncioVendita pubblicaAnnuncioVendita(Utente creatore,
                                                   String descrizione,
                                                   CategoriaCarta categoria,
                                                   double prezzo) {
        if (creatore == null || descrizione == null || descrizione.isBlank()) {
            return null;
        }

        if (prezzo <= 0) {
            return null;
        }

        AnnuncioVendita annuncio =
                new AnnuncioVendita(descrizione, categoria, creatore, prezzo);

        // 1) Aggiungo alla lista globale della piattaforma
        piattaforma.getAnnuncio().add(annuncio);

        // 2) Collego l'annuncio all'utente creatore
        controllerUtenti.aggiungiAnnuncio(creatore, annuncio);

        return annuncio;
    }

    /**
     * Crea un annuncio di scambio e lo registra sia nella piattaforma
     * sia nella lista personale dell'utente che lo ha creato.
     */
    public AnnuncioScambio pubblicaAnnuncioScambio(Utente creatore,
                                                   String descrizione,
                                                   CategoriaCarta categoria,
                                                   double valoreDiRiferimento) {
        if (creatore == null || descrizione == null || descrizione.isBlank()) {
            return null;
        }

        if (valoreDiRiferimento <= 0) {
            return null;
        }

        AnnuncioScambio annuncio =
                new AnnuncioScambio(descrizione, categoria, creatore, valoreDiRiferimento);

        // 1) Aggiungo alla lista globale della piattaforma
        piattaforma.getAnnuncio().add(annuncio);

        // 2) Collego l'annuncio all'utente creatore
        controllerUtenti.aggiungiAnnuncio(creatore, annuncio);

        return annuncio;
    }

    /**
     * Cerca un annuncio tramite il suo ID.
     *
     * @return l'annuncio trovato, oppure null se non esiste
     */
    public Annuncio cercaAnnuncioPerId(int idAnnuncio) {
        for (Annuncio annuncio : piattaforma.getAnnuncio()) {
            if (annuncio.getIdAnnuncio() == idAnnuncio) {
                return annuncio;
            }
        }

        return null;
    }

    /**
     * Restituisce tutti gli annunci di una certa categoria
     * (es. solo CARTA_SINGOLA, solo BOX...).
     */
    public List<Annuncio> cercaAnnunciPerCategoria(CategoriaCarta categoria) {
        List<Annuncio> risultato = new ArrayList<>();

        if (categoria == null) {
            return risultato;
        }

        for (Annuncio annuncio : piattaforma.getAnnuncio()) {
            if (annuncio.getCategoria() == categoria) {
                risultato.add(annuncio);
            }
        }

        return risultato;
    }

    /**
     * Restituisce solo gli annunci ancora disponibili
     * (esclude quelli in trattativa o conclusi).
     */
    public List<Annuncio> cercaAnnunciDisponibili() {
        List<Annuncio> risultato = new ArrayList<>();

        for (Annuncio annuncio : piattaforma.getAnnuncio()) {
            if (annuncio.getStato() == model.StatoAnnuncio.DISPONIBILE) {
                risultato.add(annuncio);
            }
        }

        return risultato;
    }

    /**
     * Chiude un annuncio, ma solo se è stato effettivamente
     * pubblicato tramite questo controller (cioè esiste in piattaforma).
     */
    public boolean chiudiAnnuncio(Annuncio annuncio) {
        if (annuncio == null || !piattaforma.getAnnuncio().contains(annuncio)) {
            return false;
        }

        annuncio.concludi();
        return true;
    }
}