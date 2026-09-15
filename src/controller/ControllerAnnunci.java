package controller;

import dao.AnnuncioDAO;
import dao.AnnuncioFileDAO;
import dao.CartaFisicaDAO;
import dao.CartaFisicaFileDAO;
import dao.InventarioDAO;
import dao.InventarioFileDAO;
import dao.UtenteDAO;
import dao.UtenteFileDAO;

import model.Annuncio;
import model.AnnuncioScambio;
import model.AnnuncioVendita;
import model.CartaFisica;
import model.CategoriaCarta;
import model.Utente;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce la creazione, la ricerca, l'acquisto e la chiusura degli annunci.
 */
public class ControllerAnnunci {

    private final Piattaforma piattaforma;
    private final ControllerUtenti controllerUtenti;

    private final InventarioDAO inventarioDAO;
    private final CartaFisicaDAO cartaDAO;
    private final AnnuncioDAO annuncioDAO;

    public ControllerAnnunci(
            Piattaforma piattaforma,
            ControllerUtenti controllerUtenti
    ) {

        this.piattaforma = piattaforma;
        this.controllerUtenti = controllerUtenti;

        UtenteDAO utenteDAO = new UtenteFileDAO();

        this.cartaDAO = new CartaFisicaFileDAO();
        this.inventarioDAO = new InventarioFileDAO();

        this.annuncioDAO =
                new AnnuncioFileDAO(
                        utenteDAO,
                        cartaDAO
                );
    }

    /**
     * Crea un annuncio di vendita e lo registra sia nella piattaforma
     * sia nella lista personale dell'utente che lo ha creato.
     *
     * @return l'annuncio creato, oppure null se i dati non sono validi
     */
    public AnnuncioVendita pubblicaAnnuncioVendita(
            Utente creatore,
            String descrizione,
            CategoriaCarta categoria,
            double prezzo,
            CartaFisica carta
    ) {

        if (creatore == null
                || descrizione == null
                || descrizione.isBlank()
                || carta == null) {
            return null;
        }

        if (prezzo <= 0) {
            return null;
        }

        // La carta deve appartenere all'utente
        boolean posseduta = false;

        for (CartaFisica cartaInventario :
                creatore.getInventario().getCarte()) {

            if (cartaInventario.getIdCarta()
                    == carta.getIdCarta()) {

                posseduta = true;
                break;
            }
        }

        if (!posseduta) {
            return null;
        }

        // La carta non deve essere già impegnata in uno scambio in corso
        if (carta.isBloccataInScambio()) {
            return null;
        }

        // La stessa carta non può essere presente
        // in un altro annuncio ancora attivo
        for (Annuncio annuncio :
                piattaforma.getAnnuncio()) {

            if (annuncio.getStato()
                    == model.StatoAnnuncio.CONCLUSO) {
                continue;
            }

            for (CartaFisica cartaAnnuncio :
                    annuncio.getCarte()) {

                if (cartaAnnuncio.getIdCarta()
                        == carta.getIdCarta()) {

                    return null;
                }
            }
        }

        AnnuncioVendita annuncio =
                new AnnuncioVendita(
                        descrizione,
                        categoria,
                        creatore,
                        prezzo
                );

        // Associa la carta all'annuncio
        annuncio.aggiungiCarta(carta);

        // Salva l'annuncio completo
        if (!annuncioDAO.salva(annuncio)) {
            return null;
        }

        // Aggiunta alla piattaforma
        piattaforma.getAnnuncio().add(annuncio);

        // Collegamento all'utente
        controllerUtenti.aggiungiAnnuncio(
                creatore,
                annuncio
        );

        return annuncio;
    }

    /**
     * Crea un annuncio di scambio e lo registra sia nella piattaforma
     * sia nella lista personale dell'utente che lo ha creato.
     * @return l'annuncio creato, oppure null se i dati non sono validi
     */
    public AnnuncioScambio pubblicaAnnuncioScambio(
            Utente creatore,
            String descrizione,
            CategoriaCarta categoria,
            double valoreDiRiferimento,
            CartaFisica carta
    ) {

        if (creatore == null
                || descrizione == null
                || descrizione.isBlank()
                || carta == null) {
            return null;
        }

        if (valoreDiRiferimento <= 0) {
            return null;
        }

        // La carta deve appartenere all'utente che pubblica l'annuncio
        boolean posseduta = false;

        for (CartaFisica cartaInventario :
                creatore.getInventario().getCarte()) {

            if (cartaInventario.getIdCarta()
                    == carta.getIdCarta()) {

                posseduta = true;
                break;
            }
        }

        if (!posseduta) {
            return null;
        }

        // La carta non deve essere già impegnata in un altro scambio
        if (carta.isBloccataInScambio()) {
            return null;
        }

        // La stessa carta non può essere presente
        // in un altro annuncio ancora attivo (vendita o scambio)
        for (Annuncio annuncioEsistente :
                piattaforma.getAnnuncio()) {

            if (annuncioEsistente.getStato()
                    == model.StatoAnnuncio.CONCLUSO) {
                continue;
            }

            for (CartaFisica cartaAnnuncio :
                    annuncioEsistente.getCarte()) {

                if (cartaAnnuncio.getIdCarta()
                        == carta.getIdCarta()) {

                    return null;
                }
            }
        }

        AnnuncioScambio annuncio =
                new AnnuncioScambio(
                        descrizione,
                        categoria,
                        creatore,
                        valoreDiRiferimento
                );

        annuncio.aggiungiCarta(carta);

        if (!annuncioDAO.salva(annuncio)) {
            return null;
        }

        piattaforma.getAnnuncio().add(annuncio);

        controllerUtenti.aggiungiAnnuncio(
                creatore,
                annuncio
        );

        return annuncio;
    }

    public Annuncio cercaAnnuncioPerId(int idAnnuncio) {

        for (Annuncio annuncio :
                piattaforma.getAnnuncio()) {

            if (annuncio.getIdAnnuncio() == idAnnuncio) {
                return annuncio;
            }
        }

        return null;
    }

    public List<Annuncio> cercaAnnunciPerCategoria(
            CategoriaCarta categoria
    ) {

        List<Annuncio> risultato =
                new ArrayList<>();

        if (categoria == null) {
            return risultato;
        }

        for (Annuncio annuncio :
                piattaforma.getAnnuncio()) {

            if (annuncio.getCategoria() == categoria) {
                risultato.add(annuncio);
            }
        }

        return risultato;
    }

    public List<Annuncio> cercaAnnunciDisponibili() {

        List<Annuncio> risultato =
                new ArrayList<>();

        for (Annuncio annuncio :
                piattaforma.getAnnuncio()) {

            if (annuncio.getStato()
                    == model.StatoAnnuncio.DISPONIBILE) {

                risultato.add(annuncio);
            }
        }

        return risultato;
    }

    public boolean chiudiAnnuncio(
            Annuncio annuncio
    ) {

        if (annuncio == null
                || !piattaforma.getAnnuncio()
                .contains(annuncio)) {

            return false;
        }

        try {

            annuncio.concludi();
            return true;

        } catch (Exception e) {

            return false;
        }
    }

    /**
     * Acquista un annuncio di vendita.
     *
     * L'acquisto:
     * 1. verifica che acquirente e annuncio siano validi;
     * 2. verifica che l'annuncio sia disponibile;
     * 3. impedisce al creatore di acquistare il proprio annuncio;
     * 4. verifica che le carte appartengano ancora al venditore;
     * 5. trasferisce le carte dal venditore all'acquirente;
     * 6. conclude l'annuncio;
     * 7. aggiorna il DAO.
     *
     * @return true se l'acquisto è riuscito, false altrimenti
     */
    public boolean acquistaAnnuncio(
            Utente acquirente,
            AnnuncioVendita annuncio
    ) {

        // 1. CONTROLLI DI BASE

        if (acquirente == null
                || annuncio == null) {

            return false;
        }

        if (!piattaforma.getUtenti()
                .contains(acquirente)) {

            return false;
        }

        if (!piattaforma.getAnnuncio()
                .contains(annuncio)) {

            return false;
        }

        // 2. ANNUNCIO DISPONIBILE

        if (annuncio.getStato()
                != model.StatoAnnuncio.DISPONIBILE) {

            return false;
        }

        // 3. RECUPERIAMO IL VENDITORE

        Utente venditore =
                annuncio.getCreatore();

        if (venditore == null) {
            return false;
        }
        // 4. IL CREATORE NON PUÒ ACQUISTARE
        //    IL PROPRIO ANNUNCIO

        if (venditore.getUsername()
                .equalsIgnoreCase(
                        acquirente.getUsername()
                )) {

            return false;
        }

        // 5. RECUPERIAMO LE CARTE

        List<CartaFisica> carteInVendita =
                annuncio.getCarte();

        if (carteInVendita == null
                || carteInVendita.isEmpty()) {

            return false;
        }
        // 6. VERIFICA CARTE
        for (CartaFisica carta :
                carteInVendita) {

            if (carta == null) {
                return false;
            }

            boolean presente = false;

            for (CartaFisica cartaInventario :
                    venditore.getInventario()
                            .getCarte()) {

                if (cartaInventario.getIdCarta()
                        == carta.getIdCarta()) {

                    presente = true;
                    break;
                }
            }

            if (!presente) {
                return false;
            }

            // La carta non deve essere impegnata
            // in una proposta di scambio.
            if (carta.isBloccataInScambio()) {
                return false;
            }
        }

        // 7. TRASFERIMENTO

        List<Integer> carteRimosse =
                new ArrayList<>();

        List<Integer> carteAggiunte =
                new ArrayList<>();

        try {

            // Rimuoviamo le carte dal venditore

            for (CartaFisica carta :
                    carteInVendita) {

                boolean rimossa =
                        inventarioDAO.rimuoviCarta(
                                venditore.getUsername(),
                                carta.getIdCarta()
                        );

                if (!rimossa) {

                    throw new IllegalStateException(
                            "Impossibile rimuovere la carta "
                                    + carta.getIdCarta()
                                    + " dal venditore."
                    );
                }

                carteRimosse.add(
                        carta.getIdCarta()
                );
            }

            // Aggiungiamo le carte all'acquirente

            for (CartaFisica carta :
                    carteInVendita) {

                boolean aggiunta =
                        inventarioDAO.aggiungiCarta(
                                acquirente.getUsername(),
                                carta.getIdCarta()
                        );

                if (!aggiunta) {

                    throw new IllegalStateException(
                            "Impossibile aggiungere la carta "
                                    + carta.getIdCarta()
                                    + " all'acquirente."
                    );
                }

                carteAggiunte.add(
                        carta.getIdCarta()
                );
            }

            // 8. AGGIORNIAMO GLI INVENTARI IN MEMORIA

            for (CartaFisica carta :
                    carteInVendita) {

                venditore.getInventario()
                        .rimuoviCarta(carta);

                acquirente.getInventario()
                        .aggiungiCarta(carta);
            }

            // 9. CONCLUDIAMO L'ACQUISTO

            annuncio.concludiAcquisto(
                    acquirente
            );

            // 10. AGGIORNIAMO L'ANNUNCIO SUL FILE

            if (!annuncioDAO.aggiorna(annuncio)) {

                throw new IllegalStateException(
                        "Impossibile aggiornare l'annuncio."
                );
            }

            return true;

        } catch (Exception e) {
            // ROLLBACK FILE INVENTARI

            for (Integer idCarta :
                    carteAggiunte) {

                inventarioDAO.rimuoviCarta(
                        acquirente.getUsername(),
                        idCarta
                );
            }

            for (Integer idCarta :
                    carteRimosse) {

                inventarioDAO.aggiungiCarta(
                        venditore.getUsername(),
                        idCarta
                );
            }
            // ROLLBACK IN MEMORIA

            for (CartaFisica carta :
                    carteInVendita) {

                acquirente.getInventario()
                        .rimuoviCarta(carta);

                venditore.getInventario()
                        .aggiungiCarta(carta);
            }

            // RIPRISTINO STATO ANNUNCIO

            annuncio.setStato(
                    model.StatoAnnuncio.DISPONIBILE
            );

            return false;
        }
    }
}