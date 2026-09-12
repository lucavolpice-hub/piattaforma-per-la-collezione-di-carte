package Gui;

import controller.ControllerAnnunci;
import controller.ControllerScambi;
import controller.ControllerUtenti;
import model.Annuncio;
import model.AnnuncioScambio;
import model.AnnuncioVendita;
import model.CartaFisica;
import model.CategoriaCarta;
import model.PropostaScambio;
import model.Utente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BachecaAnnunci extends JFrame {

    private final Utente utente;
    private final ControllerAnnunci controllerAnnunci;
    private final ControllerUtenti controllerUtenti;
    private final ControllerScambi controllerScambi;

    private final JComboBox<String> comboCategoria;
    private final JComboBox<String> comboTipologia;
    private final JTable tabellaAnnunci;

    // Tiene traccia degli annunci attualmente visualizzati nella tabella
    private List<Annuncio> annunciVisualizzati = new ArrayList<>();

    public BachecaAnnunci(
            Utente utente,
            ControllerAnnunci controllerAnnunci,
            ControllerUtenti controllerUtenti,
            ControllerScambi controllerScambi
    ) {

        this.utente = utente;
        this.controllerAnnunci = controllerAnnunci;
        this.controllerUtenti = controllerUtenti;
        this.controllerScambi = controllerScambi;

        setTitle("Bacheca Annunci");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // =========================
        // NORTH: TITOLO
        // =========================

        JPanel pannelloTitolo = new JPanel();

        pannelloTitolo.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 0, 0, 0
                )
        );

        JLabel titolo =
                new JLabel("Bacheca Annunci");

        titolo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        pannelloTitolo.add(titolo);

        // =========================
        // CENTER
        // =========================

        JPanel pannelloCenter = new JPanel();

        pannelloCenter.setLayout(
                new BorderLayout(0, 15)
        );

        pannelloCenter.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 20, 10, 20
                )
        );

        // =========================
        // FILTRI
        // =========================

        JPanel pannelloFiltri = new JPanel();

        pannelloFiltri.setLayout(
                new GridLayout(3, 1)
        );

        // Riga 1
        JPanel riga1 =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                0,
                                0
                        )
                );

        JLabel etichettaFiltri =
                new JLabel("FILTRI:");

        etichettaFiltri.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12
                )
        );

        riga1.add(etichettaFiltri);

        // Riga 2
        JPanel rigaCombo = new JPanel();

        JLabel etichettaCategoria =
                new JLabel("Categoria:");

        String[] categorie = {
                "Singola",
                "Set",
                "Box"
        };

        comboCategoria =
                new JComboBox<>(categorie);

        JLabel etichettaTipologia =
                new JLabel("Tipologia:");

        String[] tipologie = {
                "Scambio",
                "Vendita"
        };

        comboTipologia =
                new JComboBox<>(tipologie);

        rigaCombo.add(
                etichettaCategoria
        );

        rigaCombo.add(
                comboCategoria
        );

        rigaCombo.add(
                etichettaTipologia
        );

        rigaCombo.add(
                comboTipologia
        );

        // Riga 3
        JPanel rigaBottone =
                new JPanel();

        JButton pulsanteCerca =
                new JButton(
                        "Cerca / Applica Filtri"
                );

        rigaBottone.add(
                pulsanteCerca
        );

        pannelloFiltri.add(riga1);
        pannelloFiltri.add(rigaCombo);
        pannelloFiltri.add(rigaBottone);

        // =========================
        // TABELLA
        // =========================

        String[] colonne = {
                "Nome Oggetto",
                "Tipo",
                "Prezzo/Valore",
                "Stato"
        };

        Object[][] dati = {};

        tabellaAnnunci =
                new JTable(
                        dati,
                        colonne
                );

        tabellaAnnunci.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(200);

        tabellaAnnunci.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(80);

        tabellaAnnunci.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(100);

        tabellaAnnunci.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(100);

        JScrollPane pannelloTabella =
                new JScrollPane(
                        tabellaAnnunci
                );

        pannelloCenter.add(
                pannelloFiltri,
                BorderLayout.NORTH
        );

        pannelloCenter.add(
                pannelloTabella,
                BorderLayout.CENTER
        );

        // =========================
        // SOUTH: PULSANTI
        // =========================

        JPanel pannelloPulsanti =
                new JPanel();

        pannelloPulsanti.setLayout(
                new FlowLayout()
        );

        pannelloPulsanti.setBorder(
                BorderFactory.createEmptyBorder(
                        0, 0, 15, 0
                )
        );

        JButton pulsanteProposta =
                new JButton(
                        "Fai una Proposta/Acquista"
                );

        JButton pulsanteProfilo =
                new JButton(
                        "Vai al Profilo"
                );

        pannelloPulsanti.add(
                pulsanteProposta
        );

        pannelloPulsanti.add(
                pulsanteProfilo
        );

        // =========================
        // LISTENER CERCA
        // =========================

        pulsanteCerca.addActionListener(
                e -> applicaFiltri()
        );

        // =========================
        // LISTENER PROPOSTA / ACQUISTO
        // =========================

        pulsanteProposta.addActionListener(
                e -> gestisciPropostaOAcquisto()
        );

        // =========================
        // LISTENER PROFILO
        // =========================

        pulsanteProfilo.addActionListener(
                e -> {

                    AreaPersonale areaPersonale =
                            new AreaPersonale(
                                    utente,
                                    controllerUtenti,
                                    controllerAnnunci,
                                    controllerScambi
                            );

                    areaPersonale.setVisible(true);

                    dispose();
                }
        );

        // =========================
        // ASSEMBLAGGIO
        // =========================

        add(
                pannelloTitolo,
                BorderLayout.NORTH
        );

        add(
                pannelloCenter,
                BorderLayout.CENTER
        );

        add(
                pannelloPulsanti,
                BorderLayout.SOUTH
        );

        setLocationRelativeTo(null);

        // Carica gli annunci filtrati
        applicaFiltri();
    }

    // =====================================================
    // APPLICA FILTRI
    // =====================================================

    private void applicaFiltri() {

        String categoriaSelezionata =
                (String) comboCategoria.getSelectedItem();

        String tipologiaSelezionata =
                (String) comboTipologia.getSelectedItem();

        CategoriaCarta categoria;

        switch (categoriaSelezionata) {

            case "Singola":
                categoria =
                        CategoriaCarta.CARTA_SINGOLA;
                break;

            case "Set":
                categoria =
                        CategoriaCarta.LOTTO;
                break;

            case "Box":
                categoria =
                        CategoriaCarta.BOX;
                break;

            default:
                return;
        }

        List<Annuncio> annunci =
                controllerAnnunci.cercaAnnunciPerCategoria(
                        categoria
                );

        DefaultTableModel modello =
                new DefaultTableModel(
                        new Object[][]{},
                        new String[]{
                                "Nome Oggetto",
                                "Tipo",
                                "Prezzo/Valore",
                                "Stato"
                        }
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        // Aggiorniamo la lista degli annunci visualizzati
        annunciVisualizzati = new ArrayList<>();

        for (Annuncio annuncio : annunci) {

            boolean tipologiaCorretta;

            if (tipologiaSelezionata.equals("Scambio")) {

                tipologiaCorretta =
                        annuncio instanceof AnnuncioScambio;

            } else {

                tipologiaCorretta =
                        annuncio instanceof AnnuncioVendita;
            }

            if (!tipologiaCorretta) {
                continue;
            }

            annunciVisualizzati.add(annuncio);

            String nome =
                    annuncio.getDescrizione();

            String tipo;
            String valore;

            if (annuncio instanceof AnnuncioScambio) {

                AnnuncioScambio scambio =
                        (AnnuncioScambio) annuncio;

                tipo = "Scambio";

                valore =
                        "Val. "
                                + scambio.getValoreDiRiferimento()
                                + " €";

            } else {

                AnnuncioVendita vendita =
                        (AnnuncioVendita) annuncio;

                tipo = "Vendita";

                valore =
                        vendita.getPrezzo()
                                + " €";
            }

            String stato =
                    annuncio.getStato().toString();

            modello.addRow(
                    new Object[]{
                            nome,
                            tipo,
                            valore,
                            stato
                    }
            );
        }

        tabellaAnnunci.setModel(
                modello
        );
    }

    // =====================================================
    // PROPOSTA / ACQUISTO
    // =====================================================

    private void gestisciPropostaOAcquisto() {

        int rigaSelezionata =
                tabellaAnnunci.getSelectedRow();

        if (rigaSelezionata == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleziona prima un annuncio.",
                    "Attenzione",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (rigaSelezionata >= annunciVisualizzati.size()) {
            return;
        }

        Annuncio annuncio =
                annunciVisualizzati.get(rigaSelezionata);

        // ==========================================
        // PROPOSTA DI SCAMBIO
        // ==========================================

        if (annuncio instanceof AnnuncioScambio) {

            AnnuncioScambio annuncioScambio =
                    (AnnuncioScambio) annuncio;

            if (annuncioScambio.getCreatore()
                    .getUsername()
                    .equalsIgnoreCase(
                            utente.getUsername()
                    )) {

                JOptionPane.showMessageDialog(
                        this,
                        "Non puoi fare una proposta sul tuo stesso annuncio.",
                        "Operazione non consentita",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            List<CartaFisica> carteDisponibili =
                    controllerUtenti.getCarteDisponibili(
                            utente
                    );

            if (carteDisponibili.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Non hai carte disponibili da offrire.",
                        "Nessuna carta disponibile",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            StringBuilder elencoCarte =
                    new StringBuilder();

            for (CartaFisica carta : carteDisponibili) {

                elencoCarte.append(
                                carta.getIdCarta()
                        )
                        .append(" - ")
                        .append(
                                carta.getNomeCarta()
                        )
                        .append("\n");
            }

            String input =
                    JOptionPane.showInputDialog(
                            this,
                            "Inserisci gli ID delle carte da offrire,\n"
                                    + "separati da virgola.\n\n"
                                    + elencoCarte,
                            "Nuova proposta di scambio",
                            JOptionPane.PLAIN_MESSAGE
                    );

            if (input == null || input.isBlank()) {
                return;
            }

            List<CartaFisica> carteOfferte =
                    new ArrayList<>();

            String[] idInseriti =
                    input.split(",");

            for (String valore : idInseriti) {

                try {

                    int idCarta =
                            Integer.parseInt(
                                    valore.trim()
                            );

                    CartaFisica cartaTrovata = null;

                    for (CartaFisica carta :
                            carteDisponibili) {

                        if (carta.getIdCarta()
                                == idCarta) {

                            cartaTrovata = carta;
                            break;
                        }
                    }

                    if (cartaTrovata == null) {

                        JOptionPane.showMessageDialog(
                                this,
                                "La carta con ID "
                                        + idCarta
                                        + " non è disponibile.",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE
                        );

                        return;
                    }

                    if (!carteOfferte.contains(
                            cartaTrovata
                    )) {

                        carteOfferte.add(
                                cartaTrovata
                        );
                    }

                } catch (NumberFormatException ex) {

                    JOptionPane.showMessageDialog(
                            this,
                            "ID non valido: "
                                    + valore.trim(),
                            "Errore",
                            JOptionPane.ERROR_MESSAGE
                    );

                    return;
                }
            }

            PropostaScambio proposta =
                    controllerScambi.inviaProposta(
                            utente,
                            annuncioScambio,
                            carteOfferte
                    );

            if (proposta == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Impossibile inviare la proposta.\n"
                                + "L'annuncio potrebbe non essere più disponibile "
                                + "oppure alcune carte non sono più utilizzabili.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE
                );

                applicaFiltri();
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Proposta inviata correttamente!\n"
                            + "ID proposta: "
                            + proposta.getIdProposta(),
                    "Proposta inviata",
                    JOptionPane.INFORMATION_MESSAGE
            );

            applicaFiltri();

            return;
        }

        // ==========================================
        // ACQUISTO
        // ==========================================

        if (annuncio instanceof AnnuncioVendita) {

            AnnuncioVendita annuncioVendita =
                    (AnnuncioVendita) annuncio;

            // Non puoi acquistare il tuo stesso annuncio
            if (annuncioVendita.getCreatore()
                    .getUsername()
                    .equalsIgnoreCase(
                            utente.getUsername()
                    )) {

                JOptionPane.showMessageDialog(
                        this,
                        "Non puoi acquistare il tuo stesso annuncio.",
                        "Operazione non consentita",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // Controllo disponibilità
            if (annuncioVendita.getStato()
                    != model.StatoAnnuncio.DISPONIBILE) {

                JOptionPane.showMessageDialog(
                        this,
                        "Questo annuncio non è più disponibile.",
                        "Annuncio non disponibile",
                        JOptionPane.WARNING_MESSAGE
                );

                applicaFiltri();
                return;
            }

            // Conferma acquisto
            int conferma =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Vuoi acquistare questo annuncio?\n\n"
                                    + "Oggetto: "
                                    + annuncioVendita.getDescrizione()
                                    + "\nPrezzo: "
                                    + annuncioVendita.getPrezzo()
                                    + " €",
                            "Conferma acquisto",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

            if (conferma != JOptionPane.YES_OPTION) {
                return;
            }

            // Chiamata al controller
            boolean acquistoRiuscito =
                    controllerAnnunci.acquistaAnnuncio(
                            utente,
                            annuncioVendita
                    );

            if (!acquistoRiuscito) {

                JOptionPane.showMessageDialog(
                        this,
                        "Impossibile completare l'acquisto.\n"
                                + "L'annuncio potrebbe non essere più disponibile "
                                + "oppure le carte non sono più presenti.",
                        "Acquisto non riuscito",
                        JOptionPane.ERROR_MESSAGE
                );

                applicaFiltri();
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Acquisto completato con successo!\n\n"
                            + "Hai acquistato: "
                            + annuncioVendita.getDescrizione()
                            + "\nPrezzo: "
                            + annuncioVendita.getPrezzo()
                            + " €",
                    "Acquisto completato",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Aggiorniamo la tabella
            applicaFiltri();
        }
    }
}