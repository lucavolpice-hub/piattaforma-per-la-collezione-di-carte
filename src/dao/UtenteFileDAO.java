package dao;

import model.Utente;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class UtenteFileDAO implements UtenteDAO {

    private final Path percorsoFile;

    public UtenteFileDAO() {
        percorsoFile = Paths.get("data", "utenti.txt");

        try {
            if (Files.notExists(percorsoFile)) {
                Files.createDirectories(percorsoFile.getParent());
                Files.createFile(percorsoFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossibile inizializzare il file degli utenti",
                    e
            );
        }
    }

    @Override
    public boolean salva(Utente utente) {
        if (utente == null) {
            return false;
        }

        if (cercaPerUsername(utente.getUsername()) != null) {
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                percorsoFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(convertiInRiga(utente));
            writer.newLine();

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Utente cercaPerUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        for (Utente utente : trovaTutti()) {
            if (utente.getUsername().equalsIgnoreCase(username)) {
                return utente;
            }
        }

        return null;
    }

    @Override
    public List<Utente> trovaTutti() {
        List<Utente> utenti = new ArrayList<>();

        try {
            List<String> righe = Files.readAllLines(percorsoFile);

            for (String riga : righe) {
                if (!riga.isBlank()) {
                    utenti.add(convertiInUtente(riga));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossibile leggere il file degli utenti",
                    e
            );
        }

        return utenti;
    }

    @Override
    public boolean aggiorna(Utente utente) {
        if (utente == null) {
            return false;
        }

        List<Utente> utenti = trovaTutti();
        boolean trovato = false;

        for (int i = 0; i < utenti.size(); i++) {
            if (utenti.get(i).getUsername()
                    .equalsIgnoreCase(utente.getUsername())) {

                utenti.set(i, utente);
                trovato = true;
                break;
            }
        }

        if (!trovato) {
            return false;
        }

        return scriviTutti(utenti);
    }

    @Override
    public boolean elimina(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }

        List<Utente> utenti = trovaTutti();

        boolean rimosso = utenti.removeIf(
                utente -> utente.getUsername()
                        .equalsIgnoreCase(username)
        );

        if (!rimosso) {
            return false;
        }

        return scriviTutti(utenti);
    }

    private String convertiInRiga(Utente utente) {
        return utente.getUsername() + ";"
                + utente.getPassword();
    }

    private Utente convertiInUtente(String riga) {
        String[] dati = riga.split(";", -1);

        if (dati.length != 2) {
            throw new IllegalArgumentException(
                    "Formato utente non valido: " + riga
            );
        }

        String username = dati[0];
        String password = dati[1];

        return new Utente(username, password);
    }

    private boolean scriviTutti(List<Utente> utenti) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                percorsoFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            for (Utente utente : utenti) {
                writer.write(convertiInRiga(utente));
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            return false;
        }
    }
}