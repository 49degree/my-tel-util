package com.szxys.mhub.subsystem.mets.utils;

/**
 * Created by shiwen.chai.
 * User: Administrator
 * Date: 11-5-12
 * Time: 上午11:19
 */
public final class BCD {

    public static byte[] toBCD(String data) {
        byte result[] = new byte[data.length() / 2];
        for (int i = 0, j = 0; i < data.length(); i += 2, j++) {
            byte l = (byte)(data.charAt(i + 1) & 0x0f);
            byte h = (byte)(data.charAt(i) & 0x0f);
            result[j] = (byte)((h << 4) | l);
        }

        return result;
    }

    public static char[] toStr(byte[] data) {
        char result[] = new char[data.length * 2];
        for (int i = 0, j = 0; i < data.length; i++, j += 2) {
            result[j] = (char)((data[i] >> 4) | 0x30);
            result[j + 1] = (char)((data[i] & 0x0f) | 0x30);
        }

        return result;
    }
}
