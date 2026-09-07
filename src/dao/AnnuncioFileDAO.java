package dao;

import model.Annuncio;
import model.AnnuncioScambio;
import model.AnnuncioVendita;
import model.CategoriaCarta;
import model.StatoAnnuncio;
import model.Utente;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class AnnuncioFileDAO implements AnnuncioDAO {

    private final Path percorsoFile;
    private final UtenteDAO utenteDAO;

    public AnnuncioFileDAO(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
        percorsoFile = Paths.get("data", "annunci.txt");

        try {
            if (Files.notExists(percorsoFile)) {
                Files.createDirectories(percorsoFile.getParent());
                Files.createFile(percorsoFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossibile inizializzare il file degli annunci",
                    e
            );
        }
    }

    @Override
    public boolean salva(Annuncio annuncio) {
        if (annuncio == null) {
            return false;
        }

        if (cercaPerId(annuncio.getIdAnnuncio()) != null) {
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                percorsoFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(convertiInRiga(annuncio));
            writer.newLine();

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Annuncio cercaPerId(int idAnnuncio) {
        for (Annuncio annuncio : trovaTutti()) {
            if (annuncio.getIdAnnuncio() == idAnnuncio) {
                return annuncio;
            }
        }

        return null;
    }

    @Override
    public List<Annuncio> trovaTutti() {
        List<Annuncio> annunci = new ArrayList<>();

        try {
            List<String> righe = Files.readAllLines(percorsoFile);

            for (String riga : righe) {
                if (!riga.isBlank()) {
                    Annuncio annuncio = convertiInAnnuncio(riga);

                    if (annuncio != null) {
                        annunci.add(annuncio);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossibile leggere il file degli annunci",
                    e
            );
        }

        return annunci;
    }

    @Override
    public boolean aggiorna(Annuncio annuncio) {
        if (annuncio == null) {
            return false;
        }

        List<Annuncio> annunci = trovaTutti();
        boolean trovato = false;

        for (int i = 0; i < annunci.size(); i++) {
            if (annunci.get(i).getIdAnnuncio() == annuncio.getIdAnnuncio()) {
                annunci.set(i, annuncio);
                trovato = true;
                break;
            }
        }

        if (!trovato) {
            return false;
        }

        return scriviTutti(annunci);
    }

    @Override
    public boolean elimina(int idAnnuncio) {
        List<Annuncio> annunci = trovaTutti();

        boolean rimosso = annunci.removeIf(
                annuncio -> annuncio.getIdAnnuncio() == idAnnuncio
        );

        if (!rimosso) {
            return false;
        }

        return scriviTutti(annunci);
    }

    private String convertiInRiga(Annuncio annuncio) {
        String tipo;
        double valoreNumerico;

        if (annuncio instanceof AnnuncioVendita) {
            tipo = "VENDITA";
            valoreNumerico = ((AnnuncioVendita) annuncio).getPrezzo();
        } else if (annuncio instanceof AnnuncioScambio) {
            tipo = "SCAMBIO";
            valoreNumerico =
                    ((AnnuncioScambio) annuncio).getValoreDiRiferimento();
        } else {
            throw new IllegalArgumentException(
                    "Tipo di annuncio non gestito: " + annuncio.getClass()
            );
        }

        return tipo + ";"
                + annuncio.getIdAnnuncio() + ";"
                + annuncio.getDescrizione() + ";"
                + annuncio.getCategoria() + ";"
                + annuncio.getStato() + ";"
                + annuncio.getCreatore().getUsername() + ";"
                + valoreNumerico;
    }

    private Annuncio convertiInAnnuncio(String riga) {
        String[] dati = riga.split(";", -1);

        if (dati.length != 7) {
            throw new IllegalArgumentException(
                    "Formato annuncio non valido: " + riga
            );
        }

        String tipo = dati[0];
        int idAnnuncio = Integer.parseInt(dati[1]);
        String descrizione = dati[2];
        CategoriaCarta categoria = CategoriaCarta.valueOf(dati[3]);
        StatoAnnuncio stato = StatoAnnuncio.valueOf(dati[4]);
        String usernameCreatore = dati[5];
        double valoreNumerico = Double.parseDouble(dati[6]);

        Utente creatore = utenteDAO.cercaPerUsername(usernameCreatore);

        if (creatore == null) {
            return null;
        }

        Annuncio annuncio;

        if (tipo.equals("VENDITA")) {
            annuncio = new AnnuncioVendita(
                    descrizione,
                    categoria,
                    creatore,
                    valoreNumerico
            );
        } else if (tipo.equals("SCAMBIO")) {
            annuncio = new AnnuncioScambio(
                    descrizione,
                    categoria,
                    creatore,
                    valoreNumerico
            );
        } else {
            return null;
        }

        annuncio.setStato(stato);

        return annuncio;
    }

    private boolean scriviTutti(List<Annuncio> annunci) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                percorsoFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            for (Annuncio annuncio : annunci) {
                writer.write(convertiInRiga(annuncio));
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            return false;
        }
    }
}
