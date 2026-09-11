import controller.ControllerUtenti;
import controller.Piattaforma;
import Gui.LoginGui;
import Gui.AreaPersonale;

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

        SwingUtilities.invokeLater(() -> {

            LoginGui loginGui =
                    new LoginGui(
                            controllerUtenti,

                            utente -> {

                                AreaPersonale areaPersonale =
                                        new AreaPersonale(utente);

                                areaPersonale.setVisible(true);
                            }
                    );

            loginGui.setVisible(true);
        });
    }
}