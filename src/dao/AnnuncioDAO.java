package dao;

import model.Annuncio;

import java.util.List;

public interface AnnuncioDAO {

    boolean salva(Annuncio annuncio);

    Annuncio cercaPerId(int idAnnuncio);

    List<Annuncio> trovaTutti();

    boolean aggiorna(Annuncio annuncio);

    boolean elimina(int idAnnuncio);
}