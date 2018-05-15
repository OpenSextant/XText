/**
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.io.TikaInputStream;
import org.opensextant.xtext.Content;
import org.opensextant.xtext.ConvertedDocument;

public class WebArchiveConverter extends MessageConverter {

    /**
     * Convert MHT or .webarchive file to pure text.
     * Alternatively, export "archive" exploded on disk and then convert all children items.
     * See MessageConverter base and ArchiveNavigator solutions for that.
     *
     * @param in stream
     * @param doc original file
     */
    @Override
    protected ConvertedDocument conversionImplementation(InputStream in, File doc)
            throws IOException {
        TikaHTMLConverter htmlParser = new TikaHTMLConverter(false /* no scrub */);
        DefaultConverter objectParser = new DefaultConverter();

        ConvertedDocument d = super.conversionImplementation(in, doc);
        d.is_webArchive = true;

        if (!d.hasRawChildren()) {
            return d;
        }

        StringBuilder buf = new StringBuilder();
        for (Content binary : d.getRawChildren()) {
            logger.info("{} {} {}", d.id, binary.id, binary.mimeType);
            if (binary.mimeType == null) {
                continue;
            }
            if ("application/octet-stream".equalsIgnoreCase(binary.mimeType)) {
                ConvertedDocument obj = objectParser.convert(TikaInputStream.get(binary.content));
                if (obj != null && obj.hasText() && !isWebScript(obj.getText())) {
                    buf.append(obj.getText());
                    buf.append("\n==================\n");
                }
            } else if (binary.mimeType.startsWith("text/html")) {
                ConvertedDocument htmlDoc = htmlParser.convert(TikaInputStream.get(binary.content));
                if (htmlDoc != null && htmlDoc.hasText() && !isWebScript(htmlDoc.getText())) {
                    // Filter out HTML crap -- comments, javascript, etc. that comes through as octet-stream in these archives.
                    buf.append(htmlDoc.getText());
                    buf.append("\n==================\n");
                }
            } else if (binary.mimeType.startsWith("image")) {
                buf.append(String.format("\n[Image: %s type='%s']  ", binary.id, binary.mimeType));
            }
        }

        if (d.hasText()) {
            d.setText(d.getText() + "\n\n==================\n\n" + buf.toString());
        } else {
            d.setText(buf.toString());
        }

        return d;
    }

    /**
     *  JavaScript or any script detection.
     *
     * @param data data
     * @return if data is a script
     */
    public static boolean isWebScript(final String data) {
        if (StringUtils.isBlank(data)) {
            return true; /* not really */
        }

        int sub = Math.min(4000, data.length());
        String test = data.substring(0, sub - 1).toLowerCase().trim();

        // Typically the term 'script' does not actually appear in these octet-streams.
        if (test.contains("javascript") || test.contains("document.write(") || test.contains("xmlhttp")){
            return true;
        }
        // Less obvious clues.  Must contain all:
        if (test.contains("function") && test.contains("{") && test.contains("var ") && test.contains("=")) {
            return true;
        }

        return false;
    }

}
