public class Utils {
    static long uintBytesToLong(byte[] data) {
        return uintBytesToLong(data, 0);
    }

    static long uintBytesToLong(byte[] data, int offset) {
        long res = 0L;
        int end = offset + 4;

        // lsb = left-shift bits
        for (int i = offset, lsb = 24; i < end; ++i, lsb -= 8) {
            res |= Byte.toUnsignedLong(data[i]) << lsb;
        }

        return res;
    }

    static long ushortBytesToLong(byte[] data) {
        return ushortBytesToLong(data, 0);
    }

    static long ushortBytesToLong(byte[] data, int offset) {
        long res = 0L;
        int end = offset + 2;

        // lsb = left-shift bits
        for (int i = offset, lsb = 8; i < end; ++i, lsb -= 8) {
            res |= Byte.toUnsignedLong(data[i]) << lsb;
        }

        return res;
    }

    static void displayBytes(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            System.out.printf("%02x  ", data[i]);
            if ((i % 16 == 0 && i > 0) || i == data.length - 1)
                System.out.printf("\n");
        }
    }
}
