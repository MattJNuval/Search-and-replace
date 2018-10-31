/**
 * TODO: MATCH WHOLE WORD SEARCHES (CREATED A CHECK BOX FOR THIS)
 * TODO: MATCH WORD CASE (CREATE A CHECK BOX FOR THIS)
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import org.apache.log4j.Logger;


public class ReplaceAllForWordWorker extends SwingWorker<Integer, String> {

    private static void failIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Interrupted while searching files");
        }
    }


    private BufferedReader br;
    private FileReader fr;
    private FileWriter fw;



    private final String word;


    private final File directory;


    private final JTextArea messagesTextArea;

    private boolean checkWholeWord;
    private boolean checkWordCase;

    private String newWord;

    private int suffixesIndex;

    private boolean replaceCheckerWholeWords = false;
    private boolean replaceCheckerWordCase = false;

    final static Logger logger = Logger.getLogger(SearchAllForWordWorker.class);


    /**
     * Precondition: Stores the given value
     * Postcondition: Initializes variables
     * @param
     */
    public ReplaceAllForWordWorker(final String word, final File directory, final JTextArea messagesTextArea, boolean checkWholeWord
            , boolean checkWordCase, String newWord,int suffixesIndex) {
        this.word = word;
        this.directory = directory;
        this.messagesTextArea = messagesTextArea;
        this.checkWholeWord = checkWholeWord;
        this.checkWordCase = checkWordCase;
        this.newWord = newWord;
        this.suffixesIndex = suffixesIndex;
    }


    /**
     * Precondition: None
     * Postcondition: Works on a different thread on a GUI
     * @param
     * @override
     */
    @Override
    protected Integer doInBackground() throws Exception {

        int matches = 0;
        int counter = 0;
        String line = "";

        SuffixFileFilter filter;

        String[] suffixesArr = {"All files",".txt",".java",".py",".c",".html",".xml",".bat"};

        if(suffixesIndex != 0) {
            filter = new SuffixFileFilter(suffixesArr[suffixesIndex]);
            logger.info("File type: " + suffixesArr[suffixesIndex]);
        } else {
            filter = new SuffixFileFilter(suffixesArr);
            logger.info("File type: " + suffixesArr[suffixesIndex]);
        }


        if( !directory.toString().contains(".txt") && !directory.toString().contains(".java")
                && !directory.toString().contains(".py") && !directory.toString().contains(".c")
                && !directory.toString().contains(".xml") && !directory.toString().contains(".bat")) {

            publish("Listing all text files under the directory: " + directory);
            final List<File> textFiles = new ArrayList<>(
                    FileUtils.listFiles(directory, filter, TrueFileFilter.TRUE));  /** LOOKING FOR TXT FILES */
            ReplaceAllForWordWorker.failIfInterrupted();
            publish("Found " + textFiles.size() + " text files under the directory: " + directory);

            if (checkWordCase == false && checkWholeWord == false) {
                replaceAllForWordCase(textFiles, line);
            } else if (checkWholeWord == true && checkWordCase == false) {
                replaceAllForWholeWord(textFiles, line);
            } else if (checkWholeWord == false && checkWordCase == true) {
                replaceAllForWordCase(textFiles, line);
            } else {
                replaceAllForWordCase(textFiles, line);
            }
        } else {
            if (checkWordCase == false && checkWholeWord == false) {
                replaceForWordCase(directory, line);
            } else if (checkWholeWord == true && checkWordCase == false) {
                replaceForWholeWord(directory, line);
            } else if (checkWholeWord == false && checkWordCase == true) {
                replaceForWordCase(directory, line);
            } else {
                replaceForWordCase(directory, line);
            }
        }


        return matches;
    }


    /**
     * Precondition: A List of File type with proper directory and String type line to split the file in to lines
     * Postcondition: Replaces ALL the word cases insensitive only
     * @param
     */
    private void replaceAllForWordCase(final List<File> textFiles, String line) throws Exception {



        for (int i = 0, size = textFiles.size(); i < size; i++) /** FOR EACH FILE */{
            ReplaceAllForWordWorker.failIfInterrupted();
            final File file = textFiles.get(i); /** THIS WILL BE USED FOR SEARCHING IN TO THE FIILLEEEEE */

            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String newString = "";
            String oldText = "";

            try {

                publish("The following file(s) \"" +file+ "\" is successfully replaced with: " +"\"" + newWord + "\"");

                /** REPLACE FOR WORD CASES */
                while((line = br.readLine()) != null) {
                    oldText += line + "\r\n";
                }
                br.close();


                String newText = oldText.toLowerCase().replaceAll(word.toLowerCase(),newWord);

                FileWriter writer = new FileWriter(file);
                writer.write(newText);
                writer.close();


            } catch (Exception e) {
                e.printStackTrace();
            }

            setProgress((i + 1) * 100 / size);
        }
    }

    /**
     * Precondition: A List of File type with proper directory and String type line to split the file in to lines
     * Postcondition: Replaces ALL whole words only
     * @param
     */
    private void replaceAllForWholeWord(final List<File> textFiles, String line) throws Exception {

        for (int i = 0, size = textFiles.size(); i < size; i++) /** FOR EACH FILE */ {
            ReplaceAllForWordWorker.failIfInterrupted();
            final File file = textFiles.get(i); /** THIS WILL BE USED FOR SEARCHING IN TO THE FIILLEEEEE */
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String oldText = "";

            try {

                publish("The following file(s) \"" +file+ "\" is successfully replaced: \"" +word+ "\" with: " +"\"" + newWord + "\"");

                /** REPLACE FOR WHOLE WORDS */
                while((line = br.readLine()) != null) {
                    oldText += line + "\r\n";
                }
                br.close();

                String newText = "";



                newText = oldText.replaceAll(word, newWord);

                FileWriter writer = new FileWriter(file);
                writer.write(newText);
                writer.close();

            } catch (Exception e) {

                e.printStackTrace();
            }

            setProgress((i + 1) * 100 / size);
        }

    }

    /**
     * Precondition: A File type with proper directory and String type line to split the file in to lines
     * Postcondition: Replaces one word case only
     * @param textFile,line
     */
    public void replaceForWordCase(final File textFile, String line) throws Exception {
        ReplaceAllForWordWorker.failIfInterrupted();

        final File file = textFile; /** THIS WILL BE USED FOR SEARCHING IN TO THE FIILLEEEEE */
        fr = new FileReader(file);
        br = new BufferedReader(fr);

        String oldText = "";

        try {

            publish("The following file(s) \"" +file+ "\" is successfully replaced: \"" +word+ "\" with: " +"\"" + newWord + "\"");

            /** REPLACE FOR WORD CASES */
            while((line = br.readLine()) != null) {
                oldText += line + "\r\n";
            }
            br.close();


            String newText = oldText.toLowerCase().replaceAll(word.toLowerCase(),newWord);

            FileWriter writer = new FileWriter(file);
            writer.write(newText);
            writer.close();

            replaceCheckerWordCase = true;

        } catch (Exception e) {
            e.printStackTrace();
        }



    }



    /**
     * Precondition: A File type with proper directory and String type line to split the file in to lines
     * Postcondition: Replaces one whole word only
     * @param textFile,line
     */
    public void replaceForWholeWord(final File textFile, String line) throws Exception {
        ReplaceAllForWordWorker.failIfInterrupted();
        final File file = textFile; /** THIS WILL BE USED FOR SEARCHING IN TO THE FIILLEEEEE */
        fr = new FileReader(file);
        br = new BufferedReader(fr);
        String newString = "";

        String oldText = "";

        try {

            publish("The following file(s) \"" +file+ "\" is successfully replaced: \"" +word+ "\" with: " +"\"" + newWord + "\"");

            /** REPLACE FOR WHOLE WORDS */
            while((line = br.readLine()) != null) {
                oldText += line + "\r\n";
            }
            br.close();

            String newText = "";



            newText = oldText.replaceAll(word, newWord);

            FileWriter writer = new FileWriter(file);
            writer.write(newText);
            writer.close();
            replaceCheckerWholeWords = true;

        } catch (Exception e) {

            e.printStackTrace();
        }


    }

    public String checkDir() {
        return directory.toString();
    }


    public boolean checkReplaceWholeWord() {
        return replaceCheckerWholeWords;
    }

    public boolean checkReplaceWordCase() {
        return replaceCheckerWordCase;
    }

    @Override
    protected void process(final List<String> chunks) {
        // Updates the messages text area
        for (final String string : chunks) {
            messagesTextArea.append(string);
            messagesTextArea.append("\n");
        }
    }
}
