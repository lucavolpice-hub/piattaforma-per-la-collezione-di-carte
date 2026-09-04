package controller;

import dao.CartaFisicaDAO;
import dao.CartaFisicaFileDAO;
import dao.InventarioDAO;
import dao.InventarioFileDAO;
import dao.UtenteDAO;
import dao.UtenteFileDAO;

import model.Annuncio;
import model.CartaFisica;
import model.PropostaScambio;
import model.Recensione;
import model.Utente;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce registrazione, login, inventario e recensioni degli utenti.
 */
public class ControllerUtenti {

    private final Piattaforma piattaforma;
    private final UtenteDAO utenteDAO;
    private final CartaFisicaDAO cartaDAO;
    private final InventarioDAO inventarioDAO;

    public ControllerUtenti(Piattaforma piattaforma) {
        this.piattaforma = piattaforma;
        this.utenteDAO = new UtenteFileDAO();
        this.cartaDAO = new CartaFisicaFileDAO();
        this.inventarioDAO = new InventarioFileDAO();

        caricaUtentiDalFile();
        caricaInventariDalFile();
    }

    private void caricaUtentiDalFile() {
        List<Utente> utentiSalvati = utenteDAO.trovaTutti();

        for (Utente utente : utentiSalvati) {
            if (cercaUtente(utente.getUsername()) == null) {
                piattaforma.aggiungiUtente(utente);
            }
        }
    }

    private void caricaInventariDalFile() {
        for (Utente utente : piattaforma.getUtenti()) {
            List<Integer> idCarte =
                    inventarioDAO.trovaIdCartePerUtente(
                            utente.getUsername()
                    );

            for (Integer idCarta : idCarte) {
                CartaFisica carta = cartaDAO.cercaPerId(idCarta);

                if (carta != null) {
                    utente.getInventario().aggiungiCarta(carta);
                }
            }
        }
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

        return cercaUtente(username) == null;
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

        if (!utenteDAO.salva(nuovoUtente)) {
            return false;
        }

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

        String vecchiaPassword = utente.getPassword();
        utente.setPassword(nuovaPassword);

        if (!utenteDAO.aggiorna(utente)) {
            utente.setPassword(vecchiaPassword);
            return false;
        }

        return true;
    }

    /**
     * Aggiunge una carta all'inventario di un utente registrato,
     * salvando carta e relazione utente-carta nei file.
     */
    public boolean aggiungiCartaInventario(
            Utente utente,
            CartaFisica carta
    ) {
        if (utente == null || carta == null) {
            return false;
        }

        if (!piattaforma.getUtenti().contains(utente)) {
            return false;
        }

        if (cartaDAO.cercaPerId(carta.getIdCarta()) != null) {
            return false;
        }

        boolean cartaSalvata = cartaDAO.salva(carta);

        if (!cartaSalvata) {
            return false;
        }

        boolean collegamentoSalvato = inventarioDAO.aggiungiCarta(
                utente.getUsername(),
                carta.getIdCarta()
        );

        if (!collegamentoSalvato) {
            cartaDAO.elimina(carta.getIdCarta());
            return false;
        }

        utente.getInventario().aggiungiCarta(carta);

        return true;
    }

    /**
     * Rimuove una carta dall'inventario dell'utente e dai file.
     */
    public boolean rimuoviCartaInventario(
            Utente utente,
            CartaFisica carta
    ) {
        if (utente == null || carta == null) {
            return false;
        }

        if (!piattaforma.getUtenti().contains(utente)) {
            return false;
        }

        boolean collegamentoRimosso = inventarioDAO.rimuoviCarta(
                utente.getUsername(),
                carta.getIdCarta()
        );

        if (!collegamentoRimosso) {
            return false;
        }

        boolean cartaEliminata = cartaDAO.elimina(carta.getIdCarta());

        if (!cartaEliminata) {
            inventarioDAO.aggiungiCarta(
                    utente.getUsername(),
                    carta.getIdCarta()
            );
            return false;
        }

        utente.getInventario().rimuoviCarta(carta);

        return true;
    }

    /**
     * Restituisce le carte non bloccate in uno scambio.
     */
    public List<CartaFisica> getCarteDisponibili(Utente utente) {
        if (utente == null) {
            return new ArrayList<>();
        }

        return utente.getInventario().getCarteDisponibili();
    }

    /**
     * Collega un annuncio al suo creatore.
     * L'aggiunta dell'annuncio alla piattaforma viene gestita
     * da ControllerAnnunci.
     */
    public boolean aggiungiAnnuncio(Utente utente, Annuncio annuncio) {
        if (utente == null || annuncio == null) {
            return false;
        }

        if (!piattaforma.getUtenti().contains(utente)) {
            return false;
        }

        if (!utente.getAnnunciCreati().contains(annuncio)) {
            utente.aggiungiAnnuncio(annuncio);
        }

        return true;
    }

    /**
     * Collega una proposta all'utente proponente.
     * L'aggiunta globale viene gestita da ControllerScambi.
     */
    public boolean aggiungiProposta(
            Utente utente,
            PropostaScambio proposta
    ) {
        if (utente == null || proposta == null) {
            return false;
        }

        if (!piattaforma.getUtenti().contains(utente)) {
            return false;
        }

        if (!utente.getProposteEffettuate().contains(proposta)) {
            utente.aggiungiProposta(proposta);
        }

        return true;
    }

    /**
     * Salva una recensione nella lista delle recensioni ricevute
     * dall'utente destinatario.
     */
    public boolean aggiungiRecensione(Recensione recensione) {
        if (recensione == null || !recensione.isValida()) {
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
    public List<Recensione> getRecensioni(Utente utente) {
        if (utente == null) {
            return new ArrayList<>();
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