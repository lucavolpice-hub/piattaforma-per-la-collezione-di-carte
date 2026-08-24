package model;
import java.util.List;
import java.util.ArrayList;

public class Inventario {
    private int idInventario;


    private List<CartaFisica> carte = new ArrayList<>();

    public Inventario() {
        this.idInventario = idInventario;
    }

    public void aggiungiCarta(CartaFisica nuovaCarta) {
        carte.add(nuovaCarta);
    }

    public void rimuoviCarta(CartaFisica sottraiCarta) {
        carte.remove(sottraiCarta);
    }

    public List<CartaFisica> getCarteDisponibili() {
        // 1. Crei una nuova lista vuota per le carte effettivamente disponibili
        List<CartaFisica> disponibili = new ArrayList<>();

        // 2. Controlli ogni carta presente nel tuo inventario (la lista 'carte')
        for (CartaFisica carta : carte) {
            // 3. Se la carta NON è bloccata, la aggiungi alla lista disponibili
            // (Assumendo che nella classe CartaFisica tu abbia creato il getter isBloccataInScambio)
            if (!carta.isBloccataInScambio()) {
                disponibili.add(carta);
            }
        }


        return disponibili;
    }
}
