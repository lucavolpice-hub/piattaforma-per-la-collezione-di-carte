package Gui;

import javax.swing.*;
import java.awt.*;

public class LoginGui extends JFrame {

    public LoginGui() {
        // 1. Impostazioni base della finestra (Il Frame)
        setTitle("TCG Trade - Accesso");
        setSize(400, 250); // Larghezza, Altezza
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Chiude il programma con la X
        setLocationRelativeTo(null); // Centra la finestra nello schermo

        // Uso un BorderLayout per dividere la schermata in zone
        setLayout(new BorderLayout());

        // 2. Zona NORD: Il messaggio di benvenuto
        JLabel lblBenvenuto = new JLabel("BENVENUTO!", SwingConstants.CENTER);
        lblBenvenuto.setFont(new Font("Arial", Font.BOLD, 16));
        lblBenvenuto.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0)); // Aggiunge un po' di spazio
        add(lblBenvenuto, BorderLayout.NORTH);

        // 3. Zona CENTRO: Campi Username e Password
        // Uso un GridLayout (2 righe, 2 colonne) per allineare perfettamente etichette e campi
        JPanel panelForm = new JPanel(new GridLayout(2, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30)); // Spazio ai lati

        JLabel lblUsername = new JLabel("Username:");
        JTextField txtUsername = new JTextField(); // Campo testo normale

        JLabel lblPassword = new JLabel("Password:");
        JPasswordField txtPassword = new JPasswordField(); // Campo che nasconde i caratteri con gli ***

        // Aggiungo gli elementi alla griglia nell'ordine in cui voglio che appaiano
        panelForm.add(lblUsername);
        panelForm.add(txtUsername);
        panelForm.add(lblPassword);
        panelForm.add(txtPassword);

        add(panelForm, BorderLayout.CENTER);

        // 4. Zona SUD: I bottoni
        // Uso un FlowLayout per mettere i bottoni in fila al centro
        JPanel panelBottoni = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnAccedi = new JButton("Accedi");
        JButton btnRegistrati = new JButton("Registrati");

        panelBottoni.add(btnAccedi);
        panelBottoni.add(btnRegistrati);

        add(panelBottoni, BorderLayout.SOUTH);
    }

    // Metodo Main per avviare l'interfaccia
    public static void main(String[] args) {
        // Rende visibile la GUI
        LoginGui finestra = new LoginGui();
        finestra.setVisible(true);
    }
}