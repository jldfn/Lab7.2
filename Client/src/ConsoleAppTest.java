import java.io.*;

/**
 * Created by Денис on 23.04.2017.
 */

public class ConsoleAppTest {
    public void Test(){
        try{
            System.setIn(new FileInputStream(new File("/home/s225097/Desktop/SEB_Lab3/tests/test1/ImportCommand.txt")));
            System.setOut(new PrintStream(new FileOutputStream(new File("/home/s225097/Desktop/SEB_Lab3/tests/test1/ImportOut.txt"))));
            ConsoleApp app=new ConsoleApp();
        }catch(FileNotFoundException e){System.out.println("Файл не найдем");}
   }
}
