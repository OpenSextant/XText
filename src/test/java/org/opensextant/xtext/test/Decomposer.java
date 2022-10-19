package org.opensextant.xtext.test;

import java.io.File;
import java.io.IOException;

import org.opensextant.xtext.ConvertedDocument;
import org.opensextant.xtext.converters.EmbeddedContentConverter;

public class Decomposer {

    public static void usage() {
        System.out.println("Decomposer  <input>  ");
    }

    public static void main(String[] args) {

        String input = args[1];

        EmbeddedContentConverter conv = new EmbeddedContentConverter(0x200000);
        ConvertedDocument d;
        try {
            d = conv.convert(new File(input));
            System.out.println("Found Doc:" + d.getFilepath());
        } catch (IOException e) {
            e.printStackTrace();
            usage();
        }
    }
}
