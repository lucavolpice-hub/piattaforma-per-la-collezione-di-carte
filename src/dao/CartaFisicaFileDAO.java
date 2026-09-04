package dao;

import model.CartaFisica;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class CartaFisicaFileDAO implements CartaFisicaDAO {

    private final Path percorsoFile;

    public CartaFisicaFileDAO() {
        percorsoFile = Paths.get("data", "carte.txt");

        try {
            if (Files.notExists(percorsoFile)) {
                Files.createDirectories(percorsoFile.getParent());
                Files.createFile(percorsoFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossibile inizializzare il file delle carte", e);
        }
    }

    @Override
    public boolean salva(CartaFisica carta) {
        if (carta == null) {
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                percorsoFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(convertiInRiga(carta));
            writer.newLine();
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    private String convertiInRiga(CartaFisica carta) {
        return carta.getIdCarta() + ";"
                + carta.getNomeCarta() + ";"
                + carta.getCondizione() + ";"
                + carta.getLingua() + ";"
                + carta.isBloccataInScambio();
    }

    @Override
    public CartaFisica cercaPerId(int idCarta) {
        for (CartaFisica carta : trovaTutte()) {
            if (carta.getIdCarta() == idCarta) {
                return carta;
            }
        }

        return null;
    }

    @Override
    public List<CartaFisica> trovaTutte() {
        List<CartaFisica> carte = new ArrayList<>();

        try {
            List<String> righe = Files.readAllLines(percorsoFile);

            for (String riga : righe) {
                if (!riga.isBlank()) {
                    carte.add(convertiInCarta(riga));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Impossibile leggere il file delle carte", e);
        }

        return carte;
    }

    private CartaFisica convertiInCarta(String riga) {
        String[] dati = riga.split(";", -1);

        if (dati.length != 5) {
            throw new IllegalArgumentException("Formato non valido: " + riga);
        }

        int idCarta = Integer.parseInt(dati[0]);
        String nomeCarta = dati[1];
        String condizione = dati[2];
        String lingua = dati[3];
        boolean bloccata = Boolean.parseBoolean(dati[4]);

        CartaFisica carta = new CartaFisica(
                idCarta,
                nomeCarta,
                condizione,
                lingua
        );

        carta.setBloccataInScambio(bloccata);

        return carta;
    }

    @Override
    public boolean aggiorna(CartaFisica carta) {
        if (carta == null) {
            return false;
        }

        List<CartaFisica> carte = trovaTutte();
        boolean trovata = false;

        for (int i = 0; i < carte.size(); i++) {
            if (carte.get(i).getIdCarta() == carta.getIdCarta()) {
                carte.set(i, carta);
                trovata = true;
                break;
            }
        }

        if (!trovata) {
            return false;
        }

        return scriviTutte(carte);
    }

    @Override
    public boolean elimina(int idCarta) {
        List<CartaFisica> carte = trovaTutte();

        boolean rimossa = carte.removeIf(
                carta -> carta.getIdCarta() == idCarta
        );

        if (!rimossa) {
            return false;
        }

        return scriviTutte(carte);
    }

    private boolean scriviTutte(List<CartaFisica> carte) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                percorsoFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            for (CartaFisica carta : carte) {
                writer.write(convertiInRiga(carta));
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            return false;
        }
    }
}