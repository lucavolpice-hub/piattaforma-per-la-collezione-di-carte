package controller;

import java.util.ArrayList;
import java.util.List;
import dao.CartaFisicaDAO;
import dao.CartaFisicaFileDAO;
import model.AnnuncioScambio;
import model.CartaFisica;
import model.PropostaScambio;
import model.Utente;
import dao.InventarioDAO;
import dao.InventarioFileDAO;
import dao.AnnuncioDAO;
import dao.AnnuncioFileDAO;
import dao.PropostaScambioDAO;
import dao.PropostaScambioFileDAO;
import dao.UtenteDAO;
import dao.UtenteFileDAO;

/**
 * Gestisce l'invio, la ricerca e la gestione delle proposte di scambio.
 */
public class ControllerScambi {

    private final Piattaforma piattaforma;
    private final ControllerUtenti controllerUtenti;
    private final CartaFisicaDAO cartaDAO;
    private final InventarioDAO inventarioDAO;
    private final UtenteDAO utenteDAO;
    private final AnnuncioDAO annuncioDAO;
    private final PropostaScambioDAO propostaDAO;

    public ControllerScambi(Piattaforma piattaforma,
                            ControllerUtenti controllerUtenti) {

        this.piattaforma = piattaforma;
        this.controllerUtenti = controllerUtenti;

        this.utenteDAO = new UtenteFileDAO();

        this.cartaDAO = new CartaFisicaFileDAO();

        this.inventarioDAO = new InventarioFileDAO();

        this.annuncioDAO =
                new AnnuncioFileDAO(
                        utenteDAO,
                        cartaDAO
                );

        this.propostaDAO =
                new PropostaScambioFileDAO(
                        utenteDAO,
                        annuncioDAO,
                        cartaDAO
                );
    }

    /**
     * Crea una proposta di scambio su un annuncio e la registra sia
     * nella piattaforma sia nella lista personale del proponente.
     *
     * @return la proposta creata, oppure null se i dati non sono validi
     */
    public PropostaScambio inviaProposta(Utente proponente,
                                         AnnuncioScambio annuncio,
                                         List<CartaFisica> carteOfferte) {

        // 1. Controlli di base
        if (proponente == null || annuncio == null) {
            return null;
        }

        if (carteOfferte == null || carteOfferte.isEmpty()) {
            return null;
        }

        // 2. Il proponente deve essere un utente della piattaforma
        Utente utenteRegistrato =
                controllerUtenti.cercaUtente(proponente.getUsername());

        if (utenteRegistrato == null) {
            return null;
        }

        // 3. Non puoi proporre uno scambio sul tuo stesso annuncio
        if (annuncio.getCreatore().getUsername()
                .equals(proponente.getUsername())) {
            return null;
        }

        // 4. L'annuncio deve essere ancora disponibile
        if (annuncio.getStato() != model.StatoAnnuncio.DISPONIBILE) {
            return null;
        }

        // 5. Controlliamo che non ci siano carte duplicate
        for (int i = 0; i < carteOfferte.size(); i++) {
            for (int j = i + 1; j < carteOfferte.size(); j++) {

                if (carteOfferte.get(i).getIdCarta()
                        == carteOfferte.get(j).getIdCarta()) {
                    return null;
                }
            }
        }

        // 6. Controlliamo che tutte le carte appartengano
        //    realmente all'inventario del proponente
        List<CartaFisica> carteDisponibili =
                utenteRegistrato.getInventario().getCarteDisponibili();

        for (CartaFisica carta : carteOfferte) {

            if (carta == null) {
                return null;
            }

            boolean posseduta = false;

            for (CartaFisica cartaInventario : carteDisponibili) {

                if (cartaInventario.getIdCarta()
                        == carta.getIdCarta()) {

                    posseduta = true;
                    break;
                }
            }

            if (!posseduta) {
                return null;
            }
        }

        // 7. Blocchiamo le carte prima di creare la proposta
        List<CartaFisica> carteBloccate = new ArrayList<>();

        for (CartaFisica carta : carteOfferte) {

            carta.setBloccataInScambio(true);

            if (!cartaDAO.aggiorna(carta)) {

                // Rollback delle carte già bloccate
                for (CartaFisica cartaBloccata : carteBloccate) {
                    cartaBloccata.setBloccataInScambio(false);
                    cartaDAO.aggiorna(cartaBloccata);
                }

                // La carta che ha appena fallito viene ripristinata
                carta.setBloccataInScambio(false);

                return null;
            }

            carteBloccate.add(carta);
        }

        // 8. Creiamo la proposta
        PropostaScambio proposta =
                new PropostaScambio(
                        utenteRegistrato,
                        annuncio,
                        new ArrayList<>(carteOfferte)
                );

        // 9. Registriamo la proposta nella piattaforma
        piattaforma.getProposteScambio().add(proposta);

        try {
            // 10. Colleghiamo la proposta all'annuncio
            annuncio.aggiungiProposta(proposta);

            // 11. Colleghiamo la proposta al proponente
            controllerUtenti.aggiungiProposta(
                    utenteRegistrato,
                    proposta
            );

            return proposta;

        } catch (Exception e) {

            // Rollback:
            // se qualcosa va male, sblocchiamo le carte
            for (CartaFisica carta : carteOfferte) {
                carta.setBloccataInScambio(false);
            }

            // e rimuoviamo la proposta dalla piattaforma
            piattaforma.getProposteScambio().remove(proposta);

            return null;
        }
    }

    public PropostaScambio cercaPropostaPerId(int idProposta) {
        for (PropostaScambio proposta : piattaforma.getProposteScambio()) {
            if (proposta.getIdProposta() == idProposta) {
                return proposta;
            }
        }

        return null;
    }

    /**
     * Restituisce tutte le proposte ricevute su un dato annuncio di scambio.
     */
    public List<PropostaScambio> getProposteRicevute(AnnuncioScambio annuncio) {
        List<PropostaScambio> risultato = new ArrayList<>();

        if (annuncio == null) {
            return risultato;
        }

        for (PropostaScambio proposta : piattaforma.getProposteScambio()) {
            if (proposta.getAnnuncioRicevuto() == annuncio) {
                risultato.add(proposta);
            }
        }

        return risultato;
    }

    /**
     * Accetta una proposta, ma solo se è stata effettivamente
     * registrata tramite questo controller.
     */
    public boolean accettaProposta(
            Utente utenteCheAccetta,
            PropostaScambio proposta
    ) {

        // ==========================================
        // 1. CONTROLLI DI BASE
        // ==========================================

        if (utenteCheAccetta == null || proposta == null) {
            return false;
        }

        if (!piattaforma.getProposteScambio().contains(proposta)) {
            return false;
        }

        // ==========================================
        // 2. LA PROPOSTA DEVE ESSERE IN ATTESA
        // ==========================================

        if (proposta.getStato()
                != model.StatoProposta.IN_ATTESA) {
            return false;
        }

        AnnuncioScambio annuncio =
                proposta.getAnnuncioRicevuto();

        if (annuncio == null) {
            return false;
        }

        // ==========================================
        // 3. L'ANNUNCIO DEVE ESSERE IN TRATTATIVA
        // ==========================================

        if (annuncio.getStato()
                != model.StatoAnnuncio.IN_TRATTATIVA) {
            return false;
        }

        // ==========================================
        // 4. RECUPERIAMO I DUE UTENTI
        // ==========================================

        Utente proprietarioAnnuncio =
                annuncio.getCreatore();

        Utente proponente =
                proposta.getProponente();

        if (proprietarioAnnuncio == null
                || proponente == null) {
            return false;
        }

        // ==========================================
        // 5. SOLO IL PROPRIETARIO DELL'ANNUNCIO
        //    PUÒ ACCETTARE LA PROPOSTA
        // ==========================================

        if (!proprietarioAnnuncio.getUsername()
                .equalsIgnoreCase(
                        utenteCheAccetta.getUsername())) {
            return false;
        }

        // ==========================================
        // 6. IL PROPRIETARIO NON PUÒ ESSERE
        //    ANCHE IL PROPONENTE
        // ==========================================

        if (proprietarioAnnuncio.getUsername()
                .equalsIgnoreCase(proponente.getUsername())) {
            return false;
        }

        // ==========================================
        // 7. RECUPERIAMO LE CARTE
        // ==========================================

        List<CartaFisica> carteRichieste =
                annuncio.getCarte();

        List<CartaFisica> carteOfferte =
                proposta.getCarteOfferte();

        if (carteRichieste == null
                || carteRichieste.isEmpty()
                || carteOfferte == null
                || carteOfferte.isEmpty()) {
            return false;
        }

        // ==========================================
        // 8. LE CARTE DELL'ANNUNCIO DEVONO
        //    ESSERE ANCORA DEL PROPRIETARIO
        // ==========================================

        for (CartaFisica carta : carteRichieste) {

            boolean presente = false;

            for (CartaFisica cartaInventario :
                    proprietarioAnnuncio
                            .getInventario()
                            .getCarte()) {

                if (cartaInventario.getIdCarta()
                        == carta.getIdCarta()) {

                    presente = true;
                    break;
                }
            }

            if (!presente) {
                return false;
            }

            // Le carte dell'annuncio devono essere disponibili
            if (carta.isBloccataInScambio()) {
                return false;
            }
        }

        // ==========================================
        // 9. LE CARTE OFFERTE DEVONO ESSERE
        //    ANCORA DI PROPRIETÀ DEL PROPONENTE
        // ==========================================

        for (CartaFisica carta : carteOfferte) {

            boolean presente = false;

            for (CartaFisica cartaInventario :
                    proponente
                            .getInventario()
                            .getCarte()) {

                if (cartaInventario.getIdCarta()
                        == carta.getIdCarta()) {

                    presente = true;
                    break;
                }
            }

            if (!presente) {
                return false;
            }

            // Devono essere bloccate perché appartengono
            // alla proposta che stiamo accettando.
            if (!carta.isBloccataInScambio()) {
                return false;
            }
        }

        // ==========================================
        // 10. TRASFERIMENTO
        // ==========================================

        boolean trasferimentoRiuscito =
                trasferisciCarte(
                        proponente,
                        proprietarioAnnuncio,
                        carteOfferte,
                        carteRichieste
                );

        if (!trasferimentoRiuscito) {
            return false;
        }

        // ==========================================
        // 11. AGGIORNAMENTO DEGLI STATI
        // ==========================================

        proposta.accetta();

        annuncio.concludi();
        if (!propostaDAO.aggiorna(proposta)) {
            return false;
        }

        if (!annuncioDAO.aggiorna(annuncio)) {
            return false;
        }

        // ==========================================
        // 12. RIFIUTIAMO LE ALTRE PROPOSTE
        // ==========================================

        for (PropostaScambio altraProposta :
                piattaforma.getProposteScambio()) {

            if (altraProposta == proposta) {
                continue;
            }

            AnnuncioScambio altroAnnuncio =
                    altraProposta.getAnnuncioRicevuto();

            if (altroAnnuncio == null) {
                continue;
            }

            if (altroAnnuncio.getIdAnnuncio()
                    != annuncio.getIdAnnuncio()) {
                continue;
            }

            if (altraProposta.getStato()
                    != model.StatoProposta.IN_ATTESA) {
                continue;
            }

            altraProposta.rifiuta();

            if (!propostaDAO.aggiorna(altraProposta)) {
                return false;
            }

            for (CartaFisica carta :
                    altraProposta.getCarteOfferte()) {

                carta.setBloccataInScambio(false);

                if (!cartaDAO.aggiorna(carta)) {
                    return false;
                }
            }
        }
        return true;
    }


    public boolean rifiutaProposta(PropostaScambio proposta) {

            if (proposta == null) {
                return false;
            }

            if (!piattaforma.getProposteScambio().contains(proposta)) {
                return false;
            }

            if (proposta.getStato()
                    != model.StatoProposta.IN_ATTESA) {
                return false;
            }

            proposta.rifiuta();

            // Sblocchiamo le carte della proposta
            for (CartaFisica carta :
                    proposta.getCarteOfferte()) {

                carta.setBloccataInScambio(false);

                if (!cartaDAO.aggiorna(carta)) {
                    return false;
                }
            }

            // Persistiamo lo stato della proposta
            if (!propostaDAO.aggiorna(proposta)) {
                return false;
            }

            return true;
        }

    private boolean trasferisciCarte(
            Utente proponente,
            Utente proprietarioAnnuncio,
            List<CartaFisica> carteOfferte,
            List<CartaFisica> carteRichieste
    ) {

        if (proponente == null
                || proprietarioAnnuncio == null
                || carteOfferte == null
                || carteRichieste == null) {
            return false;
        }

        /*
         * Prima di modificare qualsiasi cosa controlliamo che
         * tutte le carte siano ancora effettivamente nei rispettivi
         * inventari.
         */

        for (CartaFisica carta : carteOfferte) {

            boolean presente = false;

            for (CartaFisica cartaInventario :
                    proponente.getInventario().getCarte()) {

                if (cartaInventario.getIdCarta()
                        == carta.getIdCarta()) {
                    presente = true;
                    break;
                }
            }

            if (!presente) {
                return false;
            }
        }

        for (CartaFisica carta : carteRichieste) {

            boolean presente = false;

            for (CartaFisica cartaInventario :
                    proprietarioAnnuncio.getInventario().getCarte()) {

                if (cartaInventario.getIdCarta()
                        == carta.getIdCarta()) {
                    presente = true;
                    break;
                }
            }

            if (!presente) {
                return false;
            }
        }

        /*
         * Conserviamo gli ID delle carte in caso sia necessario
         * ripristinare le relazioni nel file.
         */
        List<Integer> offerteRimosse = new ArrayList<>();
        List<Integer> richiesteRimosse = new ArrayList<>();

        try {

            // ==========================================
            // 1. RIMUOVIAMO LE CARTE DA PEACH
            // ==========================================

            for (CartaFisica carta : carteOfferte) {

                if (!inventarioDAO.rimuoviCarta(
                        proponente.getUsername(),
                        carta.getIdCarta())) {

                    throw new IllegalStateException(
                            "Impossibile rimuovere la carta "
                                    + carta.getIdCarta()
                                    + " da "
                                    + proponente.getUsername()
                    );
                }

                offerteRimosse.add(carta.getIdCarta());
            }

            // ==========================================
            // 2. RIMUOVIAMO LE CARTE DA MARIO
            // ==========================================

            for (CartaFisica carta : carteRichieste) {

                if (!inventarioDAO.rimuoviCarta(
                        proprietarioAnnuncio.getUsername(),
                        carta.getIdCarta())) {

                    throw new IllegalStateException(
                            "Impossibile rimuovere la carta "
                                    + carta.getIdCarta()
                                    + " da "
                                    + proprietarioAnnuncio.getUsername()
                    );
                }

                richiesteRimosse.add(carta.getIdCarta());
            }

            // ==========================================
            // 3. AGGIUNGIAMO LE CARTE DI PEACH A MARIO
            // ==========================================

            for (CartaFisica carta : carteOfferte) {

                if (!inventarioDAO.aggiungiCarta(
                        proprietarioAnnuncio.getUsername(),
                        carta.getIdCarta())) {

                    throw new IllegalStateException(
                            "Impossibile aggiungere la carta "
                                    + carta.getIdCarta()
                                    + " a "
                                    + proprietarioAnnuncio.getUsername()
                    );
                }
            }

            // ==========================================
            // 4. AGGIUNGIAMO LE CARTE DI MARIO A PEACH
            // ==========================================

            for (CartaFisica carta : carteRichieste) {

                if (!inventarioDAO.aggiungiCarta(
                        proponente.getUsername(),
                        carta.getIdCarta())) {

                    throw new IllegalStateException(
                            "Impossibile aggiungere la carta "
                                    + carta.getIdCarta()
                                    + " a "
                                    + proponente.getUsername()
                    );
                }
            }

            // ==========================================
            // 5. AGGIORNIAMO GLI INVENTARI IN MEMORIA
            // ==========================================

            for (CartaFisica carta : carteOfferte) {

                proponente.getInventario().rimuoviCarta(carta);

                proprietarioAnnuncio
                        .getInventario()
                        .aggiungiCarta(carta);
            }

            for (CartaFisica carta : carteRichieste) {

                proprietarioAnnuncio
                        .getInventario()
                        .rimuoviCarta(carta);

                proponente
                        .getInventario()
                        .aggiungiCarta(carta);
            }

            // ==========================================
            // 6. SBLOCCO DELLE CARTE
            // ==========================================

            for (CartaFisica carta : carteOfferte) {

                carta.setBloccataInScambio(false);

                if (!cartaDAO.aggiorna(carta)) {
                    throw new IllegalStateException(
                            "Impossibile aggiornare la carta "
                                    + carta.getIdCarta()
                    );
                }
            }

            for (CartaFisica carta : carteRichieste) {

                carta.setBloccataInScambio(false);

                if (!cartaDAO.aggiorna(carta)) {
                    throw new IllegalStateException(
                            "Impossibile aggiornare la carta "
                                    + carta.getIdCarta()
                    );
                }
            }

            return true;

        } catch (Exception e) {

            // ==========================================
            // ROLLBACK DEL FILE INVENTARI
            // ==========================================

            /*
             * Prima eliminiamo eventuali carte che siamo riusciti
             * ad aggiungere ai nuovi proprietari.
             */

            for (Integer idCarta : offerteRimosse) {

                inventarioDAO.rimuoviCarta(
                        proprietarioAnnuncio.getUsername(),
                        idCarta
                );
            }

            for (Integer idCarta : richiesteRimosse) {

                inventarioDAO.rimuoviCarta(
                        proponente.getUsername(),
                        idCarta
                );
            }

            /*
             * Poi ripristiniamo la proprietà originale.
             */

            for (Integer idCarta : offerteRimosse) {

                inventarioDAO.aggiungiCarta(
                        proponente.getUsername(),
                        idCarta
                );
            }

            for (Integer idCarta : richiesteRimosse) {

                inventarioDAO.aggiungiCarta(
                        proprietarioAnnuncio.getUsername(),
                        idCarta
                );
            }

            /*
             * Ripristiniamo lo stato delle carte offerte.
             * Anche se il trasferimento fallisce, devono rimanere
             * bloccate perché la proposta è ancora esistente.
             */

            for (CartaFisica carta : carteOfferte) {
                carta.setBloccataInScambio(true);
                cartaDAO.aggiorna(carta);
            }

            return false;
        }
    }
}