/*
 *
 *      Copyright 2012-2013 The MITRE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.opensextant.xtext.converters;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.opensextant.util.FileUtility;
import org.opensextant.util.TextUtils;
import org.opensextant.xtext.ConvertedDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Default conversion is almost a pass through from Tika's auto parser and BodyContentHandler.
 * Encoding, author, create date and title are saved to ConvertedDoc.  The text of the document
 * is stripped of extra blank lines.
 *
 * @author Marc C. Ubaldino, MITRE, ubaldino at mitre dot org
 */
public class DefaultConverter extends ConverterAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /* 1 MB of text from a given document */
    public final static int MAX_TEXT_SIZE = 0x100000;
    private final Detector detector = new DefaultDetector();
    private final Parser parser = new AutoDetectParser(detector);
    private final ParseContext ctx = new ParseContext();

    private int maxBuffer = MAX_TEXT_SIZE;

    public DefaultConverter() {
        ctx.set(Parser.class, parser);
    }

    public DefaultConverter(int sz) {
        this();
        maxBuffer = sz;
    }

    /**
     * Common implementation -- take an input stream and return a ConvertedDoc;
     *
     * @param input stream for raw file
     * @param doc raw file
     * @return converted doc
     * @throws IOException if underlying Tika parser/writer had an IO problem, an parser
     *             problem, or MAX_TEXT_SIZE is reached.
     */
    @Override
    protected ConvertedDocument conversionImplementation(InputStream input, java.io.File doc)
            throws IOException {
        Metadata metadata = new Metadata();
        BodyContentHandler handler = new BodyContentHandler(maxBuffer);

        try {
            parser.parse(input, handler, metadata, ctx);
        } catch (NoClassDefFoundError classErr) {
            throw new IOException("Unable to parse content due to Tika misconfiguration", classErr);
        } catch (TikaException e1) {
            throw new IOException("Tika: Unable to parse content", e1);
        } catch (SAXException e2) {
            throw new IOException("SAX: Unable to parse content", e2);
        }
        ConvertedDocument textdoc = new ConvertedDocument(doc);

        textdoc.addTitle(metadata.get(TikaCoreProperties.TITLE));
        textdoc.setEncoding(metadata.get(Metadata.CONTENT_ENCODING));
        textdoc.addCreateDate(metadata.getDate(TikaCoreProperties.CREATED));
        textdoc.addAuthor(metadata.get(TikaCoreProperties.CREATOR));

        String t = handler.toString();
        if (t != null) {
            if (textdoc.filename != null && FileUtility.isSpreadsheet(textdoc.filename)) {
                // REMOVE TRAILING BLANK LINES/ROWS
                textdoc.setText(t.trim());
            } else {
                // REMOVE REPEATING BLANK LINES
                textdoc.setText(TextUtils.reduce_line_breaks(t));
            }
        }
        textdoc.is_converted = true;
        return textdoc;
    }
}
