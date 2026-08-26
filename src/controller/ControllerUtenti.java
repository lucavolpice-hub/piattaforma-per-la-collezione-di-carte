package controller;

import java.util.ArrayList;
import java.util.List;

import model.Annuncio;
import model.CartaFisica;
import model.PropostaScambio;
import model.Recensione;
import model.Utente;

/**
 * Gestisce registrazione, login, inventario e recensioni degli utenti.
 */
public class ControllerUtenti {

    // Riferimento all'archivio centrale condiviso dai controller.
    private Piattaforma piattaforma;

    public ControllerUtenti(Piattaforma piattaforma) {
        this.piattaforma = piattaforma;
    }

    /**
     * Cerca un utente usando lo username.
     * Restituisce null se non trova nessun utente.
     */
    public Utente cercaUtente(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        for (Utente utente : piattaforma.getUtenti()) {
            if (utente.getUsername().equalsIgnoreCase(username)) {
                return utente;
            }
        }

        return null;
    }

    /**
     * Controlla che lo username non sia già usato da un altro utente.
     */
    public boolean usernameDisponibile(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }

        if (cercaUtente(username) != null) {
            return false;
        }

        return true;
    }

    /**
     * Crea e registra un nuovo utente.
     */
    public boolean registraUtente(String username, String password) {
        if (username == null || username.isBlank()) {
            return false;
        }

        if (password == null || password.isBlank()) {
            return false;
        }

        if (!usernameDisponibile(username)) {
            return false;
        }

        Utente nuovoUtente = new Utente(username, password);
        piattaforma.aggiungiUtente(nuovoUtente);

        return true;
    }

    /**
     * Verifica username e password.
     * Restituisce l'utente se il login riesce, altrimenti null.
     */
    public Utente accedi(String username, String password) {
        Utente utente = cercaUtente(username);

        if (utente == null) {
            return null;
        }

        if (utente.autentica(username, password)) {
            return utente;
        }

        return null;
    }

    /**
     * Modifica la password di un utente registrato.
     */
    public boolean cambiaPassword(Utente utente, String nuovaPassword) {
        if (utente == null) {
            return false;
        }

        if (nuovaPassword == null || nuovaPassword.isBlank()) {
            return false;
        }

        if (!piattaforma.getUtenti().contains(utente)) {
            return false;
        }

        try {
            utente.setPassword(nuovaPassword);
            return true;
        } catch (Exception e) {
            // In caso di errore imprevisto durante il cambio password,
            // logghiamo (o ignoriamo) e restituiamo false.
            return false;
        }
    }

    /**
     * Aggiunge una carta all'inventario di un utente registrato.
     */
    public boolean aggiungiCartaInventario(Utente utente, CartaFisica carta) {
        if (utente == null || carta == null) {
            return false;
        }

        if (!piattaforma.getUtenti().contains(utente)) {
            return false;
        }

        utente.getInventario().aggiungiCarta(carta);
        return true;
    }

    /**
     * Rimuove una carta dall'inventario dell'utente.
     */
    public boolean rimuoviCartaInventario(Utente utente, CartaFisica carta) {
        if (utente == null || carta == null) {
            return false;
        }

        if (!piattaforma.getUtenti().contains(utente)) {
            return false;
        }

        utente.getInventario().rimuoviCarta(carta);
        return true;
    }

    /**
     * Restituisce le carte non bloccate in uno scambio.
     */
    public List getCarteDisponibili(Utente utente) {
        if (utente == null) {
            return new ArrayList();
        }

        return utente.getInventario().getCarteDisponibili();
    }

    /**
     * Collega un annuncio sia alla piattaforma sia al suo creatore.
     * La creazione dell'annuncio viene fatta da ControllerAnnunci.
     *
     * Per ora usa getAnnuncio() e non aggiunge direttamente alla piattaforma,
     * perché non esiste ancora un metodo aggiungiAnnuncio(...).
     */
    public boolean aggiungiAnnuncio(Utente utente, Annuncio annuncio) {
        if (utente == null || annuncio == null) {
            return false;
        }

        // Controllo che l'utente sia registrato
        if (!piattaforma.getUtenti().contains(utente)) {
            return false;
        }

        // Aggiunge l'annuncio alla lista personale dell'utente
        if (!utente.getAnnunciCreati().contains(annuncio)) {
            utente.aggiungiAnnuncio(annuncio);
        }

        // Per il futuro: quando in Piattaforma ci sarà aggiungiAnnuncio(Annuncio),
        // qui si potrà chiamare: piattaforma.aggiungiAnnuncio(annuncio);
        // Per ora ci limitiamo a non fare nulla di più sulla piattaforma.

        return true;
    }

    /**
     * Collega una proposta sia alla piattaforma sia all'utente proponente.
     * La creazione della proposta viene fatta da ControllerScambi.
     *
     * Per ora non usa metodi su Piattaforma, perché non esistono ancora
     * getProposteScambio() né aggiungiPropostaScambio(...).
     */
    public boolean aggiungiProposta(Utente utente, PropostaScambio proposta) {
        if (utente == null || proposta == null) {
            return false;
        }

        if (!piattaforma.getUtenti().contains(utente)) {
            return false;
        }

        // Aggiunge la proposta alla lista personale dell'utente
        if (!utente.getProposteEffettuate().contains(proposta)) {
            utente.aggiungiProposta(proposta);
        }

        // Per il futuro: quando in Piattaforma ci sarà
        // aggiungiPropostaScambio(PropostaScambio), si potrà chiamare qui.

        return true;
    }

    /**
     * Salva una recensione nella lista delle recensioni ricevute
     * dall'utente destinatario.
     */
    public boolean aggiungiRecensione(Recensione recensione) {
        if (recensione == null) {
            return false;
        }

        if (!recensione.isValida()) {
            return false;
        }

        Utente destinatario = recensione.getDestinatario();

        if (destinatario == null) {
            return false;
        }

        if (!piattaforma.getUtenti().contains(destinatario)) {
            return false;
        }

        destinatario.aggiungiRecensioneRicevuta(recensione);
        return true;
    }

    /**
     * Restituisce le recensioni ricevute da un utente.
     */
    public List getRecensioni(Utente utente) {
        if (utente == null) {
            return new ArrayList();
        }

        return utente.getRecensioniRicevute();
    }

    /**
     * Restituisce la media dei voti ricevuti dall'utente.
     */
    public double calcolaMediaRecensioni(Utente utente) {
        if (utente == null) {
            return 0.0;
        }

        return utente.calcolaMediaRecensioni();
    }
}