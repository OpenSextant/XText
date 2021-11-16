
import org.opensextant.xtext.ConvertedDocument;
import org.opensextant.xtext.converters.MessageConverter;
import org.opensextant.xtext.converters.OfficeConverter;

public class OfficeParserTool {

    /*
     * TODO: Both MessageConverter and OfficeConverter are failing in basic .EML and .MSG files.
     *  no standards....
     */


    public static void main(String[] args) {
        OfficeConverter converter = new OfficeConverter();

        String msMsg, mimeMsg = null;
        ConvertedDocument msdoc = null, mimedoc = null;
        try {
            msdoc = converter.convert(args[0]);
            msMsg = "success - " + msdoc.getProperty("title");
        } catch (Exception err) {
            //err.printStackTrace();
            msMsg = err.getMessage();
        }

        try {
            mimedoc = new MessageConverter().convert(args[0]);
            mimeMsg = "success - " + mimedoc.getProperty("title");
        } catch (Exception err) {
            mimeMsg = err.getMessage();
        }

        System.out.println("MS OfficeConverter\n\tResult:" + msMsg);
        if (msdoc != null) {
            System.out.println("\tDoc " + msdoc.toString());
        }

        System.out.println("MIME MessageConverter\n\tResult:" + mimeMsg);
        if (mimedoc != null) {
            System.out.println("\tDoc " + mimedoc.toString());
        }
    }
}
