public class Chunk {
    private ChunkTag tag;
    private byte[] data;

    public Chunk(String tag, byte[] data) throws IllegalArgumentException {
        this.tag = validateTag(tag);
        this.data = data;
    }

    public ChunkTag getTag() {
        return tag;
    }

    public byte[] getData() {
        return data;
    }

    private ChunkTag validateTag(String tag) {
        switch (tag) {
            case "IHDR":
                return ChunkTag.IHDR;
            case "PLTE":
                return ChunkTag.PLTE;
            case "IDAT":
                return ChunkTag.IDAT;
            case "IEND":
                return ChunkTag.IEND;
            case "cHRM":
                return ChunkTag.cHRM;
            case "gAMA":
                return ChunkTag.gAMA;
            case "iCCP":
                return ChunkTag.iCCP;
            case "sBIT":
                return ChunkTag.sBIT;
            case "sRGB":
                return ChunkTag.sRGB;
            case "bKGD":
                return ChunkTag.bKGD;
            case "hIST":
                return ChunkTag.hIST;
            case "tRNS":
                return ChunkTag.tRNS;
            case "pHYs":
                return ChunkTag.pHYs;
            case "sPLT":
                return ChunkTag.sPLT;
            case "tIME":
                return ChunkTag.tIME;
            case "iTXt":
                return ChunkTag.iTXt;
            case "tEXt":
                return ChunkTag.tEXt;
            case "zTXt":
                return ChunkTag.zTXt;
            default:
                throw new IllegalArgumentException("Unknown Chunk Tag");
        }
    }

}
