
public class PNGImage {
    private Header header;

    public PNGImage(PNGData data) throws PngValidationException {
        header = new Header(data.getChunkData(ChunkTag.IHDR).get(0));
    }

    public Header getHeader() {
        return header;
    }

    enum ColourType {
        Greyscale("Greyscale"),
        Truecolour("Truecolour"),
        Indexedcolour("Indexed-colour"),
        GreyscaleAlpha("Greyscale with Alpha"),
        TruecolourAlpha("Truecolour with Alpha");

        private final String name;

        ColourType(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    class Header {

        private long width;
        private long height;
        private int bitDepth;
        private ColourType colourType;
        private int compressionMethod;
        private int filterMethod;
        private int interlaceMethod;

        public Header(byte[] ihdr) throws PngValidationException {
            parseData(ihdr);
        }

        private void parseData(byte[] data) throws PngValidationException {
            width = Utils.uintBytesToLong(data, 0);
            height = Utils.uintBytesToLong(data, 4);
            bitDepth = (int)data[8];

            switch ((int)data[9]) {
                case 0: colourType = ColourType.Greyscale; break;
                case 2: colourType = ColourType.Truecolour; break;
                case 3: colourType = ColourType.Indexedcolour; break;
                case 4: colourType = ColourType.GreyscaleAlpha; break;
                case 6: colourType = ColourType.TruecolourAlpha; break;
                default: throw new PngValidationException("PNG Validation failed: Invalid colour type in IHDR chunk");
            }

            compressionMethod = (int)data[10];
            filterMethod = (int)data[11];
            interlaceMethod = (int)data[12];
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Dimensions: %d x %d\n", width, height));
            sb.append(String.format("Bitdepth: %d bits/channel\n", bitDepth));
            sb.append(String.format("Colourtype: %s\n", colourType));
            sb.append(String.format("Compression: %d\n", compressionMethod));
            sb.append(String.format("Filter method: %d\n", filterMethod));
            sb.append(String.format("Interlace method: %d\n", interlaceMethod));

            return sb.toString();
        }

        public long getWidth() {
            return width;
        }

        public long getHeight( ) {
            return height;
        }

        public int getBitDepth( ) {
            return bitDepth;
        }

        public ColourType getColourType( ) {
            return colourType;
        }

        public int getCompressionMethod( ) {
            return compressionMethod;
        }

        public int getFilterMethod( ) {
            return filterMethod;
        }

        public int getInterlaceMethod( ) {
            return interlaceMethod;
        }
    }
}
