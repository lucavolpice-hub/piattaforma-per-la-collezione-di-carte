package Gui;

import controller.ControllerUtenti;
import model.Utente;

import javax.swing.*;

public class FinestraAccesso {
    private JPanel panelPrincipale;
    private JTextField InserisciUsername;
    private JPasswordField InserisciPassword;
    private JButton pulsanteAccedi;
    private JButton pulsanteRegistrati;
    private JLabel etichettaMessaggio;

    private final ControllerUtenti controllerUtenti;

    public FinestraAccesso(ControllerUtenti controllerUtenti) {
        this.controllerUtenti = controllerUtenti;

        pulsanteAccedi.addActionListener(e -> gestisciLogin());
        pulsanteRegistrati.addActionListener(e -> gestisciRegistrazione());
    }

    private void gestisciLogin() {
        String username = InserisciUsername.getText();
        String password = new String(InserisciPassword.getPassword());

        try {
            Utente utente = controllerUtenti.accedi(username, password);

            if (utente != null) {
                etichettaMessaggio.setText("Login riuscito: " + utente.getUsername());
            } else {
                etichettaMessaggio.setText("Username o password errati.");
            }
        } catch (Exception ex) {
            etichettaMessaggio.setText("Errore imprevisto durante il login.");
        }
    }

    private void gestisciRegistrazione() {
        String username = InserisciUsername.getText();
        String password = new String(InserisciPassword.getPassword());

        try {
            boolean registrato = controllerUtenti.registraUtente(username, password);

            if (registrato) {
                etichettaMessaggio.setText("Registrazione completata. Ora puoi accedere.");
            } else {
                etichettaMessaggio.setText("Registrazione non riuscita: username non valido o già in uso.");
            }
        } catch (Exception ex) {
            etichettaMessaggio.setText("Errore imprevisto durante la registrazione.");
        }
    }

    public JPanel getPanelPrincipale() {
        return panelPrincipale;
    }
}
