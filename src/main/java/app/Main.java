package app;

import data_access.FileDeckDataAccessObject;
import interface_adapter.review.ReviewController;
import interface_adapter.review.ReviewPresenter;
import interface_adapter.review.ReviewViewModel;
import use_case.review.ReviewInputBoundary;
import use_case.review.ReviewInteractor;
import use_case.review.ReviewOutputBoundary;
import view.LookupView;
import view.ReviewView;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final FileDeckDataAccessObject deckStorage = new FileDeckDataAccessObject("deck.json");

                final ReviewViewModel reviewViewModel = new ReviewViewModel();
                final ReviewOutputBoundary reviewPresenter = new ReviewPresenter(reviewViewModel);
                final ReviewInputBoundary reviewInteractor = new ReviewInteractor(deckStorage, reviewPresenter);
                final ReviewController reviewController = new ReviewController(reviewInteractor);

                final JTabbedPane tabs = new JTabbedPane();
                tabs.addTab("Look up", new LookupView());
                tabs.addTab("Review", new ReviewView(reviewViewModel, reviewController));

                final JFrame application = new JFrame("WordDeck");
                application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                application.setContentPane(tabs);
                application.pack();
                application.setLocationRelativeTo(null);
                application.setVisible(true);
            }
        });
    }
}
