package controller;

import java.util.ArrayList;
import java.util.List;

import model.AnnuncioScambio;
import model.CartaFisica;
import model.PropostaScambio;
import model.Utente;

/**
 * Gestisce l'invio, la ricerca e la gestione delle proposte di scambio.
 */
public class ControllerScambi {

    private final Piattaforma piattaforma;
    private final ControllerUtenti controllerUtenti;

    public ControllerScambi(Piattaforma piattaforma,
                            ControllerUtenti controllerUtenti) {
        this.piattaforma = piattaforma;
        this.controllerUtenti = controllerUtenti;
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
        for (CartaFisica carta : carteOfferte) {
            carta.setBloccataInScambio(true);
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
    public boolean accettaProposta(PropostaScambio proposta) {
        if (proposta == null || !piattaforma.getProposteScambio().contains(proposta)) {
            return false;
        }

        try {
            proposta.accetta();
            proposta.getAnnuncioRicevuto().accettaProposta(proposta);
            return true;
        } catch (Exception e) {
            // In caso di errore imprevisto durante l'accettazione,
            // restituiamo false per segnalare che l'operazione non è riuscita.
            return false;
        }
    }

    /**
     * Rifiuta una proposta, ma solo se è stata effettivamente
     * registrata tramite questo controller.
     */
    public boolean rifiutaProposta(PropostaScambio proposta) {
        if (proposta == null || !piattaforma.getProposteScambio().contains(proposta)) {
            return false;
        }

        proposta.rifiuta();
        return true;
    }
}