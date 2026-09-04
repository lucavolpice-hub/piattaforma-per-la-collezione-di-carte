package dao;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class InventarioFileDAO implements InventarioDAO {

    private final Path percorsoFile;

    public InventarioFileDAO() {
        percorsoFile = Paths.get("data", "inventari.txt");

        try {
            if (Files.notExists(percorsoFile)) {
                Files.createDirectories(percorsoFile.getParent());
                Files.createFile(percorsoFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossibile inizializzare il file degli inventari",
                    e
            );
        }
    }

    @Override
    public boolean aggiungiCarta(String username, int idCarta) {
        if (username == null || username.isBlank()) {
            return false;
        }

        if (idCarta <= 0) {
            return false;
        }

        List<Integer> idCarteUtente =
                trovaIdCartePerUtente(username);

        if (idCarteUtente.contains(idCarta)) {
            return false;
        }

        String riga = username + ";" + idCarta;

        try (BufferedWriter writer = Files.newBufferedWriter(
                percorsoFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(riga);
            writer.newLine();

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public List<Integer> trovaIdCartePerUtente(String username) {
        List<Integer> idCarte = new ArrayList<>();

        if (username == null || username.isBlank()) {
            return idCarte;
        }

        try {
            List<String> righe = Files.readAllLines(percorsoFile);

            for (String riga : righe) {
                if (riga.isBlank()) {
                    continue;
                }

                String[] dati = riga.split(";", -1);

                if (dati.length != 2) {
                    continue;
                }

                String usernameNelFile = dati[0];
                int idCarta = Integer.parseInt(dati[1]);

                if (usernameNelFile.equalsIgnoreCase(username)) {
                    idCarte.add(idCarta);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossibile leggere il file degli inventari",
                    e
            );
        }

        return idCarte;
    }

    @Override
    public boolean rimuoviCarta(String username, int idCarta) {
        if (username == null || username.isBlank()) {
            return false;
        }

        if (idCarta <= 0) {
            return false;
        }

        List<String> righeRimanenti = new ArrayList<>();
        boolean rimossa = false;

        try {
            List<String> righe = Files.readAllLines(percorsoFile);

            for (String riga : righe) {
                if (riga.isBlank()) {
                    continue;
                }

                String[] dati = riga.split(";", -1);

                boolean eLaRigaDaEliminare =
                        dati.length == 2
                                && dati[0].equalsIgnoreCase(username)
                                && Integer.parseInt(dati[1]) == idCarta;

                if (eLaRigaDaEliminare) {
                    rimossa = true;
                } else {
                    righeRimanenti.add(riga);
                }
            }

        } catch (IOException e) {
            return false;
        }

        if (!rimossa) {
            return false;
        }

        return riscriviFile(righeRimanenti);
    }

    private boolean riscriviFile(List<String> righe) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                percorsoFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            for (String riga : righe) {
                writer.write(riga);
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            return false;
        }
    }
}