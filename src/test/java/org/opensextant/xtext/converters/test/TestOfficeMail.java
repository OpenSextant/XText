package org.opensextant.xtext.converters.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.opensextant.xtext.ConvertedDocument;
import org.opensextant.xtext.converters.OfficeConverter;

public class TestOfficeMail {

    @ClassRule
    public static final TemporaryFolder TEMP_DIR = new TemporaryFolder();
    private static File TEST_FILE = null;

    @BeforeClass
    public static void setupTemporaryFolder() throws IOException {
        TEST_FILE = TEMP_DIR.newFile("mimeEmailWithAttachmentsTest.eml");
        FileUtils.copyInputStreamToFile(
                MessageConverterTest.class.getResourceAsStream("mimeEmailWithAttachmentsTest.eml"), TEST_FILE);
    }

    // @Test
    public void testMailMessageParser() {
        OfficeConverter converter = new OfficeConverter();

        try {
            ConvertedDocument doc = converter.convert(TEST_FILE);
        } catch (Exception err) {
            err.printStackTrace();
            fail("EML conversion failed");
        }
    }
}
