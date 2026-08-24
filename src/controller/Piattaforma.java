package controller;

import java.util.ArrayList;
import java.util.List;

import model.Utente;

/**
 * Rappresenta l'archivio centrale della piattaforma.
 *
 * Questa classe conserva i dati condivisi dai futuri controller.
 * Per ora i dati restano soltanto in memoria: vengono persi
 * quando il programma termina.
 *
 * Al momento viene mantenuta solo la lista degli utenti, perché
 * le classi necessarie per annunci e proposte di scambio non sono
 * ancora complete. Quelle liste verranno aggiunte in seguito.
 */
public class Piattaforma {

    /*
     * Elenco degli utenti registrati nella piattaforma.
     * La lista è privata: altre classi possono usarla tramite
     * i metodi pubblici, senza sostituirla accidentalmente.
     */
    private final List<Utente> utenti;

    /**
     * Crea una piattaforma inizialmente priva di utenti.
     */
    public Piattaforma() {
        utenti = new ArrayList<>();
    }

    /**
     * Restituisce gli utenti registrati.
     *
     * I futuri controller useranno questa lista per cercare,
     * aggiungere o rimuovere utenti.
     */
    public List<Utente> getUtenti() {
        return utenti;
    }

    /**
     * Inserisce un utente nell'archivio centrale.
     *
     * Il controllo su username vuoto o duplicato non viene fatto qui:
     * sarà responsabilità di ControllerUtenti, quando verrà creato.
     *
     * @param utente utente da memorizzare
     */
    public void aggiungiUtente(Utente utente) {
        if (utente != null) {
            utenti.add(utente);
        }
    }

    /**
     * Rimuove un utente dall'archivio.
     *
     * @return true se l'utente era presente ed è stato rimosso
     */
    public boolean rimuoviUtente(Utente utente) {
        return utenti.remove(utente);
    }
}