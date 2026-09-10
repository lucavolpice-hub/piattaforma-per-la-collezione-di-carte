package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una proposta inviata da un utente
 * in risposta a un annuncio di scambio.
 */
public class PropostaScambio {

    private static int contatore = 0;

    private int idProposta;
    private LocalDate data;
    private StatoProposta stato;
    private Utente proponente;
    private AnnuncioScambio annuncioRicevuto;
    private List<CartaFisica> carteOfferte;

    /**
     * Crea una proposta con le carte offerte dal proponente.
     */
    public PropostaScambio(Utente proponente,
                           AnnuncioScambio annuncioRicevuto,
                           List<CartaFisica> carteOfferte) {
        this.idProposta = ++contatore;
        this.data = LocalDate.now();
        this.stato = StatoProposta.IN_ATTESA;
        this.proponente = proponente;
        this.annuncioRicevuto = annuncioRicevuto;

        // La proposta conserva una propria lista delle carte offerte.
        this.carteOfferte = new ArrayList<>();

        if (carteOfferte != null) {
            this.carteOfferte.addAll(carteOfferte);
        }
    }
    /**
     * Costruttore utilizzato dalla DAO per ricostruire
     * una proposta già esistente.
     */
    public PropostaScambio(
            int idProposta,
            LocalDate data,
            StatoProposta stato,
            Utente proponente,
            AnnuncioScambio annuncioRicevuto,
            List<CartaFisica> carteOfferte
    ) {

        this.idProposta = idProposta;
        this.data = data;
        this.stato = stato;
        this.proponente = proponente;
        this.annuncioRicevuto = annuncioRicevuto;

        this.carteOfferte = new ArrayList<>();

        if (carteOfferte != null) {
            this.carteOfferte.addAll(carteOfferte);
        }

        if (idProposta > contatore) {
            contatore = idProposta;
        }
    }

    public int getIdProposta() {
        return idProposta;
    }

    public LocalDate getData() {
        return data;
    }

    public StatoProposta getStato() {
        return stato;
    }

    public void setStato(StatoProposta stato) {
        this.stato = stato;
    }

    public Utente getProponente() {
        return proponente;
    }

    public AnnuncioScambio getAnnuncioRicevuto() {
        return annuncioRicevuto;
    }

    public List<CartaFisica> getCarteOfferte() {
        return carteOfferte;
    }

    public void aggiungiCartaOfferta(CartaFisica carta) {
        if (carta != null) {
            carteOfferte.add(carta);
        }
    }

    public boolean rimuoviCartaOfferta(CartaFisica carta) {
        return carteOfferte.remove(carta);
    }

    public void accetta() {
        stato = StatoProposta.ACCETTATA;
    }

    public void rifiuta() {
        stato = StatoProposta.RIFIUTATA;
    }
    @Override
    public String toString() {
        return "Proposta scambio #" + idProposta
                + " del " + data + " [" + stato + "]";
    }
}