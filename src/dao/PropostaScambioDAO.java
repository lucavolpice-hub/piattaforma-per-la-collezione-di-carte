package dao;

import model.PropostaScambio;

import java.util.List;

public interface PropostaScambioDAO {

    boolean salva(PropostaScambio proposta);

    PropostaScambio cercaPerId(int idProposta);

    List<PropostaScambio> trovaTutte();

    boolean aggiorna(PropostaScambio proposta);

    boolean elimina(int idProposta);
}
