import controller.ControllerUtenti;
import controller.Piattaforma;

import dao.AnnuncioDAO;
import dao.AnnuncioFileDAO;
import dao.CartaFisicaDAO;
import dao.CartaFisicaFileDAO;
import dao.UtenteDAO;
import dao.UtenteFileDAO;

import model.Annuncio;
import model.AnnuncioScambio;
import model.CartaFisica;
import model.PropostaScambio;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        // ==========================================
        // CARICAMENTO PIATTAFORMA
        // ==========================================

        Piattaforma piattaforma =
                new Piattaforma(
                        new ArrayList<Annuncio>(),
                        new ArrayList<PropostaScambio>()
                );

        ControllerUtenti controllerUtenti =
                new ControllerUtenti(piattaforma);

        UtenteDAO utenteDAO = new UtenteFileDAO();
        CartaFisicaDAO cartaDAO = new CartaFisicaFileDAO();

        AnnuncioDAO annuncioDAO =
                new AnnuncioFileDAO(
                        utenteDAO,
                        cartaDAO
                );

        // ==========================================
        // CERCHIAMO MARIO
        // ==========================================

        var mario =
                controllerUtenti.cercaUtente("mario");

        if (mario == null) {
            System.out.println("Mario non trovato.");
            return;
        }

        // ==========================================
        // INVENTARIO MARIO
        // ==========================================

        System.out.println("=== INVENTARIO MARIO ===");

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

        // ==========================================
        // ASSOCIA UNA CARTA ALL'ANNUNCIO
        // ==========================================

        if (!mario.getInventario().getCarte().isEmpty()) {

            CartaFisica carta =
                    mario.getInventario()
                            .getCarte()
                            .get(0);

            annuncio.aggiungiCarta(carta);

            System.out.println(
                    "\nCarta aggiunta all'annuncio: "
                            + carta.getNomeCarta()
            );
        } else {
            System.out.println(
                    "\nMario non possiede carte."
            );
            return;
        }

        // ==========================================
        // SALVIAMO L'ANNUNCIO
        // ==========================================

        boolean aggiornato =
                annuncioDAO.aggiorna(annuncio);

        System.out.println(
                "Annuncio aggiornato: "
                        + aggiornato
        );

        // ==========================================
        // RILEGGIAMO DAL FILE
        // ==========================================

        System.out.println(
                "\n=== ANNUNCIO RICARICATO ==="
        );

        AnnuncioScambio annuncioRicaricato = null;

        for (Annuncio a :
                annuncioDAO.trovaTutti()) {

            if (a.getIdAnnuncio()
                    == annuncio.getIdAnnuncio()
                    && a instanceof AnnuncioScambio) {

                annuncioRicaricato =
                        (AnnuncioScambio) a;

                break;
            }
        }

        if (annuncioRicaricato == null) {
            System.out.println(
                    "Annuncio non trovato."
            );
            return;
        }

        System.out.println(
                "Annuncio: #"
                        + annuncioRicaricato.getIdAnnuncio()
                        + " - "
                        + annuncioRicaricato.getDescrizione()
        );

        System.out.println(
                "Carte associate:"
        );

        for (CartaFisica carta :
                annuncioRicaricato.getCarte()) {

            System.out.println(
                    "#" + carta.getIdCarta()
                            + " | "
                            + carta.getNomeCarta()
            );
        }
    }
}