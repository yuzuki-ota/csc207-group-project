package view;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class LookupView extends JPanel {
    public LookupView() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        final JTextField wordInput = new JTextField(20);
        final JButton lookupButton = new JButton("Look up");
        final JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Word"));
        searchPanel.add(wordInput);
        searchPanel.add(lookupButton);

        final JList<String> definitions = new JList<>();
        definitions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        definitions.setVisibleRowCount(6);

        final JButton saveButton = new JButton("Save selected meaning");
        final JPanel savePanel = new JPanel();
        savePanel.add(saveButton);

        // Enable these when the controller and presenter are connected.
        lookupButton.setEnabled(false);
        saveButton.setEnabled(false);

        add(searchPanel);
        add(new JLabel("Definitions"));
        add(new JScrollPane(definitions));
        add(savePanel);
    }
}
