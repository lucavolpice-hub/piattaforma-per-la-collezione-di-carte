import controller.ControllerUtenti;
import controller.Piattaforma;

import model.Annuncio;
import model.CartaFisica;
import model.PropostaScambio;
import model.Utente;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // =============================
        // PRIMA APERTURA DELL'APPLICAZIONE
        // =============================

        List<Annuncio> listaAnnunci = new ArrayList<>();
        List<PropostaScambio> listaProposte = new ArrayList<>();

        Piattaforma piattaforma = new Piattaforma(
                listaAnnunci,
                listaProposte
        );

        ControllerUtenti controllerUtenti =
                new ControllerUtenti(piattaforma);

        Utente mario = controllerUtenti.cercaUtente("mario");

        if (mario == null) {
            System.out.println("Errore: Mario non è presente in utenti.txt");
            return;
        }

        CartaFisica pikachu = new CartaFisica(
                1,
                "Pikachu",
                "Near Mint",
                "Italiano"
        );

        boolean aggiunta = controllerUtenti.aggiungiCartaInventario(
                mario,
                pikachu
        );

        System.out.println("=== AGGIUNTA CARTA ===");
        System.out.println("Pikachu aggiunta a Mario: " + aggiunta);

        System.out.println("\n=== CARTE DI MARIO IN MEMORIA ===");

        for (CartaFisica carta :
                mario.getInventario().getCarteDisponibili()) {

            System.out.println(
                    "ID: " + carta.getIdCarta()
                            + " | " + carta
            );
        }

        // ==========================================
        // SIMULAZIONE: CHIUSURA E RIAPERTURA PROGRAMMA
        // ==========================================

        System.out.println("\n=== RIAVVIO SIMULATO ===");

        List<Annuncio> nuoviAnnunci = new ArrayList<>();
        List<PropostaScambio> nuoveProposte = new ArrayList<>();

        Piattaforma piattaformaDopoRiavvio = new Piattaforma(
                nuoviAnnunci,
                nuoveProposte
        );

        ControllerUtenti controllerDopoRiavvio =
                new ControllerUtenti(piattaformaDopoRiavvio);

        Utente marioDopoRiavvio =
                controllerDopoRiavvio.cercaUtente("mario");

        if (marioDopoRiavvio == null) {
            System.out.println(
                    "Errore: Mario non è stato ricaricato dopo il riavvio"
            );
            return;
        }

        System.out.println("\n=== CARTE DI MARIO DOPO IL RIAVVIO ===");

        for (CartaFisica carta :
                marioDopoRiavvio.getInventario().getCarteDisponibili()) {

            System.out.println(
                    "ID: " + carta.getIdCarta()
                            + " | " + carta
            );
        }
    }
}