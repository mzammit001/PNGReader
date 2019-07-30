import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.CRC32;

public class PNGData {
    private CRC32 checksum;
    private List<Chunk> chunks;

    public PNGData() {
        checksum = new CRC32();
        chunks = new ArrayList<>();
    }

    public void addChunk(byte[] data, long crc) {
        String chunkTag = new String(data, 0, 4, Charset.defaultCharset());

        checksum.reset();
        checksum.update(data, 0, data.length);

        if (checksum.getValue() != crc)
            throw new IllegalArgumentException(String.format("CRC mismatch on chunk with tag: %s", chunkTag));

        byte[] chunkData = Arrays.copyOfRange(data, 4, data.length);

        try {
            chunks.add(new Chunk(chunkTag, chunkData));
        }
        catch (IllegalArgumentException ex) {
            System.out.printf("Skipping chunk with tag: %s\n", chunkTag);
        }
    }

    public List<byte[]> getChunkData(ChunkTag tag) {
        List<byte[]> result = new ArrayList<>();

        for (Chunk c : chunks) {
            if (c.getTag() == tag)
                result.add(c.getData());
        }

        return result;
    }

    public List<ChunkTag> getChunkTags() {
        List<ChunkTag> tags = new ArrayList<>();

        for (Chunk c : chunks) {
            tags.add(c.getTag());
        }

        return tags;
    }

    public void validate() throws PngValidationException {
        Map<ChunkTag, Integer> found = new HashMap<>(
                Map.ofEntries(
                    Map.entry(ChunkTag.PLTE, 0),
                    Map.entry(ChunkTag.IDAT, 0),
                    Map.entry(ChunkTag.cHRM, 0),
                    Map.entry(ChunkTag.gAMA, 0),
                    Map.entry(ChunkTag.iCCP, 0),
                    Map.entry(ChunkTag.sBIT, 0),
                    Map.entry(ChunkTag.sRGB, 0),
                    Map.entry(ChunkTag.bKGD, 0),
                    Map.entry(ChunkTag.hIST, 0),
                    Map.entry(ChunkTag.tRNS, 0),
                    Map.entry(ChunkTag.pHYs, 0),
                    Map.entry(ChunkTag.sPLT, 0),
                    Map.entry(ChunkTag.tIME, 0),
                    Map.entry(ChunkTag.iTXt, 0),
                    Map.entry(ChunkTag.tEXt, 0),
                    Map.entry(ChunkTag.zTXt, 0)
                )
        );

        boolean foundPLTE = false;

        if (chunks.get(0).getTag() != ChunkTag.IHDR)
            throw new PngValidationException("IHDR chunk must be the first chunk");

        if (chunks.get(chunks.size()-1).getTag() != ChunkTag.IEND)
            throw new PngValidationException("IEND chunk must be the last chunk");

        for (int i = 1; i < chunks.size() - 1; ++i) {
            if (chunks.get(i).getTag() == ChunkTag.PLTE) {
                foundPLTE = true;
                break;
            }
        }

        for (int i = 1; i < chunks.size() - 1; ++i) {
            switch (chunks.get(i).getTag()) {
                case IHDR:
                    throw new PngValidationException("Only one IHDR chunk can exist");
                case IEND:
                    throw new PngValidationException("Only one IEND chunk can exist");
                case PLTE:
                    if (found.get(ChunkTag.PLTE) > 0)
                        throw new PngValidationException("Only one PLTE chunk may be preset");
                    if (found.get(ChunkTag.IDAT) > 0)
                        throw new PngValidationException("PLTE chunk must be before first IDAT chunk");
                    found.put(ChunkTag.PLTE, 1);
                    break;
                case IDAT:
                    found.put(ChunkTag.IDAT, found.get(ChunkTag.IDAT) + 1);
                    break;
                 case cHRM:
                    if (found.get(ChunkTag.cHRM) > 0)
                        throw new PngValidationException("Only one cHRM chunk may be preset");
                    if ((foundPLTE && found.get(ChunkTag.PLTE) > 0) || found.get(ChunkTag.IDAT) > 0)
                        throw new PngValidationException("cHRM chunk must be before first IDAT and PLTE chunks");
                    found.put(ChunkTag.cHRM, 1);
                    break;
                case gAMA:
                    if (found.get(ChunkTag.gAMA) > 0)
                        throw new PngValidationException("Only one gAMA chunk may be preset");
                    if ((foundPLTE && found.get(ChunkTag.PLTE) > 0) || found.get(ChunkTag.IDAT) > 0)
                        throw new PngValidationException("gAMA chunk must be before first IDAT and PLTE chunks");
                    found.put(ChunkTag.gAMA, 1);
                    break;
                case iCCP:
                    if (found.get(ChunkTag.iCCP) > 0)
                        throw new PngValidationException("Only one iCCP chunk may be preset");
                    if ((foundPLTE && found.get(ChunkTag.PLTE) > 0) || found.get(ChunkTag.IDAT) > 0)
                        throw new PngValidationException("iCCP chunk must be before first IDAT and PLTE chunks");
                    if (found.get(ChunkTag.sRGB) > 0)
                        throw new PngValidationException("iCCP chunk cannot coexist with sRGB chunk");
                    found.put(ChunkTag.iCCP, 1);
                    break;
                case sBIT:
                    if (found.get(ChunkTag.sBIT) > 0)
                        throw new PngValidationException("Only one sBIT chunk may be preset");
                    if ((foundPLTE && found.get(ChunkTag.PLTE) > 0) || found.get(ChunkTag.IDAT) > 0)
                        throw new PngValidationException("sBIT chunk must be before first IDAT and PLTE chunks");
                    found.put(ChunkTag.sBIT, 1);
                    break;
                case sRGB:
                    if (found.get(ChunkTag.sRGB) > 0)
                        throw new PngValidationException("Only one sRGB chunk may be preset");
                    if ((foundPLTE && found.get(ChunkTag.PLTE) > 0) || found.get(ChunkTag.IDAT) > 0)
                        throw new PngValidationException("sRGB chunk must be before first IDAT and PLTE chunks");
                    if (found.get(ChunkTag.iCCP) > 0)
                        throw new PngValidationException("sRGB chunk cannot coexist with iCCP chunk");
                    found.put(ChunkTag.sRGB, 1);
                    break;
                case bKGD:
                    if (found.get(ChunkTag.bKGD) > 0)
                        throw new PngValidationException("Only one bKGD chunk may be preset");
                    if ((foundPLTE && found.get(ChunkTag.PLTE) == 0) || found.get(ChunkTag.IDAT) > 0)
                        throw new PngValidationException("bKGD chunk must be before first IDAT and after PLTE chunks");
                    found.put(ChunkTag.bKGD, 1);
                    break;
                case hIST:
                    if (found.get(ChunkTag.hIST) > 0)
                        throw new PngValidationException("Only one hIST chunk may be preset");
                    if ((foundPLTE && found.get(ChunkTag.PLTE) == 0) || found.get(ChunkTag.IDAT) > 0)
                        throw new PngValidationException("hIST chunk must be before first IDAT and after PLTE chunks");
                    found.put(ChunkTag.hIST, 1);
                    break;
                case tRNS:
                    if (found.get(ChunkTag.tRNS) > 0)
                        throw new PngValidationException("Only one tRNS chunk may be preset");
                    if ((foundPLTE && found.get(ChunkTag.PLTE) == 0) || found.get(ChunkTag.IDAT) > 0)
                        throw new PngValidationException("tRNS chunk must be before first IDAT and after PLTE chunks");
                    found.put(ChunkTag.tRNS, 1);
                    break;
                case pHYs:
                    if (found.get(ChunkTag.pHYs) > 0)
                        throw new PngValidationException("Only one pHYs chunk may be preset");
                    if (found.get(ChunkTag.IDAT) > 0)
                        throw new PngValidationException("pHYs chunk must be before the first IDAT chunk");
                    found.put(ChunkTag.pHYs, 1);
                    break;
                case sPLT:
                    if (found.get(ChunkTag.IDAT) > 0)
                        throw new PngValidationException("sPLT chunk must be before the first IDAT chunk");
                    found.put(ChunkTag.sPLT, found.get(ChunkTag.sPLT) + 1);
                    break;
                case tIME:
                    if (found.get(ChunkTag.tIME) > 0)
                        throw new PngValidationException("Only one tIME chunk may be preset");
                    found.put(ChunkTag.tIME, 1);
                    break;
                case iTXt:
                    found.put(ChunkTag.iTXt, found.get(ChunkTag.iTXt) + 1);
                    break;
                case tEXt:
                    found.put(ChunkTag.tEXt, found.get(ChunkTag.tEXt) + 1);
                    break;
                case zTXt:
                    found.put(ChunkTag.zTXt, found.get(ChunkTag.zTXt) + 1);
                    break;
            }
        }

        if (found.get(ChunkTag.IDAT) == 0)
            throw new PngValidationException("There must be at least one IDAT chunk");

    }
}
