package dao;

import model.Recensione;

import java.util.List;

public interface RecensioneDAO {

    boolean salva(Recensione recensione);

    Recensione cercaPerId(int idRecensione);

    List<Recensione> trovaTutte();

    boolean aggiorna(Recensione recensione);

    boolean elimina(int idRecensione);
}
