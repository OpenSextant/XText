package org.opensextant.xtext.test;

import java.io.File;
import java.io.IOException;

import org.opensextant.xtext.ConvertedDocument;
import org.opensextant.xtext.PathManager;

public class CacheTests {

    public static void main(String[] args) {
        try {
            PathManager pm = new PathManager();
            pm.enableSaveWithInput(true);
            File userInput = new File(args[0]);
            ConvertedDocument doc = pm.getCachedConversion(userInput);
            System.out.println("Successfully loaded " + userInput);
            System.out.println("FILE META " + doc.getJSONProperties());

        } catch (IOException err) {
            System.out.println(err.toString());
        }
    }

}
