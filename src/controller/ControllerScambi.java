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
        if (proponente == null || annuncio == null) {
            return null;
        }

        if (carteOfferte == null || carteOfferte.isEmpty()) {
            return null;
        }

        PropostaScambio proposta =
                new PropostaScambio(proponente, annuncio, carteOfferte);

        // 1) Aggiungo alla lista globale della piattaforma
        piattaforma.getProposteScambio().add(proposta);

        try {
            // 2) Collego la proposta all'annuncio: lo stato passa a IN_TRATTATIVA
            annuncio.aggiungiProposta(proposta);
        } catch (Exception e) {
            // In caso di errore imprevisto, la proposta resta comunque
            // registrata in piattaforma, ma segnaliamo il problema.
            return null;
        }

        // 3) Collego la proposta all'utente proponente
        controllerUtenti.aggiungiProposta(proponente, proposta);

        return proposta;
    }

    /**
     * Cerca una proposta tramite il suo ID.
     *
     * @return la proposta trovata, oppure null se non esiste
     */
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