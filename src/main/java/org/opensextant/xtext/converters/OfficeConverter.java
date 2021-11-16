package org.opensextant.xtext.converters;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.sax.BodyContentHandler;
import org.opensextant.xtext.ConvertedDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OfficeConverter extends ConverterAdapter {

    
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private OfficeParser parser = new OfficeParser();

    public OfficeConverter() { }
    
    
    /**
     * 
     * @param input input stream
     * @param doc File
     * @return ConvertedDocument
     * @throws IOException on IO failure with stream or conversion of content
     */
    @Override
    protected ConvertedDocument conversionImplementation(InputStream input, java.io.File doc)
            throws IOException {
        Metadata metadata = new Metadata();  
        ParseContext ctx = new ParseContext();
        BodyContentHandler handler = new BodyContentHandler();

        try {
            parser.parse(input, handler, metadata, ctx);
        } catch (NoClassDefFoundError classErr){
            throw new IOException("Unable to parse content due to Tika misconfiguration", classErr);
        } catch (Exception xerr) {
            throw new IOException("Unable to parse content", xerr);
        } finally {
            input.close();
        }

        /* Construct a response */
        ConvertedDocument textdoc = new ConvertedDocument(doc);

        /* Add essential metadata */
        textdoc.addTitle(metadata.get(TikaCoreProperties.TITLE));
        textdoc.setEncoding(metadata.get(Metadata.CONTENT_ENCODING));
        textdoc.addCreateDate(metadata.getDate(TikaCoreProperties.CREATED));
        textdoc.addAuthor(metadata.get(TikaCoreProperties.CREATOR));

        /* Mark the document as converted */
        textdoc.is_converted = true;
        return textdoc;
    }
       

}
