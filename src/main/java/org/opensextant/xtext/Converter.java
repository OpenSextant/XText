/// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|
//
// _____                                ____                     __                       __
///\  __`\                             /\  _`\                  /\ \__                   /\ \__
//\ \ \/\ \   _____      __     ___    \ \,\L\_\      __   __  _\ \ ,_\     __       ___ \ \ ,_\
// \ \ \ \ \ /\ '__`\  /'__`\ /' _ `\   \/_\__ \    /'__`\/\ \/'\\ \ \/   /'__`\   /' _ `\\ \ \/
//  \ \ \_\ \\ \ \L\ \/\  __/ /\ \/\ \    /\ \L\ \ /\  __/\/>  </ \ \ \_ /\ \L\.\_ /\ \/\ \\ \ \_
//   \ \_____\\ \ ,__/\ \____\\ \_\ \_\   \ `\____\\ \____\/\_/\_\ \ \__\\ \__/.\_\\ \_\ \_\\ \__\
//    \/_____/ \ \ \/  \/____/ \/_/\/_/    \/_____/ \/____/\//\/_/  \/__/ \/__/\/_/ \/_/\/_/ \/__/
//            \ \_\
//             \/_/
//
//   OpenSextant XText
//   Copyright 2012-2021 MITRE
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|
//
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
