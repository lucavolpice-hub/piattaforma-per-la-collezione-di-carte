import controller.ControllerAnnunci;
import controller.ControllerUtenti;
import model.CategoriaCarta;
import controller.Piattaforma;
import model.Utente;
import model.Annuncio;
import model.AnnuncioVendita;
import model.AnnuncioScambio;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // 1. Creo la piattaforma con una lista vuota di annunci
        List<model.Annuncio> listaAnnunci = new ArrayList<>();
        Piattaforma piattaforma = new Piattaforma(listaAnnunci);

        // 2. Creo i controller
        ControllerUtenti controllerUtenti = new ControllerUtenti(piattaforma);
        ControllerAnnunci controllerAnnunci = new ControllerAnnunci(piattaforma, controllerUtenti);

        System.out.println("=== Test registrazione e login ===");

        // 3. Registro due utenti
        boolean ok1 = controllerUtenti.registraUtente("alice", "pwd123");
        boolean ok2 = controllerUtenti.registraUtente("bob", "pwd456");

        System.out.println("Registrazione alice: " + ok1);
        System.out.println("Registrazione bob: " + ok2);

        // 4. Provo ad accedere
        Utente alice = controllerUtenti.accedi("alice", "pwd123");
        Utente bob = controllerUtenti.accedi("bob", "pwd456");

        System.out.println("Login alice: " + (alice != null ? "OK" : "FAIL"));
        System.out.println("Login bob: " + (bob != null ? "OK" : "FAIL"));

        System.out.println("\n=== Test creazione annunci ===");

        if (alice == null || bob == null) {
            System.out.println("Non posso continuare: uno dei due utenti è null.");
            return;
        }

        // 5. Creo un annuncio di vendita per alice
        AnnuncioVendita annuncioVendita = controllerAnnunci.pubblicaAnnuncioVendita(
                alice,
                "Vendo Charizard base",
                CategoriaCarta.CARTA_SINGOLA,
                50.0
        );

        System.out.println("Creato annuncio vendita: " + (annuncioVendita != null ? "OK" : "FAIL"));

        // 6. Creo un annuncio di scambio per bob
        AnnuncioScambio annuncioScambio = controllerAnnunci.pubblicaAnnuncioScambio(
                bob,
                "Cerco Pikachu per scambio",
                CategoriaCarta.CARTA_SINGOLA,
                20.0
        );

        System.out.println("Creato annuncio scambio: " + (annuncioScambio != null ? "OK" : "FAIL"));

        System.out.println("\n=== Test ricerca annunci ===");

        if (annuncioVendita != null) {
            int id = annuncioVendita.getIdAnnuncio();
            Annuncio trovato = controllerAnnunci.cercaAnnuncioPerId(id);
            System.out.println("Ricerca per ID (" + id + "): " + (trovato != null ? "OK" : "FAIL"));
        }

        List<model.Annuncio> perCategoria =
                controllerAnnunci.cercaAnnunciPerCategoria(CategoriaCarta.CARTA_SINGOLA);

        System.out.println("Annunci per categoria CARTA_SINGOLA: " + perCategoria.size());

        List<model.Annuncio> disponibili = controllerAnnunci.cercaAnnunciDisponibili();
        System.out.println("Annunci disponibili: " + disponibili.size());

        System.out.println("\n=== Test chiusura annuncio ===");

        if (annuncioVendita != null) {
            boolean chiuso = controllerAnnunci.chiudiAnnuncio(annuncioVendita);
            System.out.println("Chiusura annuncio vendita: " + (chiuso ? "OK" : "FAIL"));
            System.out.println("Stato annuncio dopo chiusura: " + annuncioVendita.getStato());
        }

        System.out.println("\n=== Fine test ===");
    }
}
