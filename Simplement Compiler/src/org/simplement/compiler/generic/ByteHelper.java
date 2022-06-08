package org.simplement.compiler.generic;

import java.io.UnsupportedEncodingException;

public final class ByteHelper {
    private ByteHelper() { }

    public static byte[] convertInteger(long integral, int byteSize) {
        if(byteSize < 1 || byteSize > 8)
            throw new IllegalArgumentException("byteSize out of range. Must be between 1 and 8 inclusive.");
        byte[] bytes = new byte[byteSize];
        for(int i = 0;i < byteSize;++ i)
            bytes[i] = (byte)((integral >> ((byteSize - i - 1) * 8)) & 0xFF);
        return bytes;
    }

    public static long getInteger(byte[] bytes, int index, int byteSize) {
        if(byteSize < 1 || byteSize > 8)
            throw new IllegalArgumentException("byteSize out of range. Must be between 1 and 8 inclusive.");
        long integral = 0L;
        for(int i = index;i < index + byteSize;++ i)
            integral = (integral << 8) | (long)(bytes[i] & 0xFF);
        return integral;
    }


    public static byte[] convertInt(int num) {
        return new byte[] {
                (byte)((num >> 24) & 0xFF),
                (byte)((num >> 16) & 0xFF),
                (byte)((num >> 8) & 0xFF),
                (byte)(num & 0xFF)
        };
    }

    public static int getInt(byte[] bytes, int index) {
        return (bytes[index] & 0xFF) << 24 |
                (bytes[index + 1] & 0xFF) << 16 |
                (bytes[index + 2] & 0xFF) << 8 |
                (bytes[index + 3] & 0xFF);
    }

    public static byte[] convertLong(long num) {
        return new byte[] {
                (byte)((num >> 56) & 0xFF),
                (byte)((num >> 48) & 0xFF),
                (byte)((num >> 40) & 0xFF),
                (byte)((num >> 32) & 0xFF),
                (byte)((num >> 24) & 0xFF),
                (byte)((num >> 16) & 0xFF),
                (byte)((num >> 8) & 0xFF),
                (byte)(num & 0xFF)
        };
    }

    public static long getLong(byte[] bytes, int index) {
        return (((long)bytes[index]) & 0xFF) << 56 |
                (((long)bytes[index + 1]) & 0xFF) << 48 |
                (((long)bytes[index + 2]) & 0xFF) << 40 |
                (((long)bytes[index + 3]) & 0xFF) << 32 |
                (((long)bytes[index + 4]) & 0xFF) << 24 |
                (((long)bytes[index + 5]) & 0xFF) << 16 |
                (((long)bytes[index + 6]) & 0xFF) << 8 |
                (((long)bytes[index + 7]) & 0xFF);
    }

    public static byte[] convertShort(short num) {
        return new byte[] {
                (byte)((num >> 8) & 0xFF),
                (byte)(num & 0xFF)
        };
    }

    public static short getShort(byte[] bytes, int index) {
        return (short)((bytes[index] & 0xFF) << 8 |
                (bytes[index + 1] & 0xFF));
    }

    public static byte[] convertDouble(double dbl) {
        return convertLong(Double.doubleToRawLongBits(dbl));
    }

    public static byte[] convertFloat(float flt) {
        return convertInt(Float.floatToRawIntBits(flt));
    }

    public static double getDouble(byte[] bytes, int index) {
        return Double.longBitsToDouble(getLong(bytes, index));
    }

    public static float getFloat(byte[] bytes, int index) {
        return Float.intBitsToFloat(getInt(bytes, index));
    }

    public static byte convertBoolean(boolean bool) {
        return bool ? (byte)1 : (byte)0;
    }

    public static boolean getBoolean(byte by) {
        return by == 1;
    }

    public static boolean getBoolean(byte[] bytes, int index) {
        return bytes[index] == 1;
    }

    public static byte[] convertString(String string) {
        try {
            byte[] strBytes = string.getBytes("UTF-8");
            int len = string.length();
            if(len > 0xFFFF)
                return new byte[0];
            byte[] encoded = new byte[strBytes.length + 2];
            encoded[0] = (byte)((len >>> 8) & 0xFF);
            encoded[1] = (byte)(len & 0xFF);
            System.arraycopy(strBytes, 0, encoded, 2, strBytes.length);
            return encoded;
        }catch(UnsupportedEncodingException ex) {
            throw new InternalError("UTF-8 not supported.", ex);
        }
    }
}
