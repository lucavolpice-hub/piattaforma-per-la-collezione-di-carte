import controller.ControllerAnnunci;
import controller.ControllerScambi;
import controller.ControllerUtenti;
import controller.Piattaforma;

import model.Annuncio;
import model.AnnuncioScambio;
import model.AnnuncioVendita;
import model.CartaFisica;
import model.CategoriaCarta;
import model.PropostaScambio;
import model.Utente;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // 1. Creo la piattaforma con le liste vuote di annunci e proposte
        List<Annuncio> listaAnnunci = new ArrayList<>();
        List<PropostaScambio> listaProposte = new ArrayList<>();
        Piattaforma piattaforma = new Piattaforma(listaAnnunci, listaProposte);

        // 2. Creo i controller
        ControllerUtenti controllerUtenti = new ControllerUtenti(piattaforma);
        ControllerAnnunci controllerAnnunci = new ControllerAnnunci(piattaforma, controllerUtenti);
        ControllerScambi controllerScambi = new ControllerScambi(piattaforma, controllerUtenti);

        System.out.println("=== Test registrazione e login ===");

        boolean ok1 = controllerUtenti.registraUtente("alice", "pwd123");
        boolean ok2 = controllerUtenti.registraUtente("bob", "pwd456");

        System.out.println("Registrazione alice: " + ok1);
        System.out.println("Registrazione bob: " + ok2);

        Utente alice = controllerUtenti.accedi("alice", "pwd123");
        Utente bob = controllerUtenti.accedi("bob", "pwd456");

        System.out.println("Login alice: " + (alice != null ? "OK" : "FAIL"));
        System.out.println("Login bob: " + (bob != null ? "OK" : "FAIL"));

        if (alice == null || bob == null) {
            System.out.println("Non posso continuare: uno dei due utenti è null.");
            return;
        }

        System.out.println("\n=== Test creazione annunci ===");

        AnnuncioVendita annuncioVendita = controllerAnnunci.pubblicaAnnuncioVendita(
                alice,
                "Vendo Charizard base",
                CategoriaCarta.CARTA_SINGOLA,
                50.0
        );

        System.out.println("Creato annuncio vendita: " + (annuncioVendita != null ? "OK" : "FAIL"));

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

        List<Annuncio> perCategoria =
                controllerAnnunci.cercaAnnunciPerCategoria(CategoriaCarta.CARTA_SINGOLA);
        System.out.println("Annunci per categoria CARTA_SINGOLA: " + perCategoria.size());

        List<Annuncio> disponibili = controllerAnnunci.cercaAnnunciDisponibili();
        System.out.println("Annunci disponibili: " + disponibili.size());

        System.out.println("\n=== Test invio proposta di scambio ===");

        if (annuncioScambio == null) {
            System.out.println("Non posso continuare: annuncioScambio è null.");
            return;
        }

        // Alice aggiunge una carta al proprio inventario e la offre a bob
        CartaFisica cartaOfferta = new CartaFisica(1, "Blastoise base", "NM", "IT");
        controllerUtenti.aggiungiCartaInventario(alice, cartaOfferta);

        List<CartaFisica> carteOfferte = new ArrayList<>();
        carteOfferte.add(cartaOfferta);

        PropostaScambio proposta = controllerScambi.inviaProposta(alice, annuncioScambio, carteOfferte);

        System.out.println("Invio proposta: " + (proposta != null ? "OK" : "FAIL"));

        if (proposta != null) {
            System.out.println("Stato annuncio scambio dopo la proposta: " + annuncioScambio.getStato());
        }

        System.out.println("\n=== Test ricerca proposte ===");

        if (proposta != null) {
            int idProposta = proposta.getIdProposta();
            PropostaScambio trovata = controllerScambi.cercaPropostaPerId(idProposta);
            System.out.println("Ricerca proposta per ID (" + idProposta + "): " + (trovata != null ? "OK" : "FAIL"));
        }

        List<PropostaScambio> proposteRicevute = controllerScambi.getProposteRicevute(annuncioScambio);
        System.out.println("Proposte ricevute sull'annuncio di bob: " + proposteRicevute.size());

        System.out.println("\n=== Test rifiuto proposta ===");

        // Creo una seconda proposta solo per testare il rifiuto
        CartaFisica secondaCarta = new CartaFisica(2, "Venusaur base", "NM", "IT");
        controllerUtenti.aggiungiCartaInventario(alice, secondaCarta);

        List<CartaFisica> secondaOfferta = new ArrayList<>();
        secondaOfferta.add(secondaCarta);

        PropostaScambio propostaDaRifiutare =
                controllerScambi.inviaProposta(alice, annuncioScambio, secondaOfferta);

        if (propostaDaRifiutare != null) {
            boolean rifiutata = controllerScambi.rifiutaProposta(propostaDaRifiutare);
            System.out.println("Rifiuto proposta: " + (rifiutata ? "OK" : "FAIL"));
            System.out.println("Stato proposta dopo rifiuto: " + propostaDaRifiutare.getStato());
        }

        System.out.println("\n=== Test accettazione proposta ===");

        if (proposta != null) {
            boolean accettata = controllerScambi.accettaProposta(proposta);
            System.out.println("Accettazione proposta: " + (accettata ? "OK" : "FAIL"));
            System.out.println("Stato proposta dopo accettazione: " + proposta.getStato());
            System.out.println("Stato annuncio scambio dopo accettazione: " + annuncioScambio.getStato());
        }

        System.out.println("\n=== Test chiusura annuncio di vendita ===");

        if (annuncioVendita != null) {
            boolean chiuso = controllerAnnunci.chiudiAnnuncio(annuncioVendita);
            System.out.println("Chiusura annuncio vendita: " + (chiuso ? "OK" : "FAIL"));
            System.out.println("Stato annuncio dopo chiusura: " + annuncioVendita.getStato());
        }

        System.out.println("\n=== Fine test ===");
    }
}