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

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.opensextant.ConfigException;
import org.opensextant.xtext.Converter;
import org.opensextant.xtext.ExclusionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Archive is traversed, but no data is written to disk unless XText is in save
 * mode. Conversion listener should be listening for Converted Docs.
 *
 * @author Marc C. Ubaldino, MITRE, ubaldino at mitre dot org
 */
public class ArchiveNavigator implements Collector {

    private final Logger log = LoggerFactory.getLogger(ArchiveNavigator.class);
    private File saveDir = null;
    private ExclusionFilter filter = null;
    private Converter converter = null;
    public boolean overwrite = false;

    /**
     * Given a working temp folder and a file filter unpack archives.  Teh working dir, saveTo, is not created.
     * It must exist ahead of time;
     *
     * @param inputFile  input archive
     * @param saveTo     output dir where entries are saved.
     * @param fileFilter file exension filter
     * @param fileConv   conversion resource, e.g. instance of XText
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public ArchiveNavigator(File inputFile, String saveTo, ExclusionFilter fileFilter,
                            Converter fileConv) throws IOException {
        this.saveDir = new File(saveTo);
        filter = fileFilter;
        converter = fileConv;

        if (filter == null || converter == null) {
            throw new IOException(
                    "Filter and converter cannot be null -- XText is the default for both.");
        }

        currentArchive = inputFile;
    }

    public String getWorkingDir() {
        return saveDir.getAbsolutePath();
    }

    private File currentArchive = null;

    /**
     * Unpack any archive. You must provide a converter -- which converts each
     * file.
     *
     * @throws ConfigException if archive output dirs were requested but unsettable or non-existant
     * @throws IOException     if archive had I/O issues or is invalid type of archive
     */
    @Override
    public void collect() throws IOException, ConfigException {
        // Get file extension
        String ext = FilenameUtils.getExtension(currentArchive.getPath());

        File archivetmp = null;

        if (ext.equalsIgnoreCase("zip")) {
            archivetmp = unzip(currentArchive);
        } else if (ext.equalsIgnoreCase("tar")) {
            archivetmp = untar(currentArchive);
        } else if (ext.equalsIgnoreCase("gz") || ext.equalsIgnoreCase("tgz")
                || ext.equalsIgnoreCase("tar.gz")) {
            String basename = FilenameUtils.getBaseName(currentArchive.getName());
            // We assume the file is a tarball. First unzip it
            File tarFile = gunzipAsTAR(currentArchive, basename);

            // Then untar it
            archivetmp = untar(tarFile);
        } else {
            throw new IOException("Unsupported archive type: EXT=" + ext);
        }
        log.info("Archive FILE={} has been processed to DIR={}", currentArchive, archivetmp);
    }

    /*
     * Un-TAR. Oops. Its just a copy of Un-TAR and I replace tar with zip.
     *
     * so there may be Zip-specific stuff here, ... but the approach is the
     * same.
     */
    public File unzip(File zipFile) throws IOException {

        try (InputStream input = new BufferedInputStream(new FileInputStream(zipFile));
             ZipArchiveInputStream in = (ZipArchiveInputStream) (new ArchiveStreamFactory().createArchiveInputStream(
                     "zip", input))) {
            File workingDir = saveDir;
            ZipArchiveEntry zipEntry;
            while ((zipEntry = (ZipArchiveEntry) in.getNextEntry()) != null) {
                if (filterEntry(zipEntry)) {
                    continue;
                }

                try {
                    File tmpFile = saveArchiveEntry(zipEntry, in, workingDir);
                    converter.convert(tmpFile);

                } catch (IOException err) {
                    log.error(
                            "Unable to save item, FILE=" + zipEntry.getName() + "!"
                                    + zipEntry.getName(), err);
                }
            }
            return workingDir;

        } catch (ArchiveException ae) {
            throw new IOException(ae);
        }
    }

    /**
     * @param theFile archive file
     * @param fname   filename
     * @return TAR file path for result.
     * @throws IOException on I/O failure
     */
    private File gunzipAsTAR(File theFile, String fname) throws IOException {

        // TODO:  more testing on this particular case:  gunzip *.gz *.tgz *.tar.gz -- a mix of tar and gunzip
        String outFilename = getWorkingDir() + '/' + fname + ".tar";
        File outFile = new File(outFilename);

        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(theFile));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(outFilename))
        ) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            return outFile;
        }
    }

    /*
     * Un-TAR Once items are saved off to temp folder, they'll be converted by
     * the file converter. The converter can choose to do something else with
     * them.
     */
    public File untar(File tarFile) throws IOException {

        String _working = FilenameUtils.concat(getWorkingDir(),
                FilenameUtils.getBaseName(tarFile.getPath()));
        if (_working == null) {
            throw new IOException("Invalid archive path for " + tarFile.getPath());
        }
        File workingDir = new File(_working);
        workingDir.mkdir();

        try (InputStream input = new BufferedInputStream(new FileInputStream(tarFile));
             TarArchiveInputStream in = (TarArchiveInputStream) (new ArchiveStreamFactory().createArchiveInputStream(
                     "tar", input))
        ) {
            TarArchiveEntry tarEntry;
            while ((tarEntry = (TarArchiveEntry) in.getNextEntry()) != null) {
                if (filterEntry(tarEntry)) {
                    continue;
                }

                try {
                    File tmpFile = saveArchiveEntry(tarEntry, in, _working);
                    converter.convert(tmpFile);
                } catch (IOException err) {
                    log.error(
                            "Unable to save item, FILE=" + tarFile.getName() + "!"
                                    + tarEntry.getName(), err);
                }
            }
        } catch (ArchiveException ae) {
            throw new IOException(ae);
        }
        return workingDir;
    }

    /**
     * save to root dir
     *
     * @param e         archive entry
     * @param archiveio inputstream
     * @param root      root folder
     * @return output folder where archive was expanded/converted
     * @throws IOException if entry could not be saved to disk, e.g., outputDir
     */
    private File saveArchiveEntry(ArchiveEntry e, InputStream archiveio, File root)
            throws IOException {
        return saveArchiveEntry(e, archiveio, root.getAbsolutePath());
    }

    /**
     *
     */
    private File saveArchiveEntry(ArchiveEntry E, InputStream archiveio, String root)
            throws IOException {

        // Note: using native OS file path is fine here.  As long as you do not
        // try any string mechanics on paths.
        //
        String targetPath = FilenameUtils.concat(root, E.getName());
        if (targetPath == null) {
            throw new IOException("Invalid archive entry target for " + E.getName());
        }
        File target = new File(targetPath);
        if (target.exists() && !overwrite) {
            return target;
        }

        target.getParentFile().mkdirs();
        log.debug("ARCHIVE_ENTRY={}", E.getName());
        try (OutputStream output = new FileOutputStream(target)) {
            IOUtils.copy(archiveio, output);
        }
        return target;
    }

    private boolean filterEntry(ArchiveEntry E) {
        if (E.isDirectory()) {
            return true;
        }
        return filter.filterOutFile(E.getName());
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }
}
