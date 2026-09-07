package Gui;

import javax.swing.*;
import java.awt.*;

public class BachecaAnnunci {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Bacheca Annunci");
        frame.setSize(750, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Aggiunto gap orizzontale e verticale al layout principale
        frame.setLayout(new BorderLayout(10, 10));

        //  NORTH: pannello titolo
        JPanel pannelloTitolo = new JPanel();
        pannelloTitolo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Margine superiore
        JLabel titolo = new JLabel("Bacheca Annunci");
        titolo.setFont(new Font("Arial", Font.BOLD, 22)); // Font più grande e in grassetto
        pannelloTitolo.add(titolo);

        //  CENTER: filtri + tabella
        JPanel pannelloCenter = new JPanel();
        pannelloCenter.setLayout(new BorderLayout(0, 15)); // 15px di spazio tra filtri e tabella
        pannelloCenter.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Margini laterali (respiro)

        // sotto-pannello dei filtri
        JPanel pannelloFiltri = new JPanel();
        pannelloFiltri.setLayout(new GridLayout(3, 1)); // 3 righe

        // Riga 1: Etichetta "FILTRI" allineata a sinistra
        JPanel riga1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel etichettaFiltri = new JLabel("FILTRI:");
        etichettaFiltri.setFont(new Font("Arial", Font.BOLD, 12));
        riga1.add(etichettaFiltri);

        // Riga 2: Combo box (già centrate dal FlowLayout di default)
        JPanel rigaCombo = new JPanel();
        JLabel etichettaCategoria = new JLabel("Categoria:");
        String[] categorie = {"Singola", "Set", "Box"};
        JComboBox<String> comboCategoria = new JComboBox<>(categorie);

        JLabel etichettaTipologia = new JLabel("Tipologia:");
        String[] tipologie = {"Scambio", "Vendita"};
        JComboBox<String> comboTipologia = new JComboBox<>(tipologie);

        rigaCombo.add(etichettaCategoria);
        rigaCombo.add(comboCategoria);
        rigaCombo.add(etichettaTipologia);
        rigaCombo.add(comboTipologia);

        // Riga 3: Pulsante cerca (inserito in un JPanel per NON farlo allungare)
        JPanel rigaBottone = new JPanel();
        JButton pulsanteCerca = new JButton("Cerca / Applica Filtri");
        rigaBottone.add(pulsanteCerca);

        pannelloFiltri.add(riga1);
        pannelloFiltri.add(rigaCombo);
        pannelloFiltri.add(rigaBottone);

        // tabella annunci
        String[] colonne = {"Nome Oggetto", "Tipo", "Prezzo/Valore", "Stato"};
        Object[][] dati = {
                {"Charizard Base", "Vendita", "150.00 €", "Disponibile"},
                {"Box Set 2020", "Scambio", "Val. 200.00 €", "Disponibile"},
                {"Pikachu Promo", "Vendita", "50.00 €", "In Trattativa"}
        };
        JTable tabellaAnnunci = new JTable(dati, colonne);

        //  Miglioramento proporzioni colonne
        tabellaAnnunci.getColumnModel().getColumn(0).setPreferredWidth(200); // Nome più largo
        tabellaAnnunci.getColumnModel().getColumn(1).setPreferredWidth(80);
        tabellaAnnunci.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabellaAnnunci.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane pannelloTabella = new JScrollPane(tabellaAnnunci);

        pannelloCenter.add(pannelloFiltri, BorderLayout.NORTH);
        pannelloCenter.add(pannelloTabella, BorderLayout.CENTER);

        //  SOUTH: pulsanti azione
        JPanel pannelloPulsanti = new JPanel();
        pannelloPulsanti.setLayout(new FlowLayout());
        pannelloPulsanti.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0)); // Margine in basso
        JButton pulsanteProposta = new JButton("Fai una Proposta/Acquista");
        JButton pulsanteProfilo = new JButton("Vai al Profilo");
        pannelloPulsanti.add(pulsanteProposta);
        pannelloPulsanti.add(pulsanteProfilo);

        //  assemblaggio finale
        frame.add(pannelloTitolo, BorderLayout.NORTH);
        frame.add(pannelloCenter, BorderLayout.CENTER);
        frame.add(pannelloPulsanti, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}