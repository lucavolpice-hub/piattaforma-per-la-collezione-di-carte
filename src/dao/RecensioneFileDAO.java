package dao;

import model.Recensione;
import model.Utente;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class RecensioneFileDAO implements RecensioneDAO {

    private final Path percorsoFile;
    private final UtenteDAO utenteDAO;

    public RecensioneFileDAO(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
        percorsoFile = Paths.get("data", "recensioni.txt");

        try {
            if (Files.notExists(percorsoFile)) {
                Files.createDirectories(percorsoFile.getParent());
                Files.createFile(percorsoFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossibile inizializzare il file delle recensioni",
                    e
            );
        }
    }

    @Override
    public boolean salva(Recensione recensione) {
        if (!recensioneValida(recensione)) {
            return false;
        }

        if (cercaPerId(recensione.getId()) != null) {
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                percorsoFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(convertiInRiga(recensione));
            writer.newLine();

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Recensione cercaPerId(int idRecensione) {
        for (Recensione recensione : trovaTutte()) {
            if (recensione.getId() == idRecensione) {
                return recensione;
            }
        }

        return null;
    }

    @Override
    public List<Recensione> trovaTutte() {
        List<Recensione> recensioni = new ArrayList<>();

        try {
            List<String> righe = Files.readAllLines(percorsoFile);

            for (String riga : righe) {
                if (!riga.isBlank()) {
                    Recensione recensione = convertiInRecensione(riga);

                    if (recensione != null) {
                        recensioni.add(recensione);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossibile leggere il file delle recensioni",
                    e
            );
        }

        return recensioni;
    }

    @Override
    public boolean aggiorna(Recensione recensione) {
        if (!recensioneValida(recensione)) {
            return false;
        }

        List<Recensione> recensioni = trovaTutte();
        boolean trovata = false;

        for (int i = 0; i < recensioni.size(); i++) {
            if (recensioni.get(i).getId() == recensione.getId()) {
                recensioni.set(i, recensione);
                trovata = true;
                break;
            }
        }

        if (!trovata) {
            return false;
        }

        return scriviTutte(recensioni);
    }

    @Override
    public boolean elimina(int idRecensione) {
        List<Recensione> recensioni = trovaTutte();

        boolean rimossa = recensioni.removeIf(
                recensione -> recensione.getId() == idRecensione
        );

        if (!rimossa) {
            return false;
        }

        return scriviTutte(recensioni);
    }

    private boolean recensioneValida(Recensione recensione) {
        return recensione != null
                && recensione.isValida()
                && recensione.getAutore() != null
                && recensione.getDestinatario() != null;
    }

    private String convertiInRiga(Recensione recensione) {
        return recensione.getId() + ";"
                + recensione.getVoto() + ";"
                + recensione.getCommento() + ";"
                + recensione.getAutore().getUsername() + ";"
                + recensione.getDestinatario().getUsername();
    }

    private Recensione convertiInRecensione(String riga) {
        String[] dati = riga.split(";", -1);

        if (dati.length != 5) {
            throw new IllegalArgumentException(
                    "Formato recensione non valido: " + riga
            );
        }

        int idRecensione = Integer.parseInt(dati[0]);
        int voto = Integer.parseInt(dati[1]);
        String commento = dati[2];
        String usernameAutore = dati[3];
        String usernameDestinatario = dati[4];

        Utente autore = utenteDAO.cercaPerUsername(usernameAutore);
        Utente destinatario = utenteDAO.cercaPerUsername(
                usernameDestinatario
        );

        if (autore == null || destinatario == null) {
            return null;
        }

        return new Recensione(
                idRecensione,
                voto,
                commento,
                autore,
                destinatario
        );
    }

    private boolean scriviTutte(List<Recensione> recensioni) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                percorsoFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            for (Recensione recensione : recensioni) {
                writer.write(convertiInRiga(recensione));
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            return false;
        }
    }
}
