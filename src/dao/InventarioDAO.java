package dao;

import java.util.List;

public interface InventarioDAO {

    boolean aggiungiCarta(String username, int idCarta);

    boolean rimuoviCarta(String username, int idCarta);

    List<Integer> trovaIdCartePerUtente(String username);
}
