package dao;
import model.StatoProposta;
import model.AnnuncioScambio;
import model.Annuncio;
import model.CartaFisica;
import model.PropostaScambio;
import model.StatoAnnuncio;
import model.Utente;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PropostaScambioFileDAO implements PropostaScambioDAO {

    private final Path percorsoFile;
    private final UtenteDAO utenteDAO;
    private final AnnuncioDAO annuncioDAO;
    private final CartaFisicaDAO cartaDAO;

    public PropostaScambioFileDAO(
            UtenteDAO utenteDAO,
            AnnuncioDAO annuncioDAO,
            CartaFisicaDAO cartaDAO
    ) {
        this.utenteDAO = utenteDAO;
        this.annuncioDAO = annuncioDAO;
        this.cartaDAO = cartaDAO;

        percorsoFile = Paths.get("data", "proposte.txt");

        try {
            if (Files.notExists(percorsoFile)) {
                Files.createDirectories(percorsoFile.getParent());
                Files.createFile(percorsoFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossibile inizializzare il file delle proposte",
                    e
            );
        }
    }

    @Override
    public boolean salva(PropostaScambio proposta) {
        if (proposta == null) {
            return false;
        }

        if (cercaPerId(proposta.getIdProposta()) != null) {
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                percorsoFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(convertiInRiga(proposta));
            writer.newLine();

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public PropostaScambio cercaPerId(int idProposta) {
        for (PropostaScambio proposta : trovaTutte()) {
            if (proposta.getIdProposta() == idProposta) {
                return proposta;
            }
        }

        return null;
    }

    @Override
    public List<PropostaScambio> trovaTutte() {
        List<PropostaScambio> proposte = new ArrayList<>();

        try {
            List<String> righe = Files.readAllLines(percorsoFile);

            for (String riga : righe) {
                if (!riga.isBlank()) {
                    PropostaScambio proposta = convertiInProposta(riga);

                    if (proposta != null) {
                        proposte.add(proposta);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossibile leggere il file delle proposte",
                    e
            );
        }

        return proposte;
    }

    @Override
    public boolean aggiorna(PropostaScambio proposta) {
        if (proposta == null) {
            return false;
        }

        List<PropostaScambio> proposte = trovaTutte();
        boolean trovata = false;

        for (int i = 0; i < proposte.size(); i++) {
            if (proposte.get(i).getIdProposta() == proposta.getIdProposta()) {
                proposte.set(i, proposta);
                trovata = true;
                break;
            }
        }

        if (!trovata) {
            return false;
        }

        return scriviTutte(proposte);
    }

    @Override
    public boolean elimina(int idProposta) {
        List<PropostaScambio> proposte = trovaTutte();

        boolean rimossa = proposte.removeIf(
                proposta -> proposta.getIdProposta() == idProposta
        );

        if (!rimossa) {
            return false;
        }

        return scriviTutte(proposte);
    }

    private String convertiInRiga(PropostaScambio proposta) {
        StringBuilder idCarteConcatenati = new StringBuilder();

        List<CartaFisica> carteOfferte = proposta.getCarteOfferte();

        for (int i = 0; i < carteOfferte.size(); i++) {
            idCarteConcatenati.append(carteOfferte.get(i).getIdCarta());

            if (i < carteOfferte.size() - 1) {
                idCarteConcatenati.append(",");
            }
        }

        return proposta.getIdProposta() + ";"
                + proposta.getData() + ";"
                + proposta.getStato() + ";"
                + proposta.getProponente().getUsername() + ";"
                + proposta.getAnnuncioRicevuto().getIdAnnuncio() + ";"
                + idCarteConcatenati;
    }

    private PropostaScambio convertiInProposta(String riga) {
        String[] dati = riga.split(";", -1);

        if (dati.length != 6) {
            throw new IllegalArgumentException(
                    "Formato proposta non valido: " + riga
            );
        }

        int idProposta = Integer.parseInt(dati[0]);
        LocalDate data = LocalDate.parse(dati[1]);
        StatoProposta stato = StatoProposta.valueOf(dati[2]);
        String usernameProponente = dati[3];
        int idAnnuncioRicevuto = Integer.parseInt(dati[4]);
        String idCarteConcatenati = dati[5];

        Utente proponente = utenteDAO.cercaPerUsername(usernameProponente);

        if (proponente == null) {
            return null;
        }

        Annuncio annuncioGenerico = annuncioDAO.cercaPerId(idAnnuncioRicevuto);

        if (!(annuncioGenerico instanceof AnnuncioScambio)) {
            return null;
        }

        AnnuncioScambio annuncioRicevuto =
                (AnnuncioScambio) annuncioGenerico;

        List<CartaFisica> carteOfferte = new ArrayList<>();

        if (!idCarteConcatenati.isBlank()) {
            String[] idCarteSeparati = idCarteConcatenati.split(",");

            for (String idCartaStringa : idCarteSeparati) {
                int idCarta = Integer.parseInt(idCartaStringa);
                CartaFisica carta = cartaDAO.cercaPerId(idCarta);

                if (carta != null) {
                    carteOfferte.add(carta);
                }
            }
        }

        return new PropostaScambio(
                idProposta,
                data,
                stato,
                proponente,
                annuncioRicevuto,
                carteOfferte
        );
    }

    private boolean scriviTutte(List<PropostaScambio> proposte) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                percorsoFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            for (PropostaScambio proposta : proposte) {
                writer.write(convertiInRiga(proposta));
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            return false;
        }
    }
}