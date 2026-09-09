package model;

import java.util.ArrayList;
import java.util.List;

public class Inventario {

    private final List<CartaFisica> carte = new ArrayList<>();

    public Inventario() {
    }

    public void aggiungiCarta(CartaFisica nuovaCarta) {
        if (nuovaCarta == null) {
            return;
        }

        if (!carte.contains(nuovaCarta)) {
            carte.add(nuovaCarta);
        }
    }

    public void rimuoviCarta(CartaFisica cartaDaRimuovere) {
        if (cartaDaRimuovere == null) {
            return;
        }

        carte.remove(cartaDaRimuovere);
    }

    /**
     * Restituisce le carte che non sono attualmente bloccate
     * in uno scambio.
     */
    public List<CartaFisica> getCarteDisponibili() {
        List<CartaFisica> disponibili = new ArrayList<>();

        for (CartaFisica carta : carte) {
            if (!carta.isBloccataInScambio()) {
                disponibili.add(carta);
            }
        }

        return disponibili;
    }

    /**
     * Restituisce tutte le carte dell'inventario,
     * comprese quelle eventualmente bloccate.
     */
    public List<CartaFisica> getCarte() {
        return new ArrayList<>(carte);
    }
}
