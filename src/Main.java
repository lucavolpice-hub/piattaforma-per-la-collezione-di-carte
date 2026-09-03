import Gui.FinestraAccesso;
import controller.ControllerAnnunci;
import controller.ControllerScambi;
import controller.ControllerUtenti;
import controller.Piattaforma;

import model.Annuncio;
import model.PropostaScambio;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Annuncio> listaAnnunci = new ArrayList<>();
        List<PropostaScambio> listaProposte = new ArrayList<>();
        Piattaforma piattaforma = new Piattaforma(listaAnnunci, listaProposte);

        ControllerUtenti controllerUtenti = new ControllerUtenti(piattaforma);
        ControllerAnnunci controllerAnnunci = new ControllerAnnunci(piattaforma, controllerUtenti);
        ControllerScambi controllerScambi = new ControllerScambi(piattaforma, controllerUtenti);

        SwingUtilities.invokeLater(() -> {
            FinestraAccesso finestraAccesso = new FinestraAccesso(controllerUtenti);

            JFrame frame = new JFrame("Accesso / Registrazione");
            frame.setContentPane(finestraAccesso.getPanelPrincipale());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}