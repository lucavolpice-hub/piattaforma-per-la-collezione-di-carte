package controller;
import dao.AnnuncioDAO;
import dao.AnnuncioFileDAO;
import dao.PropostaScambioDAO;
import dao.PropostaScambioFileDAO;
import dao.RecensioneDAO;
import dao.RecensioneFileDAO;
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


    private final InventarioDAO inventarioDAO;
    private final AnnuncioDAO annuncioDAO;
    private final PropostaScambioDAO propostaDAO;
    private final RecensioneDAO recensioneDAO;
    private final Piattaforma piattaforma;
    private final UtenteDAO utenteDAO;
    private final CartaFisicaDAO cartaDAO;

    public ControllerUtenti(Piattaforma piattaforma) {
        this.piattaforma = piattaforma;

        this.utenteDAO = new UtenteFileDAO();
        this.cartaDAO = new CartaFisicaFileDAO();
        this.inventarioDAO = new InventarioFileDAO();

        this.annuncioDAO = new AnnuncioFileDAO(utenteDAO,cartaDAO);

        this.propostaDAO = new PropostaScambioFileDAO(
                utenteDAO,
                annuncioDAO,
                cartaDAO
        );

        this.recensioneDAO = new RecensioneFileDAO(utenteDAO);

        // Ordine importante:
        caricaUtentiDalFile();
        caricaInventariDalFile();
        caricaAnnunciDalFile();
        caricaProposteDalFile();
        caricaRecensioniDalFile();
    }
    private void caricaAnnunciDalFile() {
        List<Annuncio> annunciSalvati = annuncioDAO.trovaTutti();

        for (Annuncio annuncioSalvato : annunciSalvati) {

            // Recuperiamo l'utente già presente nella piattaforma
            Utente creatore = cercaUtente(
                    annuncioSalvato.getCreatore().getUsername()
            );

            if (creatore == null) {
                continue;
            }

            Annuncio annuncio;

            if (annuncioSalvato instanceof model.AnnuncioVendita) {

                model.AnnuncioVendita vendita =
                        (model.AnnuncioVendita) annuncioSalvato;

                annuncio = new model.AnnuncioVendita(
                        vendita.getIdAnnuncio(),
                        vendita.getDescrizione(),
                        vendita.getCategoria(),
                        creatore,
                        vendita.getPrezzo(),
                        vendita.getStato()
                );

            } else if (annuncioSalvato instanceof model.AnnuncioScambio) {

                model.AnnuncioScambio scambio =
                        (model.AnnuncioScambio) annuncioSalvato;

                annuncio = new model.AnnuncioScambio(
                        scambio.getIdAnnuncio(),
                        scambio.getDescrizione(),
                        scambio.getCategoria(),
                        creatore,
                        scambio.getValoreDiRiferimento(),
                        scambio.getStato()
                );

            } else {
                continue;
            }

            piattaforma.getAnnuncio().add(annuncio);

            if (!creatore.getAnnunciCreati().contains(annuncio)) {
                creatore.aggiungiAnnuncio(annuncio);
            }
        }
    }
    private void caricaProposteDalFile() {
        List<PropostaScambio> proposteSalvate =
                propostaDAO.trovaTutte();

        for (PropostaScambio propostaSalvata : proposteSalvate) {

            Utente proponente = cercaUtente(
                    propostaSalvata.getProponente().getUsername()
            );

            if (proponente == null) {
                continue;
            }

            Annuncio annuncioGenerico = cercaAnnuncioInPiattaforma(
                    propostaSalvata.getAnnuncioRicevuto().getIdAnnuncio()
            );

            if (!(annuncioGenerico instanceof model.AnnuncioScambio)) {
                continue;
            }

            model.AnnuncioScambio annuncioRicevuto =
                    (model.AnnuncioScambio) annuncioGenerico;

            List<CartaFisica> carteOfferte = new ArrayList<>();

            for (CartaFisica cartaSalvata :
                    propostaSalvata.getCarteOfferte()) {

                CartaFisica cartaReale =
                        cercaCartaNelSistema(cartaSalvata.getIdCarta());

                if (cartaReale != null) {
                    carteOfferte.add(cartaReale);
                }
            }

            PropostaScambio proposta = new PropostaScambio(
                    propostaSalvata.getIdProposta(),
                    propostaSalvata.getData(),
                    propostaSalvata.getStato(),
                    proponente,
                    annuncioRicevuto,
                    carteOfferte
            );

            piattaforma.getProposteScambio().add(proposta);

            if (!proponente.getProposteEffettuate().contains(proposta)) {
                proponente.aggiungiProposta(proposta);
            }
        }
    }
    private Annuncio cercaAnnuncioInPiattaforma(int idAnnuncio) {
        for (Annuncio annuncio : piattaforma.getAnnuncio()) {
            if (annuncio.getIdAnnuncio() == idAnnuncio) {
                return annuncio;
            }
        }

        return null;
    }
    private CartaFisica cercaCartaNelSistema(int idCarta) {
        for (Utente utente : piattaforma.getUtenti()) {
            for (CartaFisica carta : utente.getInventario().getCarteDisponibili()) {
                if (carta.getIdCarta() == idCarta) {
                    return carta;
                }
            }
        }

        return null;
    }
    private void caricaRecensioniDalFile() {
        List<Recensione> recensioniSalvate =
                recensioneDAO.trovaTutte();

        for (Recensione recensioneSalvata : recensioniSalvate) {

            Utente autore = cercaUtente(
                    recensioneSalvata.getAutore().getUsername()
            );

            Utente destinatario = cercaUtente(
                    recensioneSalvata.getDestinatario().getUsername()
            );

            if (autore == null || destinatario == null) {
                continue;
            }

            Recensione recensione = new Recensione(
                    recensioneSalvata.getId(),
                    recensioneSalvata.getVoto(),
                    recensioneSalvata.getCommento(),
                    autore,
                    destinatario
            );

            destinatario.aggiungiRecensioneRicevuta(recensione);
        }
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