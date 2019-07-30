public enum ChunkTag {
    IHDR("IHDR"),
    PLTE("PLTE"),
    IDAT("IDAT"),
    IEND("IEND"),
    cHRM("cHRM"),
    gAMA("gAMA"),
    iCCP("iCCP"),
    sBIT("sBIT"),
    sRGB("sRGB"),
    bKGD("bKGD"),
    hIST("hIST"),
    tRNS("tRNS"),
    pHYs("pHYs"),
    sPLT("sPLT"),
    tIME("tIME"),
    iTXt("iTXt"),
    tEXt("tEXt"),
    zTXt("zTXt");

    private final String name;

    ChunkTag(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
