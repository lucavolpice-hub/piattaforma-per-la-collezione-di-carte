// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import Gui.FinestraAccesso;
import controller.ControllerAnnunci;
import controller.ControllerScambi;
import controller.ControllerUtenti;
import controller.Piattaforma;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import model.Annuncio;
import model.PropostaScambio;

public class Main {
    public static void main(String[] args) {
        List<Annuncio> listaAnnunci = new ArrayList();
        List<PropostaScambio> listaProposte = new ArrayList();
        Piattaforma piattaforma = new Piattaforma(listaAnnunci, listaProposte);
        ControllerUtenti controllerUtenti = new ControllerUtenti(piattaforma);
        new ControllerAnnunci(piattaforma, controllerUtenti);
        new ControllerScambi(piattaforma, controllerUtenti);
        SwingUtilities.invokeLater(() -> {
            FinestraAccesso finestraAccesso = new FinestraAccesso(controllerUtenti);
            JFrame frame = new JFrame("Accesso / Registrazione");
            frame.setContentPane(finestraAccesso.getPanelPrincipale());
            frame.setDefaultCloseOperation(3);
            frame.pack();
            frame.setLocationRelativeTo((Component)null);
            frame.setVisible(true);
        });
    }
}
