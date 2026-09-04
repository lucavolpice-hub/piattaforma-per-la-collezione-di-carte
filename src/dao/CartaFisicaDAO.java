package dao;

import model.CartaFisica;

import java.util.List;

public interface CartaFisicaDAO {

    boolean salva(CartaFisica carta);

    CartaFisica cercaPerId(int idCarta);

    List<CartaFisica> trovaTutte();

    boolean aggiorna(CartaFisica carta);

    boolean elimina(int idCarta);
}
