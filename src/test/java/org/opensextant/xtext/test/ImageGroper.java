package org.opensextant.xtext.test;

import java.io.IOException;

import org.opensextant.xtext.XText;

public class ImageGroper {

    public static void usage() {
        System.out.println("ImageGroper  <input>  ");
    }

    public static void main(String[] args) {

        String input = args[1];
        boolean embed = true;
        // Setting LANG=en_US in your shell.
        //
        // System.setProperty("LANG", "en_US");
        XText xt = new XText();
        xt.enableSaving(true);
        xt.getPathManager().enableSaveWithInput(embed); // creates a ./text/ Folder locally in directory.
        xt.clearSettings();
        xt.convertFileType("jpg");
        xt.convertFileType("jpeg");

        try {
            //xt.getPathManager().setConversionCache(output);
            xt.setup();
            xt.extractText(input);
        } catch (IOException ioerr) {
            ioerr.printStackTrace();
            usage();
        }
    }
}
