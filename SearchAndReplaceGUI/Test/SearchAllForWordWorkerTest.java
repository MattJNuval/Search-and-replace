import org.junit.Test;

import javax.swing.*;
import java.io.File;

import static org.junit.Assert.*;

public class SearchAllForWordWorkerTest {


    File fileName = new File("C:\\Users\\Mathe\\Documents\\SCHOOL\\Test Folder\\big.txt");


    @Test
    public void Tester() throws Exception {

        JTextArea jA = new JTextArea("");
        SearchAllForWordWorker s1 = new SearchAllForWordWorker("ebook",fileName,jA,false,false,0);

        s1.searchForWordCase(fileName,"");
        s1.searchForWholeWord(fileName,"");

        String dir = "" +  s1.checkDir();
        String expectedDir = "C:\\Users\\Mathe\\Documents\\SCHOOL\\Test Folder\\big.txt";
        boolean test1 = s1.checkSearchWholeWord();
        boolean test2 = s1.checkSearchWordCase();
        boolean expected1 = true; /** DUE TO SOME VALUES THAT ARE EXCLUDED*/
        boolean expected2 = true;

        assertEquals(expectedDir,dir);
        assertEquals(expected1,test1);
        assertEquals(expected2,test2);




    }

}