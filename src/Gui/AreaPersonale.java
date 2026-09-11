package Gui;

import model.Utente;

import javax.swing.*;
import java.awt.*;

public class AreaPersonale extends JFrame {

    private final Utente utente;

    public AreaPersonale(Utente utente) {

        this.utente = utente;

        setTitle("Area Personale - " + utente.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ==========================================
        // NORTH: PANNELLO TITOLO
        // ==========================================

        JPanel pannelloTitolo = new JPanel();

        JLabel titolo =
                new JLabel("Area Personale – Il tuo Profilo");

        pannelloTitolo.add(titolo);

        // ==========================================
        // CENTER: INVENTARIO
        // ==========================================

        JPanel pannelloCenter = new JPanel();
        pannelloCenter.setLayout(new BorderLayout());

        JLabel etichettaInventario =
                new JLabel("IL TUO INVENTARIO (Carte Disponibili)");

        String[] colonneCarte = {
                "Id",
                "Nome Carta",
                "Condizione",
                "Lingua"
        };

        Object[][] datiCarte = {
                {1, "Mewtwo Promo", "Mint", "ITA"},
                {2, "Rayquaza EX", "Good", "ENG"}
        };

        JTable tabellaCarte =
                new JTable(datiCarte, colonneCarte);

        tabellaCarte.setRowHeight(24);

        tabellaCarte.setPreferredScrollableViewportSize(
                new Dimension(650, 60)
        );

        JScrollPane scrollTabella =
                new JScrollPane(tabellaCarte);

        scrollTabella.setPreferredSize(
                new Dimension(800, 100)
        );

        JPanel pannelloBottoniInventario =
                new JPanel();

        pannelloBottoniInventario.setLayout(
                new FlowLayout()
        );

        JButton pulsanteAggiungi =
                new JButton("Aggiungi Nuova Carta");

        JButton pulsanteRimuovi =
                new JButton("Rimuovi Carta");

        pannelloBottoniInventario.add(pulsanteAggiungi);
        pannelloBottoniInventario.add(pulsanteRimuovi);

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

        // ==========================================
        // SOUTH: RECENSIONE
        // ==========================================

        JPanel pannelloRecensione = new JPanel();

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

        // ------------------------------------------
        // Riga 1: Venditore + Voto
        // ------------------------------------------

        JPanel rigaVenditoreVoto =
                new JPanel();

        rigaVenditoreVoto.setLayout(
                new FlowLayout(FlowLayout.LEFT)
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

        rigaVenditoreVoto.add(etichettaVenditore);
        rigaVenditoreVoto.add(campoVenditore);
        rigaVenditoreVoto.add(etichettaVoto);
        rigaVenditoreVoto.add(comboVoto);

        // ------------------------------------------
        // Riga 2: Commento
        // ------------------------------------------

        JLabel etichettaCommento =
                new JLabel("Commento:");

        // ------------------------------------------
        // Riga 3: Area commento
        // ------------------------------------------

        JTextArea areaCommento =
                new JTextArea(3, 30);

        areaCommento.setLineWrap(true);
        areaCommento.setWrapStyleWord(true);

        JScrollPane scrollCommento =
                new JScrollPane(areaCommento);

        // ------------------------------------------
        // Riga 4: Bottoni
        // ------------------------------------------

        JPanel rigaBottoniRecensione =
                new JPanel();

        rigaBottoniRecensione.setLayout(
                new FlowLayout()
        );

        JButton pulsanteInvia =
                new JButton("Invia Recensione");

        JButton pulsanteTorna =
                new JButton("Torna ad Annunci");

        rigaBottoniRecensione.add(pulsanteInvia);
        rigaBottoniRecensione.add(pulsanteTorna);

        // ------------------------------------------
        // Assemblaggio pannello recensione
        // ------------------------------------------

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
        // ==========================================
        // ASSEMBLAGGIO FRAME
        // ==========================================

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
        // ==========================================
        // IMPOSTAZIONI FINALI
        // ==========================================

        pack();
        setLocationRelativeTo(null);
    }
}

