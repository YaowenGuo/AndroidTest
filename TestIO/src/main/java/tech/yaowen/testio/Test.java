package tech.yaowen.testio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


import okio.Okio;
import okio.Source;

public class Test {

    void test() {

/*        byte[] bytes = {'i', 'o'};

        try ( OutputStream outputStream = new FileOutputStream("TestIO.txt") ){
            outputStream.write(bytes);
        } catch (IOException e) {
        }*/

        File file = new File("TextIO.txt");
        try {
            Source source = Okio.source(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] argus) {
        int[] chs = new int[2];
        for (int ch : chs) {
            System.out.println(ch);
        }
    }
}
