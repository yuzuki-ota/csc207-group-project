package view;

import java.awt.BorderLayout;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import interface_adapter.lookup.LookupController;
import interface_adapter.lookup.LookupViewModel;

public class LookupView extends JPanel {
    private static final int VISIBLE_DEFINITIONS = 6;

    private final LookupViewModel viewModel;
    private final LookupController controller;
    private final JTextField wordInput = new JTextField(20);
    private final JButton lookupButton = new JButton("Look up");
    private final JList<String> definitions = new JList<>();
    private final JButton saveButton = new JButton("Save selected meaning");
    private final JLabel status = new JLabel(" ");
    private List<String> displayedResults;

    public LookupView(LookupViewModel viewModel, LookupController controller) {
        this.viewModel = viewModel;
        this.controller = controller;
        setLayout(new BorderLayout());
        addSearchPanels();
        addListeners();
        updateView();
    }

    private void addSearchPanels() {
        final JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Word"));
        searchPanel.add(wordInput);
        searchPanel.add(lookupButton);

        definitions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        definitions.setVisibleRowCount(VISIBLE_DEFINITIONS);
        final JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.add(new JLabel("Definitions"), BorderLayout.NORTH);
        resultsPanel.add(new JScrollPane(definitions), BorderLayout.CENTER);

        final JPanel savePanel = new JPanel(new BorderLayout());
        savePanel.add(saveButton, BorderLayout.NORTH);
        savePanel.add(status, BorderLayout.SOUTH);
        add(searchPanel, BorderLayout.NORTH);
        add(resultsPanel, BorderLayout.CENTER);
        add(savePanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        lookupButton.addActionListener(event -> startLookup());
        wordInput.addActionListener(event -> startLookup());
        definitions.addListSelectionListener(event -> updateSaveButton());
        saveButton.addActionListener(event -> controller.saveMeaning(definitions.getSelectedIndex()));
        viewModel.addPropertyChangeListener(event -> {
            if (SwingUtilities.isEventDispatchThread()) {
                updateView();
            }
            else {
                SwingUtilities.invokeLater(this::updateView);
            }
        });
    }

    private void startLookup() {
        final String word = wordInput.getText();
        wordInput.setEnabled(false);
        lookupButton.setEnabled(false);
        definitions.setEnabled(false);
        saveButton.setEnabled(false);
        status.setText("Looking up word...");
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                controller.lookUp(word);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                }
                catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    viewModel.showResults(Collections.emptyList(), "Lookup interrupted. Please try again.");
                }
                catch (ExecutionException exception) {
                    viewModel.showResults(Collections.emptyList(), "Could not look up the word. Please try again.");
                }
                wordInput.setEnabled(true);
                lookupButton.setEnabled(true);
                definitions.setEnabled(true);
                updateSaveButton();
            }
        }.execute();
    }

    private void updateView() {
        final List<String> results = viewModel.getState();
        // A save only updates the status. Leave the selected meaning in place for a retry.
        if (results != displayedResults) {
            displayedResults = results;
            definitions.setListData(results.toArray(new String[0]));
        }
        status.setText(viewModel.getMessage());
        updateSaveButton();
    }

    private void updateSaveButton() {
        saveButton.setEnabled(lookupButton.isEnabled() && definitions.getSelectedIndex() >= 0);
    }
}
