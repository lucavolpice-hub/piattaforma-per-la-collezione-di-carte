package Gui;

import controller.ControllerUtenti;
import model.Utente;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class LoginGui extends JFrame {

    private final ControllerUtenti controllerUtenti;
    private final Consumer<Utente> onLoginSuccess;

    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JButton btnAccedi;
    private final JButton btnRegistrati;

    public LoginGui(
            ControllerUtenti controllerUtenti,
            Consumer<Utente> onLoginSuccess
    ) {

        this.controllerUtenti = controllerUtenti;
        this.onLoginSuccess = onLoginSuccess;

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        btnAccedi = new JButton("Accedi");
        btnRegistrati = new JButton("Registrati");

        // COLORI

        Color sfondo = new Color(28, 39, 58);
        Color pannello = new Color(245, 247, 250);
        Color titolo = new Color(28, 39, 58);
        Color bordo = new Color(180, 190, 205);
        Color bluScuro = new Color(15, 35, 65);

        // FINESTRA

        setTitle("TCG Trade - Accesso");
        setSize(520, 360);
        setMinimumSize(new Dimension(460, 330));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        getContentPane().setBackground(sfondo);
        setLayout(new BorderLayout());

        // TITOLO

        JPanel panelTitolo = new JPanel(new BorderLayout());
        panelTitolo.setBackground(sfondo);
        panelTitolo.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 25, 25, 25
                )
        );

        JLabel lblBenvenuto =
                new JLabel(
                        "BENVENUTO!",
                        SwingConstants.CENTER
                );

        lblBenvenuto.setFont(
                new Font("Arial", Font.BOLD, 28)
        );

        lblBenvenuto.setForeground(Color.WHITE);

        panelTitolo.add(
                lblBenvenuto,
                BorderLayout.CENTER
        );

        add(
                panelTitolo,
                BorderLayout.NORTH
        );

        // PANNELLO CENTRALE

        JPanel panelCentro =
                new JPanel(new BorderLayout());

        panelCentro.setBackground(pannello);

        panelCentro.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 50, 20, 50
                )
        );

        // FORM

        JPanel panelForm =
                new JPanel(
                        new GridLayout(
                                2,
                                2,
                                18,
                                18
                        )
                );

        panelForm.setBackground(pannello);

        JLabel lblUsername =
                new JLabel("Username:");

        JLabel lblPassword =
                new JLabel("Password:");

        for (JLabel label :
                new JLabel[]{lblUsername, lblPassword}) {

            label.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            15
                    )
            );

            label.setForeground(titolo);
        }

        configuraCampo(
                txtUsername,
                bordo
        );

        configuraCampo(
                txtPassword,
                bordo
        );

        panelForm.add(lblUsername);
        panelForm.add(txtUsername);

        panelForm.add(lblPassword);
        panelForm.add(txtPassword);

        panelCentro.add(
                panelForm,
                BorderLayout.CENTER
        );

        add(
                panelCentro,
                BorderLayout.CENTER
        );

        // BOTTONI

        JPanel panelBottoni =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                20,
                                20
                        )
                );

        panelBottoni.setBackground(pannello);

        configuraBottone(
                btnAccedi,
                bluScuro
        );

        configuraBottone(
                btnRegistrati,
                bluScuro
        );

        panelBottoni.add(btnAccedi);
        panelBottoni.add(btnRegistrati);

        add(
                panelBottoni,
                BorderLayout.SOUTH
        );

        // LISTENER

        btnAccedi.addActionListener(e ->
                eseguiLogin()
        );

        btnRegistrati.addActionListener(e ->
                eseguiRegistrazione()
        );

        txtPassword.addActionListener(e ->
                eseguiLogin()
        );
    }

// CONFIGURAZIONE CAMPI

    private void configuraCampo(
            JTextField campo,
            Color bordo
    ) {

        campo.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        campo.setPreferredSize(
                new Dimension(
                        220,
                        38
                )
        );

        campo.setBackground(Color.WHITE);
        campo.setForeground(Color.DARK_GRAY);

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                bordo,
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                7,
                                10,
                                7,
                                10
                        )
                )
        );
    }

// CONFIGURAZIONE BOTTONI

    private void configuraBottone(
            JButton bottone,
            Color colore
    ) {

        bottone.setPreferredSize(
                new Dimension(
                        155,
                        44
                )
        );

        bottone.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        bottone.setForeground(Color.WHITE);

        // FORZA IL COLORE BLU DEL BOTTONE
        bottone.setBackground(colore);
        bottone.setOpaque(true);
        bottone.setContentAreaFilled(true);
        bottone.setBorderPainted(false);

        bottone.setFocusPainted(false);

        bottone.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        bottone.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        20,
                        8,
                        20
                )
        );
    }

// LOGIN

    private void eseguiLogin() {

        String username =
                txtUsername.getText().trim();

        String password =
                new String(
                        txtPassword.getPassword()
                );

        if (username.isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Inserisci lo username.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
            );

            txtUsername.requestFocus();
            return;
        }

        if (password.isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Inserisci la password.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
            );

            txtPassword.requestFocus();
            return;
        }

        Utente utente =
                controllerUtenti.accedi(
                        username,
                        password
                );

        if (utente == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Username o password non corretti.",
                    "Accesso negato",
                    JOptionPane.ERROR_MESSAGE
            );

            txtPassword.setText("");
            txtPassword.requestFocus();
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Accesso effettuato con successo!",
                "Benvenuto",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();

        if (onLoginSuccess != null) {
            onLoginSuccess.accept(utente);
        }
    }

// REGISTRAZIONE

    private void eseguiRegistrazione() {

        JTextField usernameField =
                new JTextField();

        JPasswordField passwordField =
                new JPasswordField();

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                2,
                                2,
                                12,
                                12
                        )
                );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        5,
                        10
                )
        );

        panel.add(
                new JLabel("Username:")
        );

        panel.add(
                usernameField
        );

        panel.add(
                new JLabel("Password:")
        );

        panel.add(
                passwordField
        );

        int risultato =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Registrazione",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (risultato != JOptionPane.OK_OPTION) {
            return;
        }

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        if (username.isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Inserisci lo username.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        if (password.isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Inserisci la password.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        Utente nuovoUtente =
                controllerUtenti.registraUtente(
                        username,
                        password
                );

        if (nuovoUtente == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Username già utilizzato.",
                    "Registrazione fallita",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Utente registrato con successo!",
                "Registrazione completata",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();

        if (onLoginSuccess != null) {
            onLoginSuccess.accept(nuovoUtente);
        }
    }

}
