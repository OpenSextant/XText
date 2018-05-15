/*
 *
 *      Copyright 2014 The MITRE Corporation.
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
package org.opensextant.xtext;

import java.util.Properties;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public class Content {

    /**
     * Trivial pairing of a named/ID'd byte array, e.g., embedded object/file pulled from a containing document.
     */
    public Content() {

    }

    /**
     * any identifier for tracking the content.  If this is an object with no name or ID, caller should assign an enumeration, e.g.
     * parentID + "_part" + N,  e.g., docA_part4
     */
    public String id = null;

    public String encoding = null;

    /**
     * The mime type of the content.
     */
    public String mimeType = null;

    /**
     * a raw byte array for the content in motion.
     * This helps capture raw data before you have made a decision to write the data out and where
     */
    public byte[] content = null;

    /**
     * a proxy for the metadata sheet that eventually will end up in ConvertedDocument.meta
     */
    public Properties meta = new Properties();

    /**
     * carry Tika properties where possible
     */
    public Metadata tikaMetadata = null;
    /**
     * carry Tika MediaType where possible
     */
    public MediaType tikaMediatype = null;
}
