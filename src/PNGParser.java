import java.io.*;
import java.util.*;

public class PNGParser {
    private String filename;
    private PNGData pngData;

    public PNGParser(String filename) {
        this.filename = filename;
        this.pngData = new PNGData();
    }

    public void parse() throws IOException {
        File pngFile = new File(filename);
        FileInputStream fis = new FileInputStream(pngFile);

        byte[] buf = new byte[8];

        if (fis.read(buf, 0, 8) < 8)
            throw new IOException("Unable to read PNG Header");

        if (!Arrays.equals(buf, new byte[]{(byte)137, (byte)80, (byte)78, (byte)71, (byte)13, (byte)10, (byte)26, (byte)10}))
            throw new IOException("Invalid Header");

        // read all the chunks in
        while (fis.available() != 0) {
            byte[] length = new byte[4];
            if (fis.read(length, 0, 4) < 4)
                throw new IOException("Broken file");

            long chunkLength = uintBytesToLong(length);

            byte[] data = new byte[(int)chunkLength + 4];
            if (fis.read(data, 0, data.length) < data.length)
                throw new IOException("Broken file");

            byte[] crc = new byte[4];
            if (fis.read(crc, 0, 4) < 4)
                throw new IOException("Broken file");

            long chunkCrc = uintBytesToLong(crc);
            pngData.addChunk(data, chunkCrc);
        }

        fis.close();
    }

    public PNGData getResult() throws PngValidationException {
        pngData.validate();
        return pngData;
    }

    private long uintBytesToLong(byte[] data) {
        if (data.length > 4)
            return 0L;

        long res = 0L;

        for (int i = 0, lsb = (data.length - 1) * 8; i < data.length; ++i, lsb -= 8) {
            res |= Byte.toUnsignedLong(data[i]) << lsb;
        }

        return res;
    }
}
