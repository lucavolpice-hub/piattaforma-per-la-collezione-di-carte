import controller.ControllerScambi;
import controller.ControllerUtenti;
import controller.Piattaforma;

import model.Annuncio;
import model.AnnuncioScambio;
import model.CartaFisica;
import model.PropostaScambio;
import model.Utente;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // ==========================================
        // CARICAMENTO DELLA PIATTAFORMA
        // ==========================================

        Piattaforma piattaforma = new Piattaforma(
                new ArrayList<Annuncio>(),
                new ArrayList<PropostaScambio>()
        );

        ControllerUtenti controllerUtenti =
                new ControllerUtenti(piattaforma);

        ControllerScambi controllerScambi =
                new ControllerScambi(
                        piattaforma,
                        controllerUtenti
                );

        // ==========================================
        // RECUPERIAMO GLI UTENTI
        // ==========================================

        Utente mario =
                controllerUtenti.cercaUtente("mario");

        Utente peach =
                controllerUtenti.cercaUtente("peach");

        System.out.println("=== UTENTI ===");
        System.out.println("Mario: " + (mario != null));
        System.out.println("Peach: " + (peach != null));

        if (mario == null || peach == null) {
            System.out.println(
                    "Impossibile continuare: utenti mancanti."
            );
            return;
        }

        // ==========================================
        // RECUPERIAMO L'ANNUNCIO
        // ==========================================

        AnnuncioScambio annuncio = null;

        for (Annuncio a : piattaforma.getAnnuncio()) {
            if (a instanceof AnnuncioScambio) {
                annuncio = (AnnuncioScambio) a;
                break;
            }
        }

        System.out.println("\n=== ANNUNCIO ===");

        if (annuncio == null) {
            System.out.println("Nessun annuncio di scambio trovato.");
            return;
        }

        System.out.println(
                "Annuncio: #" + annuncio.getIdAnnuncio()
                        + " - "
                        + annuncio.getDescrizione()
        );

        System.out.println(
                "Creatore: "
                        + annuncio.getCreatore().getUsername()
        );

        System.out.println(
                "Stato: " + annuncio.getStato()
        );

        // ==========================================
        // INVENTARIO DI PEACH
        // ==========================================

        System.out.println("\n=== INVENTARIO PEACH ===");

        for (CartaFisica carta :
                peach.getInventario().getCarte()) {

            System.out.println(
                    "#" + carta.getIdCarta()
                            + " | "
                            + carta.getNomeCarta()
                            + " | bloccata: "
                            + carta.isBloccataInScambio()
            );
        }

        // ==========================================
        // RECUPERIAMO BULBASAUR E SQUIRTLE
        // ==========================================

        CartaFisica bulbasaur = null;
        CartaFisica squirtle = null;

        for (CartaFisica carta :
                peach.getInventario().getCarte()) {

            if (carta.getIdCarta() == 10) {
                bulbasaur = carta;
            }

            if (carta.getIdCarta() == 11) {
                squirtle = carta;
            }
        }

        if (bulbasaur == null || squirtle == null) {
            System.out.println(
                    "\nMancano Bulbasaur o Squirtle nell'inventario di Peach."
            );
            return;
        }

        // ==========================================
        // TEST 1: PROPOSTA VALIDA
        // ==========================================

        System.out.println(
                "\n=== TEST 1: PROPOSTA VALIDA ==="
        );

        List<CartaFisica> carteValide = new ArrayList<>();
        carteValide.add(bulbasaur);
        carteValide.add(squirtle);

        PropostaScambio propostaValida =
                controllerScambi.inviaProposta(
                        peach,
                        annuncio,
                        carteValide
                );

        System.out.println(
                "Proposta creata: "
                        + (propostaValida != null)
        );

        System.out.println(
                "Bulbasaur bloccata: "
                        + bulbasaur.isBloccataInScambio()
        );

        System.out.println(
                "Squirtle bloccata: "
                        + squirtle.isBloccataInScambio()
        );

        System.out.println(
                "Numero proposte piattaforma: "
                        + piattaforma.getProposteScambio().size()
        );

        // ==========================================
        // TEST 2: CARTA NON POSSEDUTA
        // ==========================================

        System.out.println(
                "\n=== TEST 2: CARTA NON POSSEDUTA ==="
        );

        CartaFisica cartaFalsa =
                new CartaFisica(
                        999,
                        "Charizard",
                        "Mint",
                        "Italiano"
                );

        List<CartaFisica> carteNonPossedute =
                new ArrayList<>();

        carteNonPossedute.add(cartaFalsa);

        PropostaScambio propostaNonValida =
                controllerScambi.inviaProposta(
                        peach,
                        annuncio,
                        carteNonPossedute
                );

        System.out.println(
                "Proposta creata: "
                        + (propostaNonValida != null)
        );

        System.out.println(
                "Numero proposte piattaforma: "
                        + piattaforma.getProposteScambio().size()
        );

        // ==========================================
        // TEST 3: CARTA GIÀ BLOCCATA
        // ==========================================

        System.out.println(
                "\n=== TEST 3: CARTA GIÀ BLOCCATA ==="
        );

        List<CartaFisica> cartaGiaBloccata =
                new ArrayList<>();

        cartaGiaBloccata.add(bulbasaur);

        PropostaScambio secondaProposta =
                controllerScambi.inviaProposta(
                        peach,
                        annuncio,
                        cartaGiaBloccata
                );

        System.out.println(
                "Seconda proposta creata: "
                        + (secondaProposta != null)
        );

        System.out.println(
                "Numero proposte piattaforma: "
                        + piattaforma.getProposteScambio().size()
        );
    }
}