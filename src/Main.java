import controller.ControllerUtenti;
import controller.Piattaforma;

import dao.AnnuncioDAO;
import dao.AnnuncioFileDAO;
import dao.CartaFisicaDAO;
import dao.CartaFisicaFileDAO;
import dao.PropostaScambioDAO;
import dao.PropostaScambioFileDAO;
import dao.UtenteDAO;
import dao.UtenteFileDAO;

import model.Annuncio;
import model.AnnuncioScambio;
import model.CartaFisica;
import model.CategoriaCarta;
import model.PropostaScambio;
import model.Utente;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // =============================
        // CARICHIAMO GLI UTENTI ESISTENTI
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
        Utente peach = controllerUtenti.cercaUtente("peach");

        if (mario == null || peach == null) {
            System.out.println(
                    "Errore: mario o peach non sono presenti in utenti.txt"
            );
            return;
        }

        // =============================
        // CREIAMO LE DAO CONDIVISE
        // =============================

        UtenteDAO utenteDAO = new UtenteFileDAO();
        CartaFisicaDAO cartaDAO = new CartaFisicaFileDAO();
        AnnuncioDAO annuncioDAO = new AnnuncioFileDAO(utenteDAO);
        PropostaScambioDAO propostaDAO = new PropostaScambioFileDAO(
                utenteDAO,
                annuncioDAO,
                cartaDAO
        );

        // =============================
        // CREIAMO UN ANNUNCIO DI SCAMBIO DI MARIO
        // =============================

        AnnuncioScambio annuncioScambio = new AnnuncioScambio(
                "Scambio Charizard",
                CategoriaCarta.CARTA_SINGOLA,
                mario,
                150.0
        );

        boolean annuncioSalvato = annuncioDAO.salva(annuncioScambio);

        System.out.println("=== ANNUNCIO CREATO DA MARIO ===");
        System.out.println("Annuncio salvato: " + annuncioSalvato);

        // =============================
        // CREIAMO LE CARTE OFFERTE DA PEACH
        // =============================

        CartaFisica carta1 = new CartaFisica(
                10,
                "Bulbasaur",
                "Mint",
                "Italiano"
        );

        CartaFisica carta2 = new CartaFisica(
                11,
                "Squirtle",
                "Good",
                "Inglese"
        );

        cartaDAO.salva(carta1);
        cartaDAO.salva(carta2);

        System.out.println("\n=== CARTE DI PEACH SALVATE ===");
        System.out.println("Carta 1: " + carta1);
        System.out.println("Carta 2: " + carta2);

        // =============================
        // PEACH FA UNA PROPOSTA DI SCAMBIO
        // =============================

        List<CartaFisica> carteOfferte = new ArrayList<>();
        carteOfferte.add(carta1);
        carteOfferte.add(carta2);

        PropostaScambio proposta = new PropostaScambio(
                peach,
                annuncioScambio,
                carteOfferte
        );

        boolean propostaSalvata = propostaDAO.salva(proposta);

        System.out.println("\n=== PROPOSTA DI PEACH ===");
        System.out.println("Proposta salvata: " + propostaSalvata);

        // =============================
        // LEGGIAMO TUTTE LE PROPOSTE DAL FILE
        // =============================

        System.out.println("\n=== TUTTE LE PROPOSTE NEL FILE ===");

        for (PropostaScambio p : propostaDAO.trovaTutte()) {
            System.out.println(p);
            System.out.println(
                    "  Proponente: " + p.getProponente().getUsername()
            );
            System.out.println(
                    "  Annuncio ricevuto: "
                            + p.getAnnuncioRicevuto().getDescrizione()
            );

            System.out.print("  Carte offerte: ");

            for (CartaFisica carta : p.getCarteOfferte()) {
                System.out.print(carta.getNomeCarta() + " ");
            }

            System.out.println();
        }

        // =============================
        // RICERCA PER ID
        // =============================

        System.out.println("\n=== RICERCA PROPOSTA PER ID ===");

        PropostaScambio propostaTrovata =
                propostaDAO.cercaPerId(proposta.getIdProposta());

        if (propostaTrovata != null) {
            System.out.println("Trovata: " + propostaTrovata);
        } else {
            System.out.println("Proposta non trovata");
        }

        // =============================
        // ELIMINAZIONE
        // =============================

        System.out.println("\n=== ELIMINAZIONE PROPOSTA ===");

        boolean eliminata =
                propostaDAO.elimina(proposta.getIdProposta());

        System.out.println("Proposta eliminata: " + eliminata);

        System.out.println("\n=== PROPOSTE RIMANENTI ===");

        for (PropostaScambio p : propostaDAO.trovaTutte()) {
            System.out.println(p);
        }
    }
}