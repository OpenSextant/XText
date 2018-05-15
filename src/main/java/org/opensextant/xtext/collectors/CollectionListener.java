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
package org.opensextant.xtext.collectors;

import java.io.File;
import java.io.IOException;

import org.opensextant.xtext.ConvertedDocument;

public interface CollectionListener {

    /**
     * Listen for collected and converted document.
     *
     * @param doc converted document
     * @param filepath raw path
     * @throws IOException on I/O failure,  e.g., doc/file path could not be recorded by your app.
     */
    void collected(ConvertedDocument doc, String filepath) throws IOException;

    /**
     * Listen for collected raw files.
     *
     * @param doc a file object as seen by your collection/harvester implementation.
     * @throws IOException on I/O failure
     */
    void collected(File doc) throws IOException;

    /**
     * If the item identified by ID already exists, the collector will pass by it quietly
     * @param oid object ID to check against some data store
     * @return true if item exists in the caller's collection.  Implementor can then make a decision what to do if item exists or not.
     */
    boolean exists(String oid);
}
