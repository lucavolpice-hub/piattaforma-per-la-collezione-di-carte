package model;
import java.util.ArrayList;
import java.util.List;

public class AnnuncioScambio extends Annuncio {

    private double valoreDiRiferimento;
    private List<PropostaScambio> proposte; // Utile per tenere traccia delle proposte ricevute

    public AnnuncioScambio(String descrizione,
                           CategoriaCarta categoria,
                           Utente creatore,
                           double valoreDiRiferimento) {
        super(descrizione, categoria, creatore);
        this.valoreDiRiferimento = valoreDiRiferimento;
        this.proposte = new ArrayList<>();
    }
    public AnnuncioScambio(int idAnnuncio,
                           String descrizione,
                           CategoriaCarta categoria,
                           Utente creatore,
                           double valoreDiRiferimento,
                           StatoAnnuncio stato) {

        super(idAnnuncio, descrizione, categoria, creatore, stato);
        this.valoreDiRiferimento = valoreDiRiferimento;
        this.proposte = new ArrayList<>();
    }

    public double getValoreDiRiferimento() {
        return valoreDiRiferimento;
    }

    public void setValoreDiRiferimento(double valoreDiRiferimento) {
        this.valoreDiRiferimento = valoreDiRiferimento;
    }

    // Metodi specifici per lo scambio
    public void aggiungiProposta(PropostaScambio p) {
        this.proposte.add(p);
        // Cambia lo stato in IN_TRATTATIVA quando si riceve una proposta
        this.avviaTrattativa();
    }
    public void aggiungiPropostaCaricata(PropostaScambio p) {
        if (p == null) {
            return;
        }

        if (!proposte.contains(p)) {
            proposte.add(p);
        }
    }

    public void accettaProposta(PropostaScambio p) {

        this.concludi();
    }
    public List<PropostaScambio> getProposte() {
        return new ArrayList<>(proposte);
    }
}