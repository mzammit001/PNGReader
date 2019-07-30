import java.io.FileNotFoundException;
import java.io.IOException;

public class PNGReader {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: PNGReader <input file>");
            return;
        }

        PNGParser parser = new PNGParser(args[0]);

        try {
            parser.parse();
            PNGData data = parser.getResult();

            for (ChunkTag ct : data.getChunkTags()) {
                System.out.println(ct);
            }

            PNGImage image = new PNGImage(data);
            System.out.println(image.getHeader());

        } catch (FileNotFoundException ex) {
            System.out.printf("FILE NOT FOUND: %s\n", args[0]);
            System.exit(1);
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        catch (PngValidationException ex) {
            System.out.println(ex);
            System.exit(1);
        }

    }
}
