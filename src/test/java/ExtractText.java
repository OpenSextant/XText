import java.io.IOException;

import org.apache.commons.cli.*;
import org.opensextant.ConfigException;
import org.opensextant.util.FileUtility;
import org.opensextant.xtext.ConversionListener;
import org.opensextant.xtext.ConvertedDocument;
import org.opensextant.xtext.XText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtractText {


    /**
     * Purely for logging when using the cmd line variation.
     * *
     *
     * @author ubaldino
     */
    static class MainProgramListener implements ConversionListener {

        private final Logger log = LoggerFactory.getLogger(getClass());
        private boolean verbosity = false;

        public MainProgramListener(boolean v) {
            this.verbosity = v;
        }

        @Override
        public void handleConversion(ConvertedDocument doc, String path) {
            boolean converted = false;
            if (doc != null) {
                converted = doc.is_converted;
            }
            log.info("Converted. FILE={} Status={}, Converted={}", path, doc != null, converted);
            if (this.verbosity) {
                log.info("\t {}", doc.getProperties());
            }
        }
    }

    public static void main(String[] args) throws ConfigException {

        Options options = new Options();

        // Args
        options.addOption("i", "input", true, "Input FILE or FOLDER");
        options.addOption("o", "output", true, "Output FOLDER");
        options.addOption("p", "strip-prefix", true, "Remove leading part of a path");

        // Flags
        options.addOption("v", "verbose", false, "verbose output");
        options.addOption("c", "embed-children", false, "embed children items in input folder as they are converted");
        options.addOption("e", "embed-conversion", false, "embeds the extracted children binaries in the input folder (NOT the conversions, the binaries from Archives, PST, etc)  Default behavior is to extract originals to output archive.");
        options.addOption("x", "export", false, "EXPERIMENTAL for archive files: export child conversions to a folder other than the output folder");
        options.addOption("H", "clean-html", false, "scrub HTML content to yield readable text");
        options.addOption("T", "tika-pst", false, "use Tika PST for MS Office messages");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        String input = null;
        String output = null;
        boolean embed = false;
        boolean filter_html = false;
        boolean saveChildrenWithInput = false;
        boolean verbose = false;
        String saveChildrenTo = null;
        String prefix = null;
        boolean tikapst = false;

        try {
            cmd = parser.parse(options, args);


            String opt = "input";
            if (cmd.hasOption(opt)) {
                input = cmd.getOptionValue(opt);
            }
            opt = "output";
            if (cmd.hasOption(opt)) {
                output = cmd.getOptionValue(opt);
            }

            opt = "export";
            if (cmd.hasOption(opt)) {
                saveChildrenTo = cmd.getOptionValue(opt);
                ;
            }
            opt = "strip-prefix";
            if (cmd.hasOption(opt)) {
                prefix = cmd.getOptionValue(opt);
                ;
            }

            // FLAGS
            opt = "clean-html";
            if (cmd.hasOption(opt)) {
                filter_html = true;
            }
            opt = "embed-children";
            if (cmd.hasOption(opt)) {
                saveChildrenWithInput = true;
            }
            opt = "verbose";
            if (cmd.hasOption(opt)) {
                verbose = true;
            }
            opt = "embed-conversion";
            if (cmd.hasOption(opt)) {
                embed = true;
            }
            opt = "tika-pst";
            if (cmd.hasOption(opt)) {
                tikapst = true;
            }

        } catch (Exception err) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ExtractText", " convert files to textual versions with metadata", options, "", true);
        }

        if (input == null) {
            System.out.println("An input argument is required, e.g., -input=/Folder/...");
            System.exit(-1);
        }

        XText xt = null;
        xt = new XText();
        if (tikapst) {
            xt.enableTikaPST(true);
        }

        // Setting LANG=en_US in your shell.
        //
        // System.setProperty("LANG", "en_US");

        xt.enableOverwrite(true); // Given this is a test application, we will
        // overwrite every time XText is called.
        xt.enableSaving(embed || output != null);
        xt.getPathManager().enableSaveWithInput(embed); // creates a ./text/ Folder locally in
        // directory.
        xt.enableHTMLScrubber(filter_html);
        xt.getPathManager().enableSaveChildrenWithInput(saveChildrenWithInput);

        // If user wishes to strip input paths of some prefix
        // Output will be dumped in the resulting relative path.
        xt.getPathManager().setStripPrefixPath(prefix);

        // Manage the extraction of compound files -- archives, PST mailbox file, etc.
        // ... others?
        if (!saveChildrenWithInput && saveChildrenTo != null) {
            xt.getPathManager().setExtractedChildrenCache(saveChildrenTo);
        }

        try {
            if (!embed) {
                if (output == null) {
                    output = "output";
                    xt.enableSaving(true); // Will save to output dir.
                    FileUtility.makeDirectory(output);
                    xt.getPathManager().setConversionCache(output);
                    System.out.println("Default output folder is $PWD/" + output);
                } else {
                    xt.enableSaving(true);
                    // Notice this main program requires an output path.
                    xt.getPathManager().setConversionCache(output);
                }
            }
            // Set itself to listen, as this is the main program.
            xt.setConversionListener(new MainProgramListener(verbose));

            xt.setup();
            xt.extractText(input);
        } catch (IOException ioerr) {
            System.err.println(ioerr.getMessage());
        }
    }

}
