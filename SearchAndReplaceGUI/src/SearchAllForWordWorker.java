/**
 * TODO: MATCH WHOLE WORD SEARCHES (CREATED A CHECK BOX FOR THIS)
 * TODO: MATCH WORD CASE (CREATE A CHECK BOX FOR THIS)
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class SearchAllForWordWorker extends SwingWorker<Integer, String> {

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

    private int suffixesIndex;

    final static Logger logger = Logger.getLogger(SearchAllForWordWorker.class);

    private boolean searchCheckerWholeWords;
    private boolean searchCheckerWordCase;



    /**
     * Precondition:
     * Postcondition: A constructor
     * @param
     */
    public SearchAllForWordWorker(final String word, final File directory, final JTextArea messagesTextArea,boolean checkWholeWord
            , boolean checkWordCase, int suffixesIndex) {
        this.word = word;
        this.directory = directory;
        this.messagesTextArea = messagesTextArea;
        this.checkWholeWord = checkWholeWord;
        this.checkWordCase = checkWordCase;
        this.suffixesIndex = suffixesIndex;
        logger.info("Whole Word: " + checkWholeWord);
        logger.info("Word Case: " + checkWordCase);

    }

    @Override
    protected Integer doInBackground() throws Exception {

        int matches = 0;
        int counter = 0;
        String line = "";


        String[] suffixesArr = {"All files",".txt",".java",".py",".c",".html",".xml",".bat"};
        SuffixFileFilter filter;

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
            SearchAllForWordWorker.failIfInterrupted();
            publish("Found " + textFiles.size() + " text files under the directory: " + directory);
            if (checkWordCase == false && checkWholeWord == false) {
                searchAllForWordCase(textFiles, line);
            } else if (checkWholeWord == true && checkWordCase == false) {
                searchAllForWholeWord(textFiles, line);
            } else if (checkWholeWord == false && checkWordCase == true) {
                searchAllForWordCase(textFiles, line);
            } else {
                searchAllForWordCase(textFiles, line);
            }
        } else {
            if (checkWordCase == false && checkWholeWord == false) {
                searchForWordCase(directory, line);
            } else if (checkWholeWord == true && checkWordCase == false) {
                searchForWholeWord(directory, line);
            } else if (checkWholeWord == false && checkWordCase == true) {
                searchForWordCase(directory, line);
            } else {
                searchForWordCase(directory, line);
            }
        }


        return matches;
    }




    /**
     * Precondition: A List of File type with proper directory and String type line to split the file in to lines
     * Postcondition: Searches ALL the word cases insensitive only
     * @param textFiles, line
     */
    public void searchAllForWordCase(final List<File> textFiles, String line) throws Exception {

        int counter  = 1;

        for (int i = 0, size = textFiles.size(); i < size; i++) {
            SearchAllForWordWorker.failIfInterrupted();
            final File file = textFiles.get(i); /** THIS WILL BE USED FOR SEARCHING IN TO THE FIILLEEEEE */
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            //String newString = "";



            try {

                publish("The following file: \"" + file + "\"" + " contains:");
                while ((line = br.readLine()) != null) {


                    if (line.toLowerCase().contains(word.toLowerCase())) { /** WORD CASE INSENSITIVE  */
                        publish("Line " + counter + ": \"" + line + "\"");
                        logger.info("File: " + file);
                        logger.info("Line " + counter+ ": " +line);
                    }
                    counter++;
                }
                counter = 1;


            } catch (Exception e) {
                e.printStackTrace();
            }

            setProgress((i + 1) * 100 / size);
        }
    }

    /**
     * Precondition: A List of File type with proper directory and String type line to split the file in to lines
     * Postcondition: Searches ALL the whole words only
     * @param textFiles, line
     */
    public void searchAllForWholeWord(final List<File> textFiles,String line) throws Exception {

        int counter = 1;

        for (int i = 0, size = textFiles.size(); i < size; i++) {
            SearchAllForWordWorker.failIfInterrupted();
            final File file = textFiles.get(i); /** THIS WILL BE USED FOR SEARCHING IN TO THE FIILLEEEEE */
            fr = new FileReader(file);
            br = new BufferedReader(fr);



            try {

                publish("The following file: \"" + file + "\"" + " contains:");
                while ((line = br.readLine()) != null) {

                    if (line.matches(".*\\b" + word + "\\b.*")
                            || line.matches("\\B" + word + "|" + word + "\\B}")) { /** WORD CASE INSENSITIVE  */
                        publish("Line " + counter + ": \"" + line + "\"");
                        logger.info("File: " + file);
                        logger.info("Line " + counter+ ": " +line);

                    }
                    counter++;
                }



                counter = 1;


            } catch (Exception e) {
                e.printStackTrace();
            }

            setProgress((i + 1) * 100 / size);
        }

    }


    /**
     * Precondition: A File type with proper directory and String type line to split the file in to lines
     * Postcondition: Search ALL the word case only
     * @param textFile, line
     */
    public void searchForWordCase(final File textFile, String line) throws Exception {
        SearchAllForWordWorker.failIfInterrupted();
        int counter = 1;
        final File file = textFile; /** THIS WILL BE USED FOR SEARCHING IN TO THE FIILLEEEEE */
        fr = new FileReader(file);
        br = new BufferedReader(fr);
        String newString = "";


        try {



            logger.info("File: " + file);

            publish("The following file: \"" + file + "\"" + " contains:");
            while ((line = br.readLine()) != null) {


                if (line.toLowerCase().contains(word.toLowerCase())) { /** WORD CASE INSENSITIVE  */
                    publish("Line " + counter + ": \"" + line + "\"");
                    logger.info("Line " + counter+ ": " +line);
                    searchCheckerWordCase = true;
                }
                counter++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    /**
     * Precondition: A File type with proper directory and String type line to split the file in to lines
     * Postcondition: Replaces ALL the word cases insensitive only
     * @param textFile, line
     */
    public void searchForWholeWord(final File textFile,String line) throws Exception {
        SearchAllForWordWorker.failIfInterrupted();
        int counter = 1;
        final File file = textFile; /** THIS WILL BE USED FOR SEARCHING IN TO THE FIILLEEEEE */
        fr = new FileReader(file);
        br = new BufferedReader(fr);


        try {

            logger.info("File: " + file);
            publish("The following file: \"" + file + "\"" + " contains:");
            while ((line = br.readLine()) != null) {


                if (line.matches(".*\\b" + word + "\\b.*")
                        || line.matches("\\B" + word + "|" + word + "\\B}")) { /** WORD CASE INSENSITIVE  */
                    publish("Line " + counter + ": \"" + line + "\"");
                    logger.info("Line " + counter+ ": " +line);
                    searchCheckerWholeWords = true;

                }


                searchCheckerWholeWords = true;
                counter++;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /** FOR TEST PURPOSE ONLY */
    public String checkDir() {
        return directory.toString();
    }

    public boolean checkSearchWholeWord() {
        return searchCheckerWholeWords;
    }

    public boolean checkSearchWordCase() {
        return searchCheckerWordCase;
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
