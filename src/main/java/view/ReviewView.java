package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import interface_adapter.review.ReviewController;
import interface_adapter.review.ReviewState;
import interface_adapter.review.ReviewViewModel;

public class ReviewView extends JPanel implements PropertyChangeListener {
    private final ReviewViewModel reviewViewModel;

    private final JButton startButton = new JButton("Start review");
    private final JButton endButton = new JButton("End review");
    private final JLabel wordLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel partOfSpeechLabel = new JLabel("", SwingConstants.CENTER);
    private final JTextArea definitionArea = new JTextArea(4, 30);
    private final JButton showButton = new JButton("Show definition");
    private final JButton againButton = new JButton("Again");
    private final JButton gotItButton = new JButton("Got it");
    private final JLabel messageLabel = new JLabel("", SwingConstants.CENTER);

    public ReviewView(ReviewViewModel reviewViewModel, ReviewController reviewController) {
        this.reviewViewModel = reviewViewModel;
        reviewViewModel.addPropertyChangeListener(this);
        setLayout(new BorderLayout());

        final JPanel sessionPanel = new JPanel();
        sessionPanel.add(startButton);
        sessionPanel.add(endButton);

        wordLabel.setFont(wordLabel.getFont().deriveFont(Font.BOLD, 20f));
        // Grid rows all get the same height, so the layout doesn't jump when a label is empty.
        final JPanel cardPanel = new JPanel(new GridLayout(0, 1));
        cardPanel.add(sessionPanel);
        cardPanel.add(wordLabel);
        cardPanel.add(partOfSpeechLabel);

        definitionArea.setEditable(false);
        definitionArea.setFocusable(false);
        definitionArea.setMargin(new Insets(5, 5, 5, 5));
        definitionArea.setLineWrap(true);
        definitionArea.setWrapStyleWord(true);

        final JPanel ratingPanel = new JPanel();
        ratingPanel.add(showButton);
        ratingPanel.add(againButton);
        ratingPanel.add(gotItButton);
        final JPanel bottomPanel = new JPanel(new GridLayout(0, 1));
        bottomPanel.add(ratingPanel);
        bottomPanel.add(messageLabel);

        startButton.addActionListener(event -> reviewController.startReview());
        endButton.addActionListener(event -> reviewController.endReview());
        showButton.addActionListener(event -> {
            reviewViewModel.getState().setDefinitionShown(true);
            updateView();
        });
        againButton.addActionListener(event -> reviewController.rateCard(false));
        gotItButton.addActionListener(event -> reviewController.rateCard(true));

        add(cardPanel, BorderLayout.NORTH);
        add(new JScrollPane(definitionArea), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        updateView();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        updateView();
    }

    private void updateView() {
        final ReviewState state = reviewViewModel.getState();
        wordLabel.setText(state.getWord());
        partOfSpeechLabel.setText(state.getPartOfSpeech());
        if (state.isDefinitionShown()) {
            definitionArea.setText(state.getDefinition());
        }
        else {
            definitionArea.setText("");
        }
        messageLabel.setText(state.getMessage());

        final boolean canRate = state.isReviewing() && state.isDefinitionShown();
        startButton.setEnabled(!state.isReviewing());
        endButton.setEnabled(state.isReviewing());
        showButton.setEnabled(state.isReviewing() && !state.isDefinitionShown());
        againButton.setEnabled(canRate);
        gotItButton.setEnabled(canRate);
    }
}
