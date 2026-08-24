package model;
public class AnnuncioVendita extends Annuncio {

    private double prezzo;

    public AnnuncioVendita(String descrizione,
                           CategoriaCarta categoria,
                           Utente creatore,
                           double prezzo) {
        super(descrizione, categoria, creatore);
        this.prezzo = prezzo;
    }


    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public void concludiAcquisto(Utente acquirente) {
        if (acquirente != null) {
            concludi();
        }
    }
}