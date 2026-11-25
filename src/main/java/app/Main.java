package app;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import data_access.FileDeckDataAccessObject;
import interface_adapter.review.ReviewController;
import interface_adapter.review.ReviewPresenter;
import interface_adapter.review.ReviewViewModel;
import use_case.review.ReviewDataAccessInterface;
import use_case.review.ReviewInputBoundary;
import use_case.review.ReviewInteractor;
import use_case.review.ReviewOutputBoundary;
import view.LookupView;
import view.ReviewView;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final FileDeckDataAccessObject deckStorage =
                        new FileDeckDataAccessObject("deck.json");
                final JTabbedPane tabs = new JTabbedPane();
                tabs.addTab("Look up", new LookupView());
                tabs.addTab("Review", createReviewView(deckStorage));

                final JFrame application = new JFrame("WordDeck");
                application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                application.setContentPane(tabs);
                application.pack();
                application.setLocationRelativeTo(null);
                application.setVisible(true);
            }
        });
    }

    private static ReviewView createReviewView(ReviewDataAccessInterface deckStorage) {
        final ReviewViewModel viewModel = new ReviewViewModel();
        final ReviewOutputBoundary presenter = new ReviewPresenter(viewModel);
        final ReviewInputBoundary interactor = new ReviewInteractor(deckStorage, presenter);
        return new ReviewView(viewModel, new ReviewController(interactor));
    }
}
