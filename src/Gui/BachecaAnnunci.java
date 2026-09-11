package Gui;

import controller.ControllerAnnunci;
import controller.ControllerUtenti;
import model.Annuncio;
import model.AnnuncioScambio;
import model.AnnuncioVendita;
import model.CategoriaCarta;
import model.Utente;
import model.CategoriaCarta;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;
public class BachecaAnnunci extends JFrame {

    private final Utente utente;
    private final ControllerAnnunci controllerAnnunci;
    private final ControllerUtenti controllerUtenti;

    private final JComboBox<String> comboCategoria;
    private final JComboBox<String> comboTipologia;
    private final JTable tabellaAnnunci;

    public BachecaAnnunci(
            Utente utente,
            ControllerAnnunci controllerAnnunci,
            ControllerUtenti controllerUtenti
    ) {

        this.utente = utente;
        this.controllerAnnunci = controllerAnnunci;
        this.controllerUtenti = controllerUtenti;

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
        // LISTENER PROFILO
        // =========================

        pulsanteProfilo.addActionListener(
                e -> {

                    AreaPersonale areaPersonale =
                            new AreaPersonale(
                                    utente,
                                    controllerUtenti,
                                    controllerAnnunci
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
                categoria = CategoriaCarta.CARTA_SINGOLA;
                break;

            case "Set":
                categoria = CategoriaCarta.LOTTO;
                break;

            case "Box":
                categoria = CategoriaCarta.BOX;
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
}