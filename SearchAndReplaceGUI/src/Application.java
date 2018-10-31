
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.prefs.*;

import javax.swing.*;
import javax.swing.SwingWorker.StateValue;
import javax.swing.event.MenuListener;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

public class Application extends JFrame {

    /**  */
    private static final long serialVersionUID = -8668818312732181049L;

    private Preferences pref;

    private Action searchAllCancelAction; /** SEARCH ALL BUTTON */
    private Action browseAction; /** BROWSE BUTTON */
    private Action replaceAllCancelAction; /** REPLACE ALL BUTTON */


    private JTextField wordTextField;
    private JTextField replaceTextField;
    private JTextField directoryPathTextField;
    private JTextArea messagesTextArea;
    private JProgressBar searchProgressBar;
    private JCheckBox checkWholeWord;
    private JCheckBox checkWordCase;
    private JComboBox<String> fileType;

    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem exit;



    private SearchAllForWordWorker searchAllWorker; /** SEARCH BUTTON */
    private ReplaceAllForWordWorker replaceAllWorker; /** REPLACE BUTTON */

    private String[] suffixes = {"All files",".txt",".java",".py",".c",".html",".xml",".bat"};

    public Application() {
        initActions();
        initComponents();
        addMenuListener();
    }

    /**
     * Precondition: None
     * Postcondition: Cancels a function
     * @param
     */
    private void cancel() {
        searchAllWorker.cancel(true);
        replaceAllWorker.cancel(true);
    }

    /**
     * Precondition: None
     * Postcondition: Initializes functions for buttons
     * @param
     */
    private void initActions() {


       // fileType = new JComboBox<String>();

        /** BROWSE BUTTON */
        browseAction = new AbstractAction("Browse") {

            private static final long serialVersionUID = 4669650683189592364L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                final File dir = new File(directoryPathTextField.getText()).getAbsoluteFile();
                final JFileChooser fileChooser = new JFileChooser(dir /**.getParentFile()*/);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                final int option = fileChooser.showOpenDialog(Application.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    final File selected = fileChooser.getSelectedFile();
                    directoryPathTextField.setText(selected.getAbsolutePath());
                    pref.put("directoryPathTextField",directoryPathTextField.getText()); /** SAVE CHANGES IN THE GUI!!!!! */
                }
            }
        };


        /** SEARCH ALL BUTTON */
        searchAllCancelAction = new AbstractAction("Search") {

            private static final long serialVersionUID = 4669650683189592364L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (searchAllWorker == null) {
                    searchAll();
                } else {
                    cancel();
                }
            }
        };


        /** REPLACE ALL BUTTON */
        replaceAllCancelAction = new AbstractAction("Replace all") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogButton =JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog(null, "You sure you want to Replace ALL with the current settings?", "Warning", dialogButton);

                if(dialogResult == JOptionPane.YES_OPTION) {
                    if (replaceAllWorker == null) {
                        replaceAll();
                    } else {
                        cancel();
                    }
                }
            }
        };

    }




    /**
     * Precondition: None
     * Postcondition: Builds a menu bar, menu and menu item
     * @param
     */
    private void buildMenu() {
        menuBar = new JMenuBar();
        menu = new JMenu("App");
        exit = new JMenuItem("Save and Exit");
        menu.add(exit);
        menuBar.add(menu);
    }

    /**
     * Precondition: None
     * Postcondition: Set and save current value for future use
     * @param
     */
    private void autoSaveSettings() {

        checkWordCase.setSelected(checkWordCase.isSelected());
        pref.putBoolean("checkWordCase",checkWordCase.isSelected());
        checkWholeWord.setSelected(checkWholeWord.isSelected());
        pref.putBoolean("checkWholeWord",checkWholeWord.isSelected());
        directoryPathTextField.setText(directoryPathTextField.getText());
        pref.put("directoryPathTextField",directoryPathTextField.getText());
        wordTextField.setText(wordTextField.getText());
        pref.put("wordTextField",wordTextField.getText());
        replaceTextField.setText(replaceTextField.getText());
        pref.put("replaceTextField",replaceTextField.getText());

        fileType.setSelectedItem(suffixes[fileType.getSelectedIndex()]);
        pref.put("fileType",suffixes[fileType.getSelectedIndex()]);

    }

    /**
     * Precondition: None
     * Postcondition: Creates a function for the menu item(s)
     * @param
     */
    private void addMenuListener() {
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                autoSaveSettings();

                System.exit(0);
            }
        });
    }



    /**
     * Precondition: None
     * Postcondition: Builds the GUI using gridLayout
     * @param
     */
    private void initComponents() {
        setLayout(new GridBagLayout());

        pref = Preferences.userNodeForPackage(this.getClass());
        GridBagConstraints constraints = new GridBagConstraints();

        /** MENU BAR ------------------------------------------------------------------- */
        buildMenu();
        setJMenuBar(menuBar);


        /** WORD LABEL -----------------------------------------------------------------*/
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        add(new JLabel("Word: "), constraints);

        /** REPLACE LABEL -----------------------------------------------------------------*/
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        add(new JLabel("Replace with: "), constraints);

        /** FILE TYPE LABEL -----------------------------------------------------------------*/
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.insets = new Insets(2, 2, 2, 2);
        add(new JLabel("File type: "), constraints);

        /** WORD TEXT FIELD -----------------------------------------------------------------*/
        wordTextField = new JTextField();
        wordTextField.setText(pref.get("wordTextField","Hello"));
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 4;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(wordTextField, constraints);

        /** REPLACE TEXT FIELD -----------------------------------------------------------------*/
        replaceTextField = new JTextField();
        replaceTextField.setText(pref.get("replaceTextField","Test"));
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(replaceTextField, constraints);


        /** PATH LABEL -----------------------------------------------------------------*/
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.insets = new Insets(2, 2, 2, 2);
        add(new JLabel("Path: "), constraints);

        /** DIRECTORY TEXT FIELD -----------------------------------------------------------------*/
        directoryPathTextField = new JTextField();
        directoryPathTextField.setText(pref.get("directoryPathTextField","C:\\Users\\Mathe\\Documents\\SCHOOL\\Test Folder\\big.txt"));
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridwidth = 3;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(directoryPathTextField, constraints);


        /** BROWSE BUTTON -----------------------------------------------------------------*/
        constraints = new GridBagConstraints();
        constraints.gridx = 4;
        constraints.gridy = 3;
        constraints.insets = new Insets(2, 2, 2, 2);
        add(new JButton(browseAction), constraints);


        /** REPLACE ALL BUTTON -----------------------------------------------------------------*/
        constraints = new GridBagConstraints();
        constraints.gridx = 4;
        constraints.gridy = 1;
        constraints.gridwidth = 0;
        //constraints.weighty = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0;
        add(new JButton(replaceAllCancelAction), constraints);

        /** FILE TYPE COMBO BOX  -----------------------------------------------------------------*/
        fileType = new JComboBox<String>(suffixes);
        fileType.setSelectedItem(pref.get("fileType",suffixes[fileType.getSelectedIndex()]));
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0;
        add(fileType,constraints);






        /** CHECK WHOLE WORD CHECKBOX  -----------------------------------------------------------------*/
        checkWholeWord = new JCheckBox("Whole word", pref.getBoolean("checkWholeWord",false));
        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 2;

        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0;
        add(checkWholeWord,constraints);



        /** CHECK WORD CASE CHECKBOX  -----------------------------------------------------------------*/
        checkWordCase = new JCheckBox("Word case",pref.getBoolean("checkWordCase",false));
        constraints = new GridBagConstraints();
        constraints.gridx = 3;
        constraints.gridy = 2;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0;
        add(checkWordCase,constraints);




        /** MESSAGE TEXT AREA  -----------------------------------------------------------------*/
        messagesTextArea = new JTextArea();
        messagesTextArea.setEditable(false);
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 5;
        //constraints.gridheight = 2;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(messagesTextArea), constraints);

        /** SEARCH PROGRESS BAR -----------------------------------------------------------------*/
        searchProgressBar = new JProgressBar();
        searchProgressBar.setStringPainted(false);
        searchProgressBar.setVisible(false);
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 4;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(searchProgressBar, constraints);


        /** SEARCH CANCEL BUTTON -----------------------------------------------------------------*/
        constraints = new GridBagConstraints();
        constraints.gridx = 4;
        constraints.gridy = 5;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0;
        //constraints.weighty = 0;
        add(new JButton(searchAllCancelAction), constraints);
    }






    /**
     * Precondition: None
     * Postcondition: Search for given value
     * @param
     */
    private void searchAll() {
        final String word = wordTextField.getText();
        final File directory = new File(directoryPathTextField.getText());


        /** AUTO COMPLETE ------------------------------------------------------------------------------*/
        autoSaveSettings();

        if (directory.exists()) {

            messagesTextArea
                    .setText("Searching for word '" + word + "' in the files under: " + directory.getAbsolutePath() + "\n");
            searchAllWorker = new SearchAllForWordWorker(word, directory, messagesTextArea,checkWholeWord.isSelected()
                    ,checkWordCase.isSelected(),fileType.getSelectedIndex()); /** SEARCH FUNCTION */



            searchAllWorker.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(final PropertyChangeEvent event) {
                    switch (event.getPropertyName()) {
                        case "progress":
                            searchProgressBar.setIndeterminate(false);
                            searchProgressBar.setValue((Integer) event.getNewValue());
                            break;
                        case "state":
                            switch ((StateValue) event.getNewValue()) {
                                case DONE:
                                    searchProgressBar.setVisible(false);
                                    searchAllCancelAction.putValue(Action.NAME, "Search");
                                    searchAllWorker = null;
                                    break;
                                case STARTED:
                                case PENDING:
                                    searchAllCancelAction.putValue(Action.NAME, "Cancel");
                                    searchProgressBar.setVisible(true);
                                    searchProgressBar.setIndeterminate(true);
                                    break;
                            }
                            break;
                    }
                }
            });

        } else {
            try {
                JOptionPane.showMessageDialog(null, "Directory does not exist");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        searchAllWorker.execute();
    }


    /**
     * Precondition: None
     * Postcondition: Replaces given value for new value
     * @param
     */
    private void replaceAll() {
        final String word = wordTextField.getText();
        final File directory = new File(directoryPathTextField.getText());


      autoSaveSettings();

        if (directory.exists()) {

            messagesTextArea
                    .setText("Searching for word '" + word + "' in text files under: " + directory.getAbsolutePath() + "\n");
            replaceAllWorker = new ReplaceAllForWordWorker(word, directory, messagesTextArea,checkWholeWord.isSelected(),checkWordCase.isSelected(),replaceTextField.getText(),fileType.getSelectedIndex()); /** REPLACE FUNCTION */
            replaceAllWorker.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(final PropertyChangeEvent event) {
                    switch (event.getPropertyName()) {
                        case "progress":
                            searchProgressBar.setIndeterminate(false);
                            searchProgressBar.setValue((Integer) event.getNewValue());
                            break;
                        case "state":
                            switch ((StateValue) event.getNewValue()) {
                                case DONE:
                                    searchProgressBar.setVisible(false);
                                    replaceAllCancelAction.putValue(Action.NAME, "Replace All");
                                    replaceAllWorker = null;
                                    break;
                                case STARTED:
                                case PENDING:
                                    replaceAllCancelAction.putValue(Action.NAME, "Cancel");
                                    searchProgressBar.setVisible(true);
                                    searchProgressBar.setIndeterminate(true);
                                    break;
                            }
                            break;
                    }
                }
            });

        } else {
            try {
                JOptionPane.showMessageDialog(null, "Directory does not exist");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        replaceAllWorker.execute();
    }
}
