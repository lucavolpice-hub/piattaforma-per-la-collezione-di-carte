package dao;

import model.Utente;

import java.util.List;

public interface UtenteDAO {

    boolean salva(Utente utente);

    Utente cercaPerUsername(String username);

    List<Utente> trovaTutti();

    boolean aggiorna(Utente utente);

    boolean elimina(String username);
}