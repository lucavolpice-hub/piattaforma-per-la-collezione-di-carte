import controller.ControllerAnnunci;
import controller.ControllerUtenti;
import controller.Piattaforma;
import Gui.LoginGui;
import Gui.AreaPersonale;
import Gui.BachecaAnnunci;

import model.Annuncio;
import model.PropostaScambio;

import javax.swing.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        Piattaforma piattaforma =
                new Piattaforma(
                        new ArrayList<Annuncio>(),
                        new ArrayList<PropostaScambio>()
                );

        ControllerUtenti controllerUtenti =
                new ControllerUtenti(piattaforma);

        ControllerAnnunci controllerAnnunci =
                new ControllerAnnunci(
                        piattaforma,
                        controllerUtenti
                );

        SwingUtilities.invokeLater(() -> {

            LoginGui loginGui =
                    new LoginGui(
                            controllerUtenti,

                            utente -> {

                                BachecaAnnunci bachecaAnnunci =
                                        new BachecaAnnunci(
                                                utente,
                                                controllerAnnunci,
                                                controllerUtenti
                                        );

                                bachecaAnnunci.setVisible(true);
                            }
                    );

            loginGui.setVisible(true);
        });
    }
}