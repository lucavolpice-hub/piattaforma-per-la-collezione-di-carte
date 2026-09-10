import controller.ControllerScambi;
import controller.ControllerUtenti;
import controller.Piattaforma;

import model.Annuncio;
import model.AnnuncioScambio;
import model.CartaFisica;
import model.PropostaScambio;
import model.Utente;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== DEBUG ===");
        System.out.println(
                "Working directory: "
                        + System.getProperty("user.dir")
        );

        System.out.println(
                "File inventari: "
                        + java.nio.file.Paths
                        .get("data", "inventari.txt")
                        .toAbsolutePath()
        );

        System.out.println("Contenuto inventari.txt:");

        try {
            java.nio.file.Files.lines(
                    java.nio.file.Paths.get("data", "inventari.txt")
            ).forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ==========================================
        // CARICAMENTO DELLA PIATTAFORMA
        // ==========================================

        Piattaforma piattaforma =
                new Piattaforma(
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
        // RECUPERIAMO MARIO E PEACH
        // ==========================================

        Utente mario =
                controllerUtenti.cercaUtente("mario");

        Utente peach =
                controllerUtenti.cercaUtente("peach");

        if (mario == null || peach == null) {
            System.out.println(
                    "Errore: Mario o Peach non trovati."
            );
            return;
        }

        // ==========================================
        // STATO INIZIALE
        // ==========================================

        System.out.println("=== STATO INIZIALE ===");

        stampaInventario(mario);
        stampaInventario(peach);

        // ==========================================
        // CERCHIAMO L'ANNUNCIO
        // ==========================================

        AnnuncioScambio annuncio = null;

        for (Annuncio a :
                piattaforma.getAnnuncio()) {

            if (a instanceof AnnuncioScambio) {
                annuncio = (AnnuncioScambio) a;
                break;
            }
        }

        if (annuncio == null) {
            System.out.println(
                    "Nessun annuncio di scambio trovato."
            );
            return;
        }

        System.out.println(
                "\n=== ANNUNCIO ==="
        );

        System.out.println(
                "ID: #" + annuncio.getIdAnnuncio()
        );

        System.out.println(
                "Descrizione: "
                        + annuncio.getDescrizione()
        );

        System.out.println(
                "Creatore: "
                        + annuncio.getCreatore().getUsername()
        );

        System.out.println(
                "Stato: "
                        + annuncio.getStato()
        );

        System.out.println("Carte:");

        for (CartaFisica carta :
                annuncio.getCarte()) {

            System.out.println(
                    "#" + carta.getIdCarta()
                            + " | "
                            + carta.getNomeCarta()
                            + " | bloccata: "
                            + carta.isBloccataInScambio()
            );
        }

        // ==========================================
        // CERCHIAMO UNA PROPOSTA DI PEACH
        // ==========================================

        PropostaScambio proposta = null;

        for (PropostaScambio p :
                piattaforma.getProposteScambio()) {

            if (p.getProponente()
                    .getUsername()
                    .equalsIgnoreCase("peach")
                    &&
                    p.getAnnuncioRicevuto()
                            .getIdAnnuncio()
                            == annuncio.getIdAnnuncio()
                    &&
                    p.getStato()
                            == model.StatoProposta.IN_ATTESA) {

                proposta = p;
                break;
            }
        }

        if (proposta == null) {
            System.out.println(
                    "\nNessuna proposta di Peach in attesa."
            );
            return;
        }

        System.out.println(
                "\n=== PROPOSTA ==="
        );

        System.out.println(
                "Proposta #" + proposta.getIdProposta()
        );

        System.out.println(
                "Stato: " + proposta.getStato()
        );

        System.out.println(
                "Proponente: "
                        + proposta.getProponente().getUsername()
        );

        System.out.println("Carte offerte:");

        for (CartaFisica carta :
                proposta.getCarteOfferte()) {

            System.out.println(
                    "#" + carta.getIdCarta()
                            + " | "
                            + carta.getNomeCarta()
                            + " | bloccata: "
                            + carta.isBloccataInScambio()
            );
        }

        // ==========================================
        // ACCETTIAMO LA PROPOSTA
        // ==========================================

        System.out.println(
                "\n=== ACCETTAZIONE ==="
        );

        boolean accettata =
                controllerScambi.accettaProposta(
                        mario,
                        proposta
                );

        System.out.println(
                "Accettazione riuscita: "
                        + accettata
        );

        // ==========================================
        // STATO FINALE IN MEMORIA
        // ==========================================

        System.out.println(
                "\n=== STATO FINALE ==="
        );

        System.out.println(
                "Stato proposta: "
                        + proposta.getStato()
        );

        System.out.println(
                "Stato annuncio: "
                        + annuncio.getStato()
        );

        stampaInventario(mario);
        stampaInventario(peach);

        System.out.println(
                "\n=== STATO CARTE ==="
        );

        for (CartaFisica carta :
                mario.getInventario().getCarte()) {

            System.out.println(
                    "#" + carta.getIdCarta()
                            + " | "
                            + carta.getNomeCarta()
                            + " | bloccata: "
                            + carta.isBloccataInScambio()
            );
        }

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
    }

    private static void stampaInventario(Utente utente) {

        System.out.println(
                "\n" + utente.getUsername() + ":"
        );

        for (CartaFisica carta :
                utente.getInventario().getCarte()) {

            System.out.println(
                    "  #" + carta.getIdCarta()
                            + " | "
                            + carta.getNomeCarta()
                            + " | bloccata: "
                            + carta.isBloccataInScambio()
            );
        }
    }
}