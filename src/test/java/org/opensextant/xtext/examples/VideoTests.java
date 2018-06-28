package org.opensextant.xtext.examples;

import java.io.IOException;

import org.opensextant.ConfigException;
import org.opensextant.xtext.ConversionListener;
import org.opensextant.xtext.ConvertedDocument;
import org.opensextant.xtext.XText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A trivial test program that allows you to see if Tika will get text by default from Video file formats you have.
 */
public class VideoTests implements ConversionListener {
    Logger log = LoggerFactory.getLogger(getClass());

    public void run(String input) throws IOException, ConfigException {

        XText xt = new XText();
        xt.getPathManager().enableSaveWithInput(false);
        xt.getPathManager().setConversionCache("video-output");

        xt.enableSaving(true);
        xt.convertFileType("mp4");
        xt.convertFileType("mpeg");
        xt.convertFileType("mpg");
        xt.convertFileType("avi");
        xt.convertFileType("wmv");

        xt.setMaxFileSize(0x8000000);
        xt.setup();
        xt.setConversionListener(this);
        xt.extractText(input);

    }

    public void handleConversion(ConvertedDocument d, String fpath) {
        log.info("FILE=" + d.filename + " Converted?=" + d.is_converted + " ID={} PATH={}", d.id,
                fpath);

        /* Set your document ID to something meaningful.  MD5 or SHA1 the content, filename, metadata, etc.*/
        d.id = "DOCID"+fpath;
        log.info("\t\tTry resetting Doc ID to default ID = " + d.id);
    }

    public static void main(String[] args) {

        try {
            new VideoTests().run(args[0]);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

}
