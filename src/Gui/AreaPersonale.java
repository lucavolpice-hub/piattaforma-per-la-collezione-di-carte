package Gui;

import javax.swing.*;
import java.awt.*;

public class AreaPersonale {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Area Personale");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        //NORTH: pannello titolo
        JPanel pannelloTitolo = new JPanel();
        JLabel titolo = new JLabel("Area Personale – Il tuo Profilo");
        pannelloTitolo.add(titolo);

        //CENTER: inventario (tabella + bottoni)
        JPanel pannelloCenter = new JPanel();
        pannelloCenter.setLayout(new BorderLayout()); // sotto-layout interno

        JLabel etichettaInventario = new JLabel("IL TUO INVENTARIO (Carte Disponibili)");

        String[] colonneCarte = {"Id", "Nome Carta", "Condizione", "Lingua"};
        Object[][] datiCarte = {
                {1, "Mewtwo Promo", "Mint", "ITA"},
                {2, "Rayquaza EX", "Good", "ENG"}
        };
        JTable tabellaCarte = new JTable(datiCarte, colonneCarte);
        tabellaCarte.setRowHeight(24);
        tabellaCarte.setPreferredScrollableViewportSize(new Dimension(650, 60));
        JScrollPane scrollTabella = new JScrollPane(tabellaCarte);
        scrollTabella.setPreferredSize(new Dimension(800, 100));
        JPanel pannelloBottoniInventario = new JPanel();
        pannelloBottoniInventario.setLayout(new FlowLayout());
        JButton pulsanteAggiungi = new JButton("Aggiungi Nuova Carta");
        JButton pulsanteRimuovi = new JButton("Rimuovi Carta");
        pannelloBottoniInventario.add(pulsanteAggiungi);
        pannelloBottoniInventario.add(pulsanteRimuovi);

        pannelloCenter.add(etichettaInventario, BorderLayout.NORTH);
        pannelloCenter.add(scrollTabella, BorderLayout.CENTER);
        pannelloCenter.add(pannelloBottoniInventario, BorderLayout.SOUTH);

        //SOUTH: recensione
        JPanel pannelloRecensione = new JPanel();
        pannelloRecensione.setLayout(new BoxLayout(pannelloRecensione, BoxLayout.Y_AXIS)); // impila verticalmente

        JLabel etichettaRecensione = new JLabel("LASCIA UNA RECENSIONE");
        etichettaRecensione.setFont(new Font("Arial", Font.BOLD, 16)); // Ingrandisce il testo e lo mette in grassetto
        etichettaRecensione.setAlignmentX(Component.CENTER_ALIGNMENT); // Centra la scritta nel BoxLayout

        // riga 1: Venditore + Voto affiancati
        JPanel rigaVenditoreVoto = new JPanel();
        rigaVenditoreVoto.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel etichettaVenditore = new JLabel("Venditore:");
        JTextField campoVenditore = new JTextField(15);
        JLabel etichettaVoto = new JLabel("Voto:");
        String[] voti = {"5 Stelle", "4 Stelle", "3 Stelle", "2 Stelle", "1 Stella"};
        JComboBox<String> comboVoto = new JComboBox<>(voti);

        rigaVenditoreVoto.add(etichettaVenditore);
        rigaVenditoreVoto.add(campoVenditore);
        rigaVenditoreVoto.add(etichettaVoto);
        rigaVenditoreVoto.add(comboVoto);

        // riga 2: etichetta Commento
        JLabel etichettaCommento = new JLabel("Commento:");

        // riga 3: area di testo per il commento (multi-riga)
        JTextArea areaCommento = new JTextArea(3, 30); // 3 righe visibili, 30 colonne
        areaCommento.setLineWrap(true);
        areaCommento.setWrapStyleWord(true);
        JScrollPane scrollCommento = new JScrollPane(areaCommento); // scroll interno se il testo supera le righe

        // riga 4: bottoni finali
        JPanel rigaBottoniRecensione = new JPanel();
        rigaBottoniRecensione.setLayout(new FlowLayout());
        JButton pulsanteInvia = new JButton("Invia Recensione");
        JButton pulsanteTorna = new JButton("Torna ad Annunci");
        rigaBottoniRecensione.add(pulsanteInvia);
        rigaBottoniRecensione.add(pulsanteTorna);

        // assemblaggio pannelloRecensione (dall'alto verso il basso)
        pannelloRecensione.add(etichettaRecensione);
        pannelloRecensione.add(Box.createVerticalStrut(10));   // <-- spazio
        pannelloRecensione.add(rigaVenditoreVoto);
        pannelloRecensione.add(Box.createVerticalStrut(10));   // <-- spazio
        pannelloRecensione.add(etichettaCommento);
        pannelloRecensione.add(Box.createVerticalStrut(4));    // <-- spazio piccolo, l'etichetta è "attaccata" al campo
        pannelloRecensione.add(scrollCommento);
        pannelloRecensione.add(Box.createVerticalStrut(10));   // <-- spazio
        pannelloRecensione.add(rigaBottoniRecensione);

        frame.add(pannelloTitolo, BorderLayout.NORTH);
        frame.add(pannelloCenter, BorderLayout.CENTER);
        frame.add(pannelloRecensione, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }
}