package model;

public class CartaFisica {

    private final int idCarta;
    private final String nomeCarta;
    private String condizione;
    private String lingua;
    private boolean bloccataInScambio;

    public CartaFisica(int idCarta, String nomeCarta,
                       String condizione, String lingua) {
        this.idCarta = idCarta;
        this.nomeCarta = nomeCarta;
        this.condizione = condizione;
        this.lingua = lingua;
        this.bloccataInScambio = false;
    }

    public int getIdCarta() {
        return idCarta;
    }

    public String getNomeCarta() {
        return nomeCarta;
    }

    public String getCondizione() {
        return condizione;
    }

    public void setCondizione(String condizione) {
        this.condizione = condizione;
    }

    public String getLingua() {
        return lingua;
    }

    public void setLingua(String lingua) {
        this.lingua = lingua;
    }

    public boolean isBloccataInScambio() {
        return bloccataInScambio;
    }

    public void setBloccataInScambio(boolean bloccataInScambio) {
        this.bloccataInScambio = bloccataInScambio;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof CartaFisica)) {
            return false;
        }

        CartaFisica altraCarta = (CartaFisica) obj;

        return idCarta == altraCarta.idCarta;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idCarta);
    }

    @Override
    public String toString() {
        return nomeCarta + " (" + condizione + ", " + lingua + ")";
    }
}