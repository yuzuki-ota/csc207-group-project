package app;

import view.LookupView;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final JFrame application = new JFrame("WordDeck");
                application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                application.setContentPane(new LookupView());
                application.pack();
                application.setLocationRelativeTo(null);
                application.setVisible(true);
            }
        });
    }
}
