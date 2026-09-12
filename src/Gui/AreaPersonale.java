package Gui;
import controller.ControllerScambi;
import controller.ControllerAnnunci;
import controller.ControllerUtenti;

import model.AnnuncioScambio;
import model.AnnuncioVendita;
import model.CartaFisica;
import model.CategoriaCarta;
import model.Recensione;
import model.Utente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AreaPersonale extends JFrame {

    private final Utente utente;
    private final ControllerUtenti controllerUtenti;
    private final ControllerAnnunci controllerAnnunci;
    private final ControllerScambi controllerScambi;

    private final JTable tabellaCarte;

    private final JButton pulsanteAggiungi;
    private final JButton pulsanteRimuovi;
    private final JButton pulsantePubblica;

    public AreaPersonale(
            Utente utente,
            ControllerUtenti controllerUtenti,
            ControllerAnnunci controllerAnnunci,
            ControllerScambi controllerScambi
    ) {

        this.utente = utente;
        this.controllerUtenti = controllerUtenti;
        this.controllerAnnunci = controllerAnnunci;
        this.controllerScambi = controllerScambi;

        setTitle("Area Personale - " + utente.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // =========================
        // TITOLO
        // =========================

        JPanel pannelloTitolo = new JPanel();

        JLabel titolo =
                new JLabel("Area Personale – Il tuo Profilo");

        pannelloTitolo.add(titolo);

        // =========================
        // INVENTARIO
        // =========================

        JPanel pannelloCenter = new JPanel();
        pannelloCenter.setLayout(new BorderLayout());

        JLabel etichettaInventario =
                new JLabel(
                        "IL TUO INVENTARIO (Carte Disponibili)"
                );

        String[] colonneCarte = {
                "Id",
                "Nome Carta",
                "Condizione",
                "Lingua"
        };

        Object[][] datiCarte =
                creaDatiTabellaCarte();

        tabellaCarte =
                new JTable(
                        datiCarte,
                        colonneCarte
                );

        tabellaCarte.setRowHeight(24);

        tabellaCarte.setPreferredScrollableViewportSize(
                new Dimension(650, 100)
        );

        tabellaCarte.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabellaCarte.setModel(
                new DefaultTableModel(
                        datiCarte,
                        colonneCarte
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                }
        );

        JScrollPane scrollTabella =
                new JScrollPane(tabellaCarte);

        scrollTabella.setPreferredSize(
                new Dimension(800, 130)
        );

        // =========================
        // BOTTONI INVENTARIO
        // =========================

        JPanel pannelloBottoniInventario =
                new JPanel();

        pannelloBottoniInventario.setLayout(
                new FlowLayout()
        );

        pulsanteAggiungi =
                new JButton("Aggiungi Nuova Carta");

        pulsanteRimuovi =
                new JButton("Rimuovi Carta");

        pulsantePubblica =
                new JButton("Pubblica Annuncio");

        pannelloBottoniInventario.add(
                pulsanteAggiungi
        );

        pannelloBottoniInventario.add(
                pulsanteRimuovi
        );

        pannelloBottoniInventario.add(
                pulsantePubblica
        );

        pannelloCenter.add(
                etichettaInventario,
                BorderLayout.NORTH
        );

        pannelloCenter.add(
                scrollTabella,
                BorderLayout.CENTER
        );

        pannelloCenter.add(
                pannelloBottoniInventario,
                BorderLayout.SOUTH
        );

        // =========================
        // LISTENER INVENTARIO
        // =========================

        pulsanteAggiungi.addActionListener(
                e -> aggiungiNuovaCarta()
        );

        pulsanteRimuovi.addActionListener(
                e -> rimuoviCartaSelezionata()
        );

        pulsantePubblica.addActionListener(
                e -> pubblicaAnnuncio()
        );

        // =========================
        // RECENSIONE
        // =========================

        JPanel pannelloRecensione =
                new JPanel();

        pannelloRecensione.setLayout(
                new BoxLayout(
                        pannelloRecensione,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel etichettaRecensione =
                new JLabel("LASCIA UNA RECENSIONE");

        etichettaRecensione.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );

        etichettaRecensione.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        // =========================
        // VENDITORE + VOTO
        // =========================

        JPanel rigaVenditoreVoto =
                new JPanel();

        rigaVenditoreVoto.setLayout(
                new FlowLayout(
                        FlowLayout.LEFT
                )
        );

        JLabel etichettaVenditore =
                new JLabel("Venditore:");

        JTextField campoVenditore =
                new JTextField(15);

        JLabel etichettaVoto =
                new JLabel("Voto:");

        String[] voti = {
                "5 Stelle",
                "4 Stelle",
                "3 Stelle",
                "2 Stelle",
                "1 Stella"
        };

        JComboBox<String> comboVoto =
                new JComboBox<>(voti);

        rigaVenditoreVoto.add(
                etichettaVenditore
        );

        rigaVenditoreVoto.add(
                campoVenditore
        );

        rigaVenditoreVoto.add(
                etichettaVoto
        );

        rigaVenditoreVoto.add(
                comboVoto
        );

        // =========================
        // COMMENTO
        // =========================

        JLabel etichettaCommento =
                new JLabel("Commento:");

        JTextArea areaCommento =
                new JTextArea(
                        3,
                        30
                );

        areaCommento.setLineWrap(true);
        areaCommento.setWrapStyleWord(true);

        JScrollPane scrollCommento =
                new JScrollPane(
                        areaCommento
                );

        // =========================
        // BOTTONI RECENSIONE
        // =========================

        JPanel rigaBottoniRecensione =
                new JPanel();

        rigaBottoniRecensione.setLayout(
                new FlowLayout()
        );

        JButton pulsanteInvia =
                new JButton(
                        "Invia Recensione"
                );

        JButton pulsanteTorna =
                new JButton(
                        "Torna ad Annunci"
                );

        pulsanteTorna.addActionListener(
                e -> {

                    BachecaAnnunci bachecaAnnunci =
                            new BachecaAnnunci(
                                    utente,
                                    controllerAnnunci,
                                    controllerUtenti,
                                    controllerScambi
                            );

                    bachecaAnnunci.setVisible(true);

                    dispose();
                }
        );

        rigaBottoniRecensione.add(
                pulsanteInvia
        );

        rigaBottoniRecensione.add(
                pulsanteTorna
        );

        // =========================
        // LISTENER RECENSIONE
        // =========================

        pulsanteInvia.addActionListener(
                e -> lasciaRecensione(
                        campoVenditore,
                        comboVoto,
                        areaCommento
                )
        );

        // =========================
        // COSTRUZIONE RECENSIONE
        // =========================

        pannelloRecensione.add(
                etichettaRecensione
        );

        pannelloRecensione.add(
                Box.createVerticalStrut(10)
        );

        pannelloRecensione.add(
                rigaVenditoreVoto
        );

        pannelloRecensione.add(
                Box.createVerticalStrut(10)
        );

        pannelloRecensione.add(
                etichettaCommento
        );

        pannelloRecensione.add(
                Box.createVerticalStrut(4)
        );

        pannelloRecensione.add(
                scrollCommento
        );

        pannelloRecensione.add(
                Box.createVerticalStrut(10)
        );

        pannelloRecensione.add(
                rigaBottoniRecensione
        );

        // =========================
        // FRAME
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
                pannelloRecensione,
                BorderLayout.SOUTH
        );

        pack();

        setLocationRelativeTo(null);
    }

    // =====================================================
    // PUBBLICA ANNUNCIO
    // =====================================================

    private void pubblicaAnnuncio() {

        int rigaSelezionata =
                tabellaCarte.getSelectedRow();

        if (rigaSelezionata == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleziona prima una carta dall'inventario.",
                    "Nessuna carta selezionata",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int idCarta =
                (Integer) tabellaCarte.getValueAt(
                        rigaSelezionata,
                        0
                );

        CartaFisica cartaSelezionata = null;

        for (CartaFisica carta :
                utente.getInventario().getCarte()) {

            if (carta.getIdCarta() == idCarta) {

                cartaSelezionata = carta;
                break;
            }
        }

        if (cartaSelezionata == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Carta non trovata nell'inventario.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // =========================
        // TIPO ANNUNCIO
        // =========================

        String[] tipi = {
                "Vendita",
                "Scambio"
        };

        String tipoScelto =
                (String) JOptionPane.showInputDialog(
                        this,
                        "Cosa vuoi fare con "
                                + cartaSelezionata.getNomeCarta()
                                + "?",
                        "Pubblica Annuncio",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        tipi,
                        tipi[0]
                );

        if (tipoScelto == null) {
            return;
        }

        // =========================
        // DESCRIZIONE
        // =========================

        String descrizione =
                JOptionPane.showInputDialog(
                        this,
                        "Inserisci la descrizione dell'annuncio:",
                        "Descrizione",
                        JOptionPane.PLAIN_MESSAGE
                );

        if (descrizione == null
                || descrizione.isBlank()) {
            return;
        }

        // =========================
        // CATEGORIA
        // =========================

        String[] categorie = {
                "Singola",
                "Set",
                "Box"
        };

        String categoriaScelta =
                (String) JOptionPane.showInputDialog(
                        this,
                        "Seleziona la categoria:",
                        "Categoria",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        categorie,
                        categorie[0]
                );

        if (categoriaScelta == null) {
            return;
        }

        CategoriaCarta categoria;

        switch (categoriaScelta) {

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

        // =========================
        // VENDITA
        // =========================

        if (tipoScelto.equals("Vendita")) {

            String prezzoStringa =
                    JOptionPane.showInputDialog(
                            this,
                            "Inserisci il prezzo in euro:",
                            "Prezzo",
                            JOptionPane.PLAIN_MESSAGE
                    );

            if (prezzoStringa == null) {
                return;
            }

            try {

                double prezzo =
                        Double.parseDouble(
                                prezzoStringa.trim()
                        );

                if (prezzo <= 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Il prezzo deve essere maggiore di zero.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE
                    );

                    return;
                }

                AnnuncioVendita annuncio =
                        controllerAnnunci.pubblicaAnnuncioVendita(
                                utente,
                                descrizione,
                                categoria,
                                prezzo,
                                cartaSelezionata
                        );

                if (annuncio == null) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Impossibile pubblicare l'annuncio.\n"
                                    + "La carta potrebbe essere già presente "
                                    + "in un altro annuncio.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE
                    );

                    return;
                }

                JOptionPane.showMessageDialog(
                        this,
                        "Annuncio di vendita pubblicato!\n\n"
                                + "ID annuncio: "
                                + annuncio.getIdAnnuncio()
                                + "\nCarta: "
                                + cartaSelezionata.getNomeCarta()
                                + "\nPrezzo: "
                                + prezzo
                                + " €",
                        "Annuncio pubblicato",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Inserisci un prezzo numerico valido.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE
                );
            }

            return;
        }

        // =========================
        // SCAMBIO
        // =========================

        String valoreStringa =
                JOptionPane.showInputDialog(
                        this,
                        "Inserisci il valore di riferimento della carta:",
                        "Valore di riferimento",
                        JOptionPane.PLAIN_MESSAGE
                );

        if (valoreStringa == null) {
            return;
        }

        try {

            double valore =
                    Double.parseDouble(
                            valoreStringa.trim()
                    );

            if (valore <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Il valore deve essere maggiore di zero.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            AnnuncioScambio annuncio =
                    controllerAnnunci.pubblicaAnnuncioScambio(
                            utente,
                            descrizione,
                            categoria,
                            valore,
                            cartaSelezionata
                    );

            if (annuncio == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Impossibile pubblicare l'annuncio.\n"
                                + "La carta potrebbe essere già presente "
                                + "in un altro annuncio.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Annuncio di scambio pubblicato!\n\n"
                            + "ID annuncio: "
                            + annuncio.getIdAnnuncio()
                            + "\nCarta: "
                            + cartaSelezionata.getNomeCarta()
                            + "\nValore di riferimento: "
                            + valore
                            + " €",
                    "Annuncio pubblicato",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Inserisci un valore numerico valido.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // TABELLA CARTE
    // =====================================================

    private Object[][] creaDatiTabellaCarte() {

        List<CartaFisica> carte =
                controllerUtenti.getCarteDisponibili(
                        utente
                );

        Object[][] dati =
                new Object[carte.size()][4];

        for (int i = 0; i < carte.size(); i++) {

            CartaFisica carta =
                    carte.get(i);

            dati[i][0] =
                    carta.getIdCarta();

            dati[i][1] =
                    carta.getNomeCarta();

            dati[i][2] =
                    carta.getCondizione();

            dati[i][3] =
                    carta.getLingua();
        }

        return dati;
    }

    // =====================================================
    // AGGIUNGI CARTA
    // =====================================================

    private void aggiungiNuovaCarta() {

        JTextField campoId =
                new JTextField();

        JTextField campoNome =
                new JTextField();

        JTextField campoCondizione =
                new JTextField();

        JTextField campoLingua =
                new JTextField();

        JPanel pannello =
                new JPanel(
                        new GridLayout(
                                4,
                                2,
                                5,
                                5
                        )
                );

        pannello.add(
                new JLabel("ID carta:")
        );

        pannello.add(
                campoId
        );

        pannello.add(
                new JLabel("Nome carta:")
        );

        pannello.add(
                campoNome
        );

        pannello.add(
                new JLabel("Condizione:")
        );

        pannello.add(
                campoCondizione
        );

        pannello.add(
                new JLabel("Lingua:")
        );

        pannello.add(
                campoLingua
        );

        int risultato =
                JOptionPane.showConfirmDialog(
                        this,
                        pannello,
                        "Aggiungi nuova carta",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (risultato != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            int idCarta =
                    Integer.parseInt(
                            campoId.getText().trim()
                    );

            String nome =
                    campoNome.getText().trim();

            String condizione =
                    campoCondizione.getText().trim();

            String lingua =
                    campoLingua.getText().trim();

            if (idCarta <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "L'ID della carta deve essere maggiore di zero.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            if (nome.isBlank()
                    || condizione.isBlank()
                    || lingua.isBlank()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Compila tutti i campi.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            CartaFisica carta =
                    new CartaFisica(
                            idCarta,
                            nome,
                            condizione,
                            lingua
                    );

            boolean aggiunta =
                    controllerUtenti.aggiungiCartaInventario(
                            utente,
                            carta
                    );

            if (!aggiunta) {

                JOptionPane.showMessageDialog(
                        this,
                        "Impossibile aggiungere la carta."
                                + "\nControlla che l'ID non sia già presente.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            aggiornaTabellaCarte();

            JOptionPane.showMessageDialog(
                    this,
                    "Carta aggiunta con successo.",
                    "Operazione completata",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "L'ID della carta deve essere un numero.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // RIMUOVI CARTA
    // =====================================================

    private void rimuoviCartaSelezionata() {

        int rigaSelezionata =
                tabellaCarte.getSelectedRow();

        if (rigaSelezionata == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleziona una carta da rimuovere.",
                    "Nessuna carta selezionata",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int idCarta =
                (Integer) tabellaCarte.getValueAt(
                        rigaSelezionata,
                        0
                );

        CartaFisica cartaDaRimuovere = null;

        for (CartaFisica carta :
                utente.getInventario().getCarte()) {

            if (carta.getIdCarta() == idCarta) {

                cartaDaRimuovere = carta;
                break;
            }
        }

        if (cartaDaRimuovere == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Carta non trovata nell'inventario.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        if (cartaDaRimuovere.isBloccataInScambio()) {

            JOptionPane.showMessageDialog(
                    this,
                    "La carta è attualmente bloccata in uno scambio.",
                    "Operazione non consentita",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int conferma =
                JOptionPane.showConfirmDialog(
                        this,
                        "Vuoi davvero rimuovere "
                                + cartaDaRimuovere.getNomeCarta()
                                + "?",
                        "Conferma rimozione",
                        JOptionPane.YES_NO_OPTION
                );

        if (conferma != JOptionPane.YES_OPTION) {
            return;
        }

        boolean rimossa =
                controllerUtenti.rimuoviCartaInventario(
                        utente,
                        cartaDaRimuovere
                );

        if (!rimossa) {

            JOptionPane.showMessageDialog(
                    this,
                    "Impossibile rimuovere la carta.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        aggiornaTabellaCarte();

        JOptionPane.showMessageDialog(
                this,
                "Carta rimossa con successo.",
                "Operazione completata",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // LASCIA RECENSIONE
    // =====================================================

    private void lasciaRecensione(
            JTextField campoVenditore,
            JComboBox<String> comboVoto,
            JTextArea areaCommento
    ) {

        String usernameVenditore =
                campoVenditore.getText().trim();

        String commento =
                areaCommento.getText().trim();

        if (usernameVenditore.isBlank()
                || commento.isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Compila tutti i campi.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        Utente venditore =
                controllerUtenti.cercaUtente(
                        usernameVenditore
                );

        if (venditore == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Venditore non trovato.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        int voto =
                5 - comboVoto.getSelectedIndex();

        Recensione recensione =
                new Recensione(
                        voto,
                        commento,
                        utente,
                        venditore
                );

        boolean aggiunta =
                controllerUtenti.aggiungiRecensione(
                        recensione
                );

        if (!aggiunta) {

            JOptionPane.showMessageDialog(
                    this,
                    "Impossibile aggiungere la recensione.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Recensione inviata con successo!",
                "Operazione completata",
                JOptionPane.INFORMATION_MESSAGE
        );

        campoVenditore.setText("");
        areaCommento.setText("");
        comboVoto.setSelectedIndex(0);
    }

    // =====================================================
    // AGGIORNA TABELLA
    // =====================================================

    private void aggiornaTabellaCarte() {

        Object[][] dati =
                creaDatiTabellaCarte();

        String[] colonneCarte = {
                "Id",
                "Nome Carta",
                "Condizione",
                "Lingua"
        };

        tabellaCarte.setModel(
                new DefaultTableModel(
                        dati,
                        colonneCarte
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                }
        );

        tabellaCarte.setRowHeight(24);

        tabellaCarte.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
    }
}

