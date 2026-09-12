import controller.ControllerAnnunci;
import controller.ControllerScambi;
import controller.ControllerUtenti;
import controller.Piattaforma;

import Gui.LoginGui;
import Gui.BachecaAnnunci;

import model.Annuncio;
import model.PropostaScambio;

import javax.swing.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        // ==========================================
        // PIATTAFORMA
        // ==========================================

        Piattaforma piattaforma =
                new Piattaforma(
                        new ArrayList<Annuncio>(),
                        new ArrayList<PropostaScambio>()
                );

        // ==========================================
        // CONTROLLER UTENTI
        // ==========================================

        ControllerUtenti controllerUtenti =
                new ControllerUtenti(
                        piattaforma
                );

        // ==========================================
        // CONTROLLER ANNUNCI
        // ==========================================

        ControllerAnnunci controllerAnnunci =
                new ControllerAnnunci(
                        piattaforma,
                        controllerUtenti
                );

        // ==========================================
        // CONTROLLER SCAMBI
        // ==========================================

        ControllerScambi controllerScambi =
                new ControllerScambi(
                        piattaforma,
                        controllerUtenti
                );

        // ==========================================
        // AVVIO GUI
        // ==========================================

        SwingUtilities.invokeLater(() -> {

            LoginGui loginGui =
                    new LoginGui(
                            controllerUtenti,

                            utente -> {

                                BachecaAnnunci bachecaAnnunci =
                                        new BachecaAnnunci(
                                                utente,
                                                controllerAnnunci,
                                                controllerUtenti,
                                                controllerScambi
                                        );

                                bachecaAnnunci.setVisible(true);
                            }
                    );

            loginGui.setVisible(true);
        });
    }
}
