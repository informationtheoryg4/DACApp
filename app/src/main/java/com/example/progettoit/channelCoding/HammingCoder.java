package com.example.progettoit.channelCoding;

import static com.example.progettoit.channelCoding.HammingDecoder.hDecode;
import static com.example.progettoit.channelCoding.HammingDecoder.parityCheck;

public class HammingCoder {

    public static String hEncode(String p) {

        if(p.length()%8!=0)
            throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i+8 <= p.length(); i+=8) {
            sb.append(hamming_12_8_Code(p.substring(i, i+8)));
        }

        return sb.toString();
    }

    public static String hamming_7_4_Code(String p) {

        if(p.length()!=4)
            return null;

        String[] GT = {
                "1110000",
                "1001100",
                "0101010",
                "1101001"};
        String tmp = "0000000";

        char [] res = tmp.toCharArray();
        for (int j = 0; j < GT[0].length(); j++) {
            for (int i = 0; i < p.length(); i++) {
                res[j] = xor((Character.getNumericValue(GT[i].charAt(j))*Character.getNumericValue(p.charAt(i))),Character.getNumericValue(res[j]));
            }
        }

        return String.valueOf(res);
    }

    public static char xor(int b1, int b2) {
        return b1==b2? '0' : '1';
    }

    public static String hamming_12_8_Code(String p) {

        if(p.length()!=8)
            throw new IllegalArgumentException();

        String[] GT = {
                "111000000000",
                "100110000000",
                "010101000000",
                "110100100000",
                "100000011000",
                "010000010100",
                "110000010010",
                "000100010001"
        };
        String tmp = "000000000000";

        char [] res = tmp.toCharArray();
        for (int j = 0; j < GT[0].length(); j++) {
            for (int i = 0; i < p.length(); i++) {
                res[j] = xor((Character.getNumericValue(GT[i].charAt(j))*Character.getNumericValue(p.charAt(i))),
                        Character.getNumericValue(res[j]));
            }
        }

        return String.valueOf(res);
    }

    public static void main(String...args){
        String ex = "100110000110101111100011";
        System.out.println(ex);
        String enc = hEncode(ex);
        System.out.println(enc);
        char [] chars = enc.toCharArray();
        chars[24] = '0';
        String enc1 = new String(chars);
        System.out.println(enc1);
        System.out.println(hDecode(enc1));
    }

}
