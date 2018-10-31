import org.junit.Test;

import javax.swing.*;
import java.io.File;

import static org.junit.Assert.*;

public class ReplaceAllForWordWorkerTest {


    File fileName = new File("C:\\Users\\Mathe\\Documents\\SCHOOL\\Test Folder\\big.txt");
    //File fileName = new File("C:\\Users\\Mathe\\Documents\\SCHOOL\\Test Folder\\MathewTester.txt");

    @Test
    public void Tester() throws Exception {

        JTextArea jA = new JTextArea("");
        ReplaceAllForWordWorker r1 = new ReplaceAllForWordWorker("Hello",fileName,jA,false,false,"Test",0);

        r1.replaceForWordCase(fileName,"");
        r1.replaceForWholeWord(fileName,"");

        String dir = "" + r1.checkDir();
        String expectedDir = "C:\\Users\\Mathe\\Documents\\SCHOOL\\Test Folder\\big.txt";
        //String expectedDir = "C:\\Users\\Mathe\\Documents\\SCHOOL\\Test Folder\\MathewTester.txt";


        boolean test1 = r1.checkReplaceWordCase();
        boolean test2 = r1.checkReplaceWholeWord();
        boolean expected1 = true;
        boolean expected2 = true;

        assertEquals(expectedDir,dir);
        assertEquals(expected1,test1);
        assertEquals(expected2,test2);


    }

}