import java.io.IOException;

import org.opensextant.util.FileUtility;
import org.opensextant.xtext.ConversionListener;
import org.opensextant.xtext.ConvertedDocument;
import org.opensextant.xtext.XText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.getopt.LongOpt;

public class ExtractText {

    public static void usage() {
        System.out.println();
        System.out.println("==========XText Usage=============");
        System.out.println("XText --input input  [--help] "
                + "\n\t[--embed-conversion | --output folder ]   "
                + "\n\t[--embed-children   | --export folder] "
                + "\n\t[--clean-html]   [--strip-prefix path]");
        System.out.println("\t--help  print this message");
        System.out.println("\t--input  where <input> is file or folder");
        System.out.println("\t--output  where <folder> is output is a folder where you want to archive converted docs");
        System.out.println("\t--embed-children embeds the saved conversions in the input folder under 'xtext/'");
        System.out.println("\t--embed-conversion embeds the extracted children binaries in the input folder");
        System.out.println("         (NOT the conversions, the binaries from Archives, PST, etc)");
        System.out.println("         Default behavior is to extract originals to output archive.");
        System.out.println("\t--export folder\tOpposite of -c. Extract children and save to <folder>");
        System.out.println("         NOTE: -e has same effect as setting output to input");
        System.out.println("\t--clean-html enables HTML scrubbing");
        System.out.println("========================");
    }

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

    public static void main(String[] args) {

        LongOpt[] options = { new LongOpt("input", LongOpt.REQUIRED_ARGUMENT, null, 'i'),
                new LongOpt("output", LongOpt.REQUIRED_ARGUMENT, null, 'o'),
                new LongOpt("export", LongOpt.REQUIRED_ARGUMENT, null, 'x'),
                new LongOpt("strip-prefix", LongOpt.REQUIRED_ARGUMENT, null, 'p'),
                new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v'),
                new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
                new LongOpt("clean-html", LongOpt.NO_ARGUMENT, null, 'H'),
                new LongOpt("embed-conversion", LongOpt.NO_ARGUMENT, null, 'e'),
                new LongOpt("embed-children", LongOpt.NO_ARGUMENT, null, 'c'),
                new LongOpt("tika-pst", LongOpt.NO_ARGUMENT, null, 'T') };

        // "hcex:i:o:p:"
        gnu.getopt.Getopt opts = new gnu.getopt.Getopt("XText", args, "", options);

        String input = null;
        String output = null;
        boolean embed = false;
        boolean filter_html = false;
        boolean saveChildrenWithInput = false;
        boolean verbose = false;
        String saveChildrenTo = null;
        String prefix = null;

        XText xt = new XText();

        try {
            int c;
            while ((c = opts.getopt()) != -1) {
                switch (c) {

                case 0:
                    // Long opt processed.

                    break;

                case 'i':
                    input = opts.getOptarg();
                    break;
                case 'o':
                    output = opts.getOptarg();
                    break;
                case 'H':
                    filter_html = true;
                    break;
                case 'c':
                    saveChildrenWithInput = true;
                    break;
                case 'x':
                    saveChildrenTo = opts.getOptarg();
                    break;
                case 'p':
                    prefix = opts.getOptarg();
                    break;
                case 'v':
                    verbose = true;
                    break;
                case 'e':
                    embed = true;
                    System.out.println("Saving conversions to Input folder.  Output folder will be ignored.");
                    break;
                case 'T':
                    xt.enableTikaPST(true);
                    break;
                case 'h':
                default:
                    usage();
                    System.exit(1);
                }
            }
        } catch (Exception err) {
            usage();
            System.exit(1);
        }

        if (input == null) {
            System.out.println("An input argument is required, e.g., -input=/Folder/...");
            System.exit(-1);
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
            usage();
            ioerr.printStackTrace();
        }
    }

}
