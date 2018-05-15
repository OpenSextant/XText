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

package org.opensextant.xtext;

import java.io.IOException;

import org.opensextant.ConfigException;

/**
 * Converters are strongly recommended to provide a ability to read a File object
 *  ... and get more metadata from that implementation
 * as well as a an ability to read from a String
 *
 * Inputstreams and IO are handled solely within the implemenation.
 *
 * see converters.ConverterAdapter as a base class.
 *
 * @author Marc C. Ubaldino, MITRE, ubaldino at mitre dot org
 */
public interface Converter {

    /**
     * Convert.
     *
     * @param doc file object to convert
     * @return the converted document
     * @throws IOException i/o error
     * @throws ConfigException cfg err
     */
    ConvertedDocument convert(java.io.File doc) throws IOException, ConfigException;

    /**
     * Convert.
     *
     * @param data file path to convert
     * @return the converted document
     * @throws IOException i/o error
     * @throws ConfigException cfg err
     */
    ConvertedDocument convert(String data) throws IOException, ConfigException;
}
